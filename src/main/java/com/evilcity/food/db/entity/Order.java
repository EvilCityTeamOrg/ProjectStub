package com.evilcity.food.db.entity;

import com.evilcity.food.db.ConnectionManager;
import com.evilcity.food.db.DBAbstractEntity;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Order extends DBAbstractEntity {
    public Order(Document raw) {
        super("orders", raw);
    }

    public void setUserId(String userId) { set("userId", userId);}
    public void setText(String text) { set("text", text);}

    public String getUserId() { return getString("userId"); }
    public String getText() { return getString("text"); }

    public static Order getOrderById(String uid){
        Document raw = ConnectionManager.getDatabase().getCollection("orders")
                .find(Filters.eq("uid", uid)).first();
        return new Order(raw);
    }

    public static List<Order> getOrdersByUserId(String userId){
        MongoCursor<Document> raw = ConnectionManager.getDatabase().getCollection("orders")
                .find(Filters.eq("userId", userId)).iterator();

        ArrayList<Order> orders = new ArrayList<>();

        while (raw.hasNext()){
            orders.add(new Order(raw.next()));
        }
        raw.close();
        return orders;
    }
}
