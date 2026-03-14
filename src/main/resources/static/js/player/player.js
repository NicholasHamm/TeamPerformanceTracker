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

    const renderPlayerView = () => {
        const $container = $('#pageContent');

        if (!$container.length) return;

        $container.html(`
            <div class="mt-3">
                <h2 class="mb-3">My Sessions</h2>

                <table id="playerSessionTable" class="table table-striped">
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
        `);
    };

    const initPlayerNavbar = () => {
        window.renderNavbar({
            items: playerNavbar.items,

            onNavigate: (page) => {
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
                { data: 'datetime' },
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
        renderPlayerView();
        loadPlayerSessions();
        initPlayerNavbar();
    };

})();