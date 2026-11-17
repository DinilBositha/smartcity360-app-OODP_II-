package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.dto.AnnouncementDTO;
import java.util.List;

public interface AnnouncementDAO {
    List<AnnouncementDTO> getAllAnnouncements();
    List<AnnouncementDTO> getActiveAnnouncements();
    boolean createAnnouncement(AnnouncementDTO announcement);
    boolean updateAnnouncement(AnnouncementDTO announcement);
    boolean deleteAnnouncement(long id);
}
