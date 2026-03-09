import { render as renderAdmin } from "./views/admin.js";
import { render as renderCoach } from "./views/coach.js";
import { render as renderPlayer } from "./views/player.js";

export function getToken() {
    return localStorage.getItem("jwt");
}

export function clearToken() {
    localStorage.removeItem("jwt");
    localStorage.removeItem("role");
}

export function authHeaders() {
    const token = getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
}

export function handleUnauthorized() {
    clearToken();
    showLogin();
}

function renderPageForRole(role) {
    const pageContent = document.getElementById("pageContent");
    const pageTitle = document.getElementById("pageTitle");

    if (!pageContent) return;

    switch (role) {
        case "ADMIN":
            pageTitle.textContent = "Admin Dashboard";
            renderAdmin(pageContent);
            break;
        case "COACH":
            pageTitle.textContent = "Coach Dashboard";
            renderCoach(pageContent);
            break;
        case "PLAYER":
            pageTitle.textContent = "Player Dashboard";
            renderPlayer(pageContent);
            break;
        default:
            pageTitle.textContent = "Dashboard";
            pageContent.innerHTML = "<p>Unauthorized role</p>";
    }
}

export function showLogin() {
    $("#loginView").show();
    $("#appView").hide();
}

export function showApp() {
    $("#loginView").hide();
    $("#appView").show();

    const role = localStorage.getItem("role");
    renderPageForRole(role);
}

document.addEventListener("DOMContentLoaded", () => {
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