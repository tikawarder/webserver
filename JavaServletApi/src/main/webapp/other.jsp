<%@ page import="org.example.DataDao" %>
<%@ page import="java.sql.Date" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Adatok megjelenítése</title>
</head>
<body>
<h2>A beküldött adatok:</h2>

<p><strong>Felhasználónév:</strong> ${param.name}</p>
<p><strong>Született:</strong> ${param.birthdate}</p>
<%
    String birthDateStr = request.getParameter("birthdate"); // pl. "1990-06-15"
    int age = -1;
    if (birthDateStr != null) {
        java.time.LocalDate birthDate = java.time.LocalDate.parse(birthDateStr);
        java.time.LocalDate now = java.time.LocalDate.now();
        age = java.time.Period.between(birthDate, now).getYears();
    }
%>
<p><strong>Életkor:</strong> <%= age %></p>
<p><strong>Város:</strong> ${param.city}</p>

<br>
<%
    DataDao dao = new DataDao();
	dao.insertUser(request.getParameter("name"), request.getParameter("birthdate"), request.getParameter("city"));
%>
<a href="form.jsp">Eltároltuk az adatbázisba, majd vissza az űrlaphoz</a>
</body>
</html>
