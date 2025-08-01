const fetchData = async () => {
    console.log("várok?");
    const res = await fetch('https://api.restful-api.dev/objects');
    console.log("haladok");
    const json = await res.json();
    console.log(json);
    console.log("haladok tovább");
    return json;
};
//fetchData();

fetchData()
    .then(data => {
        console.log(data);
        console.log("Siker:", data);
    })
    .catch(error => {
        console.error("Hibaliba:", error);
    });

// Promise példa: sikeres és sikertelen eset
// Változó, ami meghatározza, hogy sikerüljön vagy ne
const type = "hiba"; // vagy "siker"

const p = new Promise((resolve, reject) => {
    setTimeout(() => {
        if (type === "siker") {
            resolve("Siker!");
        } else {
            reject("Hiba történt");
        }
    }, 1000);
});

p.then(result => {
    console.log("✅", result); // ha sikerült
}).catch(error => {
    console.error("❌", error); // ha hiba történt
});
