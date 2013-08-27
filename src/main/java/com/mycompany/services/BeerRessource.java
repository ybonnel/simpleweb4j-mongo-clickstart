package com.mycompany.services;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mycompany.model.Beer;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.resource.RestResource;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BeerRessource extends RestResource<Beer> {

    private static final String COL_NAME = "beers";

    public BeerRessource(String route) {
        super(route, Beer.class);
    }

    @Override
    public Beer getById(String id) throws HttpErrorException {
        return Beer.fromDbObject(
                MongoService.getDB().getCollection(COL_NAME).findOne(
                        new BasicDBObject("_id", new ObjectId(id))));
    }

    @Override
    public Collection<Beer> getAll() throws HttpErrorException {
        List<Beer> beers = new ArrayList<>();
        for (DBObject object : MongoService.getDB().getCollection(COL_NAME).find()) {
            beers.add(Beer.fromDbObject(object));
        }
        return beers;
    }

    @Override
    public void update(String id, Beer resource) throws HttpErrorException {
        resource.setId(id);
        MongoService.getDB().getCollection(COL_NAME).save(resource.toDbObject());
    }

    @Override
    public Beer create(Beer resource) throws HttpErrorException {
        DBObject object = resource.toDbObject();
        MongoService.getDB().getCollection(COL_NAME).save(object);
        return Beer.fromDbObject(object);
    }

    @Override
    public void delete(String id) throws HttpErrorException {
        DBCollection collection = MongoService.getDB().getCollection(COL_NAME);
        DBObject object = collection.findOne(new BasicDBObject("_id", new ObjectId(id)));
        MongoService.getDB().getCollection(COL_NAME).remove(object);
    }
}
