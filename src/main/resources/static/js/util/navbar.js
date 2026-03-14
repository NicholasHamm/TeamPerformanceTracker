(() => {
    'use strict';

    function renderNavbar(config) {
        const navContainer = document.getElementById('navItems');
        if (!navContainer) return;

        navContainer.innerHTML = '';

        const items = config.items || [];

        items.forEach((item, index) => {
            const html = `
                <li class="nav-item">
                    <a href="#" class="nav-link ${index === 0 ? 'active' : ''}" data-page="${item.id}">
                        <i class="${item.icon} me-2"></i>${item.label}
                    </a>
                </li>
            `;

            navContainer.insertAdjacentHTML('beforeend', html);
        });

        const logoutHtml = `
            <li class="nav-item ms-3">
                <a href="#" class="nav-link" id="logoutBtn">
                    <i class="fa-solid fa-right-from-bracket me-2"></i>Logout
                </a>
            </li>
        `;

        navContainer.insertAdjacentHTML('beforeend', logoutHtml);

        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();

                if (config.onLogout) {
                    config.onLogout();
                }
            });
        }

        navContainer.querySelectorAll('.nav-link[data-page]').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();

                navContainer.querySelectorAll('.nav-link')
                    .forEach(el => el.classList.remove('active'));

                link.classList.add('active');

                if (config.onNavigate) {
                    config.onNavigate(link.dataset.page);
                }
            });
        });
    }

    window.renderNavbar = renderNavbar;

    $(document).on('click', '#logoutBtn', function (e) {
        e.preventDefault();
        clearToken();
        showLogin();
    });

})();