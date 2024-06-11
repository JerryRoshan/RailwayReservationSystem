package com.jerry.demoRailwayV1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DB_URL = "jdbc:mysql://localhost:3306/railways";
	private static final String DB_USER = "jerry";
	private static final String DB_PASSWORD = "root";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		int age = Integer.parseInt(request.getParameter("age"));
		String gender = request.getParameter("gender");
		String berthPreference = request.getParameter("berth_preference");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirm_password");

		if (!password.equals(confirmPassword)) {
			response.getWriter().write("Passwords do not match!");
			return;
		}
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "INSERT INTO passenger_details (name, age, gender, berth_preference, username, password) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setString(1, name);
				statement.setInt(2, age);
				statement.setString(3, gender);
				statement.setString(4, berthPreference);
				statement.setString(5, username);
				statement.setString(6, password);
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.getWriter().write("Registration failed");
			return;
		}

		//response.getWriter().write("Registration successful");

        TestRedis redisClient = new TestRedis();
        boolean isRegistered = redisClient.registerUser(username, password);
        if (isRegistered) {
            response.getWriter().write("Registration successful!");
        } else {
            response.getWriter().write("User already exists!");
        }
	}
}
