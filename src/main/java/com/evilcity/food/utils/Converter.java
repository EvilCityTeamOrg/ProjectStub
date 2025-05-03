package com.evilcity.food.utils;

import io.javalin.http.Context;
import org.bson.Document;

import java.util.List;

public class Converter {
    public static void sendJson(Context ctx, List<?> list){
        ctx.contentType("application/json");
        ctx.result(new Document().append("values", list).toJson());
    }
}
