<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<html>
<head>
<title>Search Results</title>
</head>
<body>
	<h2>Search Results</h2>
	<form action="bookTicket" method="post">
		<table border="1">
			<tr>
				<th>Select</th>
				<th>Train ID</th>
				<th>Train Name</th>
				<th>Source</th>
				<th>Destination</th>
			</tr>
			<c:forEach var="train" items="${requestScope.trains}">
				<tr>
					<td><input type="radio" name="selectedTrain"
						value="${train.train_id}"></td>
					<td>${train.train_id}</td>
					<td>${train.train_name}</td>
					<td>${train.source}</td>
					<td>${train.destination}</td>
				</tr>
			</c:forEach>
		</table>
		<button type="submit" class="button">Book Selected Train</button>
	</form>
</body>
</html>
