package com.evilcity.food.db.entity;

import com.evilcity.food.db.ConnectionManager;
import com.evilcity.food.db.DBAbstractEntity;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Progress extends DBAbstractEntity {
    public Progress(Document raw) {
        super("progress", raw);
    }

    public void setQuestId(String questId) { set("questId", questId); }
    public void setUserId(String userId) { set("userId", userId); }
    public void setProgress(int progress) { set("progress", progress); }

    public String getQuestId() { return getString("questId"); }
    public String getUserId() { return getString("userId"); }
    public int getProgress() { return getInt("progress"); }

    public Quest getQuest() { return Quest.findQuestById(getQuestId()); }
    public User getUser() { return User.getUserByUID(getUserId()); }


    public static Progress createProgress(String uid, String questId, String userId, int progress){
        Document raw = new Document()
                .append("uid", uid)
                .append("questId", questId)
                .append("userId", userId)
                .append("progress", progress);
        ConnectionManager.getDatabase().getCollection("progress").insertOne(raw);
        return new Progress(raw);
    }

    public static List<Progress> getProgressByUserId(String userId){
        MongoCursor<Document> raw = ConnectionManager.getDatabase().getCollection("progress")
                .find(Filters.eq("userId", userId)).iterator();

        ArrayList<Progress> progresses = new ArrayList<>();
        while (raw.hasNext()){
            progresses.add(new Progress(raw.next()));
        }
        raw.close();
        return progresses;
    }
    public static Progress getProgressByUid(String uid){
        Document raw = ConnectionManager.getDatabase().getCollection("progress")
                .find(Filters.eq("uid", uid)).first();
        return new Progress(raw);
    }
}
