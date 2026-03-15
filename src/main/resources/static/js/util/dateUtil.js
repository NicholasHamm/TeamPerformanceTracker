(() => {
    'use strict';

    const formatDateGMT = (value) => {
        if (!value) return '';

        const date = new Date(value);

        return new Intl.DateTimeFormat('en-GB', {
            timeZone: 'Etc/Greenwich',
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        }).format(date);
    };

    const getDefaultSessionDateTime = () => {
        const now = new Date();
        now.setHours(now.getHours() - 1);
        now.setMinutes(0, 0, 0);

        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');

        return `${year}-${month}-${day}T${hours}:${minutes}`;
    };
	
	const getTodayDate = () => {
	    const now = new Date();

	    const year = now.getFullYear();
	    const month = String(now.getMonth() + 1).padStart(2, '0');
	    const day = String(now.getDate()).padStart(2, '0');

	    return `${year}-${month}-${day}`;
	};

	window.getTodayDate = getTodayDate;
    window.formatDateGMT = formatDateGMT;
    window.getDefaultSessionDateTime = getDefaultSessionDateTime;

})();