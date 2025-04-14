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

<p><strong>Felhasználónév:</strong> ${param.username}</p>
<p><strong>Életkor:</strong> ${param.age}</p>

<br>
<a href="form.jsp">Vissza az űrlaphoz</a>
</body>
</html>
