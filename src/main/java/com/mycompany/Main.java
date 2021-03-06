package com.mycompany;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mycompany.model.Beer;
import com.mycompany.services.BeerRessource;
import com.mycompany.services.MongoService;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.IOException;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;

/**
 * Main class.
 */
public class Main {

    private static boolean dev = true;

    public static boolean isDev() {
        return dev;
    }

    /**
     * Start the server.
     * @param port http port to listen.
     * @param waitStop true to wait the stop.
     */
    public static void startServer(int port, boolean waitStop, int portMongo) throws IOException {
        // Set the http port.
        setPort(port);
        // Set the path to static resources.
        setPublicResourcesPath("/com/mycompany/public");

        MongodExecutable mongodExe = null;
        MongodProcess mongodProc = null;
        if (isDev() && waitStop) {
            MongodStarter runtime = MongodStarter.getDefaultInstance();
            mongodExe = runtime.prepare(new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(portMongo, Network.localhostIsIPv6()))
                    .build());
            mongodProc = mongodExe.start();
        }

        if (isDev()) {
            MongoService.setMongoClient(new MongoClient("localhost", portMongo));
        } else {
            String uriAsString = System.getProperty("MONGOHQ_URL_BEERS");
            MongoClientURI uri = new MongoClientURI(uriAsString);
            MongoService.setMongoClient(new MongoClient(uri));
            MongoService.setDbName(uri.getDatabase());
        }

        resource(new BeerRessource("/beer"));
        // Start the server.
        start(waitStop);

        if (isDev() && waitStop) {
            mongodProc.stop();
            mongodExe.stop();
        }
    }

    /**
     * @return port to use
     */
    private static int getPort() {
        // Heroku
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            dev = false;
            return Integer.parseInt(herokuPort);
        }

        // Cloudbees
        String cloudbeesPort = System.getProperty("app.port");
        if (cloudbeesPort != null) {
            dev = false;
            return Integer.parseInt(cloudbeesPort);
        }

        // Default port;
        return 9999;
    }

    /**
     * @return port to use
     */
    private static int getPortMongo() {
        // Default port;
        return 9998;
    }

    public static void main(String[] args) throws IOException {
        // For main, we want to wait the stop.
        startServer(getPort(), true, getPortMongo());
    }
}
