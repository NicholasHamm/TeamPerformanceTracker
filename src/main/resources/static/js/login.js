import {showApp} from "./app.js";

const LOGIN_URL = `auth/login`;

$(document).ready(function () {
    $("#loginForm").on("submit", function (e) {
        e.preventDefault();

        const credentials = {
            username: $("#loginUsername").val().trim(),
            password: $("#loginPassword").val()
        };

        $.ajax({
            url: LOGIN_URL,
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(credentials),
            success: function (response) {
                localStorage.setItem("jwt", response.token);
                localStorage.setItem("role", response.role);
                showApp();
            },
            error: function () {
                $("#loginError").removeClass("d-none").text("Invalid username or password");
            }
        });
    });
});