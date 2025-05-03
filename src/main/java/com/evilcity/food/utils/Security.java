package com.evilcity.food.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Security {
    private static MessageDigest messageDigest;

    public static void init() throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance("sha-256");
    }

    public static String generateHash(String password){
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = messageDigest.digest(bytes);
        return Base64.getEncoder().encodeToString(bytes1);
    }
}
