<%@ include file="include.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>input Form</title>
</head>
<body>
<h2>Fill this form please</h2>

<form id="myForm" action="store" method="POST">
    <label for="name">Name:</label><br>
    <input type="text" id="name" name="name" required><br><br>

    <label for="birthdate">Birth date:</label><br>
    <input type="date" id="birthdate" name="birthdate" required><br><br>

    <label for="city">Your city:</label><br>
    <input type="text" id="city" name="city" required><br><br>

    <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">

    <button type="submit">Send</button>
</form>
    <script src="/jsDevelopment/formValidation.js"></script>
</body>
</html>
