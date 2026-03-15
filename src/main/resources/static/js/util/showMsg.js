(() => {
    'use strict';

    const showMsg = (msgBox, message, type = 'info') => {
        if (!msgBox || !message?.length) return;

        const msgIcon = msgBox.querySelector('i');
        const msgText = msgBox.querySelector('.msg-text');

        if (!msgIcon || !msgText) return;

        msgBox.classList.remove(
            'd-none',
            'alert-success',
            'alert-danger',
            'alert-warning',
            'alert-info'
        );

        let alertClass = 'alert-info';
        let iconClass = 'bi-info-circle-fill';

        switch (type) {
            case 'success':
            case 'alert-success':
                alertClass = 'alert-success';
                iconClass = 'bi-check-circle-fill';
                break;

            case 'danger':
            case 'alert-danger':
                alertClass = 'alert-danger';
                iconClass = 'bi-exclamation-octagon-fill';
                break;

            case 'warning':
            case 'alert-warning':
                alertClass = 'alert-warning';
                iconClass = 'bi-exclamation-triangle-fill';
                break;
        }

        msgBox.classList.add(alertClass);
        msgIcon.className = `bi ${iconClass} fs-5 flex-shrink-0`;
        msgText.innerHTML = message;
        msgBox.classList.remove('d-none');
    };

    const hideMsg = (msgBox) => {
        if (!msgBox) return;
        msgBox.classList.add('d-none');
    };
	
	const extractErrorMessage = (xhr, fallbackMessage) => {
	    if (xhr.responseJSON?.message) return xhr.responseJSON.message;
	    if (xhr.responseJSON?.error) return xhr.responseJSON.error;
	    if (typeof xhr.responseText === 'string' && xhr.responseText.trim().length > 0) {
	        return xhr.responseText;
	    }
	    return fallbackMessage;
	};

    // expose globally so other scripts can use them
	window.extractErrorMessage = extractErrorMessage;
    window.showMsg = showMsg;
    window.hideMsg = hideMsg;

})();