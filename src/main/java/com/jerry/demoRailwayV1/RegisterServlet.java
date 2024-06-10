package com.jerry.demoRailwayV1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        if (!password.equals(confirmPassword)) {
            response.getWriter().write("Passwords do not match!");
            return;
        }

        TestRedis redisClient = new TestRedis();
        boolean isRegistered = redisClient.registerUser(username, password);
        if (isRegistered) {
            response.getWriter().write("Registration successful!");
        } else {
            response.getWriter().write("User already exists!");
        }
    }
}
