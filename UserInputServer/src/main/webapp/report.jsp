<%@ include file="include.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Report of the stored data</title>
</head>
<body>
<h2>Received data:</h2>

<p><strong>Name:</strong> ${param.name}</p>
<p><strong>Birth:</strong> ${param.birthdate}</p>
<%
    String birthDateStr = request.getParameter("birthdate");
    int age = -1;
    if (birthDateStr != null) {
        java.time.LocalDate birthDate = java.time.LocalDate.parse(birthDateStr);
        java.time.LocalDate now = java.time.LocalDate.now();
        age = java.time.Period.between(birthDate, now).getYears();
    }
%>
<p><strong>Age:</strong> <%= age %></p>
<p><strong>City:</strong> ${param.city}</p>
<br>
<a href="form.jsp">They are stored in the database and this link returns you to a new input form</a>
<br>
<a href="index.jsp">If not you can call the welcome link here</a>
</body>
</html>
