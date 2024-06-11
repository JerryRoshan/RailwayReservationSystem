package com.jerry.demoRailwayV1;

import redis.clients.jedis.Jedis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class TestRedis {
	private Jedis jedis;
	Connection con;
    private String Url,dbname,dbpass;
	public TestRedis() {
		this.jedis = new Jedis(
				"redis://default:Of8MNmxJOKITx3xDdNhzd343O4IFGfJe@redis-11256.c73.us-east-1-2.ec2.redns.redis-cloud.com:11256");
		Url = "jdbc:mysql://localhost:3306/railways";
        dbname = "jerry"; 
        dbpass = "root"; 
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(Url, dbname, dbpass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // Handle exception - possibly rethrow as a RuntimeException
            throw new RuntimeException("JDBC Driver not found.", e);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception - possibly rethrow as a RuntimeException
            throw new RuntimeException("Failed to connect to the database.", e);
        }
	}

	public boolean registerUser(String username, String password) {
		if (jedis.exists(username)) {
			return false; // User already exists
		}
		String hashedPassword = hashPassword(password);
		jedis.set(username, hashedPassword);
		jedis.expire(username, 60); // delete the keys after 60 seconds
		return true;
	}

	public boolean authenticateUser(String username, String password) {
		if (!jedis.exists(username)) {
			// User does not exist in the cache so we must check in the mysql db
			String storedPasswordFromDb = queryDatabaseForUserPassword(username);
			if (storedPasswordFromDb != null) {
				//String hashedPassword = hashPassword(storedPasswordFromDb);
				jedis.set(username, storedPasswordFromDb); // Update Redis cache
				jedis.expire(username, 160); // delete the keys after 60 seconds
			} else {
				return false; // User does not exist in the database either
			}
		}
		String storedPassword = jedis.get(username);
		//String hashedPassword = hashPassword(password);
		return storedPassword.equals(password);
	}

	// Pseudo-method to query the database for a user's password
	private String queryDatabaseForUserPassword(String username) {
		String Password= null;
		// Prepare SQL statement
		String sql = "SELECT password FROM passenger_details WHERE username = ?";
		try (PreparedStatement statement = con.prepareStatement(sql)) {
			statement.setString(1, username);
            // Execute query
            ResultSet resultSet = statement.executeQuery();
            // Check if user exists
            if (resultSet.next()) {
                // Retrieve hashed password from the result set
                Password = resultSet.getString("password");
            }
		}catch (SQLException e) {
            e.printStackTrace();
        }
		return Password; 
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
