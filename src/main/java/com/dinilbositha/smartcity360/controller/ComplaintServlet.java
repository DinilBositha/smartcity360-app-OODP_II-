package com.dinilbositha.smartcity360.controller;

import com.dinilbositha.smartcity360.chain.ComplaintChain;
import com.dinilbositha.smartcity360.chain.ComplaintHandler;
import com.dinilbositha.smartcity360.dao.ComplaintDAO;
import com.dinilbositha.smartcity360.dao.ComplaintDAOImpl;
import com.dinilbositha.smartcity360.dto.ComplaintDTO;
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

@WebServlet(name = "complaintServlet", urlPatterns = {"/api/complaints", "/complaints"})
public class ComplaintServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String handlerIdParam = req.getParameter("handlerId");
        String userIdParam = req.getParameter("userId");
        List<ComplaintDTO> list;

        if (handlerIdParam != null && !handlerIdParam.trim().isEmpty()) {
            try {
                long handlerId = Long.parseLong(handlerIdParam.trim());
                list = complaintDAO.getComplaintsByHandlerId(handlerId);
            } catch (NumberFormatException e) {
                list = complaintDAO.getAllComplaints();
            }
        } else if (userIdParam != null && !userIdParam.trim().isEmpty()) {
            try {
                long userId = Long.parseLong(userIdParam.trim());
                list = complaintDAO.getComplaintsByUserId(userId);
            } catch (NumberFormatException e) {
                list = complaintDAO.getAllComplaints();
            }
        } else {
            list = complaintDAO.getAllComplaints();
        }

        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(list));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String action = req.getParameter("action");
        String contentType = req.getContentType();
        JsonObject bodyJson = null;

        // If JSON body, check if action is in the body
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            try {
                bodyJson = JsonParser.parseString(sb.toString()).getAsJsonObject();
                if (action == null && bodyJson.has("action")) {
                    action = bodyJson.get("action").getAsString();
                }
            } catch (Exception ignored) {}
        }

        // Action: Escalate (Chain of Responsibility)
        if ("escalate".equalsIgnoreCase(action)) {
            handleEscalate(req, resp, bodyJson);
            return;
        }

        // Action: Handle / Resolve
        if ("handle".equalsIgnoreCase(action) || "resolve".equalsIgnoreCase(action)) {
            handleResolve(req, resp, bodyJson);
            return;
        }

        // Action: Generic update status
        if ("update".equalsIgnoreCase(action)) {
            handleStatusUpdate(req, resp, bodyJson);
            return;
        }

        // Action: Delete complaint
        if ("delete".equalsIgnoreCase(action)) {
            handleDelete(req, resp, bodyJson);
            return;
        }

        // Default: Create new complaint
        handleCreate(req, resp, bodyJson);
    }

    private void handleEscalate(HttpServletRequest req, HttpServletResponse resp, JsonObject json) throws IOException {
        long id = extractLong(req, json, "id");
        String reason = extractString(req, json, "reason");

        Map<String, Object> responseMap = new HashMap<>();
        if (id <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("success", false);
            responseMap.put("message", "Invalid complaint ID");
        } else {
            boolean escalated = complaintDAO.escalateComplaint(id, reason);
            if (escalated) {
                ComplaintDTO updated = complaintDAO.findById(id);
                responseMap.put("success", true);
                responseMap.put("message", "Complaint successfully escalated up the Chain of Responsibility");
                responseMap.put("complaint", updated);
                responseMap.put("newHandler", updated != null ? updated.getHandlerName() : "Unknown");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("success", false);
                responseMap.put("message", "Could not escalate. Complaint may already be at top level or not found.");
            }
        }
        resp.getWriter().print(gson.toJson(responseMap));
    }

    private void handleResolve(HttpServletRequest req, HttpServletResponse resp, JsonObject json) throws IOException {
        long id = extractLong(req, json, "id");
        Map<String, Object> responseMap = new HashMap<>();

        if (id <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("success", false);
            responseMap.put("message", "Invalid complaint ID");
        } else {
            boolean handled = complaintDAO.updateStatus(id, "handled");
            responseMap.put("success", handled);
            responseMap.put("message", handled ? "Complaint resolved and marked as Handled" : "Failed to update complaint");
        }
        resp.getWriter().print(gson.toJson(responseMap));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        handleDelete(req, resp, null);
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, JsonObject json) throws IOException {
        long id = extractLong(req, json, "id");
        Map<String, Object> responseMap = new HashMap<>();

        if (id <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("success", false);
            responseMap.put("message", "Invalid complaint ID");
        } else {
            boolean deleted = complaintDAO.deleteComplaint(id);
            responseMap.put("success", deleted);
            responseMap.put("message", deleted ? "Complaint deleted successfully from database" : "Complaint not found or could not be deleted");
            if (!deleted) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        resp.getWriter().print(gson.toJson(responseMap));
    }

    private void handleStatusUpdate(HttpServletRequest req, HttpServletResponse resp, JsonObject json) throws IOException {
        long id = extractLong(req, json, "id");
        String status = extractString(req, json, "status");

        Map<String, Object> responseMap = new HashMap<>();
        if (id > 0 && status != null) {
            boolean updated = complaintDAO.updateStatus(id, status);
            responseMap.put("success", updated);
            responseMap.put("message", updated ? "Status updated" : "Complaint not found");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("success", false);
            responseMap.put("message", "Missing id or status");
        }
        resp.getWriter().print(gson.toJson(responseMap));
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp, JsonObject json) throws IOException {
        ComplaintDTO complaint = new ComplaintDTO();

        if (json != null) {
            if (json.has("category")) complaint.setCategory(json.get("category").getAsString());
            if (json.has("description")) complaint.setDescription(json.get("description").getAsString());
            if (json.has("location")) complaint.setLocation(json.get("location").getAsString());
            if (json.has("severity")) complaint.setSeverity(json.get("severity").getAsInt());
            if (json.has("userId")) complaint.setUserId(json.get("userId").getAsLong());
            if (json.has("complaintCode")) complaint.setComplaintCode(json.get("complaintCode").getAsString());
        } else {
            complaint.setCategory(req.getParameter("category"));
            complaint.setDescription(req.getParameter("description"));
            complaint.setLocation(req.getParameter("location"));

            String sevStr = req.getParameter("severity");
            complaint.setSeverity(sevStr != null && !sevStr.trim().isEmpty() ? Integer.parseInt(sevStr.trim()) : 5);

            String userStr = req.getParameter("userId");
            complaint.setUserId(userStr != null && !userStr.trim().isEmpty() ? Long.parseLong(userStr.trim()) : 1L);
        }

        Map<String, Object> responseMap = new HashMap<>();
        if (complaint.getDescription() == null || complaint.getDescription().trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMap.put("success", false);
            responseMap.put("message", "Description is required");
        } else {
            boolean saved = complaintDAO.saveComplaint(complaint);
            if (saved) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                responseMap.put("success", true);
                responseMap.put("message", "Complaint registered and routed through Chain of Responsibility");
                responseMap.put("complaintCode", complaint.getComplaintCode());
                responseMap.put("handler", complaint.getHandlerName());
                responseMap.put("complaint", complaint);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseMap.put("success", false);
                responseMap.put("message", "Database error occurred while saving complaint");
            }
        }
        resp.getWriter().print(gson.toJson(responseMap));
    }

    private long extractLong(HttpServletRequest req, JsonObject json, String key) {
        if (json != null && json.has(key)) {
            try { return json.get(key).getAsLong(); } catch (Exception ignored) {}
        }
        String p = req.getParameter(key);
        if (p != null && !p.trim().isEmpty()) {
            try { return Long.parseLong(p.trim()); } catch (Exception ignored) {}
        }
        return -1L;
    }

    private String extractString(HttpServletRequest req, JsonObject json, String key) {
        if (json != null && json.has(key)) {
            try { return json.get(key).getAsString(); } catch (Exception ignored) {}
        }
        return req.getParameter(key);
    }
}
