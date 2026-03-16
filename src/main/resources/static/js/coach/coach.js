(() => {
    'use strict';

    const coachNavbar = {
        items: [
            { id: 'sessions', label: 'Training Sessions', icon: 'fa-solid fa-calendar' },
            { id: 'create-session', label: 'New Session', icon: 'fa-solid fa-calendar-plus' },
            { id: 'session-averages', label: 'Session Averages', icon: 'fa-solid fa-chart-column' }
        ]
    };

    const state = {
        currentPage: 'sessions',
        selectedSessionId: null,
        selectedSession: null
    };

    function renderCoachShell() {
        $('#pageContent').html(`
            <div id="sessionsListSection" class="mt-3"></div>
            <div id="sessionDetailsSection" class="mt-3 d-none"></div>
            <div id="sessionAveragesSection" class="mt-3 d-none"></div>
        `);
    }

    function hideAllSections() {
        $('#sessionsListSection').addClass('d-none');
        $('#sessionDetailsSection').addClass('d-none');
        $('#sessionAveragesSection').addClass('d-none');
    }

	function initCoachNavbar() {
	    const visibleItems = coachNavbar.items
	        .filter(item =>
	            !(item.id === 'create-session' && state.currentPage !== 'sessions')
	        )
	        .map(item => ({
	            ...item,
	            active:
	                item.id === state.currentPage ||
	                (state.currentPage === 'session-details' && item.id === 'sessions')
	        }));

	    window.renderNavbar({
	        items: visibleItems,
	        onNavigate: handleNavigation,
	        onLogout: () => {
	            clearToken();
	            showLogin();
	        }
	    });
	}

    function handleNavigation(page) {
        if (page === 'create-session') {
            if (state.currentPage === 'sessions') {
                if (typeof window.openCreateSessionModal === 'function') {
                    window.openCreateSessionModal();
                } else {
                    console.error('openCreateSessionModal is not available');
                }
            }
            return;
        }

        state.currentPage = page;
        renderCurrentPage();
    }

    function renderCurrentPage() {
        initCoachNavbar();
        hideAllSections();

        switch (state.currentPage) {
            case 'sessions':
                $('#sessionsListSection').removeClass('d-none');
                window.renderCoachSessionsSection();
                break;

            case 'session-averages':
                $('#sessionAveragesSection').removeClass('d-none');
                window.renderCoachAveragesSection();
                break;

            case 'session-details':
                $('#sessionDetailsSection').removeClass('d-none');
                window.renderCoachUploadSection();
                break;

            default:
                state.currentPage = 'sessions';
                $('#sessionsListSection').removeClass('d-none');
                window.renderCoachSessionsSection();
        }
    }

    function setSelectedSession(session) {
        state.selectedSession = session || null;
        state.selectedSessionId = session ? Number(session.id) : null;
    }

    window.coachPage = {
        setSelectedSession,

        getSelectedSession() {
            return state.selectedSession;
        },

        getSelectedSessionId() {
            return state.selectedSessionId;
        },

        goTo(page) {
            state.currentPage = page;
            renderCurrentPage();
        }
    };

    window.renderCoach = () => {
        state.currentPage = 'sessions';
        state.selectedSession = null;
        state.selectedSessionId = null;

        renderCoachShell();
        renderCurrentPage();
    };
})();