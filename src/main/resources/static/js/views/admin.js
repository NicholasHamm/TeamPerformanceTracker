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

    const renderAdmin = () => {
        const $container = $('#pageContent');

        if (!$container.length) return;

        $container.html(`
            <div class="mt-3">
                <table id="userTable" class="table table-striped">
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

            <div class="modal fade" id="userModal" tabindex="-1" aria-labelledby="userModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="userModalLabel">Create New User</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>

                        <div class="modal-body">
                            <form id="userForm">
                                <div id="createUserError" class="alert d-none d-flex align-items-center gap-2">
                                    <i class="bi fs-5 flex-shrink-0"></i>
                                    <div class="msg-text"></div>
                                </div>

                                <div class="mb-3">
                                    <label for="createUsername" class="form-label">Username</label>
                                    <input type="text" class="form-control" id="createUsername" required>
                                </div>

                                <div class="mb-3">
                                    <label for="createPassword" class="form-label">Password</label>
                                    <input type="password" class="form-control" id="createPassword" required>
                                </div>

                                <div class="mb-3">
                                    <label for="createFirstName" class="form-label">First Name</label>
                                    <input type="text" class="form-control" id="createFirstName" required>
                                </div>

                                <div class="mb-3">
                                    <label for="createLastName" class="form-label">Last Name</label>
                                    <input type="text" class="form-control" id="createLastName" required>
                                </div>

                                <div class="mb-3">
                                    <label for="createRole" class="form-label">Role</label>
                                    <select class="form-control" id="createRole" required>
                                        <option value="PLAYER">PLAYER</option>
                                        <option value="COACH">COACH</option>
                                        <option value="ADMIN">ADMIN</option>
                                    </select>
                                </div>
                            </form>
                        </div>

                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-primary" id="saveUserBtn">Save</button>
                        </div>
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
        $('#userModal').modal('show');
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
                if (userTable) {
                    userTable.ajax.reload();
                }
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }

                let message = 'Failed to save user';

                if (xhr.responseJSON?.message) {
                    message = xhr.responseJSON.message;
                } else if (xhr.responseText) {
                    message = xhr.responseText;
                }

                const msgBox = document.getElementById('createUserError');
                showMsg(msgBox, message, 'danger');
            }
        });
    };

    const initAdminNavbar = () => {
        window.renderNavbar({
            items: adminNavbar.items,
            onNavigate: (page) => {
                switch (page) {
                    case 'dashboard':
                        renderAdmin();
                        loadAdminData();
                        break;

                    case 'create-user':
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
        renderAdmin();
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