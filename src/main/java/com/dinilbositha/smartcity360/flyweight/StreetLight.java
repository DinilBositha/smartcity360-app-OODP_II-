package com.dinilbositha.smartcity360.flyweight;

import java.util.Objects;


 // Context class maintaining unique extrinsic properties of an individual streetlight,
 // referencing the shared intrinsic LightType flyweight.

public class StreetLight {
    private final String id;
    private final double latitude;
    private final double longitude;
    private int brightness;
    private boolean operational;
    private final LightType type;

    public StreetLight(String id, double latitude, double longitude, int brightness, LightType type) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.latitude = latitude;
        this.longitude = longitude;
        this.brightness = Math.max(0, Math.min(100, brightness));
        this.operational = true;
        this.type = Objects.requireNonNull(type, "LightType flyweight cannot be null");
    }

    public String getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getBrightness() { return brightness; }
    public boolean isOperational() { return operational; }
    public LightType getType() { return type; }

    public void setBrightness(int brightness) {
        this.brightness = Math.max(0, Math.min(100, brightness));
    }

    public void setOperational(boolean operational) {
        this.operational = operational;
    }

    public String getTelemetry() {
        return type.formatTelemetry(id, latitude, longitude, brightness, operational);
    }
}
