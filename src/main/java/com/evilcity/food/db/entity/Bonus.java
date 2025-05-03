package com.evilcity.food.db.entity;

import com.evilcity.food.db.ConnectionManager;
import com.evilcity.food.db.DBAbstractEntity;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Bonus extends DBAbstractEntity {
    public Bonus(Document raw) {
        super("bonuses", raw);
    }

    public void setName(String name) { set("name", name); }
    public void setUserId(String id) { set("userId", id); }
    public void setText(String text) { set("text", text); }

    public String getName() { return getString("name"); }
    public String getUserId() { return getString("userId"); }
    public String getText() { return  getString("text"); }

    public static Bonus createBonus(String uid, String name, String userId, String text) {
        Document raw = new Document()
                .append("uid", uid)
                .append("name", name)
                .append("userId", userId)
                .append("text", text);
        ConnectionManager.getDatabase().getCollection("bonuses").insertOne(raw);
        return new Bonus(raw);
    }

    public static List<Bonus> getBonusesByUserId(String userId){
        MongoCursor<Document> raw = ConnectionManager.getDatabase().getCollection("bonuses")
                .find(Filters.eq("userId", userId)).iterator();
        ArrayList<Bonus> bonuses = new ArrayList<>();
        while (raw.hasNext()){
            bonuses.add(new Bonus(raw.next()));
        }
        raw.close();
        return bonuses;
    }
    public static Bonus getBonusByUid(String uid){
        Document raw = ConnectionManager.getDatabase().getCollection("bonuses")
                .find(Filters.eq("uid", uid)).first();
        return new Bonus(raw);
    }
}
