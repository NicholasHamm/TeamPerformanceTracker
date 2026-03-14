(() => {
    'use strict';

    const PLAYER_SESSION_URL = '/api/player/sessions';
    let playerSessionsTable = null;

    const renderPlayer = () => {
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

    const loadPlayerSessions = () => {
        if ($.fn.DataTable.isDataTable('#playerSessionTable')) {
            $('#playerSessionTable').DataTable().destroy();
        }

        playerSessionsTable = $('#playerSessionTable').DataTable({
            destroy: true,
            ajax: {
                url: PLAYER_SESSION_URL,
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
        renderPlayer();
        loadPlayerSessions();
    };

})();