// src/components/PageCounter.js
import React, { useState, useEffect, useRef } from 'react'; // 1. useRef importálása

function PageCounter() {
    const [count, setCount] = useState(0);

    // 2. Egy referencia (ref) létrehozása, ami követi, hogy lefutott-e már a hatás.
    // A ref értéke megmarad a renderelések között, de nem vált ki új renderelést.
    const effectRan = useRef(false);

    useEffect(() => {
        // 3. Ha már lefutott egyszer, akkor álljunk meg, ne csináljunk semmit.
        if (effectRan.current === true) {
            return;
        }

        // --- Eredeti Logika ---
        const savedCount = localStorage.getItem('pageLoadCount');
        let currentCount = savedCount ? parseInt(savedCount, 10) : 0;

        currentCount += 1;

        localStorage.setItem('pageLoadCount', currentCount);
        setCount(currentCount);

        // 4. Beállítjuk a flag-et true-ra, hogy többször ne fusson le.
        effectRan.current = true;

    }, []);

    return (
        <div>
            <h2>Oldalmegnyitások</h2>
            <div className="counter-display">{count}</div>
            <p>(React Hook kezeli)</p>
        </div>
    );
}

export default PageCounter;