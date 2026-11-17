package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.composite.BuildingLeaf;
import com.dinilbositha.smartcity360.composite.ZoneComponent;
import com.dinilbositha.smartcity360.composite.ZoneComposite;
import com.dinilbositha.smartcity360.util.DBConnection;

import java.sql.*;
import java.util.*;

public class ZoneDAOImpl implements ZoneDAO {

    @Override
    public ZoneComposite loadHierarchyFromDB() {
        String sql = "SELECT id, parent_id, component_type, name, status FROM city_components ORDER BY id ASC";
        Map<Long, ZoneComponent> nodeById = new LinkedHashMap<>();
        ZoneComposite root = null;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Long[]> parentLinks = new ArrayList<>();

            while (rs.next()) {
                long id = rs.getLong("id");
                long parentId = rs.getLong("parent_id");
                boolean hasParent = !rs.wasNull();
                String type = rs.getString("component_type");
                String name = rs.getString("name");
                String status = rs.getString("status");

                boolean isActive = !"inactive".equalsIgnoreCase(status);

                if ("building".equalsIgnoreCase(type)) {
                    BuildingLeaf leaf = new BuildingLeaf(name, 120.0);
                    leaf.setStatus(status);
                    leaf.setPowerActive(isActive);
                    nodeById.put(id, leaf);
                } else {
                    ZoneComposite comp = new ZoneComposite(name, capitalize(type));
                    comp.setStatus(status);
                    comp.setActive(isActive);
                    nodeById.put(id, comp);
                    if (!hasParent && root == null) {
                        root = comp;
                    }
                }

                if (hasParent) {
                    parentLinks.add(new Long[]{id, parentId});
                }
            }

            // Link parent and child components
            for (Long[] link : parentLinks) {
                long childId = link[0];
                long parentId = link[1];
                ZoneComponent child = nodeById.get(childId);
                ZoneComponent parent = nodeById.get(parentId);

                if (parent instanceof ZoneComposite && child != null) {
                    ((ZoneComposite) parent).add(child);
                }
            }

        } catch (SQLException e) {
            System.err.println("[ZoneDAO] Database load failed, falling back: " + e.getMessage());
        }

        return root;
    }

    @Override
    public boolean updateZoneStatus(String zoneName, String status) {
        String targetStatus = status.toLowerCase();
        // Step 1: Find target node id
        String findSql = "SELECT id, component_type FROM city_components WHERE LOWER(name) = LOWER(?) LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement psFind = conn.prepareStatement(findSql)) {

            psFind.setString(1, zoneName != null ? zoneName.trim() : "");
            try (ResultSet rs = psFind.executeQuery()) {
                if (!rs.next()) return false;
                long targetId = rs.getLong("id");

                // Step 2: Recursively collect all descendant IDs
                Set<Long> allIds = new LinkedHashSet<>();
                allIds.add(targetId);
                collectDescendantIds(conn, targetId, allIds);

                // Step 3: Batch update status
                StringBuilder inClause = new StringBuilder();
                for (int i = 0; i < allIds.size(); i++) {
                    inClause.append(i > 0 ? ",?" : "?");
                }

                String updateSql = "UPDATE city_components SET status = ? WHERE id IN (" + inClause + ")";
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setString(1, targetStatus);
                    int idx = 2;
                    for (Long id : allIds) {
                        psUpdate.setLong(idx++, id);
                    }
                    int updated = psUpdate.executeUpdate();
                    return updated > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ZoneDAO] Update status failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean logActivity(String activityType, String message, String severity, String zoneName) {
        String sql = "INSERT INTO activity_log (activity_type, icon, message, severity, related_table, related_id) " +
                     "VALUES (?, ?, ?, ?, 'city_components', (SELECT id FROM city_components WHERE LOWER(name) = LOWER(?) LIMIT 1))";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, activityType != null ? activityType : "ZONE_COMMAND");
            ps.setString(2, "GRID");
            ps.setString(3, message);
            ps.setString(4, severity != null ? severity : "info");
            ps.setString(5, zoneName != null ? zoneName.trim() : "");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ZoneDAO] Activity logging failed: " + e.getMessage());
            return false;
        }
    }

    private void collectDescendantIds(Connection conn, long parentId, Set<Long> allIds) throws SQLException {
        String sql = "SELECT id FROM city_components WHERE parent_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, parentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long childId = rs.getLong("id");
                    if (allIds.add(childId)) {
                        collectDescendantIds(conn, childId, allIds);
                    }
                }
            }
        }
    }

    @Override
    public long createComponent(Long parentId, String type, String name, String address, Double lat, Double lng) {
        String sql = "INSERT INTO city_components (parent_id, component_type, name, address, lat, lng, status) VALUES (?, ?, ?, ?, ?, ?, 'active')";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (parentId != null && parentId > 0) {
                ps.setLong(1, parentId);
            } else {
                ps.setNull(1, Types.BIGINT);
            }
            ps.setString(2, type != null ? type.toLowerCase() : "building");
            ps.setString(3, name);
            ps.setString(4, address != null ? address : "");
            ps.setDouble(5, lat != null ? lat : 6.9271);
            ps.setDouble(6, lng != null ? lng : 79.8612);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        long newId = keys.getLong(1);
                        logActivity("ZONE_CREATE", "Added new " + type + ": " + name, "info", name);
                        return newId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ZoneDAO] createComponent error: " + e.getMessage());
        }
        return -1L;
    }

    @Override
    public boolean deleteComponent(long id) {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            Set<Long> allIds = new LinkedHashSet<>();
            allIds.add(id);
            collectDescendantIds(conn, id, allIds);

            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < allIds.size(); i++) {
                inClause.append(i > 0 ? ",?" : "?");
            }

            // Remove related activity logs or streetlights references if needed
            String cleanLogs = "UPDATE activity_log SET related_id = NULL WHERE related_table = 'city_components' AND related_id IN (" + inClause + ")";
            try (PreparedStatement psLogs = conn.prepareStatement(cleanLogs)) {
                int idx = 1;
                for (Long cid : allIds) psLogs.setLong(idx++, cid);
                psLogs.executeUpdate();
            }

            String deleteSql = "DELETE FROM city_components WHERE id IN (" + inClause + ")";
            try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                int idx = 1;
                for (Long cid : allIds) psDel.setLong(idx++, cid);
                return psDel.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ZoneDAO] deleteComponent error: " + e.getMessage());
        }
        return false;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}
