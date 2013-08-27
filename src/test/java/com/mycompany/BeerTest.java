package com.mycompany;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.model.Beer;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import fr.ybonnel.simpleweb4j.test.SimpleWeb4jTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.stop;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BeerTest extends SimpleWeb4jTest {


    private MongodExecutable mongodExe = null;
    private MongodProcess mongodProc = null;

    @Before
    public void setup() throws IOException {
        Random random = new Random();
        int portMongo = Integer.getInteger("test.mongo.port", random.nextInt(10000) + 10000);
        Main.startServer(getPort(), false, portMongo);

        MongodStarter runtime = MongodStarter.getDefaultInstance();
        mongodExe = runtime.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(portMongo, Network.localhostIsIPv6()))
                .build());
        mongodProc = mongodExe.start();

    }

    @After
    public void tearDown() {
        stop();
        mongodProc.stop();
        mongodExe.stop();
    }



    @Test
    public void should_return_no_member() {
        List<Beer> beers = new Gson().fromJson(HttpRequest.get(defaultUrl() + "/beer").body(), new TypeToken<List<Class>>(){}.getType());
        assertTrue(beers.isEmpty());
    }

    public String insertMember() {
        Beer beer = new Beer();
        beer.setName("name");
        Gson gson = new Gson();
        HttpRequest send = HttpRequest.post(defaultUrl() + "/beer").send(gson.toJson(beer));
        assertEquals(send.body(), 201, send.code());

        List<Beer> beers = gson.fromJson(HttpRequest.get(defaultUrl() + "/beer").body(), new TypeToken<List<Beer>>(){}.getType());
        assertEquals(1, beers.size());
        assertEquals("name", beers.get(0).getName());
        return beers.get(0).getId();
    }

    @Test
    public void should_create_and_get_by_id() {
        String id = insertMember();

        Beer newBeer = new Gson().fromJson(HttpRequest.get(defaultUrl() + "/beer/" + id).body(), Beer.class);
        assertNotNull(newBeer);
        assertEquals(id, newBeer.getId());
        assertEquals("name", newBeer.getName());
    }

    @Test
    public void should_update() {
        String id = insertMember();
        Gson gson = new Gson();

        Beer newBeer = new Beer();
        newBeer.setName("newName");

        assertEquals(204, HttpRequest.put(defaultUrl() + "/beer/" + id).send(gson.toJson(newBeer)).code());

        List<Beer> beers = gson.fromJson(HttpRequest.get(defaultUrl() + "/beer").body(), new TypeToken<List<Beer>>(){}.getType());
        assertEquals(1, beers.size());
        assertEquals("newName", beers.get(0).getName());
    }

    @Test
    public void should_delete() {
        String id = insertMember();
        Gson gson = new Gson();

        assertEquals(204, HttpRequest.delete(defaultUrl() + "/beer/" + id).code());
        List<Beer> beers = gson.fromJson(HttpRequest.get(defaultUrl() + "/beer").body(), new TypeToken<List<Beer>>(){}.getType());
        assertTrue(beers.isEmpty());
    }

}
