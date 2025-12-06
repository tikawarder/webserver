// my-frontend/src/App.js

import React, { useState } from 'react'; // 1. Beimportáljuk a useState hookot

// A Komponens definiálása (függvény-komponens)
function Counter() {
  // 2. State (állapot) definiálása: 
  // 'count' az aktuális érték, 'setCount' a függvény az érték módosításához, 
  // a kezdőérték 0.
  const [count, setCount] = useState(0);

  // Eseménykezelő függvény a növeléshez
  const increment = () => {
    // 3. Az állapot frissítése. 
    // A React tudja, hogy a "count" megváltozott, és automatikusan frissíti a felületet.
    setCount(count + 1);
  };

  return (
    <div style={{ padding: '20px', border: '1px solid #ccc', borderRadius: '5px' }}>
      <h1>React Számláló</h1>
      
      {/* 4. Az aktuális állapot megjelenítése */}
      <p>Az aktuális érték: <strong>{count}</strong></p>
      
      {/* 5. A gomb eseménykezelője: a "increment" függvényt hívja meg */}
      <button onClick={increment} 
              style={{ padding: '10px 20px', fontSize: '16px', cursor: 'pointer' }}>
        Kattints a növeléshez
      </button>
    </div>
  );
}

// Exportáljuk a komponenst, hogy a projekt többi része használhassa
export default Counter;