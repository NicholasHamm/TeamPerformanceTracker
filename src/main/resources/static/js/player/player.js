(() => {
    'use strict';

    const PERFORMANCE_URL = '/api/player/sessions';

    const playerNavbar = {
        items: [
            { id: 'sessions', label: 'My Sessions', icon: 'fa-solid fa-calendar-check' },
            { id: 'trends', label: 'Performance Trends', icon: 'fa-solid fa-chart-column' }
        ]
    };

    let playerSessionsTable = null;
    let currentPage = 'sessions';

	const renderPlayerView = () => {
	    const $container = $('#pageContent');
	    if (!$container.length) return;

	    $container.html(`
	        <div class="content-panel">
	            <div class="content-panel-header">
	                <div>
	                    <h2 class="content-panel-title mb-1">My Sessions</h2>
	                    <p class="content-panel-subtitle mb-0">
	                        Review your recorded training performance across all sessions
	                    </p>
	                </div>
	            </div>

	            <div class="table-shell">
	                <table id="playerSessionTable" class="table table-striped align-middle mb-0">
	                    <thead>
	                        <tr>
	                            <th>Datetime</th>
	                            <th>Type</th>
	                            <th>Duration</th>
	                            <th>Total Distance</th>
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

    const initPlayerNavbar = () => {
        const navItems = playerNavbar.items.map(item => ({
            ...item,
            active: item.id === currentPage
        }));

        window.renderNavbar({
            items: navItems,

            onNavigate: (page) => {
                currentPage = page;

                switch (page) {
                    case 'sessions':
                        renderPlayerView();
                        loadPlayerSessions();
                        initPlayerNavbar();
                        break;

                    case 'trends':
                        window.renderPlayerTrendsSection();
                        initPlayerNavbar();
                        break;
                }
            },

            onLogout: () => {
                clearToken();
                showLogin();
            }
        });
    };

    const loadPlayerSessions = () => {
        if ($.fn.DataTable.isDataTable('#playerSessionTable')) {
            $('#playerSessionTable').DataTable().destroy();
        }

        playerSessionsTable = $('#playerSessionTable').DataTable({
            destroy: true,
            ajax: {
                url: PERFORMANCE_URL,
                dataSrc: '',
                headers: authHeaders(),
                error: function (xhr) {
                    if (xhr.status === 401 || xhr.status === 403) {
                        handleUnauthorized();
                    }
                }
            },
            columns: [
                {
                    data: 'datetime',
                    render: function (data) {
                        return formatDateGMT(data);
                    }
                },
                { data: 'type' },
                { data: 'duration' },
                { data: 'totalDistance' },
                { data: 'highIntensityDistance' },
                { data: 'topSpeed' },
                { data: 'effortRating' }
            ]
        });
    };

    window.renderPlayer = () => {
        currentPage = 'sessions';
        renderPlayerView();
        loadPlayerSessions();
        initPlayerNavbar();
    };

})();