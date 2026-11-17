package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.dto.ComplaintDTO;
import java.util.List;

public interface ComplaintDAO {
    boolean saveComplaint(ComplaintDTO complaint);
    List<ComplaintDTO> getAllComplaints();
    List<ComplaintDTO> getComplaintsByUserId(long userId);
    List<ComplaintDTO> getComplaintsByHandlerId(long handlerId);
    ComplaintDTO findById(long id);
    boolean updateStatus(long id, String newStatus);
    boolean escalateComplaint(long id, String reason);
    boolean deleteComplaint(long id);
}
