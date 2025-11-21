<%@ include file="include.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
    <h2>Login page</h2>

    <%-- Error handling: From LoginServlet --%>
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
            <p style="color: red;"><%= errorMessage %></p>
    <%
        }
    %>
    <p>Please login for using the search form</p>

    <form method="POST" action="login">
        <label for="username">User:</label>
        <input type="text" id="username" name="username" required><br><br>

        <label for="password">Password:</label>
        <input type="password" id="pwd" name="pwd" required><br><br>

        <input type="submit" value="Login">
    </form>
</body>
</html>