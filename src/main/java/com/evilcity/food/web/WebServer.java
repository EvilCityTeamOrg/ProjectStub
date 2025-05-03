package com.evilcity.food.web;

import com.evilcity.food.Main;
import com.evilcity.food.db.ConnectionManager;
import com.evilcity.food.db.entity.Progress;
import com.evilcity.food.db.entity.Quest;
import com.evilcity.food.db.entity.User;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.bson.BsonArray;
import org.bson.Document;

import java.util.*;

public class WebServer {
    private static Javalin app;

    /**
     * Initializes and starts HTTP webserver with RestAPI
     * @throws IllegalStateException if WebServer was initialized already
     */
    public static void init() {
        if (app != null) throw new IllegalStateException("WebServer is running already!");
        app = Javalin.create();
        app.unsafeConfig().staticFiles.add(Main.getStringCommandLineArgument("static"), Location.EXTERNAL);
        app.start(8080);

        app.post("/register", (ctx) -> {
            Properties props = new Properties();
            props.load(ctx.bodyInputStream());
            String username = props.getProperty("username");
            String email = props.getProperty("email");
            String password = props.getProperty("password");

            if(username == null || password == null || email == null){
                ctx.status(400);
                return;
            }

            User user = User.getUserByUsername(username);
            if (user != null){
                ctx.status(400);
                return;
            }
            User.registerUser(ConnectionManager.generateRandomUID(), username, email, password);
            ctx.result();
        });

        app.get("/login", (ctx) -> {
           String username = ctx.queryParam("username");
           String password = ctx.queryParam("password");
           if(username == null || password == null){
               ctx.status(400);
               return;
           }
           User user = User.getUserByUsername(username);
           if(user == null){
               ctx.status(403);
               return;
           }
           boolean compared = user.comparePassword(password);
           if(!compared){
               ctx.status(403);
               return;
           }
           ctx.sessionAttribute("userId", user.uid());
           ctx.result();
        });

        app.get("/quest", (ctx) -> {
            List<Quest> quests = Quest.getQuests();
            if(ctx.sessionAttribute("userId") == null){
                ctx.status(401);
                return;
            }
            List<Progress> progress = Progress.getProgressByUserId(ctx.sessionAttribute("userId"));
            Set<String> completed = new HashSet<>();
            for (Progress p : progress) {
                if(p.getProgress() == 0){
                    completed.add(p.uid());
                }
            }
            quests = quests.stream().filter(quest -> !completed.contains(quest.uid())).toList();
            Document raw = new Document().append("values", quests);
            ctx.contentType("application/json");
            ctx.result(raw.toJson());
        });

        app.get("/logout", (ctx) -> {
            ctx.sessionAttribute("userId", null);
            ctx.result();
        });

        app.get("/myquest", (ctx) -> {
           User user = User.getUserByUID(ctx.sessionAttribute("userId"));
           if(user == null){
               ctx.status(401);
               return;
           }
           List<Quest> quests = Quest.getQuestsByUserId(user.uid());
           Document raw = new Document().append("values", quests);
           ctx.contentType("application/json");
           ctx.result(raw.toJson());
        });

        app.get()
    }
}
