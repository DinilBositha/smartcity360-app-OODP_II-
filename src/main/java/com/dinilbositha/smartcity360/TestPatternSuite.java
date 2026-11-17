package com.dinilbositha.smartcity360;

import com.dinilbositha.smartcity360.chain.*;
import com.dinilbositha.smartcity360.composite.*;
import com.dinilbositha.smartcity360.decorator.*;
import com.dinilbositha.smartcity360.dto.ComplaintDTO;
import com.dinilbositha.smartcity360.flyweight.*;
import com.dinilbositha.smartcity360.interpreter.*;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestPatternSuite {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  SmartCity360 Enterprise Architecture Verification");
        System.out.println("==================================================");

        testDecorator();
        testComposite();
        testChain();
        testFlyweight();
        testInterpreter();

        System.out.println("\n>>> ALL 5 DESIGN PATTERN SUITES PASSED CLEANLY! <<<\n");
    }

    private static void testDecorator() {
        System.out.print("[Decorator Pattern] Testing Security Data Stream Pipeline... ");
        String raw = "{\"meterId\":\"M-091\",\"kwh\":145.2,\"ts\":1709726400}";
        byte[] input = raw.getBytes(StandardCharsets.UTF_8);

        DataStream stream = new AuthenticationDecorator(
                                new EncryptionDecorator(
                                    new CompressionDecorator(
                                        new MeterStream())));

        byte[] processed = stream.write(input);
        byte[] restored = stream.read(processed);
        String output = new String(restored, StandardCharsets.UTF_8);

        if (!raw.equals(output)) {
            throw new AssertionError("Decorator roundtrip failed! Expected: " + raw + ", Got: " + output);
        }

        // Test tampering detection
        processed[processed.length - 1] ^= 0xFF;
        boolean caught = false;
        try {
            stream.read(processed);
        } catch (StreamException e) {
            caught = true;
        }
        if (!caught) {
            throw new AssertionError("AuthenticationDecorator failed to catch stream tampering!");
        }

        System.out.println("PASSED (GZIP + AES-128 + HMAC-SHA256 verified)");
    }

    private static void testComposite() {
        System.out.print("[Composite Pattern] Testing Hierarchical Zone Topology... ");
        ZoneComposite city = ZoneRegistry.createDefaultCity();

        double initialUsage = city.getUsage();
        if (initialUsage <= 0) {
            throw new AssertionError("Initial aggregate usage must be > 0, got: " + initialUsage);
        }

        List<String> logs = new ArrayList<>();
        city.execute("shutdown power", logs);

        double shutdownUsage = city.getUsage();
        if (shutdownUsage != 0.0) {
            throw new AssertionError("Aggregate usage after shutdown must be 0, got: " + shutdownUsage);
        }

        city.execute("restore power", logs);
        if (city.getUsage() <= 0) {
            throw new AssertionError("Aggregate usage after restore must be > 0");
        }

        System.out.println("PASSED (Recursive dispatch & aggregate usage verified)");
    }

    private static void testChain() {
        System.out.print("[Chain of Responsibility] Testing Complaint Escalation Chain... ");
        ComplaintDTO c1 = new ComplaintDTO(); c1.setSeverity(2);
        ComplaintDTO c2 = new ComplaintDTO(); c2.setSeverity(5);
        ComplaintDTO c3 = new ComplaintDTO(); c3.setSeverity(8);
        ComplaintDTO c4 = new ComplaintDTO(); c4.setSeverity(10);

        ComplaintChain.route(c1);
        ComplaintChain.route(c2);
        ComplaintChain.route(c3);
        ComplaintChain.route(c4);

        if (!"Local Staff".equals(c1.getHandlerName())) throw new AssertionError("Expected Local Staff, got " + c1.getHandlerName());
        if (!"Supervisor".equals(c2.getHandlerName())) throw new AssertionError("Expected Supervisor, got " + c2.getHandlerName());
        if (!"City Manager".equals(c3.getHandlerName())) throw new AssertionError("Expected City Manager, got " + c3.getHandlerName());
        if (!"Emergency Director".equals(c4.getHandlerName())) throw new AssertionError("Expected Emergency Director, got " + c4.getHandlerName());

        System.out.println("PASSED (Dynamic routing across Tier 1 - Tier 4 verified)");
    }

    private static void testFlyweight() {
        System.out.print("[Flyweight Pattern] Testing Shared Intrinsic Streetlight Fleet... ");
        LightType t1 = LightFactory.getDefaultStandard();
        LightType t2 = LightFactory.getDefaultStandard();

        if (t1 != t2) {
            throw new AssertionError("LightFactory must return identical shared flyweight reference!");
        }

        FleetBenchmark.MemoryReport report = FleetBenchmark.analyze(50000, 3);
        if (report.savingsPercent < 70.0) {
            throw new AssertionError("Flyweight memory savings should exceed 70%, got: " + report.savingsPercent);
        }

        System.out.println("PASSED (Reference caching & memory optimization verified)");
    }

    private static void testInterpreter() {
        System.out.print("[Interpreter Pattern] Testing Policy DSL Parser & AST Evaluation... ");
        String dsl = "noise > 60 AND (zone == \"residential\" OR hour >= 22)";
        Expression expr = PolicyParser.parse(dsl);

        // Verify AST JSON serialization
        JsonObject astJson = expr.toAstJson();
        if (astJson == null || !"logical".equals(astJson.get("type").getAsString())) {
            throw new AssertionError("AST JSON serialization failed: expected logical root");
        }

        PolicyContext ctx1 = new PolicyContext();
        ctx1.set("noise", 65.0);
        ctx1.set("zone", "residential");
        ctx1.set("hour", 14.0);

        if (!expr.interpret(ctx1)) {
            throw new AssertionError("DSL test 1 should have evaluated to TRUE");
        }

        PolicyContext ctx2 = new PolicyContext();
        ctx2.set("noise", 50.0);
        ctx2.set("zone", "residential");
        ctx2.set("hour", 23.0);

        if (expr.interpret(ctx2)) {
            throw new AssertionError("DSL test 2 should have evaluated to FALSE (noise <= 60)");
        }

        System.out.println("PASSED (AST grammar, JSON tree & dynamic evaluation verified)");
    }
}
