package com.dinilbositha.smartcity360.flyweight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


 // Thread-safe Flyweight Factory caching shared LightType instances.

public final class LightFactory {
    private static final Map<String, LightType> REGISTRY = new ConcurrentHashMap<>();

    private LightFactory() {}

    public static LightType get(String model, int wattage, int lumens, int colorTemp, String manufacturer, String firmware) {
        String key = String.format("%s:%d:%d:%d:%s:%s", model, wattage, lumens, colorTemp, manufacturer, firmware);
        return REGISTRY.computeIfAbsent(key, k -> new LightType(model, wattage, lumens, colorTemp, manufacturer, firmware));
    }

    public static LightType getDefaultStandard() {
        return get("Lumiled-Smart-50W", 50, 6000, 4000, "Philips", "v2.4.1");
    }

    public static int getRegisteredTypeCount() {
        return REGISTRY.size();
    }

    public static void clear() {
        REGISTRY.clear();
    }
}
