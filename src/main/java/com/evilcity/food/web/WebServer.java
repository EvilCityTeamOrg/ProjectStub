package com.evilcity.food.web;

import io.javalin.Javalin;

public class WebServer {
    private static Javalin app;

    /**
     * Initializes and starts HTTP webserver with RestAPI
     * @throws IllegalStateException if WebServer was initialized already
     */
    public static void init() {
        if (app != null) throw new IllegalStateException("WebServer is running already!");
        app = Javalin.create();
        app.start(8080);
    }
}
