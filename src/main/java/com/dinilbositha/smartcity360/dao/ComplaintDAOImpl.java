package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.chain.ComplaintChain;
import com.dinilbositha.smartcity360.chain.ComplaintHandler;
import com.dinilbositha.smartcity360.dto.ComplaintDTO;
import com.dinilbositha.smartcity360.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComplaintDAOImpl implements ComplaintDAO {

    public static String getHandlerName(long handlerId) {
        ComplaintHandler h = ComplaintChain.getHandlerById(handlerId);
        return h != null ? h.getHandlerName() : "Local Staff";
    }

    @Override
    public boolean saveComplaint(ComplaintDTO complaint) {
        if (complaint.getComplaintCode() == null || complaint.getComplaintCode().trim().isEmpty()) {
            complaint.setComplaintCode("C-" + (1000 + new Random().nextInt(9000)));
        }
        if (complaint.getUserId() == null || complaint.getUserId() <= 0) {
            complaint.setUserId(1L); // Default to citizen.john
        }
        if (complaint.getStatus() == null || complaint.getStatus().trim().isEmpty()) {
            complaint.setStatus("pending");
        }

        // Apply Chain of Responsibility Pattern to determine handler
        ComplaintHandler assigned = ComplaintChain.route(complaint);
        long handlerId = assigned.getHandlerId();

        String insertSql = "INSERT INTO complaints (complaint_code, user_id, category, description, location, severity, status, assigned_handler_id) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, complaint.getComplaintCode());
            stmt.setLong(2, complaint.getUserId());
            stmt.setString(3, complaint.getCategory());
            stmt.setString(4, complaint.getDescription());
            stmt.setString(5, complaint.getLocation() != null ? complaint.getLocation() : "");
            stmt.setInt(6, complaint.getSeverity());
            stmt.setString(7, complaint.getStatus());
            stmt.setLong(8, handlerId);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                rs = stmt.getGeneratedKeys();
                long complaintId = 0;
                if (rs.next()) {
                    complaintId = rs.getLong(1);
                    complaint.setId(complaintId);
                }

                if (complaintId > 0) {
                    // Log initial routing step
                    String logSql = "INSERT INTO complaint_routing_log (complaint_id, handler_id, action, notes) VALUES (?, ?, 'received', ?)";
                    try (PreparedStatement logStmt = conn.prepareStatement(logSql)) {
                        logStmt.setLong(1, complaintId);
                        logStmt.setLong(2, handlerId);
                        logStmt.setString(3, "Initial assignment to " + assigned.getHandlerName() + " (Severity " + complaint.getSeverity() + ")");
                        logStmt.executeUpdate();
                    }

                    // Log activity
                    String actSql = "INSERT INTO activity_log (activity_type, icon, message, severity, related_table, related_id) VALUES ('complaint', '', ?, 'info', 'complaints', ?)";
                    try (PreparedStatement actStmt = conn.prepareStatement(actSql)) {
                        actStmt.setString(1, "New complaint " + complaint.getComplaintCode() + " (" + complaint.getCategory() + ") routed to " + assigned.getHandlerName());
                        actStmt.setLong(2, complaintId);
                        actStmt.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ignored) {}
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                DBConnection.getInstance().close(conn);
            }
        }
        return false;
    }

    @Override
    public List<ComplaintDTO> getAllComplaints() {
        List<ComplaintDTO> list = new ArrayList<>();
        String sql = "SELECT c.id, c.complaint_code, c.user_id, c.category, c.description, c.location, " +
                     "c.severity, c.status, c.assigned_handler_id, c.submitted_at, c.handled_at, " +
                     "h.name AS handler_name " +
                     "FROM complaints c " +
                     "LEFT JOIN complaint_handlers h ON c.assigned_handler_id = h.id " +
                     "ORDER BY c.submitted_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ComplaintDTO> getComplaintsByUserId(long userId) {
        List<ComplaintDTO> list = new ArrayList<>();
        String sql = "SELECT c.id, c.complaint_code, c.user_id, c.category, c.description, c.location, " +
                     "c.severity, c.status, c.assigned_handler_id, c.submitted_at, c.handled_at, " +
                     "h.name AS handler_name " +
                     "FROM complaints c " +
                     "LEFT JOIN complaint_handlers h ON c.assigned_handler_id = h.id " +
                     "WHERE c.user_id = ? " +
                     "ORDER BY c.submitted_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ComplaintDTO> getComplaintsByHandlerId(long handlerId) {
        List<ComplaintDTO> list = new ArrayList<>();
        String sql = "SELECT c.id, c.complaint_code, c.user_id, c.category, c.description, c.location, " +
                     "c.severity, c.status, c.assigned_handler_id, c.submitted_at, c.handled_at, " +
                     "h.name AS handler_name " +
                     "FROM complaints c " +
                     "LEFT JOIN complaint_handlers h ON c.assigned_handler_id = h.id " +
                     "WHERE c.assigned_handler_id = ? " +
                     "ORDER BY c.submitted_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, handlerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ComplaintDTO findById(long id) {
        String sql = "SELECT c.id, c.complaint_code, c.user_id, c.category, c.description, c.location, " +
                     "c.severity, c.status, c.assigned_handler_id, c.submitted_at, c.handled_at, " +
                     "h.name AS handler_name " +
                     "FROM complaints c " +
                     "LEFT JOIN complaint_handlers h ON c.assigned_handler_id = h.id " +
                     "WHERE c.id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateStatus(long id, String newStatus) {
        String sql = "UPDATE complaints SET status = ?, handled_at = IF(? = 'handled' OR ? = 'closed', CURRENT_TIMESTAMP, handled_at) WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newStatus);
                stmt.setString(2, newStatus);
                stmt.setString(3, newStatus);
                stmt.setLong(4, id);
                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    // Log handled event
                    ComplaintDTO c = findById(id);
                    if (c != null) {
                        String logSql = "INSERT INTO complaint_routing_log (complaint_id, handler_id, action, notes) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement logStmt = conn.prepareStatement(logSql)) {
                            logStmt.setLong(1, id);
                            logStmt.setLong(2, c.getAssignedHandlerId());
                            logStmt.setString(3, "handled".equalsIgnoreCase(newStatus) ? "handled" : "received");
                            logStmt.setString(4, "Status updated to " + newStatus);
                            logStmt.executeUpdate();
                        }
                    }
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                DBConnection.getInstance().close(conn);
            }
        }
        return false;
    }

    @Override
    public boolean escalateComplaint(long id, String reason) {
        ComplaintDTO complaint = findById(id);
        if (complaint == null) return false;

        long currentHandlerId = complaint.getAssignedHandlerId();
        ComplaintHandler currentHandler = ComplaintChain.getHandlerById(currentHandlerId);
        if (currentHandler == null) return false;

        // Escalate up the Chain of Responsibility
        ComplaintHandler nextHandler = currentHandler.escalate(complaint);
        if (nextHandler == null) {
            // Already at top of chain (Emergency Director)
            return false;
        }

        long newHandlerId = nextHandler.getHandlerId();
        String updateSql = "UPDATE complaints SET assigned_handler_id = ?, status = 'escalated' WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setLong(1, newHandlerId);
                stmt.setLong(2, id);
                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    // Log escalation passed action
                    String logSql = "INSERT INTO complaint_routing_log (complaint_id, handler_id, action, notes) VALUES (?, ?, 'passed', ?)";
                    try (PreparedStatement logStmt = conn.prepareStatement(logSql)) {
                        logStmt.setLong(1, id);
                        logStmt.setLong(2, currentHandlerId);
                        logStmt.setString(3, "Escalated to " + nextHandler.getHandlerName() + (reason != null && !reason.trim().isEmpty() ? ": " + reason : ""));
                        logStmt.executeUpdate();
                    }

                    // Activity log
                    String actSql = "INSERT INTO activity_log (activity_type, icon, message, severity, related_table, related_id) VALUES ('complaint', '', ?, 'warning', 'complaints', ?)";
                    try (PreparedStatement actStmt = conn.prepareStatement(actSql)) {
                        actStmt.setString(1, "Complaint " + complaint.getComplaintCode() + " escalated from " + currentHandler.getHandlerName() + " to " + nextHandler.getHandlerName());
                        actStmt.setLong(2, id);
                        actStmt.executeUpdate();
                    }

                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                DBConnection.getInstance().close(conn);
            }
        }
        return false;
    }

    private ComplaintDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        ComplaintDTO dto = new ComplaintDTO();
        dto.setId(rs.getLong("id"));
        dto.setComplaintCode(rs.getString("complaint_code"));
        dto.setUserId(rs.getLong("user_id"));
        dto.setCategory(rs.getString("category"));
        dto.setDescription(rs.getString("description"));
        dto.setLocation(rs.getString("location"));
        dto.setSeverity(rs.getInt("severity"));
        dto.setStatus(rs.getString("status"));
        dto.setAssignedHandlerId(rs.getLong("assigned_handler_id"));
        dto.setHandlerName(rs.getString("handler_name") != null ? rs.getString("handler_name") : getHandlerName(dto.getAssignedHandlerId()));

        Timestamp subTs = rs.getTimestamp("submitted_at");
        if (subTs != null) {
            dto.setSubmittedAt(subTs.toInstant().toString());
        }
        Timestamp handTs = rs.getTimestamp("handled_at");
        if (handTs != null) {
            dto.setHandledAt(handTs.toInstant().toString());
        }
        return dto;
    }

    @Override
    public boolean deleteComplaint(long id) {
        String deleteLogs = "DELETE FROM complaint_routing_log WHERE complaint_id = ?";
        String deleteComplaint = "DELETE FROM complaints WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psLogs = conn.prepareStatement(deleteLogs);
                 PreparedStatement psComp = conn.prepareStatement(deleteComplaint)) {

                psLogs.setLong(1, id);
                psLogs.executeUpdate();

                psComp.setLong(1, id);
                int affected = psComp.executeUpdate();

                conn.commit();
                return affected > 0;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("[ComplaintDAO] deleteComplaint error: " + e.getMessage());
        }
        return false;
    }
}
