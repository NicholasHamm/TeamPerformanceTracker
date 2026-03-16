(() => {
    'use strict';

    const coachNavbar = {
        items: [
            { id: 'sessions', label: 'Training Sessions', icon: 'fa-solid fa-calendar' },
            { id: 'create-session', label: 'New Session', icon: 'fa-solid fa-calendar-plus' },
            { id: 'session-averages', label: 'Session Averages', icon: 'fa-solid fa-chart-column' }
        ]
    };

    const state = {
        currentPage: 'sessions',
        selectedSessionId: null,
        selectedSession: null
    };

    function renderCoachShell() {
        $('#pageContent').html(`
            <div id="sessionsListSection" class="mt-3"></div>
            <div id="sessionDetailsSection" class="mt-3 d-none"></div>
            <div id="sessionAveragesSection" class="mt-3 d-none"></div>

            <div class="modal fade" id="sessionModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Create New Training Session</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="sessionForm">
                                <div id="createSessionError" class="alert d-none d-flex align-items-center gap-2">
                                    <i class="bi fs-5 flex-shrink-0"></i>
                                    <div class="msg-text"></div>
                                </div>

                                <div class="mb-3">
                                    <label for="createDatetime" class="form-label">Datetime</label>
                                    <input type="datetime-local" class="form-control" id="createDatetime" required>
                                </div>

                                <div class="mb-3">
                                    <label for="createType" class="form-label">Training Type</label>
                                    <select class="form-control" id="createType" required>
                                        <option value="">Select type</option>
                                        <option value="CONDITIONING">Conditioning</option>
                                        <option value="TACTICAL">Tactical</option>
                                        <option value="MATCH_SIMULATION">Match Simulation</option>
                                        <option value="SPEED">Speed</option>
                                        <option value="RECOVERY">Recovery</option>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label for="createDuration" class="form-label">Duration (minutes)</label>
                                    <input type="number" min="1" class="form-control" id="createDuration" required>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-primary" id="saveSessionBtn">Save</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="performanceModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Add Player Data</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="performanceForm">
                                <div id="createPerformanceError" class="alert d-none d-flex align-items-center gap-2">
                                    <i class="bi fs-5 flex-shrink-0"></i>
                                    <div class="msg-text"></div>
                                </div>

                                <div class="mb-3">
                                    <label for="performancePlayer" class="form-label">Player</label>
                                    <select class="form-control" id="performancePlayer" required>
                                        <option value="">Select player</option>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label for="totalDistance" class="form-label">Total Distance</label>
                                    <input type="number" min="0" step="0.01" class="form-control" id="totalDistance" required>
                                </div>

                                <div class="mb-3">
                                    <label for="highIntensityDistance" class="form-label">High Intensity Distance</label>
                                    <input type="number" min="0" step="0.01" class="form-control" id="highIntensityDistance" required>
                                </div>

                                <div class="mb-3">
                                    <label for="topSpeed" class="form-label">Top Speed</label>
                                    <input type="number" min="0" step="0.01" class="form-control" id="topSpeed" required>
                                </div>

                                <div class="mb-3">
                                    <label for="effortRating" class="form-label">Effort Rating</label>
                                    <input type="number" min="1" max="10" class="form-control" id="effortRating" required>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-primary" id="savePerformanceBtn">Save</button>
                        </div>
                    </div>
                </div>
            </div>
        `);
    }

    function hideAllSections() {
        $('#sessionsListSection').addClass('d-none');
        $('#sessionDetailsSection').addClass('d-none');
        $('#sessionAveragesSection').addClass('d-none');
    }

	function initCoachNavbar() {
	    const visibleItems = coachNavbar.items
	        .filter(item =>
	            !(item.id === 'create-session' && state.currentPage !== 'sessions')
	        )
	        .map(item => ({
	            ...item,
	            active:
	                item.id === state.currentPage ||
	                (state.currentPage === 'session-details' && item.id === 'sessions')
	        }));

	    window.renderNavbar({
	        items: visibleItems,
	        onNavigate: handleNavigation,
	        onLogout: () => {
	            clearToken();
	            showLogin();
	        }
	    });
	}

    function handleNavigation(page) {
        if (page === 'create-session') {
            if (state.currentPage === 'sessions') {
                if (typeof window.openCreateSessionModal === 'function') {
                    window.openCreateSessionModal();
                } else {
                    console.error('openCreateSessionModal is not available');
                }
            }
            return;
        }

        state.currentPage = page;
        renderCurrentPage();
    }

    function renderCurrentPage() {
        initCoachNavbar();
        hideAllSections();

        switch (state.currentPage) {
            case 'sessions':
                $('#sessionsListSection').removeClass('d-none');
                window.renderCoachSessionsSection();
                break;

            case 'session-averages':
                $('#sessionAveragesSection').removeClass('d-none');
                window.renderCoachAveragesSection();
                break;

            case 'session-details':
                $('#sessionDetailsSection').removeClass('d-none');
                window.renderCoachUploadSection();
                break;

            default:
                state.currentPage = 'sessions';
                $('#sessionsListSection').removeClass('d-none');
                window.renderCoachSessionsSection();
        }
    }

    function setSelectedSession(session) {
        state.selectedSession = session || null;
        state.selectedSessionId = session ? Number(session.id) : null;
    }

    window.coachPage = {
        setSelectedSession,

        getSelectedSession() {
            return state.selectedSession;
        },

        getSelectedSessionId() {
            return state.selectedSessionId;
        },

        goTo(page) {
            state.currentPage = page;
            renderCurrentPage();
        }
    };

    window.renderCoach = () => {
        state.currentPage = 'sessions';
        state.selectedSession = null;
        state.selectedSessionId = null;

        renderCoachShell();
        renderCurrentPage();
    };
})();