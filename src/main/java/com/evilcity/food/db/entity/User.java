package com.evilcity.food.db.entity;

import com.evilcity.food.db.ConnectionManager;
import com.evilcity.food.db.DBAbstractEntity;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class User extends DBAbstractEntity {
    public User(Document raw) {
        super("users", raw);
    }
    public String getUsername() {
        return getString("username");
    }
    public void setUsername(String username) {
        set("username", username);
    }

    public static User getUserByUsername(String username) {
        Document raw = ConnectionManager.getDatabase().getCollection("users")
                .find(Filters.eq("username", username))
                .first();
        return raw == null ? null : new User(raw);
    }
    public static User registerUser(String uid, String username) {
        Document raw = new Document()
                .append("uid", uid)
                .append("username", username);
        ConnectionManager.getDatabase().getCollection("users").insertOne(raw);
        return new User(raw);
    }
}
