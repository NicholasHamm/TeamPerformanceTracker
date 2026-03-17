(() => {
    'use strict';

    const SESSION_URL = '/api/sessions';
    let sessionsTable = null;
	let isCreatingSession = false;

    function renderSessionsSection() {

        $('#sessionsListSection').html(`
            <h2 class="mb-3">Training Sessions</h2>

            <div id="createSessionSuccess" class="alert d-none d-flex align-items-center gap-2">
                <i class="bi fs-5 flex-shrink-0"></i>
                <div class="msg-text"></div>
            </div>

            <table id="sessionTable" class="table table-striped">
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
        `);
    }

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

	function createSession() {
		if (isCreatingSession) return;
		isCreatingSession = true;
		
		const saveBtn = $('#saveSessionBtn');
		saveBtn.prop('disabled', true);
		
	    const msgBox = document.getElementById('createSessionSuccess');
	    const errorBox = document.getElementById('createSessionError');

	    hideMsg(errorBox);
	    hideMsg(msgBox);

		const rawDatetime = $('#createDatetime').val();

		const payload = {
		    datetime: rawDatetime && rawDatetime.length === 16 ? `${rawDatetime}:00` : rawDatetime,
		    type: $('#createType').val(),
		    duration: Number($('#createDuration').val())
		};

	    $.ajax({
	        type: 'POST',
	        url: SESSION_URL,
	        contentType: 'application/json',
	        headers: authHeaders(),
	        data: JSON.stringify(payload),
			success: () => {
			    $('#sessionModal').modal('hide');

			    renderSessionsSection();
			    loadSessionsTable();

			    const msgBox = document.getElementById('createSessionSuccess');
			    showMsg(msgBox, 'Training session created successfully', 'success');
			},
	        error: (xhr) => {
	            if (xhr.status === 401 || xhr.status === 403) {
	                handleUnauthorized();
	                return;
	            }

	            const message = window.extractCoachError
	                ? window.extractCoachError(xhr, 'Failed to create session')
	                : 'Failed to create session';

	            showMsg(errorBox, message, 'danger');
	        },
			complete: () => {
			    isCreatingSession = false;
			    saveBtn.prop('disabled', false);
			}
	    });
	}
	
	window.openCreateSessionModal = openCreateSessionModal;

	$(document).off('click', '#saveSessionBtn').on('click', '#saveSessionBtn', function () {
	    createSession();
	});
	
    $(document).on('click', '.manage-session-btn', function () {

        const sessionId = Number($(this).data('session-id'));

        const session = getSessionById(sessionId);

        window.coachPage.setSelectedSession(session);

        window.coachPage.goTo('session-details');
    });

})();