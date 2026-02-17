$(document).ready(function () {
    let userTable;
    let apiUrl = 'http://localhost:8081/api/admin/users';
	 // Initialize DataTable
    function initializeTable() {
        userTable = $('#userTable').DataTable({ajax: {
                url: apiUrl,
                dataSrc: '',
            },
            columns: [
                { data: 'username' },
                { data: 'firstName' },
                { data: 'lastName' },
                { data: 'role' },
            ],
        });
    }

    initializeTable();
	
	// Open modal for adding new wine
	    $('#addUserBtn').click(function () {
	        $('#userForm')[0].reset();
	        $('#userModalLabel').text('Add New User');
	        $('#userModal').modal('show');
	    });

	    // Save (Create/Update) wine
	    $('#saveUserBtn').click(function () {
	        const user = {
	            username: $('#username').val(),
	            password: $('#password').val(),
	            firstname: $('#firstname').val(),
	            lastname: $('#lastname').val(),
	            role: $('#role').val(),
	        };
			
            // Create
            $.ajax({
                url: apiUrl,
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(user),
                success: function () {
                    $('#userModal').modal('hide');
                },
				error: function (xhr) {
				    console.log("STATUS:", xhr.status);
				    console.log("RESPONSE:", xhr.responseText); 
				    console.log("JSON:", xhr.responseJSON);
				}
            });
			$('#userModal').on('hidden.bs.modal', function () {
			    userTable.ajax.reload();
			});
	    });
	    
});