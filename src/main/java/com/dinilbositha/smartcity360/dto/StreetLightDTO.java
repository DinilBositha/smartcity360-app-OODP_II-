package com.dinilbositha.smartcity360.dto;

public class StreetLightDTO {
    private long id;
    private String lightCode;
    private long typeId;
    private String model;
    private int wattage;
    private int lumens;
    private int colorTemp;
    private double lat;
    private double lng;
    private boolean isOn = true;
    private boolean reportedIssue = false;

    public StreetLightDTO() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getLightCode() { return lightCode; }
    public void setLightCode(String lightCode) { this.lightCode = lightCode; }

    public long getTypeId() { return typeId; }
    public void setTypeId(long typeId) { this.typeId = typeId; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getWattage() { return wattage; }
    public void setWattage(int wattage) { this.wattage = wattage; }

    public int getLumens() { return lumens; }
    public void setLumens(int lumens) { this.lumens = lumens; }

    public int getColorTemp() { return colorTemp; }
    public void setColorTemp(int colorTemp) { this.colorTemp = colorTemp; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public boolean isOn() { return isOn; }
    public void setOn(boolean on) { isOn = on; }

    public boolean isReportedIssue() { return reportedIssue; }
    public void setReportedIssue(boolean reportedIssue) { this.reportedIssue = reportedIssue; }
}
