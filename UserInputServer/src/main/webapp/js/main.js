function showWelcomeMessage() {
    alert("Welcome to the JSP-based web app!");
}

function updateTime() {
    const el = document.getElementById("currentTime");
    el.innerText = new Date().toLocaleTimeString();
}
