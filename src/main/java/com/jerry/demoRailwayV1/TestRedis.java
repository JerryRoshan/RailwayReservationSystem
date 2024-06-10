package com.jerry.demoRailwayV1;

import redis.clients.jedis.Jedis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class TestRedis {
    private Jedis jedis;

    public TestRedis() {
        this.jedis = new Jedis("redis://default:Of8MNmxJOKITx3xDdNhzd343O4IFGfJe@redis-11256.c73.us-east-1-2.ec2.redns.redis-cloud.com:11256");
    }

    public boolean registerUser(String username, String password) {
        if (jedis.exists(username)) {
            return false; // User already exists
        }
        String hashedPassword = hashPassword(password);
        jedis.set(username, hashedPassword);
        return true;
    }

    public boolean authenticateUser(String username, String password) {
        if (!jedis.exists(username)) {
            return false; // User does not exist
        }
        String storedPassword = jedis.get(username);
        String hashedPassword = hashPassword(password);
        return storedPassword.equals(hashedPassword);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        TestRedis testRedis = new TestRedis();
        if (testRedis.jedis.ping().equals("PONG")) {
            System.out.println("Connected to Redis!!");
        }

        // Example usage:
        // Registration
        boolean isRegistered = testRedis.registerUser("newuser", "password123");
        System.out.println("User registration successful: " + isRegistered);

        // Authentication
        boolean isAuthenticated = testRedis.authenticateUser("newuser", "password123");
        System.out.println("User authentication successful: " + isAuthenticated);
    }
}

