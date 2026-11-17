package com.dinilbositha.smartcity360.interpreter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

 // Context holding telemetry and environmental variables for policy evaluation.

public class PolicyContext {
    private final Map<String, Object> variables = new HashMap<>();

    public PolicyContext() {}

    public PolicyContext(Map<String, Object> initial) {
        if (initial != null) {
            variables.putAll(initial);
        }
    }

    public void set(String key, Object value) {
        variables.put(key, value);
    }

    public Object get(String key) {
        return variables.get(key);
    }

    public Double getNumber(String key) {
        Object val = variables.get(key);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        if (val != null) {
            try {
                return Double.parseDouble(val.toString());
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    public String getString(String key) {
        Object val = variables.get(key);
        return val != null ? val.toString() : null;
    }

    public boolean has(String key) {
        return variables.containsKey(key);
    }

    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(variables);
    }
}
