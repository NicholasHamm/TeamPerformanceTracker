const API_BASE = "";

function getToken() {
    return localStorage.getItem("jwt");
}

function setToken(token) {
    localStorage.setItem("jwt", token);
}

function clearToken() {
    localStorage.removeItem("jwt");
}

function authHeaders() {
    const token = getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
}

function showLogin() {
    $("#loginView").show();
    $("#appView").hide();
}

function showApp() {
    $("#loginView").hide();
    $("#appView").show();

    if (typeof loadAdminData === "function") {
        loadAdminData();
    }
}

function handleUnauthorized() {
    clearToken();
    showLogin();
}

$(document).ready(function () {
    if (getToken()) {
        showApp();
    } else {
        showLogin();
    }

    $("#logoutBtn").on("click", function () {
        clearToken();
        showLogin();
    });
});