package com.dinilbositha.smartcity360.composite;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


 //Composite node representing aggregate city zones (City, District, Street).

public class ZoneComposite extends ZoneComponent {
    private final List<ZoneComponent> children = new ArrayList<>();

    public ZoneComposite(String name, String type) {
        super(name, type);
    }

    @Override
    public void add(ZoneComponent component) {
        children.add(Objects.requireNonNull(component, "Child component cannot be null"));
    }

    @Override
    public void remove(ZoneComponent component) {
        children.remove(component);
    }

    @Override
    public List<ZoneComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void execute(String command, List<String> logs) {
        String cmd = command != null ? command.trim().toLowerCase() : "";
        if (cmd.contains("shut") || cmd.contains("emerg")) {
            setActive(false);
        } else if (cmd.contains("rest")) {
            setActive(true);
        }

        logs.add(String.format("[%s: %s] Broadcasting command '%s' to %d sub-nodes", 
                getType(), getName(), command, children.size()));
        for (ZoneComponent child : children) {
            child.execute(command, logs);
        }
    }

    @Override
    public double getUsage() {
        return children.stream().mapToDouble(ZoneComponent::getUsage).sum();
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", getName());
        json.addProperty("type", getType());
        json.addProperty("usage", getUsage());
        json.addProperty("isLeaf", false);
        json.addProperty("active", isActive());
        json.addProperty("status", getStatus());

        JsonArray arr = new JsonArray();
        for (ZoneComponent child : children) {
            arr.add(child.toJson());
        }
        json.add("children", arr);
        return json;
    }
}
