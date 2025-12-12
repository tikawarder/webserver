// counter.js

// Számláló (Jobb alsó) logika: imperatív DOM manipuláció

// 1. Megkeressük a számláló értékét a böngésző local storage-ban.
let pageCount = localStorage.getItem('pageLoadCount');

// 2. Kezdőérték beállítása 0-ra, ha még nem volt mentve.
if (pageCount === null) {
    pageCount = 0;
} else {
    // Stringből számmá alakítás.
    pageCount = parseInt(pageCount);
}

// 3. Növeljük az értéket 1-gyel.
pageCount++;

// 4. Mentjük az új értéket.
localStorage.setItem('pageLoadCount', pageCount);

// 5. Frissítjük a HTML elemet (DOM manipuláció).
document.getElementById('counter-display').textContent = pageCount;