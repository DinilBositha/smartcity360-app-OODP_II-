package com.dinilbositha.smartcity360.dto;

import java.sql.Timestamp;

public class PolicyEvaluationDTO {
    private long id;
    private long policyId;
    private String policyName;
    private String contextJson;
    private boolean result;
    private Timestamp evaluatedAt;

    public PolicyEvaluationDTO() {}

    public PolicyEvaluationDTO(long id, long policyId, String policyName, String contextJson, boolean result, Timestamp evaluatedAt) {
        this.id = id;
        this.policyId = policyId;
        this.policyName = policyName;
        this.contextJson = contextJson;
        this.result = result;
        this.evaluatedAt = evaluatedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getPolicyId() { return policyId; }
    public void setPolicyId(long policyId) { this.policyId = policyId; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public String getContextJson() { return contextJson; }
    public void setContextJson(String contextJson) { this.contextJson = contextJson; }

    public boolean isResult() { return result; }
    public void setResult(boolean result) { this.result = result; }

    public Timestamp getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(Timestamp evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
