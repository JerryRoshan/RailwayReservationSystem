package com.jerry.demoRailwayV1;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/selectClass")
public class SelectClassServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/railways";
    private static final String DB_USER = "jerry";
    private static final String DB_PASSWORD = "root";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int selectedTrainId = Integer.parseInt(request.getParameter("selectedTrain"));
        String username = request.getParameter("username"); // Get the username from the request
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT * FROM train_classes WHERE train_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedTrainId);
            rs = stmt.executeQuery();

            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Select Class</title></head><body>");
            out.println("<h2>Select Class and Enter Passenger Details</h2>");
            out.println("<form action='bookTicket' method='post'>");
            out.println("<input type='hidden' name='username' value='" + username + "'>"); // Include the username as a hidden field
            out.println("<input type='hidden' name='train_id' value='" + selectedTrainId + "'>");
            out.println("<table border='1'><tr><th>Select</th><th>Class Type</th><th>Price</th><th>Available Seats</th></tr>");
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td><input type='radio' name='class_id' value='" + rs.getInt("class_id") + "'></td>");
                out.println("<td>" + rs.getString("class_type") + "</td>");
                out.println("<td>" + rs.getDouble("price") + "</td>");
                out.println("<td>" + rs.getInt("available_seats") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");

            out.println("<h3>Passenger Details</h3>");
            for (int i = 1; i <= 6; i++) {
                out.println("<h4>Passenger " + i + "</h4>");
                out.println("Name: <input type='text' name='passenger_name_" + i + "'><br>");
                out.println("Age: <input type='number' name='passenger_age_" + i + "'><br>");
                out.println("Gender: <input type='text' name='passenger_gender_" + i + "'><br>");
                out.println("Class: <input type='text' name='passenger_class_" + i + "'><br>");
                out.println("Email: <input type='email' name='passenger_email_" + i + "'><br>");
            }

            out.println("<button type='submit' class='button'>Book Ticket</button>");
            out.println("</form>");
            out.println("</body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
