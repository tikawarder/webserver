<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Simple JSP Example</title>
</head>
<body>
<h2>JSP űrlap kitöltése</h2>

<form action="other.jsp" method="POST">
    <label for="name">Név:</label><br>
    <input type="text" id="name" name="name" required><br><br>

    <label for="birthdate">Születési dátum:</label><br>
    <input type="date" id="birthdate" name="birthdate" required><br><br>

    <label for="city">Városod:</label><br>
    <input type="text" id="city" name="city" required><br><br>

    <input type="submit" value="Küldés">
</form>
</body>
</html>
