document.getElementById('myForm').addEventListener('submit', function (event) {
    const name = document.getElementById("name").value.trim();
    const city = document.getElementById("city").value.trim();
    const pattern = /^[a-zA-Z0-9 ]*$/;

    let errors = [];

    if (name.length > 20) {
        errors.push("Name must be max 20 characters.");
    }
    if (!pattern.test(name)) {
        errors.push("Name must not contain special characters.");
    }

    if (city.length > 25) {
        errors.push("City must be max 25 characters.");
    }
    if (!pattern.test(city)) {
        errors.push("City must not contain special characters.");
    }

    if (errors.length > 0) {
        event.preventDefault();
        alert(errors.join("\n"));
    }
});
