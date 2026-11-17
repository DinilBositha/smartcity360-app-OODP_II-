const API_BASE = (function getApiBase() {
    // Dynamically resolve API URL relative to application context
    const path = window.location.pathname;
    const base = path.substring(0, path.lastIndexOf('/'));
    return base ? `${base}/api` : '/api';
})();

let currentComplaints = [];

/**
 * Fetch complaints directly from smartcity360 Database via API
 */
async function fetchComplaints() {
    try {
        const response = await fetch(`${API_BASE}/complaints?userId=1`, {
            headers: { 'Accept': 'application/json' }
        });

        if (response.ok) {
            const data = await response.json();
            currentComplaints = data.map(item => ({
                id: item.complaintCode || ('C-' + item.id),
                dbId: item.id,
                category: item.category,
                description: item.description,
                location: item.location || '',
                severity: item.severity,
                status: item.status || 'pending',
                handler: item.handlerName || getHandlerForSeverity(item.severity),
                submittedAt: item.submittedAt || new Date().toISOString()
            }));
            renderComplaints();
            renderNearbyLights();
        }
    } catch (err) {
        console.error('Failed to fetch complaints from DB:', err);
    }
}

/**
 * Fetch announcements directly from smartcity360 Database via API
 */
async function fetchAnnouncements() {
    try {
        const response = await fetch(`${API_BASE}/announcements`, {
            headers: { 'Accept': 'application/json' }
        });

        if (response.ok) {
            const data = await response.json();
            renderAnnouncementsList(data);
            return;
        }
    } catch (err) {
        console.error('Failed to fetch announcements from DB:', err);
    }
    renderAnnouncementsList([]);
}

/* ============================================================
   COMPLAINT MODAL
   ============================================================ */
function openComplaintModal(presetCategory) {
    document.getElementById('complaintModal').classList.add('show');
    document.getElementById('cmDescription').value = '';

    if (presetCategory) {
        document.getElementById('cmCategory').value = presetCategory;
        if (presetCategory === 'Street Lighting') {
            document.getElementById('cmDescription').value = 'Streetlight issue: ';
            document.getElementById('cmSeverity').value = 4;
        } else if (presetCategory === 'Public Safety') {
            document.getElementById('cmDescription').value = 'Power outage: ';
            document.getElementById('cmSeverity').value = 7;
        } else {
            document.getElementById('cmSeverity').value = 5;
        }
    } else {
        document.getElementById('cmSeverity').value = 5;
    }

    document.getElementById('cmLocation').value = '';
    updateSeverityLabel();
    setTimeout(() => document.getElementById('cmDescription').focus(), 100);
}

function closeComplaintModal() {
    document.getElementById('complaintModal').classList.remove('show');
}

function updateSeverityLabel() {
    const v = parseInt(document.getElementById('cmSeverity').value, 10);
    const label = v <= 3 ? 'Low' : v <= 6 ? 'Medium' : v <= 8 ? 'High' : 'Critical';
    document.getElementById('cmSeverityLabel').textContent = `${v} / 10 · ${label}`;
}

/* ============================================================
   SUBMIT COMPLAINT (Saves directly to smartcity360 Database)
   ============================================================ */
async function submitComplaint() {
    const category = document.getElementById('cmCategory').value;
    const description = document.getElementById('cmDescription').value.trim();
    const severity = parseInt(document.getElementById('cmSeverity').value, 10);
    const location = document.getElementById('cmLocation').value.trim();

    if (!description) {
        showToast('Please describe the issue', 'warning');
        return;
    }

    const payload = {
        userId: 1, // John Perera (citizen.john)
        category,
        description,
        location,
        severity
    };

    closeComplaintModal();

    try {
        const response = await fetch(`${API_BASE}/complaints`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const result = await response.json();
            const code = result.complaintCode || '';
            const handler = result.handler || getHandlerForSeverity(severity);
            showToast(`Complaint ${code} submitted to database — routed to ${handler}`, 'success');
            // Immediately refresh complaints from the database
            await fetchComplaints();
        } else {
            showToast('Failed to save complaint to database', 'warning');
        }
    } catch (e) {
        console.error('Error submitting complaint to database:', e);
        showToast('Database connection error', 'warning');
    }
}

function getHandlerForSeverity(sev) {
    if (sev <= 3) return 'Local Staff';
    if (sev <= 6) return 'Supervisor';
    if (sev <= 8) return 'City Manager';
    return 'Emergency Director';
}

/* ============================================================
   RENDER
   ============================================================ */
