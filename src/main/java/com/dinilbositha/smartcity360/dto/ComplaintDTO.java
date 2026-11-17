package com.dinilbositha.smartcity360.dto;

public class ComplaintDTO {
    private Long id;
    private String complaintCode;
    private Long userId;
    private String category;
    private String description;
    private String location;
    private int severity;
    private String status;
    private Long assignedHandlerId;
    private String handlerName;
    private Long relatedLightId;
    private String submittedAt;
    private String handledAt;

    public ComplaintDTO() {}

    public ComplaintDTO(Long id, String complaintCode, Long userId, String category,
                        String description, String location, int severity, String status,
                        Long assignedHandlerId, String handlerName, String submittedAt, String handledAt) {
        this.id = id;
        this.complaintCode = complaintCode;
        this.userId = userId;
        this.category = category;
        this.description = description;
        this.location = location;
        this.severity = severity;
        this.status = status;
        this.assignedHandlerId = assignedHandlerId;
        this.handlerName = handlerName;
        this.submittedAt = submittedAt;
        this.handledAt = handledAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComplaintCode() {
        return complaintCode;
    }

    public void setComplaintCode(String complaintCode) {
        this.complaintCode = complaintCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAssignedHandlerId() {
        return assignedHandlerId;
    }

    public void setAssignedHandlerId(Long assignedHandlerId) {
        this.assignedHandlerId = assignedHandlerId;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public Long getRelatedLightId() {
        return relatedLightId;
    }

    public void setRelatedLightId(Long relatedLightId) {
        this.relatedLightId = relatedLightId;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getHandledAt() {
        return handledAt;
    }

    public void setHandledAt(String handledAt) {
        this.handledAt = handledAt;
    }
}
