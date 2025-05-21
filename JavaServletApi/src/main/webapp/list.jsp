<<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="myData.Person" %>

<html>
<head>
    <title>Users List</title>
</head>
<body>
<h1>Users from the database</h1>

<ul>
    <%
        List<Person> persons = (List<Person>) request.getAttribute("persons");
        if (persons != null && !persons.isEmpty()) {
            for (Person person : persons) {
    %>
    <li>
        <strong>ID:</strong> <%= person.getId() %><br>
        <strong>Name:</strong> <%= person.getName() %><br>
        <strong>Birth date:</strong> <%= person.getBirthDay() %><br>
        <strong>City:</strong> <%= person.getCity() %>
    </li>
    <hr>
    <%
        }
    } else {
    %>
    <li>No users found.</li>
    <%
        }
    %>
</ul>

<p><a href="<%= request.getContextPath() %>/form.jsp">Towards to input form</a></p>
</body>
</html>
