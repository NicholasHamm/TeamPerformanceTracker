(() => {
    'use strict';

    const SESSION_URL = '/api/sessions';

    let metricChart = null;
    let selectedSessionId = null;
    let loadedAverages = null;

    const metricConfig = {
        totalDistance: {
            label: 'Average Total Distance',
            unit: 'm'
        },
        distancePerMin: {
            label: 'Average Distance / Min',
            unit: 'm/min'
        },
        highIntensityDistance: {
            label: 'Average High Intensity Distance',
            unit: 'm'
        },
        topSpeed: {
            label: 'Average Top Speed',
            unit: 'm/s'
        },
        effortRating: {
            label: 'Average Effort Rating',
            unit: ''
        }
    };

    const renderCoachAveragesSection = () => {
		const $container = $('#sessionAveragesSection');
        if (!$container.length) return;

        $container.html(`
            <div class="mt-3">
                <h2 class="mb-3">Session Averages</h2>

                <div id="coachAveragesMsg" class="alert d-none d-flex align-items-center gap-2">
                    <i class="bi fs-5 flex-shrink-0"></i>
                    <div class="msg-text"></div>
                </div>

                <div class="row g-3 align-items-end mb-4">
                    <div class="col-md-8">
                        <label for="averageSessionSelect" class="form-label">Select Session</label>
                        <select id="averageSessionSelect" class="form-control">
                            <option value="">Select session</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <button class="btn btn-primary w-100" id="loadSessionAveragesBtn">Search</button>
                    </div>
                </div>

                <div id="sessionAverageCards" class="row g-3 d-none">
                    <div class="col-md-4">
                        <div class="card metric-card h-100" data-metric="totalDistance">
                            <div class="card-body text-center">
                                <h6 class="text-muted">Average Total Distance</h6>
                                <h3 id="avgTotalDistance">0</h3>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="card metric-card h-100" data-metric="distancePerMin">
                            <div class="card-body text-center">
                                <h6 class="text-muted">Average Distance / Min</h6>
                                <h3 id="avgDistancePerMin">0</h3>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="card metric-card h-100" data-metric="highIntensityDistance">
                            <div class="card-body text-center">
                                <h6 class="text-muted">Average High Intensity Distance</h6>
                                <h3 id="avgHighIntensityDistance">0</h3>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="card metric-card h-100" data-metric="topSpeed">
                            <div class="card-body text-center">
                                <h6 class="text-muted">Average Top Speed</h6>
                                <h3 id="avgTopSpeed">0</h3>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="card metric-card h-100" data-metric="effortRating">
                            <div class="card-body text-center">
                                <h6 class="text-muted">Average Effort Rating</h6>
                                <h3 id="avgEffortRating">0</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="metricChartModal" tabindex="-1" aria-labelledby="metricChartModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="metricChartModalLabel">Metric Breakdown</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div style="height: 400px;">
                                <canvas id="metricBreakdownChart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `);

        loadSessionsIntoDropdown();
    };

    const loadSessionsIntoDropdown = () => {
        $.ajax({
            type: 'GET',
            url: SESSION_URL,
            headers: authHeaders(),
            success: function (sessions) {
                const $select = $('#averageSessionSelect');
                $select.empty();
                $select.append('<option value="">Select session</option>');

                sessions.forEach(session => {
                    const label = `${formatDateGMT(session.datetime)} - ${session.type}`;
                    $select.append(`<option value="${session.id}">${label}</option>`);
                });
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                }
            }
        });
    };

    const loadSessionAverages = () => {
        const sessionId = Number($('#averageSessionSelect').val());
        const msgBox = document.getElementById('coachAveragesMsg');

        if (!sessionId) {
            showMsg(msgBox, 'Please select a session', 'warning');
            return;
        }

        hideMsg(msgBox);
        selectedSessionId = sessionId;

        $.ajax({
            type: 'GET',
            url: `${SESSION_URL}/${sessionId}/averages`,
            headers: authHeaders(),
            success: function (data) {
                loadedAverages = data;

                $('#avgTotalDistance').text(data.averageTotalDistance?.toFixed(2) ?? '0');
                $('#avgDistancePerMin').text(data.averageDistancePerMin?.toFixed(2) ?? '0');
                $('#avgHighIntensityDistance').text(data.averageHighIntensityDistance?.toFixed(2) ?? '0');
                $('#avgTopSpeed').text(data.averageTopSpeed?.toFixed(2) ?? '0');
                $('#avgEffortRating').text(data.averageEffortRating?.toFixed(2) ?? '0');

                $('#sessionAverageCards').removeClass('d-none');
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }

                const message = extractCoachAverageError(xhr, 'Failed to load session averages');
                showMsg(msgBox, message, 'danger');
                $('#sessionAverageCards').addClass('d-none');
            }
        });
    };

    const openMetricModal = (metricKey) => {
        if (!selectedSessionId || !metricConfig[metricKey]) return;

        $.ajax({
            type: 'GET',
            url: `${SESSION_URL}/${selectedSessionId}/metrics/${metricKey}`,
            headers: authHeaders(),
            success: function (data) {
                renderMetricBreakdownChart(metricKey, data);
                $('#metricChartModal').modal('show');
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                }
            }
        });
    };

    const renderMetricBreakdownChart = (metricKey, data) => {
        const config = metricConfig[metricKey];
        const labels = data.map(item => item.playerName);
        const values = data.map(item => item.value);

        if (metricChart) {
            metricChart.destroy();
        }

        $('#metricChartModalLabel').text(`${config.label} by Player`);

        metricChart = new Chart(document.getElementById('metricBreakdownChart'), {
            type: 'bar',
            data: {
                labels,
                datasets: [{
                    label: `${config.label}${config.unit ? ` (${config.unit})` : ''}`,
                    data: values,					
					backgroundColor: labels.map(() => getRandomColor()), 
	                borderColor: 'rgba(0, 0, 0, 0.1)',
	                borderWidth: 1
	            }]
	        },
	        options: {
	            responsive: true,
	            maintainAspectRatio: false,
				plugins: {
			        legend: {
			            display: false
			        }
			    },
	            scales: {
	                x: { ticks: { autoSkip: false } },
	                y: { beginAtZero: true }
	            }
	        }
        });
    };
	
	const getRandomColor = () => {
	  const letters = '0123456789ABCDEF';
	  let color = '#';
	  for (let i = 0; i < 6; i++) {
	    color += letters[Math.floor(Math.random() * 16)];
	  }
	  return color;
	};

    const extractCoachAverageError = (xhr, fallbackMessage) => {
        if (xhr.responseJSON?.message) return xhr.responseJSON.message;
        if (xhr.responseJSON?.error) return xhr.responseJSON.error;
        if (typeof xhr.responseText === 'string' && xhr.responseText.trim().length > 0) {
            return xhr.responseText;
        }
        return fallbackMessage;
    };

	window.renderCoachAveragesSection = () => {
	    renderCoachAveragesSection();
	};
		
    $(document).on('click', '#loadSessionAveragesBtn', function () {
        loadSessionAverages();
    });

    $(document).on('click', '.metric-card', function () {
        const metricKey = $(this).data('metric');
        openMetricModal(metricKey);
    });
})();