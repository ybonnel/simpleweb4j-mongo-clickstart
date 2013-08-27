package com.mycompany.model;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

public class Beer {

    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Beer fromDbObject(DBObject object) {
        if (object == null) {
            return null;
        }
        Beer beer = new Beer();
        beer.setId(object.get("_id").toString());
        beer.setName(object.get("name").toString());
        return beer;
    }

    public DBObject toDbObject() {
        DBObject object = new BasicDBObject();
        if (id != null) {
            object.put("_id", new ObjectId(id));
        }
        object.put("name", name);
        return object;
    }
}
