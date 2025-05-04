package com.evilcity.food.db.entity;

import com.evilcity.food.db.ConnectionManager;
import com.evilcity.food.db.DBAbstractEntity;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Quest extends DBAbstractEntity {
    public Quest(Document raw) {
        super("quests", raw);
    }

    public void setName(String name){ set("name", name); }
    public void setText (String text) { set("text", text); }
    public void setIdRestaurant(String id) { set("restaurantId", id); }
    public String getName() { return  getString("name"); }
    public String getText() { return  getString("text"); }
    public String getIdRestaurant() { return getString("restaurantId"); }

    public Restaurant getQuest() {
        return Restaurant.getRestaurantByUid(getIdRestaurant());
    }

    public static Quest createNewQuest(String uid,String name, String text, String restaurantId) {
        Document raw = new Document()
                .append("uid", uid)
                .append("name", name)
                .append("text", text)
                .append("restaurantId", restaurantId);
        ConnectionManager.getDatabase().getCollection("quests").insertOne(raw);
        return new Quest(raw);
    }

    public static Quest findQuestById(String uid){
        Document raw =  ConnectionManager.getDatabase().getCollection("quests")
                .find(Filters.eq("uid", uid))
                .first();
        return raw == null ? null : new Quest(raw);
    }

    public static List<Quest> getQuests(){
        MongoCursor<Document> raw = ConnectionManager.getDatabase().getCollection("quests")
                .find().iterator();

        ArrayList<Quest> quests = new ArrayList<>();

        while (raw.hasNext()){
            quests.add(new Quest(raw.next()));
        }
        raw.close();
        return quests;
    }

    public static List<Quest> getQuestsByUserId(String userId){
        MongoCursor<Document> raw = ConnectionManager.getDatabase().getCollection("quests")
                .find(Filters.eq("userId", userId)).iterator();

        ArrayList<Quest> quests = new ArrayList<>();

        while (raw.hasNext()){
            quests.add(new Quest(raw.next()));
        }
        raw.close();
        return quests;
    }

    public static List<Quest> getQuestsByRestaurantId(String restaurantId){
        MongoCursor<Document> raw = ConnectionManager.getDatabase().getCollection("quests")
                .find(Filters.eq("restaurantId", restaurantId)).iterator();

        ArrayList<Quest> quests = new ArrayList<>();

        while (raw.hasNext()){
            quests.add(new Quest(raw.next()));
        }
        raw.close();
        return quests;
    }

}
