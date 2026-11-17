package com.dinilbositha.smartcity360.flyweight;

import java.util.Objects;

 // Intrinsic state shared across thousands of smart streetlights.
 // Immutable to prevent cross-device side effects.

public final class LightType {
    private final String model;
    private final int wattage;
    private final int lumens;
    private final int colorTemp;
    private final String manufacturer;
    private final String firmware;

    public LightType(String model, int wattage, int lumens, int colorTemp, String manufacturer, String firmware) {
        this.model = Objects.requireNonNull(model, "Model cannot be null");
        this.wattage = wattage;
        this.lumens = lumens;
        this.colorTemp = colorTemp;
        this.manufacturer = Objects.requireNonNull(manufacturer, "Manufacturer cannot be null");
        this.firmware = Objects.requireNonNull(firmware, "Firmware cannot be null");
    }

    public String getModel() { return model; }
    public int getWattage() { return wattage; }
    public int getLumens() { return lumens; }
    public int getColorTemp() { return colorTemp; }
    public String getManufacturer() { return manufacturer; }
    public String getFirmware() { return firmware; }

    public String formatTelemetry(String id, double lat, double lon, int brightness, boolean on) {
        return String.format("Light [%s] @ (%.4f, %.4f) | Model: %s (%dW, %dK) | Status: %s | Output: %d%%",
                id, lat, lon, model, wattage, colorTemp, on ? "ON" : "OFF", brightness);
    }
}
