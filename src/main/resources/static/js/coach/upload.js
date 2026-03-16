(() => {
    'use strict';

    const SESSION_URL = '/api/sessions';
    let performanceTable = null;

    function renderUploadSection() {
        const session = window.coachPage.getSelectedSession();

        $('#sessionDetailsSection').html(`
            <div id="uploadMsg" class="alert d-none d-flex align-items-center gap-2">
                <i class="bi fs-5 flex-shrink-0"></i>
                <div class="msg-text"></div>
            </div>
            <div class="card mb-4">
                <div class="card-body">
                    <h4 class="card-title mb-3">Session ${session?.id ?? ''}</h4>
                    <div class="row">
                        <div class="col-md-4">
                            <strong>Datetime:</strong>
                            <div>${session?.datetime ?? ''}</div>
                        </div>
                        <div class="col-md-4">
                            <strong>Type:</strong>
                            <div>${session?.type ?? ''}</div>
                        </div>
                        <div class="col-md-4">
                            <strong>Duration:</strong>
                            <div>${session?.duration ?? 0} min</div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <button class="btn btn-outline-secondary" id="backToSessionsBtn">
                    <i class="fa-solid fa-arrow-left me-2"></i>Back to Sessions
                </button>
                <button class="btn btn-success" id="addPerformanceBtn">Add Player Data</button>
            </div>

            <h5 class="mb-3">Current Player Performance</h5>
            <table id="performanceTable" class="table table-striped">
                <thead>
                    <tr>
                        <th>Player</th>
                        <th>Total Distance</th>
                        <th>Distance/Min</th>
                        <th>High Intensity Distance</th>
                        <th>Top Speed</th>
                        <th>Effort Rating</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
        `);
    }

    function loadPerformanceTable() {
        const sessionId = window.coachPage.getSelectedSessionId();
        if (!sessionId) return;

        if ($.fn.DataTable.isDataTable('#performanceTable')) {
            $('#performanceTable').DataTable().destroy();
        }

        performanceTable = $('#performanceTable').DataTable({
            destroy: true,
            ajax: {
                url: `${SESSION_URL}/${sessionId}/performance`,
                dataSrc: function (json) {
                    return Array.isArray(json) ? json : (json.performances || []);
                },
                headers: authHeaders(),
                error: function (xhr) {
                    if (xhr.status === 401 || xhr.status === 403) {
                        handleUnauthorized();
                    }
                }
            },
            columns: [
                { data: 'playerName' },
                { data: 'totalDistance' },
                { data: 'distancePerMin' },
                { data: 'highIntensityDistance' },
                { data: 'topSpeed' },
                { data: 'effortRating' }
            ]
        });
    }

	const openPerformanceModal = () => {
	    const sessionId = window.coachPage.getSelectedSessionId();
	    if (!sessionId) return;

	    const msgBox = document.getElementById('uploadMsg');
	    hideMsg(msgBox);

	    const form = $('#performanceForm')[0];
	    if (form) form.reset();

	    hideMsg(document.getElementById('createPerformanceError'));

	    loadPlayersIntoSelect(sessionId);
	};

	function savePerformance() {
	    const sessionId = window.coachPage.getSelectedSessionId();
	    const session = window.coachPage.getSelectedSession();
	    if (!sessionId) return;

	    const msgBox = document.getElementById('uploadMsg');
	    const totalDistance = Number($('#totalDistance').val());
	    const duration = Number(session?.duration || 0);
	    const playerName = $('#performancePlayer option:selected').text();

	    const payload = {
	        playerId: Number($('#performancePlayer').val()),
	        totalDistance,
	        highIntensityDistance: Number($('#highIntensityDistance').val()),
	        topSpeed: Number($('#topSpeed').val()),
	        effortRating: Number($('#effortRating').val())
	    };

	    if (duration > 0) {
	        payload.distancePerMin = Number((totalDistance / duration).toFixed(2));
	    }

	    $.ajax({
	        type: 'POST',
	        url: `${SESSION_URL}/${sessionId}/performance`,
	        contentType: 'application/json',
	        headers: authHeaders(),
	        data: JSON.stringify(payload),
	        success: function () {
	            $('#performanceModal').modal('hide');
	            showMsg(msgBox, `${playerName} performance uploaded successfully`, 'success');

	            if (performanceTable) {
	                performanceTable.ajax.reload();
	            }
	        },
	        error: function (xhr) {
	            if (xhr.status === 401 || xhr.status === 403) {
	                handleUnauthorized();
	                return;
	            }

	            const message = window.extractCoachError(xhr, 'Failed to upload player data');
	            showMsg(document.getElementById('createPerformanceError'), message, 'danger');
	        }
	    });
	}

	const loadPlayersIntoSelect = (sessionId) => {
	    const msgBox = document.getElementById('uploadMsg');

	    $.ajax({
	        type: 'GET',
	        url: `${SESSION_URL}/${sessionId}/available`,
	        headers: authHeaders(),
	        success: function (players) {
	            if (!players || players.length === 0) {
	                showMsg(
	                    msgBox,
	                    'All players already have performance data for this session.',
	                    'warning'
	                );
	                return;
	            }

	            const $select = $('#performancePlayer');
	            $select.empty();
	            $select.append('<option value="">Select player</option>');

	            players.forEach(player => {
	                const fullName = `${player.firstName} ${player.lastName}`;
	                $select.append(`<option value="${player.id}">${fullName}</option>`);
	            });

	            $('#performanceModal').modal('show');
	        },
	        error: function (xhr) {
	            if (xhr.status === 401 || xhr.status === 403) {
	                handleUnauthorized();
	            }
	        }
	    });
	};

    window.extractCoachError = (xhr, fallbackMessage) => {
        if (xhr.responseJSON?.error) return xhr.responseJSON.error;
        if (xhr.responseJSON?.message) return xhr.responseJSON.message;
        if (typeof xhr.responseText === 'string' && xhr.responseText.trim().length > 0) {
            return xhr.responseText;
        }
        return fallbackMessage;
    };

    window.renderCoachUploadSection = () => {
        renderUploadSection();
        loadPerformanceTable();
    };

    window.openPerformanceModal = openPerformanceModal;

    $(document).on('click', '#addPerformanceBtn', function () {
        openPerformanceModal();
    });

    $(document).on('click', '#savePerformanceBtn', function () {
        savePerformance();
    });

	$(document).on('click', '#backToSessionsBtn', function () {
	    window.coachPage.goTo('sessions');
	});
})();