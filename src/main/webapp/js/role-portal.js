/**
 * Role Portal Engine for SmartCity360 (Chain of Responsibility Pattern)
 * Powers Local Staff, Supervisor, City Manager, and Emergency Director portals.
 */

const API_BASE = (function getApiBase() {
    const path = window.location.pathname;
    const base = path.substring(0, path.lastIndexOf('/'));
    return base ? `${base}/api` : '/api';
})();

let portalComplaints = [];
let pendingEscalateId = null;

async function fetchPortalComplaints() {
    if (!window.ROLE_CONFIG) return;
    const handlerId = window.ROLE_CONFIG.handlerId;

    try {
        const response = await fetch(`${API_BASE}/complaints?handlerId=${handlerId}`, {
            headers: { 'Accept': 'application/json' }
        });

        if (response.ok) {
            const data = await response.json();
            portalComplaints = data.map(item => ({
                id: item.complaintCode || ('C-' + item.id),
                dbId: item.id,
                category: item.category,
                description: item.description,
                location: item.location || 'Not specified',
                severity: item.severity,
                status: item.status || 'pending',
                handler: item.handlerName || window.ROLE_CONFIG.roleName,
                submittedAt: item.submittedAt || new Date().toISOString(),
                handledAt: item.handledAt || null
            }));
            renderPortal();
        }
    } catch (err) {
        console.error('Error fetching role complaints:', err);
    }
}

