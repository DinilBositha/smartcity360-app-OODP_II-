package com.dinilbositha.smartcity360.composite;

import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.List;


 //Base component interface for hierarchical city zones.

public abstract class ZoneComponent {
    private final String name;
    private final String type;
    private boolean active = true;
    private String status = "active";

    protected ZoneComponent(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.status = active ? "active" : "inactive";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status : "active";
        this.active = !"inactive".equalsIgnoreCase(this.status);
    }

    public abstract void execute(String command, List<String> logs);

    public abstract double getUsage();

    public void add(ZoneComponent component) {
        throw new UnsupportedOperationException("Leaf nodes cannot have children");
    }

    public void remove(ZoneComponent component) {
        throw new UnsupportedOperationException("Leaf nodes cannot have children");
    }

    public List<ZoneComponent> getChildren() {
        return Collections.emptyList();
    }

    public ZoneComponent find(String targetName) {
        if (targetName == null) return null;
        if (this.name.equalsIgnoreCase(targetName.trim())) {
            return this;
        }
        for (ZoneComponent child : getChildren()) {
            ZoneComponent match = child.find(targetName);
            if (match != null) return match;
        }
        return null;
    }

    public abstract JsonObject toJson();
}
