import React, { useState, useEffect, useRef } from 'react';

function PageCounter() {
    const [count, setCount] = useState(0);
    const effectRan = useRef(false);

    useEffect(() => {
        if (effectRan.current === true) {
            return;
        }
        const savedCount = localStorage.getItem('pageLoadCount');
        let currentCount = savedCount ? parseInt(savedCount, 10) : 0;

        currentCount += 1;

        localStorage.setItem('pageLoadCount', currentCount);
        setCount(currentCount);

        effectRan.current = true;
    }, []);

    return (
        <div>
            <h2>Page refresh count</h2>
            <div className="counter-display">{count}</div>
            <p>(Operated by React hook)</p>
        </div>
    );
}

export default PageCounter;