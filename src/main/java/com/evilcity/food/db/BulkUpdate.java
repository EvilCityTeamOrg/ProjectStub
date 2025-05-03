package com.evilcity.food.db;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;

public class BulkUpdate {
    private DBAbstractEntity parent;
    private Bson bson;

    public BulkUpdate(DBAbstractEntity parent) {
        this.parent = parent;
    }
    public BulkUpdate setOne(String key, Object value) {
        if (bson == null) bson = Updates.set(key, value);
        else bson = Updates.combine(bson, Updates.set(key, value));
        return this;
    }
    public void run() {
        ConnectionManager.getDatabase().getCollection(parent.collection).findOneAndUpdate(Filters.eq("uid", parent.uid()), bson);
    }
}
