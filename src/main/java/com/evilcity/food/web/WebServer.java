package com.evilcity.food.web;

import com.evilcity.food.Main;
import com.evilcity.food.db.ConnectionManager;
import com.evilcity.food.db.entity.*;
import com.evilcity.food.utils.Converter;
import com.mongodb.client.model.Filters;
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
             Converter.sendJson(ctx, quests.stream().filter(quest -> !completed.contains(quest.uid())).toList());
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
            Converter.sendJson(ctx, Progress.getProgressByUserId(user.uid())
                    .stream()
                    .peek(progress ->
                            progress.getRaw().append("quest", progress.getQuest().getRaw()))
                    .toList()
            );
        });

        app.get("/orders", (ctx) -> {
            User user = User.getUserByUID(ctx.sessionAttribute("userId"));
            if(user == null){
                ctx.status(401);
                return;
            }
            Converter.sendJson(ctx, Order.getOrdersByUserId(user.uid()));
        });

        app.get("/bonuses/receive", (ctx) -> {
            User user = User.getUserByUID(ctx.sessionAttribute("userId"));
            if(user == null){
                ctx.status(401);
                return;
            }
            List<Bonus> bonuses = Bonus.getBonusesByUserId(user.uid());
            Converter.sendJson(ctx, bonuses);
        });

        app.put("/user/change", (ctx) -> {
            User user = User.getUserByUID(ctx.sessionAttribute("userId"));
            if(user == null){
                ctx.status(401);
                return;
            }
            Properties props = new Properties();
            props.load(ctx.bodyInputStream());
            String username = props.getProperty("username");
            String email = props.getProperty("email");
            String password = props.getProperty("password");

            if(password == null || username == null || email == null){
                ctx.status(400);
                return;
            }

            if(!user.comparePassword(password)){
                ctx.status(403);
                return;
            }

            user.bulkUpdate().setOne("username", username).setOne("email", email).setOne("password", password).run();
            ctx.result();
        });

        app.delete("/user/delete", (ctx) -> {
            User user = User.getUserByUID(ctx.sessionAttribute("userId"));
            if(user == null){
                ctx.status(401);
                return;
            }
            ConnectionManager.getDatabase().getCollection("users").deleteOne(user.getRaw());
            ConnectionManager.getDatabase().getCollection("progress").deleteMany(Filters.eq(
                    "userId", user.uid()
            ));
            ConnectionManager.getDatabase().getCollection("bonuses").deleteMany(Filters.eq(
                    "userId", user.uid()
            ));
            ConnectionManager.getDatabase().getCollection("orders").deleteMany(Filters.eq(
                    "userId", user.uid()
            ));
            ctx.result();
        });

        app.get("/user/get", (ctx) -> {
            User user = User.getUserByUID(ctx.sessionAttribute("userId"));
            if(user == null){
                ctx.status(401);
                return;
            }
            Document raw = new Document()
                    .append("username", user.getUsername())
                    .append("email", user.getEmail());

            ctx.result(raw.toJson());
        });

        app.post("/quest/take", (ctx) -> {
            User user = User.getUserByUID(ctx.sessionAttribute("userId"));
            if(user == null){
                ctx.status(401);
                return;
            }

            Quest quest = Quest.findQuestById(ctx.queryParam("id"));

            if(quest == null){
                ctx.status(400);
                return;
            }

            Progress.createProgress(
                    ConnectionManager.generateRandomUID(),
                    quest.uid(),
                    user.uid(),
                    100
            );
            ctx.result();
        });
    }
}
