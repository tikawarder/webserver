document.addEventListener("DOMContentLoaded", () => {
    const birthEl = document.getElementById("birthDay");
    const ageEl = document.getElementById("ageResult");

    if (birthEl && ageEl) {
        const birthStr = birthEl.textContent.trim();

        if (birthStr) {
            const birthDate = new Date(birthStr);
            const today = new Date();

            let age = today.getFullYear() - birthDate.getFullYear();
            const m = today.getMonth() - birthDate.getMonth();

            if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }

            ageEl.textContent = age;
        }
    }
});
