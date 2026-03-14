(() => {
    'use strict';

    const SESSION_URL = '/api/sessions';
    let sessionsTable = null;

    function renderSessionsSection() {
        $('#sessionsListSection').html(`
           <h2 class="mb-3">Training Sessions</h2><table id="sessionTable" class="table table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Datetime</th>
                        <th>Type</th>
                        <th>Duration (min)</th>
                        <th>Upload Data</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
        `);
    }

    function loadSessionsTable() {
        if ($.fn.DataTable.isDataTable('#sessionTable')) {
            $('#sessionTable').DataTable().destroy();
        }

        sessionsTable = $('#sessionTable').DataTable({
            destroy: true,
            ajax: {
                url: SESSION_URL,
                dataSrc: '',
                headers: authHeaders(),
                error: function (xhr) {
                    if (xhr.status === 401 || xhr.status === 403) {
                        handleUnauthorized();
                    }
                }
            },
            columns: [
                { data: 'id' },
                { data: 'datetime' },
                { data: 'type' },
                { data: 'duration' },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: function (data, type, row) {
                        return `
                            <button type="button"
                                    class="btn btn-sm btn-outline-primary manage-session-btn"
                                    data-session-id="${row.id}">
                                Manage
                            </button>
                        `;
                    }
                }
            ]
        });
    }

    function openCreateSessionModal() {
        const modalEl = document.getElementById('sessionModal');
        if (!modalEl) {
            console.error('sessionModal not found in DOM');
            return;
        }

        const form = $('#sessionForm')[0];
        if (form) form.reset();

        hideMsg(document.getElementById('createSessionError'));
        $('#sessionModal').modal('show');
    }

    function saveSession() {
        const trainingSession = {
            datetime: $('#createDatetime').val(),
            type: $('#createType').val(),
            duration: Number($('#createDuration').val())
        };

        $.ajax({
            type: 'POST',
            url: SESSION_URL,
            contentType: 'application/json',
            headers: authHeaders(),
            data: JSON.stringify(trainingSession),
            success: function () {
                $('#sessionModal').modal('hide');
                if (sessionsTable) sessionsTable.ajax.reload();
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }
                const message = window.extractCoachError(xhr, 'Failed to save training session');
                showMsg(document.getElementById('createSessionError'), message, 'danger');
            }
        });
    }

    function getSessionById(sessionId) {
        if (!sessionsTable) return null;
        const rows = sessionsTable.rows().data().toArray();
        return rows.find(row => Number(row.id) === Number(sessionId)) || null;
    }

    window.renderCoachSessionsSection = () => {
        renderSessionsSection();
        loadSessionsTable();
    };

    window.openCreateSessionModal = openCreateSessionModal;

    $(document).on('click', '#addSessionBtn', function () {
        openCreateSessionModal();
    });

    $(document).on('click', '#saveSessionBtn', function () {
        saveSession();
    });

    $(document).on('click', '.manage-session-btn', function () {
        const sessionId = Number($(this).data('session-id'));
        const session = getSessionById(sessionId);

        window.coachPage.setSelectedSession(session);
        window.renderCoachUploadSection();
        window.coachPage.showSessionDetails();
    });
})();