function renderPortal() {
    const container = document.getElementById('complaintQueue');
    const badgeActive = document.getElementById('statActive');
    const badgeHandled = document.getElementById('statHandled');
    const badgeTotal = document.getElementById('statTotal');

    const activeList = portalComplaints.filter(c => c.status !== 'handled' && c.status !== 'closed');
    const handledList = portalComplaints.filter(c => c.status === 'handled');

    if (badgeActive) badgeActive.textContent = activeList.length;
    if (badgeHandled) badgeHandled.textContent = handledList.length;
    if (badgeTotal) badgeTotal.textContent = portalComplaints.length;

    if (!container) return;

    if (portalComplaints.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <div class="empty-state-title">No complaints in your queue</div>
                <div class="empty-state-sub">New complaints within your severity threshold (${window.ROLE_CONFIG.severityRange}) will appear here in real time.</div>
            </div>`;
        return;
    }

    container.innerHTML = portalComplaints.map(c => {
        const isHandled = c.status === 'handled' || c.status === 'closed';
        const isEscalated = c.status === 'escalated';
        const sevClass = c.severity <= 3 ? 'low' : c.severity <= 6 ? 'med' : c.severity <= 8 ? 'high' : 'crit';
        const statusBadge = isHandled ? 'Resolved' : isEscalated ? '⚡ Escalated to You' : 'Pending Action';
        const statusBadgeClass = isHandled ? 'badge-handled' : isEscalated ? 'badge-escalated' : 'badge-pending';
        const icon = getCategoryIcon(c.category);

        const canEscalate = window.ROLE_CONFIG.nextRole !== null && !isHandled;

        return `
            <div class="card complaint-card ${isHandled ? 'is-handled' : ''}">
                <div class="card-header">
                    <div class="card-title-group">
                        <span class="category-icon">${icon}</span>
                        <div>
                            <div class="card-title">${escapeHtml(c.category)}</div>
                            <div class="card-code">${escapeHtml(c.id)} · Location: ${escapeHtml(c.location)}</div>
                        </div>
                    </div>
                    <div class="card-badges">
                        <span class="severity-badge ${sevClass}">Severity ${c.severity}/10</span>
                        <span class="status-badge ${statusBadgeClass}">${statusBadge}</span>
                    </div>
                </div>

                <div class="card-body">
                    <p class="description-text">${escapeHtml(c.description)}</p>
                </div>

                <div class="card-footer">
                    <div class="meta-info">
                        <span>Submitted ${formatTime(c.submittedAt)}</span>
                        ${c.handledAt ? `<span>· Resolved ${formatTime(c.handledAt)}</span>` : ''}
                    </div>

                    ${!isHandled ? `
                        <div class="action-btn-group" style="display:flex; gap:6px; flex-wrap:wrap; margin-top:8px;">
                            <button class="btn btn-resolve" onclick="resolveComplaint(${c.dbId}, '${escapeHtml(c.id)}')">
                                Mark Resolved
                            </button>
                            ${canEscalate ? `
                                <button class="btn btn-escalate" onclick="openEscalateModal(${c.dbId}, '${escapeHtml(c.id)}', '${window.ROLE_CONFIG.nextRole}')">
                                    Escalate to ${window.ROLE_CONFIG.nextRole}
                                </button>
                            ` : ''}
                            <button class="btn btn-delete" style="background:var(--danger-soft); color:var(--danger); border:1px solid rgba(248,113,113,0.25); padding:6px 12px; border-radius:var(--r-sm); font-size:0.75rem; font-weight:500; cursor:pointer;" onclick="deleteComplaint(${c.dbId}, '${escapeHtml(c.id)}')">
                                Delete
                            </button>
                        </div>
                    ` : `
                        <div class="resolved-note">
                            Resolved by ${window.ROLE_CONFIG.roleName}
                        </div>
                    `}
                </div>
            </div>
        `;
    }).join('');
}

async function deleteComplaint(dbId, code) {
    if (!confirm(`Are you sure you want to permanently delete complaint ${code} from MySQL database?`)) return;
    try {
        const response = await fetch(`${API_BASE}/complaints?action=delete&id=${dbId}`, {
            method: 'POST',
            headers: { 'Accept': 'application/json' }
        });
        if (response.ok) {
            showToast(`Complaint ${code} deleted from database`, 'success');
            await fetchPortalComplaints();
        } else {
            showToast('Failed to delete complaint', 'error');
        }
    } catch (err) {
        showToast('Connection error', 'error');
    }
}

async function resolveComplaint(dbId, code) {
    if (!confirm(`Mark complaint ${code} as resolved?`)) return;

    try {
        const response = await fetch(`${API_BASE}/complaints?action=handle&id=${dbId}`, {
            method: 'POST',
            headers: { 'Accept': 'application/json' }
        });

        if (response.ok) {
            showToast(`Complaint ${code} resolved successfully!`, 'success');
            await fetchPortalComplaints();
        } else {
            showToast('Failed to resolve complaint', 'error');
        }
    } catch (err) {
        showToast('Connection error', 'error');
    }
}

function openEscalateModal(dbId, code, nextRole) {
    pendingEscalateId = dbId;
    document.getElementById('escalateTargetName').textContent = nextRole;
    document.getElementById('escalateCode').textContent = code;
    document.getElementById('escalateReason').value = '';
    document.getElementById('escalateModal').classList.add('show');
    setTimeout(() => document.getElementById('escalateReason').focus(), 100);
}

function closeEscalateModal() {
    document.getElementById('escalateModal').classList.remove('show');
    pendingEscalateId = null;
}

async function confirmEscalation() {
    if (!pendingEscalateId) return;
    const reason = document.getElementById('escalateReason').value.trim();

    try {
        const response = await fetch(`${API_BASE}/complaints?action=escalate&id=${pendingEscalateId}&reason=${encodeURIComponent(reason)}`, {
            method: 'POST',
            headers: { 'Accept': 'application/json' }
        });

        if (response.ok) {
            const data = await response.json();
            closeEscalateModal();
            showToast(`Complaint escalated to ${data.newHandler || window.ROLE_CONFIG.nextRole}!`, 'success');
            await fetchPortalComplaints();
        } else {
            showToast('Escalation failed', 'error');
        }
    } catch (err) {
        showToast('Connection error', 'error');
    }
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
    if (!toast) return;
    const icon = type === 'success' ? '✅' : '⚠️';
    toast.innerHTML = `<span>${icon}</span> ${message}`;
    toast.className = `toast ${type} show`;
    clearTimeout(toast._timer);
    toast._timer = setTimeout(() => toast.classList.remove('show'), 3500);
}

// Initial fetch & real-time poll every 3s
document.addEventListener('DOMContentLoaded', () => {
    fetchPortalComplaints();
    setInterval(fetchPortalComplaints, 3000);
});
