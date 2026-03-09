import { authHeaders, handleUnauthorized } from "../app.js";
import { showMsg, hideMsg } from "../util/showMsg.js";

const PLAYERS_URL = `/api/players`;
const SESSION_URL = `/api/sessions`;

let sessionsTable = null;

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
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>

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
                                    <option value="OTHER">OTHER</option>
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
    `;

    bindEvents();
    loadCoachData();
}

function bindEvents() {
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

                let message = "Failed to save training session";

                if (xhr.responseJSON?.message) {
                    message = xhr.responseJSON.message;
                } else if (xhr.responseText) {
                    message = xhr.responseText;
                }

                const msgBox = document.getElementById("createSessionError");
                showMsg(msgBox, message, "danger");
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
            { data: "duration" }
        ]
    });
}

function loadCoachData() {
    if ($.fn.DataTable.isDataTable("#sessionTable")) {
        $("#sessionTable").DataTable().destroy();
    }
    initSessionTable();
}