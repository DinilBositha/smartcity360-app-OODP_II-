package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.composite.ZoneComposite;

public interface ZoneDAO {
    ZoneComposite loadHierarchyFromDB();
    boolean updateZoneStatus(String zoneName, String status);
    boolean logActivity(String activityType, String message, String severity, String zoneName);
    long createComponent(Long parentId, String type, String name, String address, Double lat, Double lng);
    boolean deleteComponent(long id);
}