function renderComplaints() {
    const container = document.getElementById('myComplaints');
    const countEl = document.getElementById('complaintCount');
    const complaints = currentComplaints;

    const active = complaints.filter(c => c.status !== 'closed').length;
    countEl.textContent = `${active} Active`;

    if (complaints.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <div class="empty-state-text">You haven't submitted any complaints yet.</div>
            </div>`;
        return;
    }

    container.innerHTML = complaints.map(c => {
        const icon = getCategoryIcon(c.category);
        const statusClass = c.status || 'pending';
        const statusText = c.status === 'handled' ? 'Handled'
            : c.status === 'escalated' ? 'Escalated'
                : c.status === 'closed' ? 'Closed' : 'Pending';

        return `
            <div class="complaint-item">
                <div class="complaint-icon">${icon}</div>
                <div class="complaint-body">
                    <div class="complaint-title">${escapeHtml(c.category)} <span style="font-size: 0.75rem; color: #94a3b8; font-weight: normal;">(${escapeHtml(c.id)})</span></div>
                    <div class="complaint-desc">${escapeHtml(c.description)}</div>
                    <div class="complaint-meta">
                        <span class="complaint-status ${statusClass}">${statusText}</span>
                        <span>Severity ${c.severity}/10</span>
                        <span class="complaint-handler">→ ${escapeHtml(c.handler)}</span>
                        <span>${formatTime(c.submittedAt)}</span>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function renderNearbyLights() {
    const grid = document.getElementById('nearbyLights');
    const complaints = currentComplaints;
    const reportedCount = complaints.filter(c =>
        c.category === 'Street Lighting' && c.status !== 'handled' && c.status !== 'closed'
    ).length;

    grid.innerHTML = '';
    for (let i = 0; i < 40; i++) {
        const dot = document.createElement('div');
        let cls = 'light-dot';
        if (i < reportedCount) {
            cls += ' reported';
        } else if (Math.random() < 0.78) {
            cls += ' on';
        } else {
            cls += ' off';
        }
        dot.className = cls;
        dot.title = `Streetlight SL-${String(i + 1).padStart(3, '0')}`;
        grid.appendChild(dot);
    }
}

function renderAnnouncementsList(announcements) {
    const container = document.getElementById('announcementsList');
    if (!announcements || announcements.length === 0) {
        container.innerHTML = `
            <div class="empty-state" style="padding: 16px; color: #94a3b8; font-size: 0.85rem;">
                No active announcements at this time.
            </div>`;
        return;
    }

    container.innerHTML = announcements.map(a => `
        <div class="announcement">
            <div class="announcement-icon">${a.icon || '📢'}</div>
            <div class="announcement-body">
                <div class="announcement-title">${escapeHtml(a.title)}</div>
                <div class="announcement-text">${escapeHtml(a.body)}</div>
                <div class="announcement-time">${formatTime(a.publishedAt || new Date().toISOString())}</div>
            </div>
        </div>
    `).join('');
}

/* ============================================================
   HELPERS
   ============================================================ */
function getCategoryIcon(cat) {
    const icons = {
        'Street Lighting': '💡',
        'Waste Collection': '🗑️',
        'Water Supply': '💧',
        'Road Damage': '🛣️',
        'Public Safety': '🚨',
        'Noise Pollution': '🔊'
    };
    return icons[cat] || '📋';
}

function formatTime(iso) {
    if (!iso) return '';
    const diff = Date.now() - new Date(iso).getTime();
    if (diff < 60000) return 'just now';
    if (diff < 3600000) return Math.floor(diff / 60000) + 'm ago';
    if (diff < 86400000) return Math.floor(diff / 3600000) + 'h ago';
    return Math.floor(diff / 86400000) + 'd ago';
}

function escapeHtml(s) {
    if (!s) return '';
    const div = document.createElement('div');
    div.textContent = s;
    return div.innerHTML;
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    const icon = type === 'success' ? '✅' : '⚠️';
    toast.innerHTML = `<span>${icon}</span> ${message}`;
    toast.className = `toast ${type} show`;
    clearTimeout(toast._timer);
    toast._timer = setTimeout(() => toast.classList.remove('show'), 3500);
}

function scrollToComplaints() {
    document.getElementById('complaintsPanel').scrollIntoView({ behavior: 'smooth', block: 'center' });
}

function scrollToAnnouncements() {
    document.getElementById('announcementsPanel').scrollIntoView({ behavior: 'smooth', block: 'center' });
}

function scrollToNearby() {
    document.getElementById('nearbyPanel').scrollIntoView({ behavior: 'smooth', block: 'center' });
}

/* ============================================================
   INIT
   ============================================================ */
// Initial fetch from smartcity360 database
fetchComplaints();
fetchAnnouncements();

/* Poll for updates directly from database (every 3s) */
setInterval(fetchComplaints, 3000);

/* Close modal on Escape */
document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeComplaintModal();
});

/* Close modal on backdrop click */
document.getElementById('complaintModal').addEventListener('click', e => {
    if (e.target.id === 'complaintModal') closeComplaintModal();
});
