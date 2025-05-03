package com.evilcity.food.db;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * Abstract logic for database entities.
 * Every entity that uses this class <b>MUST</b> contain unique uid field for identification.
 */
public class DBAbstractEntity {
    protected final Document raw;
    protected final String collection;

    public Document getRaw() {
        return raw;
    }

    public DBAbstractEntity(String collection, Document raw) {
        this.collection = collection;
        this.raw = raw;
    }

    protected String getString(String key) {
        return raw.getString(key);
    }
    protected void set(String key, String value) {
        raw.put(key, value);
        ConnectionManager.update(collection, uid(), key, value);
    }
    protected int getInt(String key) {
        return raw.getInteger(key);
    }
    protected void set(String key, int value) {
        raw.put(key, value);
        ConnectionManager.update(collection, uid(), key, value);
    }
    protected boolean getBoolean(String key) {
        return raw.getBoolean(key);
    }
    protected void set(String key, boolean value) {
        raw.put(key, value);
        ConnectionManager.update(collection, uid(), key, value);
    }
    protected Document getChildDocument(String key) {
        return raw.get(key, Document.class);
    }
    protected void set(String key, Document value) {
        raw.put(key, value);
        ConnectionManager.update(collection, uid(), key, value);
    }
    protected MongoCollection<Document> getCollection() {
        return ConnectionManager.getDatabase().getCollection(collection);
    }

    public String uid() {
        return getString("uid");
    }

    /**
     * Creates BulkUpdate instance designed to update multiple values in single database request.
     */
    public BulkUpdate bulkUpdate() {
        return new BulkUpdate(this);
    }

    @Override
    public String toString() {
        return super.toString() + "=" + raw.toJson();
    }
}
