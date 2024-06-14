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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

@WebServlet("/bookTicket")
public class BookTicketServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/railways";
    private static final String DB_USER = "jerry";
    private static final String DB_PASSWORD = "root";
    
    private static final String HBASE_ZOOKEEPER_QUORUM = "hbase-docker";
    private static final String HBASE_ZOOKEEPER_CLIENTPORT = "2181";
    private static final String HBASE_TABLE_NAME = "booking_details";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String username = request.getParameter("username"); // Get the username from the request
        int trainId = Integer.parseInt(request.getParameter("train_id"));
        int classId = Integer.parseInt(request.getParameter("class_id"));

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        org.apache.hadoop.hbase.client.Connection hbaseConn = null;
        Table table = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // Start transaction

            // Fetch the selected class details
            String classSql = "SELECT * FROM train_classes WHERE class_id = ?";
            stmt = conn.prepareStatement(classSql);
            stmt.setInt(1, classId);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Class not found");
            }
            double price = rs.getDouble("price");
            int availableSeats = rs.getInt("available_seats");

            // Initialize HBase connection
            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", HBASE_ZOOKEEPER_QUORUM);
            conf.set("hbase.zookeeper.property.clientPort", HBASE_ZOOKEEPER_CLIENTPORT);
            hbaseConn = ConnectionFactory.createConnection(conf);
            table = hbaseConn.getTable(TableName.valueOf(HBASE_TABLE_NAME));

            int passengerCount = 0;
            for (int i = 1; i <= 6; i++) {
                String passengerName = request.getParameter("passenger_name_" + i);
                if (passengerName == null || passengerName.isEmpty()) {
                    continue;
                }
                passengerCount++;
                int age = Integer.parseInt(request.getParameter("passenger_age_" + i));
                String gender = request.getParameter("passenger_gender_" + i);
                String classOfTrain = request.getParameter("passenger_class_" + i);
                String email = request.getParameter("passenger_email_" + i);

                // Assign seat number
                int seatNumber = availableSeats - passengerCount + 1;

                // Insert booking details into HBase
                String rowKey = passengerName+"_"+Integer.toString(seatNumber);
                Put put = new Put(Bytes.toBytes(rowKey));
                put.addColumn("cf1".getBytes(), "train_id".getBytes(), Integer.toString(trainId).getBytes());
                put.addColumn("cf1".getBytes(), "class_id".getBytes(), Integer.toString(classId).getBytes());
                put.addColumn("cf1".getBytes(), "passenger_name".getBytes(), passengerName.getBytes());
                put.addColumn("cf1".getBytes(), "age".getBytes(), Integer.toString(age).getBytes());
                put.addColumn("cf1".getBytes(), "gender".getBytes(), gender.getBytes());
                put.addColumn("cf1".getBytes(), "class_of_train".getBytes(), classOfTrain.getBytes());
                put.addColumn("cf1".getBytes(), "email".getBytes(), email.getBytes());
                put.addColumn("cf1".getBytes(), "price".getBytes(), Double.toString(price).getBytes());
                put.addColumn("cf1".getBytes(), "seat_number".getBytes(), Integer.toString(seatNumber).getBytes());
                put.addColumn("cf1".getBytes(), "username".getBytes(), username.getBytes());
                table.put(put);
            }

            if (passengerCount > availableSeats) {
                throw new SQLException("Not enough seats available");
            }

            // Update available seats in MySQL
            String updateSeatsSql = "UPDATE train_classes SET available_seats = available_seats - ? WHERE class_id = ?";
            stmt = conn.prepareStatement(updateSeatsSql);
            stmt.setInt(1, passengerCount);
            stmt.setInt(2, classId);
            stmt.executeUpdate();

            conn.commit(); // Commit transaction

            PrintWriter out = response.getWriter();
            out.println("<html><head><title>Booking Confirmation</title></head><body>");
            out.println("<h2>Booking Successful!</h2>");
            out.println("</body></html>");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                if (table != null) table.close();
                if (hbaseConn != null) hbaseConn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
