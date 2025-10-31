function showWelcomeMessage() {
    alert("Welcome to the JSP-based web app!");
}

function updateTime() {
    const el = document.getElementById("currentTime");
    el.innerText = new Date().toLocaleTimeString();
}

function encryptData(message, key) {
    // 1. A CryptoJS objektum használata (feltételezi, hogy a crypto-js.js már betöltődött)
    const encrypted = CryptoJS.AES.encrypt(message, key).toString();
    alert("Eredeti üzenet: " + message);
    alert("Titkosított üzenet: " + encrypted);
    return encrypted;
}

// Példa hívás:
// encryptData("Szeretném ezt titkosítani", "mySecretKey");