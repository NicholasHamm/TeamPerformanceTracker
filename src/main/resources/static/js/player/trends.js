(() => {
    'use strict';

    const PERFORMANCE_URL = '/api/player/sessions';

    let allTrendData = [];
    let distanceChart = null;
    let speedChart = null;
    let effortChart = null;

    const renderTrendsView = () => {
        const $container = $('#pageContent');

        if (!$container.length) return;

        $container.html(`
            <div class="mt-3">
                <h2 class="mb-3">Performance Trends</h2>

                <div id="trendError" class="alert d-none d-flex align-items-center gap-2">
                    <i class="bi fs-5 flex-shrink-0"></i>
                    <div class="msg-text"></div>
                </div>

                <div id="noTrendDataMsg" class="alert alert-info d-none">
                    No trend data available
                </div>

                <div class="row g-3 mb-3">
                    <div class="col-md-4">
                        <label for="trendStartDate" class="form-label">Start Date</label>
                        <input type="date" id="trendStartDate" class="form-control">
                    </div>
                    <div class="col-md-4">
                        <label for="trendEndDate" class="form-label">End Date</label>
                        <input type="date" id="trendEndDate" class="form-control">
                    </div>
                    <div class="col-md-4 d-flex align-items-end">
                        <button class="btn btn-primary w-100" id="applyTrendFilterBtn">Apply Filter</button>
                    </div>
                </div>

                <div class="card mb-4" id="trendSummaryCard">
                    <div class="card-body text-center">
                        <h6 class="text-muted mb-2" id="trendSummaryLabel">Average Total Distance</h6>
                        <h3 class="mb-0" id="trendSummaryValue">0</h3>
                    </div>
                </div>

                <ul class="nav nav-tabs mb-3" id="trendTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="distance-tab"
                                data-bs-toggle="tab"
                                data-bs-target="#distance-pane"
                                type="button" role="tab">
                            Total Distance
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="speed-tab"
                                data-bs-toggle="tab"
                                data-bs-target="#speed-pane"
                                type="button" role="tab">
                            Top Speed
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="effort-tab"
                                data-bs-toggle="tab"
                                data-bs-target="#effort-pane"
                                type="button" role="tab">
                            Effort Rating
                        </button>
                    </li>
                </ul>

                <div class="tab-content" id="trendTabContent">
                    <div class="tab-pane fade show active" id="distance-pane" role="tabpanel">
                        <canvas id="distanceChart" height="120"></canvas>
                    </div>
                    <div class="tab-pane fade" id="speed-pane" role="tabpanel">
                        <canvas id="speedChart" height="120"></canvas>
                    </div>
                    <div class="tab-pane fade" id="effort-pane" role="tabpanel">
                        <canvas id="effortChart" height="120"></canvas>
                    </div>
                </div>
            </div>
        `);
		
		$('#trendStartDate').val(getTodayDate());
		$('#trendEndDate').val(getTodayDate());
    };

    const calculateAverage = (data, field) => {
        if (!data || data.length === 0) return 0;
        const total = data.reduce((sum, item) => sum + Number(item[field] || 0), 0);
        return (total / data.length).toFixed(2);
    };

    const updateTrendSummary = (label, value) => {
        $('#trendSummaryLabel').text(label);
        $('#trendSummaryValue').text(value);
    };

    const renderTrendCharts = (data) => {
        const labels = data.map(item => item.datetime.split('T')[0]);
        const totalDistance = data.map(item => item.totalDistance);
        const topSpeed = data.map(item => item.topSpeed);
        const effortRating = data.map(item => item.effortRating);

        if (distanceChart) distanceChart.destroy();
        if (speedChart) speedChart.destroy();
        if (effortChart) effortChart.destroy();

        distanceChart = new Chart(document.getElementById('distanceChart'), {
            type: 'line',
            data: {
                labels,
                datasets: [{
                    label: 'Total Distance (m)',
                    data: totalDistance
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });

        speedChart = new Chart(document.getElementById('speedChart'), {
            type: 'line',
            data: {
                labels,
                datasets: [{
                    label: 'Top Speed (m/s)',
                    data: topSpeed
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });

        effortChart = new Chart(document.getElementById('effortChart'), {
            type: 'bar',
            data: {
                labels,
                datasets: [{
                    label: 'Effort Rating',
                    data: effortRating
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });

        updateTrendSummary(
            'Average Total Distance',
            calculateAverage(data, 'totalDistance')
        );
    };

    const bindTrendTabEvents = (data) => {
        $('#distance-tab').off('shown.bs.tab').on('shown.bs.tab', function () {
            updateTrendSummary(
                'Average Total Distance',
                calculateAverage(data, 'totalDistance')
            );
        });

        $('#speed-tab').off('shown.bs.tab').on('shown.bs.tab', function () {
            updateTrendSummary(
                'Average Top Speed',
                calculateAverage(data, 'topSpeed')
            );
        });

        $('#effort-tab').off('shown.bs.tab').on('shown.bs.tab', function () {
            updateTrendSummary(
                'Average Effort Rating',
                calculateAverage(data, 'effortRating')
            );
        });
    };

    const applyTrendFilter = () => {
        const start = $('#trendStartDate').val();
        const end = $('#trendEndDate').val();

        if (start && end && new Date(start) > new Date(end)) {
            showMsg(document.getElementById('trendError'), 'Invalid Date Range', 'danger');
            return;
        }

        hideMsg(document.getElementById('trendError'));

        const filtered = allTrendData.filter(item => {
            const sessionDate = item.datetime.split('T')[0];

            if (start && sessionDate < start) return false;
            if (end && sessionDate > end) return false;

            return true;
        });

        if (!filtered.length) {
            $('#noTrendDataMsg').removeClass('d-none');
            $('#trendSummaryCard').addClass('d-none');
            $('#trendTabs').addClass('d-none');
            $('#trendTabContent').addClass('d-none');
            return;
        }

        $('#noTrendDataMsg').addClass('d-none');
        $('#trendSummaryCard').removeClass('d-none');
        $('#trendTabs').removeClass('d-none');
        $('#trendTabContent').removeClass('d-none');

        renderTrendCharts(filtered);
        bindTrendTabEvents(filtered);
    };

    const loadTrendData = () => {
        $.ajax({
            type: 'GET',
            url: PERFORMANCE_URL,
            headers: authHeaders(),
            success: function (data) {
                allTrendData = data || [];

                if (!allTrendData.length) {
                    $('#noTrendDataMsg').removeClass('d-none');
                    $('#trendSummaryCard').addClass('d-none');
                    $('#trendTabs').addClass('d-none');
                    $('#trendTabContent').addClass('d-none');
                    return;
                }

                $('#noTrendDataMsg').addClass('d-none');
                $('#trendSummaryCard').removeClass('d-none');
                $('#trendTabs').removeClass('d-none');
                $('#trendTabContent').removeClass('d-none');

                renderTrendCharts(allTrendData);
                bindTrendTabEvents(allTrendData);
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                }
            }
        });
    };

    window.renderPlayerTrendsSection = () => {
        renderTrendsView();
        loadTrendData();
    };

    $(document).on('click', '#applyTrendFilterBtn', function () {
        applyTrendFilter();
    });
})();