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
<p id="birthDate"><strong>Birth:</strong> ${param.birthdate}</p>
<p><strong>Age:</strong> <span id="ageResult">(calculating...)</span></p>
<p><strong>City:</strong> ${param.city}</p>
<br>
<a href="form.jsp">They are stored in the database and this link returns you to a new input form</a>
<br>
<a href="index.jsp">If not you can call the welcome link here</a>
    <script src="/jsDevelopment/age-calculator.js"></script>
</body>
</html>
