package com.evilcity.food.db;

import com.evilcity.food.Main;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ConnectionManager {
    private static final Logger log = LoggerFactory.getLogger("Database");

    private static MongoClient client;
    private static MongoDatabase database;

    /**
     * Initializes connection with MongoDB by IP(or domain) in --DatabaseIP.<br>
     * Use --DatabaseSkipAuth to connect without authorization<br>
     * Or provide --DatabaseLogin and --DatabasePassword<br><br>
     * Entire app uses one database provided by --DatabaseName
     */
    public static void connect() {
        client = MongoClients.create(
                Main.getBooleanCommandLineArgument("DatabaseSkipAuth") ?
                "mongodb://" + Main.getStringCommandLineArgument("DatabaseIP") :
                "mongodb://" + Main.getStringCommandLineArgument("DatabaseLogin")
                        + Main.getStringCommandLineArgument("DatabasePassword")
                        + "@" + Main.getStringCommandLineArgument("DatabaseIP")
        );
        log.info("Connected to database successfully!");
        database = client.getDatabase(Main.getStringCommandLineArgument("DatabaseName"));
    }
    public static String generateRandomUID() {
        return UUID.randomUUID().toString();
    }

    public static void update(String collection, String uid, String key, Object value) {
        database.getCollection(collection).findOneAndUpdate(Filters.eq("uid", uid), Updates.set(key, value));
    }
    public static MongoDatabase getDatabase() {
        return database;
    }
}
