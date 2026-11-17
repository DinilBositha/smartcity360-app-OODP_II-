package com.dinilbositha.smartcity360.controller;

import com.dinilbositha.smartcity360.composite.BuildingLeaf;
import com.dinilbositha.smartcity360.composite.ZoneComponent;
import com.dinilbositha.smartcity360.composite.ZoneComposite;
import com.dinilbositha.smartcity360.composite.ZoneRegistry;
import com.dinilbositha.smartcity360.dao.*;
import com.dinilbositha.smartcity360.dao.StreetLightDAO;
import com.dinilbositha.smartcity360.dao.StreetLightDAOImpl;
import com.dinilbositha.smartcity360.dao.PolicyDAO;
import com.dinilbositha.smartcity360.dao.PolicyDAOImpl;
import com.dinilbositha.smartcity360.dao.ZoneDAO;
import com.dinilbositha.smartcity360.dao.ZoneDAOImpl;
import com.dinilbositha.smartcity360.dto.*;
import com.dinilbositha.smartcity360.dto.StreetLightDTO;
import com.dinilbositha.smartcity360.dto.PolicyDTO;
import com.dinilbositha.smartcity360.dto.PolicyEvaluationDTO;
import com.dinilbositha.smartcity360.decorator.*;
import com.dinilbositha.smartcity360.flyweight.FleetBenchmark;
import com.dinilbositha.smartcity360.flyweight.LightFactory;
import com.dinilbositha.smartcity360.flyweight.LightType;
import com.dinilbositha.smartcity360.interpreter.Expression;
import com.dinilbositha.smartcity360.interpreter.PolicyContext;
import com.dinilbositha.smartcity360.interpreter.PolicyParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;


  // Demonstrates Gang of Four (GoF) structural and behavioral design patterns:
 // 1. Decorator Pattern: Security telemetry pipeline (Compression + AES-128 + HMAC-SHA256)
 // 2. Composite Pattern: Recursive city topology dispatch with MySQL database persistence
 // 3. Flyweight Pattern: Shared intrinsic streetlight types with 70%+ memory savings and DB fleet sync
 // 4. Interpreter Pattern: Policy DSL mini-language with AST execution and evaluation audit logging

