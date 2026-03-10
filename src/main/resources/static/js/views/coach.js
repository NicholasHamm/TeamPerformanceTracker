import { authHeaders, handleUnauthorized } from "../app.js";
import { showMsg, hideMsg } from "../util/showMsg.js";

const SESSION_URL = "/api/sessions";

let sessionsTable = null;
let performanceTable = null;
let selectedSessionId = null;

export function render(container) {
    if (!container) return;

    container.innerHTML = `
        <div class="mt-3">
            <button class="btn btn-primary mb-3" id="addSessionBtn">Create New Training Session</button>

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
        </div>

        <div id="sessionDetailsSection" class="mt-4 d-none">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 id="selectedSessionTitle" class="mb-0">Session Player Performance</h4>
                <button class="btn btn-success" id="addPerformanceBtn">Add Player Data</button>
            </div>

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
        </div>

        <!-- Create Session Modal -->
        <div class="modal fade" id="sessionModal" tabindex="-1" aria-labelledby="sessionModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="sessionModalLabel">Create New Training Session</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
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
                                    <option value="GYM">Gym</option>
                                    <option value="PITCH">Pitch</option>
                                    <option value="TRACK">Track</option>
                                    <option value="RECOVERY">Recovery</option>
                                    <option value="OTHER">Other</option>
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

        <!-- Add Player Performance Modal -->
        <div class="modal fade" id="performanceModal" tabindex="-1" aria-labelledby="performanceModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="performanceModalLabel">Add Player Data</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
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
                                <label for="distancePerMin" class="form-label">Distance Per Minute</label>
                                <input type="number" min="0" step="0.01" class="form-control" id="distancePerMin" required>
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
    `;

    bindEvents();
    loadCoachData();
}

function bindEvents() {
    bindSessionEvents();
    bindPerformanceEvents();
}

function bindSessionEvents() {
    $("#addSessionBtn").off("click").on("click", function () {
        const form = $("#sessionForm")[0];
        if (form) form.reset();

        hideMsg(document.getElementById("createSessionError"));
        $("#sessionModalLabel").text("Create New Training Session");
        $("#sessionModal").modal("show");
    });

    $("#saveSessionBtn").off("click").on("click", function () {
        const trainingSession = {
            datetime: $("#createDatetime").val(),
            type: $("#createType").val(),
            duration: Number($("#createDuration").val())
        };

        $.ajax({
            url: SESSION_URL,
            method: "POST",
            contentType: "application/json",
            headers: authHeaders(),
            data: JSON.stringify(trainingSession),
            success: function () {
                $("#sessionModal").modal("hide");
                if (sessionsTable) {
                    sessionsTable.ajax.reload();
                }
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }

                const message = extractErrorMessage(xhr, "Failed to save training session");
                showMsg(document.getElementById("createSessionError"), message, "danger");
            }
        });
    });

    $(document)
        .off("click", ".manage-session-btn")
        .on("click", ".manage-session-btn", function () {
            selectedSessionId = Number($(this).data("session-id"));
            $("#sessionDetailsSection").removeClass("d-none");
            $("#selectedSessionTitle").text(`Session ${selectedSessionId} - Player Performance`);
            loadPerformanceTable(selectedSessionId);
        });
}

function bindPerformanceEvents() {
    $("#addPerformanceBtn").off("click").on("click", function () {
        if (!selectedSessionId) return;

        const form = $("#performanceForm")[0];
        if (form) form.reset();

        hideMsg(document.getElementById("createPerformanceError"));
        loadPlayersIntoSelect();
        $("#performanceModal").modal("show");
    });

    $("#savePerformanceBtn").off("click").on("click", function () {
        if (!selectedSessionId) return;

        const payload = {
            playerId: Number($("#performancePlayer").val()),
            totalDistance: Number($("#totalDistance").val()),
            distancePerMin: Number($("#distancePerMin").val()),
            highIntensityDistance: Number($("#highIntensityDistance").val()),
            topSpeed: Number($("#topSpeed").val()),
            effortRating: Number($("#effortRating").val())
        };

        $.ajax({
            url: `${SESSION_URL}/${selectedSessionId}/performance`,
            method: "POST",
            contentType: "application/json",
            headers: authHeaders(),
            data: JSON.stringify(payload),
            success: function () {
                $("#performanceModal").modal("hide");
                if (performanceTable) {
                    performanceTable.ajax.reload();
                }
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }

                const message = extractErrorMessage(xhr, "Failed to upload player data");
                showMsg(document.getElementById("createPerformanceError"), message, "danger");
            }
        });
    });
}

function initSessionTable() {
    sessionsTable = $("#sessionTable").DataTable({
        destroy: true,
        ajax: {
            url: SESSION_URL,
            dataSrc: "",
            headers: authHeaders(),
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                }
            }
        },
        columns: [
            { data: "id" },
            { data: "datetime" },
            { data: "type" },
            { data: "duration" },
            {
                data: null,
                orderable: false,
                searchable: false,
                render: function (data, type, row) {
                    return `
                        <button
                            type="button"
                            class="btn btn-sm btn-outline-primary manage-session-btn"
                            data-session-id="${row.id}">
                            Manage Players
                        </button>
                    `;
                }
            }
        ]
    });
}

function loadPerformanceTable(sessionId) {
    if ($.fn.DataTable.isDataTable("#performanceTable")) {
        $("#performanceTable").DataTable().destroy();
    }

    performanceTable = $("#performanceTable").DataTable({
        destroy: true,
        ajax: {
            url: `${SESSION_URL}/${sessionId}/performance`,
            dataSrc: "",
            headers: authHeaders(),
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                }
            }
        },
        columns: [
            { data: "playerName" },
            { data: "totalDistance" },
            { data: "distancePerMin" },
            { data: "highIntensityDistance" },
            { data: "topSpeed" },
            { data: "effortRating" }
        ]
    });
}

function loadPlayersIntoSelect() {
    if (!selectedSessionId) return;

    $.ajax({
        url: `${SESSION_URL}/${selectedSessionId}/available`,
        method: "GET",
        headers: authHeaders(),
        success: function (players) {
            const select = $("#performancePlayer");
            select.empty();
            select.append(`<option value="">Select player</option>`);

            players.forEach(player => {
                const fullName = `${player.firstName} ${player.lastName}`;
                select.append(`<option value="${player.id}">${fullName}</option>`);
            });
        },
        error: function (xhr) {
            if (xhr.status === 401 || xhr.status === 403) {
                handleUnauthorized();
            }
        }
    });
}

function loadCoachData() {
    if ($.fn.DataTable.isDataTable("#sessionTable")) {
        $("#sessionTable").DataTable().destroy();
    }
    initSessionTable();
}

function extractErrorMessage(xhr, fallbackMessage) {
    if (xhr.responseJSON?.error) return xhr.responseJSON.error;
    if (xhr.responseJSON?.message) return xhr.responseJSON.message;
    if (typeof xhr.responseText === "string" && xhr.responseText.trim().length > 0) {
        return xhr.responseText;
    }
    return fallbackMessage;
}