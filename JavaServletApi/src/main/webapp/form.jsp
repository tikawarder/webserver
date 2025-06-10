<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>input Form</title>
    <script>
        function validateForm() {
            const name = document.getElementById("name").value.trim();
            const city = document.getElementById("city").value.trim();

            const pattern = /^[a-zA-Z0-9 ]*$/;

            if (name.length > 20) {
                alert("'Name' field must be 20 max length");
                return false;
            }
            if (!pattern.test(name)) {
                alert("'Name' field should not contain special chars");
                return false;
            }
            if (city.length > 25) {
                alert("'City' field must be 25 max length");
                return false;
            }
            if (!pattern.test(city)) {
                alert("A 'City' field should not contain special chars");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
<h2>Fill this form please</h2>

<form action="store" method="POST" onsubmit="return validateForm()">
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
