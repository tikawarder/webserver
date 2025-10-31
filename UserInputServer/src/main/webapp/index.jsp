<%@ include file="include.jsp" %>
<html>
<head><title>Welcome</title>
    <script src="js/main.js"></script>
    <script src="js/dist/crypto-js.js"></script>
</head>
    <body onload="showWelcomeMessage()">
        <h1>Hello world, this is the start page</h1>
        <p>Current time: <span id="currentTime">--:--:--</span></p>
        <button onclick="updateTime()">Refresh the time</button>
        <p>Demonstrate the import npm dependency with this function</p>
         <button onclick="encryptData('Szeretném ezt titkosítani', 'mySecretKey')">encrypt this</button>
        <br>
        <a href="hello">Are you ready to store some data?</a>
    </body>
</html>