@WebServlet(name = "PatternDemonstratorServlet", urlPatterns = {"/api/patterns/*"})
public class PatternDemonstratorServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final ZoneDAO zoneDAO = new ZoneDAOImpl();
    private final PolicyDAO policyDAO = new PolicyDAOImpl();
    private final StreetLightDAO streetLightDAO = new StreetLightDAOImpl();
    private static ZoneComposite activeCity;

    private synchronized ZoneComposite getActiveCity() {
        if (activeCity == null) {
            try {
                ZoneComposite fromDb = zoneDAO.loadHierarchyFromDB();
                if (fromDb != null && !fromDb.getChildren().isEmpty()) {
                    activeCity = fromDb;
                }
            } catch (Exception e) {
                System.err.println("[PatternDemonstratorServlet] Failed to load city tree from DB: " + e.getMessage());
            }
            if (activeCity == null) {
                activeCity = ZoneRegistry.createDefaultCity();
            }
        }
        return activeCity;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        JsonObject res = new JsonObject();
        try {
            switch (pathInfo) {
                case "/decorator":
                    handleDecorator(req, res);
                    break;
                case "/composite":
                    handleComposite(req, res);
                    break;
                case "/flyweight":
                    handleFlyweight(req, res);
                    break;
                case "/interpreter":
                    handleGetInterpreter(req, res);
                    break;
                default:
                    res.addProperty("status", "error");
                    res.addProperty("message", "Unknown pattern endpoint: " + pathInfo);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (Exception e) {
            res.addProperty("status", "error");
            res.addProperty("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        resp.getWriter().write(gson.toJson(res));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        JsonObject res = new JsonObject();
        try {
            if ("/interpreter".equals(pathInfo)) {
                handleInterpreter(req, res);
            } else if ("/flyweight".equals(pathInfo)) {
                handleFlyweightPost(req, res);
            } else {
                res.addProperty("status", "error");
                res.addProperty("message", "Unknown POST endpoint: " + pathInfo);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            res.addProperty("status", "error");
            res.addProperty("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        resp.getWriter().write(gson.toJson(res));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        JsonObject res = new JsonObject();
        if ("/interpreter".equals(pathInfo)) {
            long id = 0;
            try {
                id = Long.parseLong(req.getParameter("id"));
            } catch (Exception ignored) {}
            boolean ok = policyDAO.deletePolicy(id);
            res.addProperty("status", ok ? "success" : "error");
            res.addProperty("message", ok ? "Policy successfully removed from database" : "Failed to delete policy");
        } else {
            res.addProperty("status", "error");
            res.addProperty("message", "Unknown DELETE endpoint: " + pathInfo);
        }
        resp.getWriter().write(gson.toJson(res));
    }


    // 1. DECORATOR PATTERN (Telemetry Security)

    private void handleDecorator(HttpServletRequest req, JsonObject res) {
        String raw = req.getParameter("data");
        if (raw == null || raw.isEmpty()) {
            raw = "{\"meterId\":\"SL-0421\",\"kwh\":42.8,\"ts\":1709726400}";
        }

        boolean enc = "true".equalsIgnoreCase(req.getParameter("encrypt"));
        boolean comp = "true".equalsIgnoreCase(req.getParameter("compress"));
        boolean auth = "true".equalsIgnoreCase(req.getParameter("auth"));

        long startNs = System.nanoTime();

        DataStream stream = new MeterStream();
        List<String> pipeline = new ArrayList<>();
        pipeline.add("MeterStream (Raw Base)");

        if (comp) {
            stream = new CompressionDecorator(stream);
            pipeline.add("CompressionDecorator (GZIP)");
        }
        if (enc) {
            stream = new EncryptionDecorator(stream);
            pipeline.add("EncryptionDecorator (AES-128)");
        }
        if (auth) {
            stream = new AuthenticationDecorator(stream);
            pipeline.add("AuthenticationDecorator (HMAC-SHA256)");
        }

        byte[] inputBytes = raw.getBytes(StandardCharsets.UTF_8);
        byte[] processed = stream.write(inputBytes);
        byte[] restored = stream.read(processed);
        long durationUs = (System.nanoTime() - startNs) / 1000;

        res.addProperty("status", "success");
        res.addProperty("pattern", "Decorator Pattern");
        res.addProperty("backend", "Java Enterprise (com.dinilbositha.smartcity360.decorator)");
        res.add("pipeline", gson.toJsonTree(pipeline));
        res.addProperty("originalLength", inputBytes.length);
        res.addProperty("processedLength", processed.length);
        res.addProperty("executionTimeUs", durationUs);
        res.addProperty("processedBase64", Base64.getEncoder().encodeToString(processed));
        res.addProperty("roundTripValid", new String(restored, StandardCharsets.UTF_8).equals(raw));
    }


    // 2. COMPOSITE PATTERN (City Zone Topology)

    private void handleComposite(HttpServletRequest req, JsonObject res) {
        ZoneComposite currentCity = getActiveCity();

        String action = req.getParameter("action");
        if ("tree".equalsIgnoreCase(action)) {
            try {
                ZoneComposite dbTree = zoneDAO.loadHierarchyFromDB();
                if (dbTree != null && !dbTree.getChildren().isEmpty()) {
                    synchronized (this) {
                        activeCity = dbTree;
                        currentCity = dbTree;
                    }
                }
            } catch (Exception e) {
                System.err.println("[handleComposite] DB tree load fallback: " + e.getMessage());
            }
            res.addProperty("status", "success");
            res.addProperty("pattern", "Composite Pattern");
            res.addProperty("backend", "Java Enterprise (com.dinilbositha.smartcity360.composite)");
            res.add("tree", currentCity.toJson());
            res.addProperty("totalUsageKwh", currentCity.getUsage());
            res.addProperty("dataSource", "MySQL (city_components)");
            return;
        }

        String command = req.getParameter("command");
        if (command == null || command.isEmpty()) {
            command = "report usage";
        }

        String zoneName = req.getParameter("zone");
        ZoneComponent target = currentCity;
        if (zoneName != null && !zoneName.trim().isEmpty()) {
            ZoneComponent found = currentCity.find(zoneName.trim());
            if (found != null) {
                target = found;
            }
        }

        List<String> logs = new ArrayList<>();
        target.execute(command, logs);

        // Synchronize state with MySQL database and audit log
        boolean dbUpdated = false;
        boolean dbLogged = false;
        String normalizedCmd = command.trim().toLowerCase();

        if (normalizedCmd.contains("shutdown")) {
            dbUpdated = zoneDAO.updateZoneStatus(target.getName(), "inactive");
            dbLogged = zoneDAO.logActivity("ZONE_SHUTDOWN", 
                "Grid shutdown executed for " + target.getName() + " and descendant nodes.", 
                "warning", target.getName());
        } else if (normalizedCmd.contains("restore")) {
            dbUpdated = zoneDAO.updateZoneStatus(target.getName(), "active");
            dbLogged = zoneDAO.logActivity("ZONE_RESTORE", 
                "Power restored for " + target.getName() + " and descendant nodes.", 
                "success", target.getName());
        } else if (normalizedCmd.contains("emergency")) {
            dbUpdated = zoneDAO.updateZoneStatus(target.getName(), "emergency");
            dbLogged = zoneDAO.logActivity("ZONE_EMERGENCY", 
                "Emergency isolation triggered for " + target.getName() + " and descendant nodes.", 
                "error", target.getName());
        } else {
            dbLogged = zoneDAO.logActivity("ZONE_QUERY", 
                "Telemetry queried for " + target.getName(), 
                "info", target.getName());
        }

        res.addProperty("status", "success");
        res.addProperty("pattern", "Composite Pattern");
        res.addProperty("backend", "Java Enterprise (com.dinilbositha.smartcity360.composite)");
        res.addProperty("targetZone", target.getName());
        res.addProperty("command", command);
        res.addProperty("targetUsageKwh", target.getUsage());
        res.addProperty("totalUsageKwh", currentCity.getUsage());
        res.addProperty("dbPersisted", dbUpdated);
        res.addProperty("dbLogged", dbLogged);
        res.add("executionTrace", gson.toJsonTree(logs));
        res.add("tree", currentCity.toJson());
    }


    // 3. FLYWEIGHT PATTERN (Streetlight Fleet)

    private void handleFlyweight(HttpServletRequest req, JsonObject res) {
        int count = 50000;
        try {
            String cParam = req.getParameter("count");
            if (cParam != null) count = Integer.parseInt(cParam);
        } catch (NumberFormatException ignored) {}

        // Ensure standard types registered in Flyweight factory
        LightType standard = LightFactory.get("Lumiled-Smart-50W", 50, 6000, 4000, "Philips", "v2.4.1");
        LightType highway = LightFactory.get("AeroOptic-Highway-120W", 120, 16000, 5000, "Osram", "v3.1.0");
        LightType park = LightFactory.get("EcoSolar-Pathway-25W", 25, 2800, 3000, "Cree", "v1.8.4");

        FleetBenchmark.MemoryReport report = FleetBenchmark.analyze(count, LightFactory.getRegisteredTypeCount());

        JsonArray typeArr = new JsonArray();
        for (LightType lt : new LightType[]{standard, highway, park}) {
            JsonObject tObj = new JsonObject();
            tObj.addProperty("model", lt.getModel());
            tObj.addProperty("wattage", lt.getWattage());
            tObj.addProperty("lumens", lt.getLumens());
            tObj.addProperty("colorTemp", lt.getColorTemp());
            tObj.addProperty("manufacturer", lt.getManufacturer());
            tObj.addProperty("firmware", lt.getFirmware());
            tObj.addProperty("memoryBytes", 60);
            typeArr.add(tObj);
        }

        // Also fetch live streetlights from MySQL database
        List<StreetLightDTO> liveLights = streetLightDAO.getAllLights();

        res.addProperty("status", "success");
        res.addProperty("pattern", "Flyweight Pattern");
        res.addProperty("backend", "Java Enterprise (com.dinilbositha.smartcity360.flyweight)");
        res.addProperty("totalLights", report.totalLights);
        res.addProperty("uniqueFlyweights", report.uniqueFlyweights);
        res.addProperty("unoptimizedBytes", report.unoptimizedBytes);
        res.addProperty("flyweightBytes", report.flyweightBytes);
        res.addProperty("savingsPercent", Math.round(report.savingsPercent * 10.0) / 10.0);
        res.add("sharedTypes", typeArr);
        res.add("liveLights", gson.toJsonTree(liveLights));
    }

    private void handleFlyweightPost(HttpServletRequest req, JsonObject res) throws IOException {
        JsonObject body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
        String action = body.has("action") ? body.get("action").getAsString() : "";
        long id = body.has("id") ? body.get("id").getAsLong() : 0;

        if ("toggle".equalsIgnoreCase(action)) {
            boolean isOn = body.has("isOn") && body.get("isOn").getAsBoolean();
            boolean ok = streetLightDAO.updateLightStatus(id, isOn);
            res.addProperty("status", ok ? "success" : "error");
            res.addProperty("message", ok ? "Streetlight power toggled in database" : "Failed to toggle streetlight");
        } else if ("reportIssue".equalsIgnoreCase(action)) {
            boolean issue = !body.has("issue") || body.get("issue").getAsBoolean();
            boolean ok = streetLightDAO.reportIssue(id, issue);
            res.addProperty("status", ok ? "success" : "error");
            res.addProperty("message", ok ? "Issue logged for streetlight" : "Failed to report issue");
        } else if ("create".equalsIgnoreCase(action)) {
            StreetLightDTO dto = new StreetLightDTO();
            dto.setLightCode(body.has("code") ? body.get("code").getAsString() : "");
            dto.setTypeId(body.has("typeId") ? body.get("typeId").getAsLong() : 1);
            dto.setOn(true);
            boolean ok = streetLightDAO.createLight(dto);
            res.addProperty("status", ok ? "success" : "error");
            res.add("light", gson.toJsonTree(dto));
        } else if ("delete".equalsIgnoreCase(action)) {
            boolean ok = streetLightDAO.deleteLight(id);
            res.addProperty("status", ok ? "success" : "error");
        } else {
            res.addProperty("status", "error");
            res.addProperty("message", "Unknown flyweight action: " + action);
        }
    }


    // 4. INTERPRETER PATTERN (Policy DSL & Charts)

    private void handleGetInterpreter(HttpServletRequest req, JsonObject res) {
        List<PolicyDTO> policies = policyDAO.getAllPolicies();
        List<PolicyEvaluationDTO> evals = policyDAO.getRecentEvaluations(10);
        int total = policyDAO.getTotalEvaluationsCount();
        double passRate = policyDAO.getComplianceRate();

        JsonObject stats = new JsonObject();
        stats.addProperty("total", total);
        stats.addProperty("passRate", passRate);

        res.addProperty("status", "success");
        res.addProperty("pattern", "Interpreter Pattern");
        res.addProperty("backend", "Java Enterprise (com.dinilbositha.smartcity360.interpreter)");
        res.add("policies", gson.toJsonTree(policies));
        res.add("stats", stats);
        res.add("recentEvaluations", gson.toJsonTree(evals));
    }

    private void handleInterpreter(HttpServletRequest req, JsonObject res) throws IOException {
        String action = req.getParameter("action");
        JsonObject body = null;
        try {
            body = JsonParser.parseReader(req.getReader()).getAsJsonObject();
            if (action == null && body.has("action")) {
                action = body.get("action").getAsString();
            }
        } catch (Exception ignored) {}

        if (body == null) body = new JsonObject();

        // CRUD: Create Policy
        if ("create".equalsIgnoreCase(action)) {
            PolicyDTO p = new PolicyDTO();
            p.setName(body.has("name") ? body.get("name").getAsString() : "New Municipal Policy");
            p.setCategory(body.has("category") ? body.get("category").getAsString() : "General");
            String dsl = body.has("dslText") ? body.get("dslText").getAsString() : 
                         (body.has("rule") ? body.get("rule").getAsString() : "");
            p.setDslText(dsl);
            p.setDescription(body.has("description") ? body.get("description").getAsString() : "");
            p.setActive(true);

            boolean ok = policyDAO.createPolicy(p);
            res.addProperty("status", ok ? "success" : "error");
            res.addProperty("message", ok ? "Policy rule successfully saved to database" : "Failed to create policy");
            res.add("policy", gson.toJsonTree(p));
            return;
        }

        // CRUD: Update Policy
        if ("update".equalsIgnoreCase(action)) {
            long id = body.has("id") ? body.get("id").getAsLong() : 0;
            PolicyDTO p = policyDAO.getPolicyById(id);
            if (p == null) {
                p = new PolicyDTO();
                p.setId(id);
            }
            if (body.has("name")) p.setName(body.get("name").getAsString());
            if (body.has("category")) p.setCategory(body.get("category").getAsString());
            if (body.has("dslText")) p.setDslText(body.get("dslText").getAsString());
            else if (body.has("rule")) p.setDslText(body.get("rule").getAsString());
            if (body.has("description")) p.setDescription(body.get("description").getAsString());
            if (body.has("active")) p.setActive(body.get("active").getAsBoolean());

            boolean ok = policyDAO.updatePolicy(p);
            res.addProperty("status", ok ? "success" : "error");
            res.addProperty("message", ok ? "Policy updated in database" : "Failed to update policy");
            return;
        }

        // CRUD: Delete Policy
        if ("delete".equalsIgnoreCase(action)) {
            long id = 0;
            if (req.getParameter("id") != null) {
                try { id = Long.parseLong(req.getParameter("id")); } catch (Exception ignored) {}
            } else if (body.has("id")) {
                id = body.get("id").getAsLong();
            }
            boolean ok = policyDAO.deletePolicy(id);
            res.addProperty("status", ok ? "success" : "error");
            res.addProperty("message", ok ? "Policy deleted from database" : "Failed to delete policy");
            return;
        }

        // GoF Interpreter Pattern Execution & Evaluation Logging
        String rule = body.has("rule") ? body.get("rule").getAsString() : "";
        JsonObject ctxJson = body.has("context") ? body.getAsJsonObject("context") : new JsonObject();
        long policyId = body.has("policyId") ? body.get("policyId").getAsLong() : 1;

        long startNs = System.nanoTime();

        PolicyContext ctx = new PolicyContext();
        for (Map.Entry<String, com.google.gson.JsonElement> entry : ctxJson.entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
                if (entry.getValue().getAsJsonPrimitive().isNumber()) {
                    ctx.set(entry.getKey(), entry.getValue().getAsNumber().doubleValue());
                } else if (entry.getValue().getAsJsonPrimitive().isBoolean()) {
                    ctx.set(entry.getKey(), entry.getValue().getAsBoolean());
                } else {
                    ctx.set(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }

        Expression expr = PolicyParser.parse(rule);
        boolean outcome = expr.interpret(ctx);
        long durationUs = (System.nanoTime() - startNs) / 1000;

        // Persist evaluation into MySQL policy_evaluations table
        policyDAO.logEvaluation(policyId, gson.toJson(ctxJson), outcome);

        // Generate dynamic metric comparisons for Interactive Canvas Chart
        JsonArray metricsArr = new JsonArray();
        for (Map.Entry<String, com.google.gson.JsonElement> entry : ctxJson.entrySet()) {
            if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isNumber()) {
                double val = entry.getValue().getAsDouble();
                JsonObject mObj = new JsonObject();
                mObj.addProperty("variable", entry.getKey());
                mObj.addProperty("actual", val);

                double threshold = 55.0;
                String unit = "";
                if ("noise".equalsIgnoreCase(entry.getKey())) {
                    threshold = 55.0;
                    unit = " dB";
                } else if ("hour".equalsIgnoreCase(entry.getKey())) {
                    threshold = 18.0;
                    unit = " h";
                } else if ("pm25".equalsIgnoreCase(entry.getKey()) || "air".equalsIgnoreCase(entry.getKey())) {
                    threshold = 35.0;
                    unit = " µg";
                } else if ("speed".equalsIgnoreCase(entry.getKey())) {
                    threshold = 50.0;
                    unit = " km/h";
                } else if ("energy".equalsIgnoreCase(entry.getKey())) {
                    threshold = 100.0;
                    unit = " kW";
                }
                mObj.addProperty("threshold", threshold);
                mObj.addProperty("unit", unit);
                mObj.addProperty("satisfied", outcome);
                metricsArr.add(mObj);
            }
        }

        res.addProperty("status", "success");
        res.addProperty("pattern", "Interpreter Pattern");
        res.addProperty("backend", "Java Enterprise (com.dinilbositha.smartcity360.interpreter)");
        res.addProperty("rule", rule);
        res.addProperty("ast", expr.toTree(0));
        res.add("astJson", expr.toAstJson());
        res.addProperty("result", outcome);
        res.addProperty("executionTimeUs", durationUs);
        res.addProperty("dbLogged", true);
        res.add("contextEvaluated", ctxJson);
        res.add("metrics", metricsArr);
        res.add("chartMetrics", metricsArr);
        res.add("recentEvaluations", gson.toJsonTree(policyDAO.getRecentEvaluations(10)));

        JsonObject stats = new JsonObject();
        stats.addProperty("total", policyDAO.getTotalEvaluationsCount());
        stats.addProperty("passRate", policyDAO.getComplianceRate());
        res.add("stats", stats);
    }
}
