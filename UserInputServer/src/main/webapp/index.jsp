<%@ include file="include.jsp" %>
<html>
<head><title>Welcome</title>
    <script src="js/main.js"></script>
</head>
    <body onload="showWelcomeMessage()">
        <h1>Hello world, this is the start page</h1>
        <p>Current time: <span id="currentTime">--:--:--</span></p>
        <button onclick="updateTime()">Refresh the time</button>
        <br>
        <a href="hello">Are you ready to store some data?</a>
    </body>
</html>