package com.evilcity.food.db.entity;

import com.evilcity.food.db.ConnectionManager;
import com.evilcity.food.db.DBAbstractEntity;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Restaurant extends DBAbstractEntity {
    public Restaurant(Document raw) {
        super("restaurants", raw);
    }

    public void setName(String name) { set("name", name); }
    public void setIconUrl(String iconUrl) { set("iconUrl", iconUrl); }
    public void setLat(List<String> lat) {
        StringBuilder sb = new StringBuilder();
        for (String la : lat) {
            sb.append(la).append("|");
        }
        set("lat", sb.toString());
    }
    public List<String> getLat() { return new ArrayList<>(List.of(getString("lat").split("\\|"))); }
    public void setLon(List<String> lon) {
        StringBuilder sb = new StringBuilder();
        for (String la : lon) {
            sb.append(la).append("|");
        }
        set("lon", sb.toString());
    }
    public List<String> getLon() { return new ArrayList<>(List.of(getString("lon").split("\\|"))); }

    public static List<Restaurant> getRestaurant() {
        MongoCursor<Document> raw = ConnectionManager.getDatabase().getCollection("restaurants").find().iterator();
        ArrayList<Restaurant> list = new ArrayList<>();
        while (raw.hasNext()) {
            list.add(new Restaurant(raw.next()));
        }
        raw.close();
        return list;
    }
    public static Restaurant createRestaurant(String uid, String name, String iconUrl, String lat, String lon){
        Document raw = new Document()
                .append("uid", uid)
                .append("name", name)
                .append("iconUrl", iconUrl)
                .append("lat", lat)
                .append("lon", lon);
        ConnectionManager.getDatabase().getCollection("restaurants").insertOne(raw);
        return new Restaurant(raw);
    }
    public static Restaurant getRestaurantByUid(String uid){
        Document raw = ConnectionManager.getDatabase().getCollection("restaurants")
                .find(Filters.eq("uid", uid)).first();
        return new Restaurant(raw);
    }
}
