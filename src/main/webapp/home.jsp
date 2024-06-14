<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Railway Reservation System - Home</title>
<style>
body {
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 0;
    background-color: #f2f2f2;
}

.header {
    background-color: #4CAF50;
    color: #fff;
    padding: 10px 0;
    text-align: center;
}

.container {
    max-width: 1200px;
    margin: 20px auto;
    padding: 20px;
    background-color: #fff;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.section {
    margin-bottom: 20px;
}

.section h2 {
    border-bottom: 2px solid #4CAF50;
    padding-bottom: 10px;
}

.section-content {
    padding: 10px;
    background-color: #f9f9f9;
    border: 1px solid #ddd;
}

.button {
    display: inline-block;
    padding: 10px 20px;
    margin: 10px 0;
    background-color: #4CAF50;
    color: #fff;
    text-decoration: none;
    border-radius: 4px;
}

.button:hover {
    background-color: #45a049;
}
</style>
</head>
<body>
    <div class="header">
        <h1>Welcome to Railway Reservation System</h1>
    </div>
    <div class="container">
        <div class="section">
            <h2>User Profile</h2>
            <div class="section-content">
                <!-- Display user profile information here -->
                <p>
                    Username: <span id="username"><%= session.getAttribute("username") != null ? session.getAttribute("username") : "Guest" %></span>
                </p>
                <!-- Add more profile details as needed -->
            </div>
        </div>
        <div class="section">
            <h2>Booking History</h2>
            <div class="section-content">
                <!-- Add functionality to show booking history -->
                <a href="#" class="button">View Booking History</a>
            </div>
        </div>
        <div class="section">
            <h2>Book Ticket</h2>
            <div class="section-content">
                <!-- Add functionality to book tickets -->
                <a href="book_train.jsp" class="button">Book a Ticket</a>
            </div>
        </div>
    </div>
</body>
</html>
