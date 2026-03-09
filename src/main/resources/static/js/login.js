const LOGIN_URL = `${API_BASE}/api/auth/login`;

$(document).ready(function () {
    $("#loginForm").on("submit", function (e) {
        e.preventDefault();

        const credentials = {
            username: $("#loginUsername").val().trim(),
            password: $("#loginPassword").val()
        };

        console.log("Sending login request:", credentials);

        $.ajax({
            url: LOGIN_URL,
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(credentials),
            success: function (response) {
                console.log("Login success:", response);
                localStorage.setItem("jwt", response.token);
                $("#loginError").addClass("d-none").text("");
                $("#loginForm")[0].reset();
                showApp();
            },
            error: function (xhr) {
                console.log("Login failed");
                console.log("Status:", xhr.status);
                console.log("Response:", xhr.responseText);

                let message = "Invalid username or password";

                if (xhr.responseJSON && xhr.responseJSON.message) {
                    message = xhr.responseJSON.message;
                }

                $("#loginError").removeClass("d-none").text(message);
            }
        });
    });
});