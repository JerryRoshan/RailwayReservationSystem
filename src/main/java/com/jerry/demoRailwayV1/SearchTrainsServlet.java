package com.jerry.demoRailwayV1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

            List<Train> trains = new ArrayList<>();
            while (rs.next()) {
                Train train = new Train();
                train.setTrainId(rs.getInt("train_id"));
                train.setTrainName(rs.getString("train_name"));
                train.setSource(rs.getString("source"));
                train.setDestination(rs.getString("destination"));
                trains.add(train);
            }

            request.setAttribute("trains", trains);
            request.getRequestDispatcher("searchResults.jsp").forward(request, response);

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

class Train {
    private int trainId;
    private String trainName;
    private String source;
    private String destination;

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
