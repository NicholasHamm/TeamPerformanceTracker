(() => {
    'use strict';

    const SESSION_URL = '/api/sessions';

    let performanceTable = null;
    let performanceMode = 'create';
    let editingPlayerId = null;

	const renderUploadSection = () => {
	    const session = window.coachPage.getSelectedSession();

	    $('#sessionDetailsSection').html(`
	        <div class="content-panel">
	            <div class="content-panel-header">
	                <div>
	                    <h2 class="content-panel-title mb-1">Session Performance</h2>
	                    <p class="content-panel-subtitle mb-0">
	                        Manage player performance data for the selected session
	                    </p>
	                </div>
	            </div>

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
	                <button class="btn btn-primary" id="addPerformanceBtn">
	                    <i class="fa-solid fa-plus me-2"></i>Add Player Data
	                </button>
	            </div>

	            <div class="table-shell">
	                <table id="performanceTable" class="table table-striped align-middle mb-0">
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
	            </div>
	        </div>
	    `);
	};
    const loadPerformanceTable = () => {
        const sessionId = window.coachPage.getSelectedSessionId();
        if (!sessionId) return;

        if ($.fn.DataTable.isDataTable('#performanceTable')) {
            $('#performanceTable').DataTable().destroy();
        }

        performanceTable = $('#performanceTable').DataTable({
            destroy: true,
            ajax: {
                url: `${SESSION_URL}/${sessionId}/performance`,
                dataSrc: (json) => {
                    return Array.isArray(json) ? json : (json.performances || []);
                },
                headers: authHeaders(),
                error: (xhr) => {
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
                { data: 'effortRating' },
                {
                    data: null,
                    orderable: false,
                    render: (data, type, row) => {
                        return `
                            <button class="btn btn-sm btn-outline-primary edit-performance-btn"
                                    data-player-id="${row.playerId}">
                                Edit
                            </button>
                            <button class="btn btn-sm btn-outline-danger delete-performance-btn ms-2"
                                    data-player-id="${row.playerId}"
                                    data-player-name="${row.playerName}">
                                Delete
                            </button>
                        `;
                    }
                }
            ]
        });
    };

    const resetPerformanceModalForCreate = () => {
        performanceMode = 'create';
        editingPlayerId = null;

        const form = $('#performanceForm')[0];
        if (form) form.reset();

        $('#performanceModal .modal-title').text('Add Player Data');
        $('#savePerformanceBtn').text('Save');

        const $playerSelect = $('#performancePlayer');
        $playerSelect.prop('disabled', false);
        $playerSelect.empty().append('<option value="">Select player</option>');

        hideMsg(document.getElementById('createPerformanceError'));
    };

    const openPerformanceModal = () => {
        const sessionId = window.coachPage.getSelectedSessionId();
        if (!sessionId) return;

        const msgBox = document.getElementById('uploadMsg');
        hideMsg(msgBox);

        resetPerformanceModalForCreate();
        loadPlayersIntoSelect(sessionId);
    };

    const openEditPerformanceModal = (row) => {
        if (!row) return;

        performanceMode = 'edit';
        editingPlayerId = Number(row.playerId);

        const form = $('#performanceForm')[0];
        if (form) form.reset();

        hideMsg(document.getElementById('createPerformanceError'));

        $('#performanceModal .modal-title').text('Edit Player Data');
        $('#savePerformanceBtn').text('Update');

        const $playerSelect = $('#performancePlayer');
        $playerSelect.empty();
        $playerSelect.append(`<option value="${row.playerId}">${row.playerName}</option>`);
        $playerSelect.val(row.playerId);
        $playerSelect.prop('disabled', true);

        $('#totalDistance').val(row.totalDistance);
        $('#highIntensityDistance').val(row.highIntensityDistance);
        $('#topSpeed').val(row.topSpeed);
        $('#effortRating').val(row.effortRating);

        $('#performanceModal').modal('show');
    };

    const createPerformance = () => {
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
            success: () => {
                document.activeElement?.blur();
                $('#performanceModal').modal('hide');

                showMsg(msgBox, `${playerName} performance uploaded successfully`, 'success');

                if (performanceTable) performanceTable.ajax.reload();
            },
            error: (xhr) => {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }

                const message = window.extractCoachError(xhr, 'Failed to upload player data');
                showMsg(document.getElementById('createPerformanceError'), message, 'danger');
            }
        });
    };

    const updatePerformance = () => {
        const sessionId = window.coachPage.getSelectedSessionId();
        const session = window.coachPage.getSelectedSession();
        if (!sessionId || !editingPlayerId) return;

        const msgBox = document.getElementById('uploadMsg');
        const totalDistance = Number($('#totalDistance').val());
        const duration = Number(session?.duration || 0);

        const payload = {
            playerId: editingPlayerId,
            totalDistance,
            highIntensityDistance: Number($('#highIntensityDistance').val()),
            topSpeed: Number($('#topSpeed').val()),
            effortRating: Number($('#effortRating').val())
        };

        if (duration > 0) {
            payload.distancePerMin = Number((totalDistance / duration).toFixed(2));
        }

        $.ajax({
            type: 'PUT',
            url: `${SESSION_URL}/${sessionId}/performance`,
            contentType: 'application/json',
            headers: authHeaders(),
            data: JSON.stringify(payload),
            success: () => {
                document.activeElement?.blur();
                $('#performanceModal').modal('hide');

                showMsg(msgBox, 'Performance data updated successfully', 'success');

                if (performanceTable) performanceTable.ajax.reload();
            },
            error: (xhr) => {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }

                const message = window.extractCoachError(xhr, 'Failed to update player data');
                showMsg(document.getElementById('createPerformanceError'), message, 'danger');
            }
        });
    };

    const savePerformance = () => {
        if (performanceMode === 'edit') updatePerformance();
        else createPerformance();
    };

    const deletePerformance = (playerId, playerName) => {
        const sessionId = window.coachPage.getSelectedSessionId();
        if (!sessionId || !playerId) return;

        const msgBox = document.getElementById('uploadMsg');

        if (!confirm(`Delete performance data for ${playerName}?`)) return;

        $.ajax({
            type: 'DELETE',
            url: `${SESSION_URL}/${sessionId}/player/${playerId}`,
            headers: authHeaders(),
            success: () => {
                showMsg(msgBox, 'Performance data deleted successfully', 'success');

                if (performanceTable) performanceTable.ajax.reload();
            },
            error: (xhr) => {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }

                const message = window.extractCoachError(xhr, 'Failed to delete player data');
                showMsg(msgBox, message, 'danger');
            }
        });
    };

    const loadPlayersIntoSelect = (sessionId) => {
        const msgBox = document.getElementById('uploadMsg');

        $.ajax({
            type: 'GET',
            url: `${SESSION_URL}/${sessionId}/available`,
            headers: authHeaders(),
            success: (players) => {
                if (!players || players.length === 0) {
                    showMsg(msgBox, 'All players already have performance data for this session.', 'warning');
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
            error: (xhr) => {
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

    $(document).on('click', '#addPerformanceBtn', () => openPerformanceModal());

    $(document).on('click', '#savePerformanceBtn', () => savePerformance());

    $(document).on('click', '.edit-performance-btn', function () {
        const row = performanceTable.row($(this).closest('tr')).data();
        openEditPerformanceModal(row);
    });

    $(document).on('click', '.delete-performance-btn', function () {
        const playerId = Number($(this).data('player-id'));
        const playerName = $(this).data('player-name');
        deletePerformance(playerId, playerName);
    });

    $(document).on('click', '#backToSessionsBtn', () => {
        window.coachPage.goTo('sessions');
    });

})();