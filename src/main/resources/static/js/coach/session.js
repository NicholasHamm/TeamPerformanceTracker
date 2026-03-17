(() => {
    'use strict';

    const SESSION_URL = '/api/sessions';
    let sessionsTable = null;
	let selectedSessionIdToDelete = null;
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
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
			
			<div class="modal fade" id="deleteSessionModal" tabindex="-1" aria-hidden="true">
			    <div class="modal-dialog modal-dialog-centered">
			        <div class="modal-content border-0 shadow">

			            <div class="modal-header">
			                <h5 class="modal-title">Delete Training Session</h5>
			                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
			            </div>

			            <div class="modal-body">
			                <p class="mb-0" id="deleteSessionMessage">
			                    Are you sure you want to delete this session? 
			                    <br><strong>All associated player data will also be permanently removed.</strong>
			                </p>
			            </div>

			            <div class="modal-footer">
			                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
			                    Cancel
			                </button>
			                <button type="button" class="btn btn-danger" id="confirmDeleteSessionBtn">
			                    Delete
			                </button>
			            </div>

			        </div>
			    </div>
			</div>
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
				            <div class="d-flex gap-2">
				                <button class="btn btn-sm btn-outline-primary manage-session-btn"
				                        data-session-id="${row.id}">
				                    Manage
				                </button>
				                <button class="btn btn-sm btn-outline-danger delete-session-btn"
				                        data-session-id="${row.id}">
				                    Delete
				                </button>
				            </div>`;
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
	
	$(document).off('click', '#confirmDeleteSessionBtn').on('click', '#confirmDeleteSessionBtn', function () {

	    if (!selectedSessionIdToDelete) return;

	    $.ajax({
	        type: 'DELETE',
	        url: `${SESSION_URL}/${selectedSessionIdToDelete}`,
	        headers: authHeaders(),

	        success: () => {
	            const modalEl = document.getElementById('deleteSessionModal');
	            bootstrap.Modal.getInstance(modalEl).hide();

	            renderSessionsSection();
	            loadSessionsTable();

	            const msgBox = document.getElementById('createSessionSuccess');
	            showMsg(msgBox, 'Training Session deleted', 'warn');
	        },

	        error: (xhr) => {

	            const modalEl = document.getElementById('deleteSessionModal');
	            bootstrap.Modal.getInstance(modalEl).hide();

	            if (xhr.status === 401 || xhr.status === 403) {
	                handleUnauthorized();
	                return;
	            }

	            const msgBox = document.getElementById('createSessionSuccess');
	            showMsg(msgBox, 'Failed to delete session data', 'danger');
	        }
	    });
	});
	
	window.openCreateSessionModal = openCreateSessionModal;

	$(document).off('click', '#saveSessionBtn').on('click', '#saveSessionBtn', function () {
	    createSession();
	});
	
	$(document).off('click', '.delete-session-btn').on('click', '.delete-session-btn', function () {
	    selectedSessionIdToDelete = Number($(this).data('session-id'));

	    const modal = new bootstrap.Modal(document.getElementById('deleteSessionModal'));
	    modal.show();
	});
	
    $(document).on('click', '.manage-session-btn', function () {

        const sessionId = Number($(this).data('session-id'));

        const session = getSessionById(sessionId);

        window.coachPage.setSelectedSession(session);

        window.coachPage.goTo('session-details');
    });

})();