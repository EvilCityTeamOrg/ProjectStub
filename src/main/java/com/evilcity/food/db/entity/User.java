package com.evilcity.food.db.entity;

import com.evilcity.food.db.ConnectionManager;
import com.evilcity.food.db.DBAbstractEntity;
import com.evilcity.food.utils.Security;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.security.MessageDigest;

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
    public void setEmail(String email) { set("email", email); }
    public void setPassword(String password) { set("password", Security.generateHash(password)); }
    public boolean comparePassword(String password1) { return Security.generateHash(password1).equals(getString("password")); }
    public String getEmail() { return getString("email"); }

    public static User getUserByUsername(String username) {
        Document raw = ConnectionManager.getDatabase().getCollection("users")
                .find(Filters.eq("username", username))
                .first();
        return raw == null ? null : new User(raw);
    }
    public static User getUserByUID(String uid) {
        Document raw = ConnectionManager.getDatabase().getCollection("users")
                .find(Filters.eq("uid", uid))
                .first();
        return raw == null ? null : new User(raw);
    }
    public static User registerUser(String uid, String username, String email, String password) {
        Document raw = new Document()
                .append("uid", uid)
                .append("username", username)
                .append("email", email)
                .append("password", Security.generateHash(password));
        ConnectionManager.getDatabase().getCollection("users").insertOne(raw);
        return new User(raw);
    }
}
