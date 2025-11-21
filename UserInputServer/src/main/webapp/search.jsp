<%@ include file="include.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>search Form</title>
</head>
<body>
<h2>Fill this form please</h2>

<form id="searchForm" action="search" method="GET">
    <label for="nameInput">Name (Person Name):</label>
    <input type="text" id="nameInput" name="nameInput" required>
    <button type="submit">Search</button>
</form>
</body>
</html>