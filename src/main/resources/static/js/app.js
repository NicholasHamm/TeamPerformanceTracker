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

        if (!pageContent) return;

        switch (role) {
            case 'ADMIN':
                pageTitle.textContent = 'Admin Dashboard';
                if (window.renderAdmin) {
                    window.renderAdmin(pageContent);
                }
                break;

            case 'COACH':
                pageTitle.textContent = 'Coach Dashboard';
                if (window.renderCoach) {
                    window.renderCoach(pageContent);
                }
                break;

            case 'PLAYER':
                pageTitle.textContent = 'Player Dashboard';
                if (window.renderPlayer) {
                    window.renderPlayer(pageContent);
                }
                break;

            default:
                pageTitle.textContent = 'Dashboard';
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