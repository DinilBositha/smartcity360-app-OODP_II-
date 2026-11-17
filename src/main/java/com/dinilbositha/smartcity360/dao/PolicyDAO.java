package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.dto.PolicyDTO;
import com.dinilbositha.smartcity360.dto.PolicyEvaluationDTO;
import java.util.List;

public interface PolicyDAO {
    List<PolicyDTO> getAllPolicies();
    PolicyDTO getPolicyById(long id);
    PolicyDTO findByCode(String code);
    boolean createPolicy(PolicyDTO policy);
    boolean updatePolicy(PolicyDTO policy);
    boolean deletePolicy(long id);
    boolean logEvaluation(long policyId, String contextJson, boolean result);
    List<PolicyEvaluationDTO> getRecentEvaluations(int limit);
    int getTotalEvaluationsCount();
    double getComplianceRate();
}
