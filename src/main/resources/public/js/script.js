document.addEventListener("DOMContentLoaded", function () {
    const bomDialog = document.getElementById("bomDialog");

    if (bomDialog) {
        bomDialog.addEventListener("click", function (event) {
            if (event.target === bomDialog) {
                bomDialog.close();
            }
        });
    }
});

function closeSuccessModal() {
    const successModal = document.getElementById("successModal");

    if (successModal) {
        successModal.style.display = "none";
    }
}

function closeOfferModal() {
    const offerModal = document.getElementById("offerModal");

    if (offerModal) {
        offerModal.style.display = "none";
    }
}

let selectedAcceptForm = null;

function openAcceptModal(button) {
    selectedAcceptForm = button.closest("form");
    document.getElementById("acceptDialog").showModal();
}

function submitAcceptForm() {
    selectedAcceptForm.submit();
}


function togglePassword() {
    const passwordInput = document.getElementById("password");
    const passwordEye = document.getElementById("passwordEye");

    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        passwordEye.classList.remove("fa-eye");
        passwordEye.classList.add("fa-eye-slash");
    } else {
        passwordInput.type = "password";
        passwordEye.classList.remove("fa-eye-slash");
        passwordEye.classList.add("fa-eye");
    }
}