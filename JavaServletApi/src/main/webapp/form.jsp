<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Simple JSP Example</title>
</head>
<body>
<h2>Fill this form please</h2>

<form action="store" method="POST">
    <label for="name">Name:</label><br>
    <input type="text" id="name" name="name" required><br><br>

    <label for="birthdate">Birth date:</label><br>
    <input type="date" id="birthdate" name="birthdate" required><br><br>

    <label for="city">Your city:</label><br>
    <input type="text" id="city" name="city" required><br><br>

    <input type="submit" value="Send">
</form>
</body>
</html>
