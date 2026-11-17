package com.dinilbositha.smartcity360.composite;

import com.google.gson.JsonObject;
import java.util.List;


//Leaf node representing an individual building or infrastructure facility.

public class BuildingLeaf extends ZoneComponent {
    private double usageKwh;
    private boolean powerActive = true;

    public BuildingLeaf(String name, double usageKwh) {
        super(name, "Building");
        this.usageKwh = Math.max(0, usageKwh);
    }

    public boolean isPowerActive() {
        return powerActive;
    }

    public void setPowerActive(boolean active) {
        this.powerActive = active;
        setActive(active);
        if (!active) {
            this.usageKwh = 0;
        }
    }

    @Override
    public void execute(String command, List<String> logs) {
        String cmd = command != null ? command.trim().toLowerCase() : "";
        switch (cmd) {
            case "shutdown power":
            case "shutdown":
            case "emergency":
                setPowerActive(false);
                logs.add(String.format("[Leaf: %s] Power GRID ISOLATED (Usage: 0 kW)", getName()));
                break;
            case "restore power":
            case "restore":
                setPowerActive(true);
                this.usageKwh = 120.5;
                logs.add(String.format("[Leaf: %s] Power RESTORED (Usage: %.1f kW)", getName(), usageKwh));
                break;
            case "report usage":
            case "report":
                logs.add(String.format("[Leaf: %s] Telemetry: %.1f kWh, Status: %s", 
                        getName(), usageKwh, powerActive ? "ONLINE" : "OFFLINE"));
                break;
            default:
                logs.add(String.format("[Leaf: %s] Executed generic action: %s", getName(), command));
                break;
        }
    }

    @Override
    public double getUsage() {
        return powerActive ? usageKwh : 0.0;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", getName());
        json.addProperty("type", getType());
        json.addProperty("usage", getUsage());
        json.addProperty("isLeaf", true);
        json.addProperty("powerActive", powerActive);
        json.addProperty("active", isActive());
        json.addProperty("status", getStatus());
        return json;
    }
}
