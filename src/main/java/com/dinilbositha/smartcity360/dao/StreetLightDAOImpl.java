package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.dto.StreetLightDTO;
import com.dinilbositha.smartcity360.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StreetLightDAOImpl implements StreetLightDAO {

    @Override
    public List<StreetLightDTO> getAllLights() {
        List<StreetLightDTO> list = new ArrayList<>();
        String sql = "SELECT sl.id, sl.light_code, sl.type_id, st.type_key AS model, " +
                     "st.wattage, st.lumens, st.color_temp_k, sl.lat, sl.lng, sl.is_on, sl.reported_issue " +
                     "FROM streetlights sl " +
                     "JOIN streetlight_types st ON sl.type_id = st.id " +
                     "ORDER BY sl.id ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                StreetLightDTO dto = new StreetLightDTO();
                dto.setId(rs.getLong("id"));
                dto.setLightCode(rs.getString("light_code"));
                dto.setTypeId(rs.getLong("type_id"));
                dto.setModel(rs.getString("model"));
                dto.setWattage(rs.getInt("wattage"));
                dto.setLumens(rs.getInt("lumens"));
                dto.setColorTemp(rs.getInt("color_temp_k"));
                dto.setLat(rs.getDouble("lat"));
                dto.setLng(rs.getDouble("lng"));
                dto.setOn(rs.getBoolean("is_on"));
                dto.setReportedIssue(rs.getBoolean("reported_issue"));
                list.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("[StreetLightDAO] getAllLights error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateLightStatus(long id, boolean isOn) {
        String sql = "UPDATE streetlights SET is_on = ?, last_status_change = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isOn);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StreetLightDAO] updateLightStatus error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean reportIssue(long id, boolean hasIssue) {
        String sql = "UPDATE streetlights SET reported_issue = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, hasIssue);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StreetLightDAO] reportIssue error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean createLight(StreetLightDTO light) {
        String sql = "INSERT INTO streetlights (light_code, type_id, lat, lng, is_on, reported_issue) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String code = light.getLightCode();
            if (code == null || code.trim().isEmpty()) {
                code = "SL-" + (System.currentTimeMillis() % 10000);
            }
            long typeId = light.getTypeId() > 0 ? light.getTypeId() : 1;
            ps.setString(1, code);
            ps.setLong(2, typeId);
            ps.setDouble(3, light.getLat() != 0 ? light.getLat() : 6.9271);
            ps.setDouble(4, light.getLng() != 0 ? light.getLng() : 79.8612);
            ps.setBoolean(5, light.isOn());
            ps.setBoolean(6, light.isReportedIssue());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        light.setId(rs.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[StreetLightDAO] createLight error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteLight(long id) {
        String sql = "DELETE FROM streetlights WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StreetLightDAO] deleteLight error: " + e.getMessage());
        }
        return false;
    }
}
