import * as CryptoJS from 'crypto-js';

export function showWelcomeMessage() {
    alert("Welcome to the JSP-based web app!");
}

export function updateTime() {
    const el = document.getElementById("currentTime");
    el.innerText = new Date().toLocaleTimeString();
}

export function encryptData(message, key) {
    const encrypted = CryptoJS.AES.encrypt(message, key).toString();
    alert("Original message: " + message);
    alert("Encrypted message: " + encrypted);
    return encrypted;
}
