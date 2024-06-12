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

@WebServlet("/searchTrains")
public class SearchTrainsServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/railways";
    private static final String DB_USER = "jerry";
    private static final String DB_PASSWORD = "root";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String source = request.getParameter("source");
        String destination = request.getParameter("destination");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT * FROM trains WHERE source = ? AND destination = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, source);
            stmt.setString(2, destination);
            rs = stmt.executeQuery();

            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Search Results</title></head><body>");
            out.println("<h2>Search Results</h2>");
            out.println("<form action='bookTicket' method='post'>");
            out.println("<table border='1'><tr><th>Select</th><th>Train ID</th><th>Train Name</th><th>Source</th><th>Destination</th></tr>");
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td><input type='radio' name='selectedTrain' value='" + rs.getInt("train_id") + "'></td>");
                out.println("<td>" + rs.getInt("train_id") + "</td>");
                out.println("<td>" + rs.getString("train_name") + "</td>");
                out.println("<td>" + rs.getString("source") + "</td>");
                out.println("<td>" + rs.getString("destination") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<button type='submit' class='button'>Book Selected Train</button>");
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
