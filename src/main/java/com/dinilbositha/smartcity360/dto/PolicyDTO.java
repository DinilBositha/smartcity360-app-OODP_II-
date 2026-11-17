package com.dinilbositha.smartcity360.dto;

import java.sql.Timestamp;


 //Data Transfer Object representing a municipal policy rule in the database.
 // Evaluated dynamically by the Interpreter Pattern AST engine.

public class PolicyDTO {
    private long id;
    private String policyCode;
    private String name;
    private String description;
    private String dslText;
    private String category;
    private boolean active = true;
    private Timestamp createdAt;

    public PolicyDTO() {}

    public PolicyDTO(long id, String policyCode, String name, String category, String dslText, boolean active) {
        this.id = id;
        this.policyCode = policyCode;
        this.name = name;
        this.category = category;
        this.dslText = dslText;
        this.active = active;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPolicyCode() { return policyCode; }
    public void setPolicyCode(String policyCode) { this.policyCode = policyCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDslText() { return dslText; }
    public void setDslText(String dslText) { this.dslText = dslText; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
