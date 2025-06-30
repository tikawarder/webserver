<%@ include file="include.jsp" %>
<html>
<head>
    <title>Users List</title>
</head>
<body>
<h1>Users from the database</h1>

<ul>
    <c:choose>
        <c:when test="${not empty persons}">
            <c:forEach var="person" items="${persons}">
                <li>
                    <strong>ID:</strong> ${person.id}<br>
                    <strong>Name:</strong> ${person.name}<br>
                    <strong>Birth date:</strong> ${person.birthDay}<br>
                    <strong>City:</strong> ${person.city}
                </li>
                <hr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <li>No users found.</li>
        </c:otherwise>
    </c:choose>
</ul>

<p><a href="<%= request.getContextPath() %>/form.jsp">Towards to input form</a></p>
</body>
</html>
