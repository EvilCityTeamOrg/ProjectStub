package com.evilheroes.iwannaeat.web;

import io.javalin.Javalin;

public class WebServer {

    private static Javalin app;

    public static void init(){
        app = Javalin.create();

        app.get("/", context -> {
           context.result("Hi");
        });

        app.start();
    }
}
