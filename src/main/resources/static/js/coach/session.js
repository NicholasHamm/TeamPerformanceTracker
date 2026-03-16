(() => {
    'use strict';

    const SESSION_URL = '/api/sessions';
    let sessionsTable = null;

	const renderSessionsSection = () => {
	    $('#sessionsListSection').html(`
	        <div class="content-panel">
	            <div class="content-panel-header">
	                <div>
	                    <h2 class="content-panel-title mb-1">Training Sessions</h2>
	                    <p class="content-panel-subtitle mb-0">
	                        View, manage, and upload player performance data for each session
	                    </p>
	                </div>
	            </div>

	            <div id="createSessionSuccess" class="alert d-none d-flex align-items-center gap-2 mt-3">
	                <i class="bi fs-5 flex-shrink-0"></i>
	                <div class="msg-text"></div>
	            </div>

	            <div class="table-shell">
	                <table id="sessionTable" class="table table-striped align-middle mb-0">
	                    <thead>
	                        <tr>
	                            <th>ID</th>
	                            <th>Datetime</th>
	                            <th>Type</th>
	                            <th>Duration</th>
	                            <th>Upload Data</th>
	                        </tr>
	                    </thead>
	                    <tbody></tbody>
	                </table>
	            </div>
	        </div>
	    `);
	};

    function loadSessionsTable() {

        if ($.fn.DataTable.isDataTable('#sessionTable')) {
            $('#sessionTable').DataTable().destroy();
        }

        sessionsTable = $('#sessionTable').DataTable({

            ajax: {
                url: SESSION_URL,
                dataSrc: '',
                headers: authHeaders(),
                error(xhr) {
                    if (xhr.status === 401 || xhr.status === 403) {
                        handleUnauthorized();
                    }
                }
            },

            columns: [
                { data: 'id' },
                {
                    data: 'datetime',
                    render(data) {
                        return formatDateGMT(data);
                    }
                },
                { data: 'type' },
                { data: 'duration' },
                {
                    data: null,
                    render(data, type, row) {
                        return `
                        <button class="btn btn-sm btn-outline-primary manage-session-btn"
                                data-session-id="${row.id}">
                            Manage
                        </button>`;
                    }
                }
            ]
        });
    }

    function getSessionById(id) {

        if (!sessionsTable) return null;

        const rows = sessionsTable.rows().data().toArray();

        return rows.find(s => Number(s.id) === Number(id));
    }

    window.renderCoachSessionsSection = () => {

        renderSessionsSection();
        loadSessionsTable();
    };
	
	function openCreateSessionModal() {
	    const modalEl = document.getElementById('sessionModal');
	    if (!modalEl) {
	        console.error('sessionModal not found in DOM');
	        return;
	    }

	    const form = $('#sessionForm')[0];
	    if (form) form.reset();

	    $('#createDatetime').val(getDefaultSessionDateTime());
	    hideMsg(document.getElementById('createSessionError'));
	    $('#sessionModal').modal('show');
	}

	window.openCreateSessionModal = openCreateSessionModal;

    $(document).on('click', '.manage-session-btn', function () {

        const sessionId = Number($(this).data('session-id'));

        const session = getSessionById(sessionId);

        window.coachPage.setSelectedSession(session);

        window.coachPage.goTo('session-details');
    });

})();