package com.jerry.demoRailwayV1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        TestRedis redisClient = new TestRedis();
        boolean isAuthenticated = redisClient.authenticateUser(username, password);
        if (isAuthenticated) {
            //response.getWriter().write("Login successful!");
        	response.sendRedirect("home.html");
        } else {
            response.getWriter().write("Invalid credentials!");
        }
    }
}
