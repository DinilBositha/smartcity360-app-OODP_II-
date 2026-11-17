package com.dinilbositha.smartcity360.controller;

import com.dinilbositha.smartcity360.dao.AnnouncementDAO;
import com.dinilbositha.smartcity360.dao.AnnouncementDAOImpl;
import com.dinilbositha.smartcity360.dto.AnnouncementDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "announcementServlet", urlPatterns = {"/api/announcements", "/announcements"})
public class AnnouncementServlet extends HttpServlet {

    private final AnnouncementDAO announcementDAO = new AnnouncementDAOImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String allParam = req.getParameter("all");
        List<AnnouncementDTO> announcements;
        if ("true".equalsIgnoreCase(allParam)) {
            announcements = announcementDAO.getAllAnnouncements();
        } else {
            announcements = announcementDAO.getActiveAnnouncements();
        }

        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(announcements));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String action = req.getParameter("action");
        JsonObject body = null;

        String contentType = req.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            try (BufferedReader reader = req.getReader()) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                body = JsonParser.parseString(sb.toString()).getAsJsonObject();
                if (action == null && body.has("action")) {
                    action = body.get("action").getAsString();
                }
            } catch (Exception ignored) {}
        }

        Map<String, Object> res = new HashMap<>();

        if ("delete".equalsIgnoreCase(action)) {
            long id = 0;
            if (req.getParameter("id") != null) {
                try { id = Long.parseLong(req.getParameter("id")); } catch (Exception ignored) {}
            } else if (body != null && body.has("id")) {
                id = body.get("id").getAsLong();
            }
            boolean deleted = announcementDAO.deleteAnnouncement(id);
            res.put("success", deleted);
            res.put("message", deleted ? "Announcement deleted from database" : "Failed to delete announcement");
        } else if ("update".equalsIgnoreCase(action)) {
            AnnouncementDTO dto = new AnnouncementDTO();
            long id = 0;
            if (body != null && body.has("id")) id = body.get("id").getAsLong();
            dto.setId(id);
            dto.setTitle(body != null && body.has("title") ? body.get("title").getAsString() : req.getParameter("title"));
            dto.setBody(body != null && body.has("body") ? body.get("body").getAsString() : req.getParameter("body"));
            dto.setIcon(body != null && body.has("icon") ? body.get("icon").getAsString() : "NOTICE");

            boolean updated = announcementDAO.updateAnnouncement(dto);
            res.put("success", updated);
            res.put("message", updated ? "Announcement updated successfully" : "Failed to update announcement");
        } else {
            // Create announcement
            AnnouncementDTO dto = new AnnouncementDTO();
            String title = body != null && body.has("title") ? body.get("title").getAsString() : req.getParameter("title");
            String bodyText = body != null && body.has("body") ? body.get("body").getAsString() : req.getParameter("body");
            String icon = body != null && body.has("icon") ? body.get("icon").getAsString() : req.getParameter("icon");

            dto.setTitle(title != null ? title : "Municipal Notice");
            dto.setBody(bodyText != null ? bodyText : "");
            dto.setIcon(icon != null ? icon : "NOTICE");

            boolean created = announcementDAO.createAnnouncement(dto);
            res.put("success", created);
            res.put("message", created ? "Announcement created and published" : "Failed to create announcement");
            res.put("announcement", dto);
        }

        resp.getWriter().print(gson.toJson(res));
    }
}
