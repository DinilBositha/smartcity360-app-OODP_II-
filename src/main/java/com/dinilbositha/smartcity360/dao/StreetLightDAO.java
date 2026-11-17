package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.dto.StreetLightDTO;
import java.util.List;

public interface StreetLightDAO {
    List<StreetLightDTO> getAllLights();
    boolean updateLightStatus(long id, boolean isOn);
    boolean reportIssue(long id, boolean hasIssue);
    boolean createLight(StreetLightDTO light);
    boolean deleteLight(long id);
}
