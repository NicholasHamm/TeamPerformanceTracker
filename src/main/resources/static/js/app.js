(() => {
    'use strict';

    const getToken = () => localStorage.getItem('jwt');

    const clearToken = () => {
        localStorage.removeItem('jwt');
        localStorage.removeItem('role');
    };

    const authHeaders = () => {
        const token = getToken();
        return token ? { Authorization: `Bearer ${token}` } : {};
    };

    const showLogin = () => {
        $('#loginView').show();
        $('#appView').hide();
    };

    const showApp = () => {
        $('#loginView').hide();
        $('#appView').show();

        const role = localStorage.getItem('role');
        renderPageForRole(role);
    };

    const handleUnauthorized = () => {
        clearToken();
        showLogin();
    };

    const renderPageForRole = (role) => {
        const pageContent = document.getElementById('pageContent');
        const pageTitle = document.getElementById('pageTitle');
		const navTitle = document.getElementById('navbarWelcome');

        if (!pageContent) return;

        switch (role) {
            case 'ADMIN':
                if (window.renderAdmin) {
					navTitle.textContent = "Admin Dashboard";
                    window.renderAdmin(pageContent);
                }
                break;

            case 'COACH':
                if (window.renderCoach) {
					navTitle.textContent = "Coach Dashboard";
                    window.renderCoach(pageContent);
                }
                break;

            case 'PLAYER':
                if (window.renderPlayer) {
					navTitle.textContent = "Player Dashboard";
					window.renderPlayer(pageContent);
                }
                break;

            default:
                pageTitle.textContent = 'Unauthorized';
                pageContent.innerHTML = '<p>Unauthorized role</p>';
        }
    };

    window.getToken = getToken;
    window.clearToken = clearToken;
    window.authHeaders = authHeaders;
    window.handleUnauthorized = handleUnauthorized;
    window.showLogin = showLogin;
    window.showApp = showApp;

    $(function () {
        if (getToken()) {
            showApp();
        } else {
            showLogin();
        }

        $('#logoutBtn').on('click', function () {
            clearToken();
            showLogin();
        });
    });

})();