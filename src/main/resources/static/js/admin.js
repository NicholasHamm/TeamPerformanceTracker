const ADMIN_URL = `${API_BASE}/api/admin/users`;

let userTable = null;

function initializeUserTable() {
    userTable = $("#userTable").DataTable({
        ajax: {
            url: ADMIN_URL,
            dataSrc: "",
            beforeSend: function (xhr) {
                const token = getToken();
                if (token) {
                    xhr.setRequestHeader("Authorization", `Bearer ${token}`);
                }
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                }
            }
        },
        columns: [
            { data: "username" },
            { data: "firstName" },
            { data: "lastName" },
            { data: "role" }
        ]
    });
}

function loadAdminData() {
    if (!userTable) {
        initializeUserTable();
    } else {
        userTable.ajax.reload();
    }
}

$(document).ready(function () {
    $("#addUserBtn").on("click", function () {
        $("#userForm")[0].reset();
        $("#userModalLabel").text("Create New User");
        $("#userModal").modal("show");
    });

    $("#saveUserBtn").on("click", function () {
        const user = {
            username: $("#createUsername").val().trim(),
            password: $("#createPassword").val(),
            firstName: $("#createFirstName").val().trim(),
            lastName: $("#createLastName").val().trim(),
            role: $("#createRole").val()
        };

        $.ajax({
            url: ADMIN_URL,
            method: "POST",
            contentType: "application/json",
            headers: authHeaders(),
            data: JSON.stringify(user),
            success: function () {
                $("#userModal").modal("hide");
                if (userTable) {
                    userTable.ajax.reload();
                }
            },
            error: function (xhr) {
                console.log("STATUS:", xhr.status);
                console.log("RESPONSE:", xhr.responseText);
                console.log("JSON:", xhr.responseJSON);

                if (xhr.status === 401 || xhr.status === 403) {
                    handleUnauthorized();
                    return;
                }

                alert(xhr.responseJSON?.message || "Failed to save user");
            }
        });
    });
});