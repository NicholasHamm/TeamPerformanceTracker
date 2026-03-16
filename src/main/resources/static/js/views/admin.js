(() => {
    'use strict';

    const ADMIN_URL = '/api/users';

    const adminNavbar = {
        items: [
            { id: 'dashboard', label: 'Admin Dashboard', icon: 'fa-solid fa-house' },
            { id: 'create-user', label: 'Create User', icon: 'fa-solid fa-user-plus' }
        ]
    };

    let userTable = null;
    let currentPage = 'dashboard';

    const renderAdminView = () => {
        const $container = $('#pageContent');

        if (!$container.length) return;

		$container.html(`
		        <div class="content-panel">
		            <div class="content-panel-header">
		                <div>
		                    <h2 class="content-panel-title mb-1">User Management</h2>
		                </div>
		            </div>

		            <div id="createUserSuccess" class="alert d-none d-flex align-items-center gap-2">
		                <i class="bi fs-5 flex-shrink-0"></i>
		                <div class="msg-text"></div>
		            </div>

		            <div class="table-shell">
		                <table id="userTable" class="table table-striped align-middle mb-0">
		                    <thead>
		                        <tr>
		                            <th>Username</th>
		                            <th>First name</th>
		                            <th>Last name</th>
		                            <th>Role</th>
		                        </tr>
		                    </thead>
		                    <tbody></tbody>
		                </table>
		            </div>
		        </div>
            </div>
        `);
    };

    const loadAdminData = () => {
        if ($.fn.DataTable.isDataTable('#userTable')) {
            $('#userTable').DataTable().destroy();
        }
        initUserTable();
    };

    const initUserTable = () => {
        userTable = $('#userTable').DataTable({
            destroy: true,
            ajax: {
                url: ADMIN_URL,
                dataSrc: '',
                headers: authHeaders(),
                error: function (xhr) {
                    if (xhr.status === 401 || xhr.status === 403) {
                        handleUnauthorized();
                    }
                }
            },
            columns: [
                { data: 'username' },
                { data: 'firstName' },
                { data: 'lastName' },
                { data: 'role' }
            ]
        });
    };

    const openCreateUserModal = () => {
        const form = $('#userForm')[0];
        if (form) form.reset();

        hideMsg(document.getElementById('createUserError'));

        $('#userModalLabel').text('Create New User');
		const modalEl = document.getElementById('userModal');
		const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
		modal.show();
    };

    const saveUser = () => {
        const user = {
            username: $('#createUsername').val().trim(),
            password: $('#createPassword').val(),
            firstName: $('#createFirstName').val().trim(),
            lastName: $('#createLastName').val().trim(),
            role: $('#createRole').val()
        };

        $.ajax({
            type: 'POST',
            url: ADMIN_URL,
            contentType: 'application/json',
            headers: authHeaders(),
            data: JSON.stringify(user),
            success: function () {
                $('#userModal').modal('hide');
                showMsg(document.getElementById('createUserSuccess'), `User: [${user.username}] created successfully`, 'success');
                if (userTable) {
                    userTable.ajax.reload();
                }
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }

                const message = extractErrorMessage(xhr, 'Failed to save user');
                const msgBox = document.getElementById('createUserError');
                showMsg(msgBox, message, 'danger');
            }
        });
    };

    const initAdminNavbar = () => {
        const navItems = adminNavbar.items.map(item => ({
            ...item,
            active: item.id === currentPage
        }));

        window.renderNavbar({
            items: navItems,
            onNavigate: (page) => {
                switch (page) {
                    case 'dashboard':
                        currentPage = 'dashboard';
                        renderAdminView();
                        loadAdminData();
                        initAdminNavbar();
                        break;

                    case 'create-user':
                        // keep dashboard highlighted because this is a modal action
                        currentPage = 'dashboard';
                        initAdminNavbar();
                        openCreateUserModal();
                        break;
                }
            },
            onLogout: () => {
                clearToken();
                showLogin();
            }
        });
    };

    window.renderAdmin = () => {
        currentPage = 'dashboard';
        renderAdminView();
        loadAdminData();
        initAdminNavbar();
    };

    window.openCreateUserModal = openCreateUserModal;

    $(document).on('click', '#addUserBtn', function () {
        openCreateUserModal();
    });

    $(document).on('click', '#saveUserBtn', function () {
        saveUser();
    });

})();