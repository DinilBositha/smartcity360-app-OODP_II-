package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.dto.AnnouncementDTO;
import com.dinilbositha.smartcity360.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAOImpl implements AnnouncementDAO {

    @Override
    public List<AnnouncementDTO> getAllAnnouncements() {
        List<AnnouncementDTO> list = new ArrayList<>();
        String sql = "SELECT id, icon, title, body, published_at FROM announcements ORDER BY published_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AnnouncementDTO dto = new AnnouncementDTO();
                dto.setId(rs.getLong("id"));
                dto.setIcon(rs.getString("icon"));
                dto.setTitle(rs.getString("title"));
                dto.setBody(rs.getString("body"));

                Timestamp pubTs = rs.getTimestamp("published_at");
                if (pubTs != null) {
                    dto.setPublishedAt(pubTs.toInstant().toString());
                }
                list.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("[AnnouncementDAO] getAllAnnouncements error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<AnnouncementDTO> getActiveAnnouncements() {
        List<AnnouncementDTO> list = new ArrayList<>();
        String sql = "SELECT id, icon, title, body, published_at FROM announcements WHERE active = 1 ORDER BY published_at DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AnnouncementDTO dto = new AnnouncementDTO();
                dto.setId(rs.getLong("id"));
                dto.setIcon(rs.getString("icon"));
                dto.setTitle(rs.getString("title"));
                dto.setBody(rs.getString("body"));

                Timestamp pubTs = rs.getTimestamp("published_at");
                if (pubTs != null) {
                    dto.setPublishedAt(pubTs.toInstant().toString());
                }
                list.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("[AnnouncementDAO] getActiveAnnouncements error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean createAnnouncement(AnnouncementDTO announcement) {
        String sql = "INSERT INTO announcements (icon, title, body, active) VALUES (?, ?, ?, 1)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, announcement.getIcon() != null ? announcement.getIcon() : "NOTICE");
            ps.setString(2, announcement.getTitle());
            ps.setString(3, announcement.getBody());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        announcement.setId(rs.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[AnnouncementDAO] createAnnouncement error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateAnnouncement(AnnouncementDTO announcement) {
        String sql = "UPDATE announcements SET title = ?, body = ?, icon = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, announcement.getTitle());
            ps.setString(2, announcement.getBody());
            ps.setString(3, announcement.getIcon() != null ? announcement.getIcon() : "NOTICE");
            ps.setLong(4, announcement.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AnnouncementDAO] updateAnnouncement error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteAnnouncement(long id) {
        String sql = "DELETE FROM announcements WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AnnouncementDAO] deleteAnnouncement error: " + e.getMessage());
        }
        return false;
    }
}
