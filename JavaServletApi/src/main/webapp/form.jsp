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
    <label for="username">Felhasználónév:</label><br>
    <input type="text" id="username" name="username"><br><br>

    <label for="age">Életkor:</label><br>
    <input type="text" id="age" name="age"><br><br>

    <input type="submit" value="Küldés">
</form>
</body>
</html>
