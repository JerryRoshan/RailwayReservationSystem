package com.jerry.demoRailwayV1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
        	HttpSession session = request.getSession();
        	session.setAttribute("username", username);
        	response.sendRedirect("home.jsp");
        } else {
            response.getWriter().write("Invalid credentials!");
        }
    }
}
