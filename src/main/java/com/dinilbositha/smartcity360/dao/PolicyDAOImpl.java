package com.dinilbositha.smartcity360.dao;

import com.dinilbositha.smartcity360.dto.PolicyDTO;
import com.dinilbositha.smartcity360.dto.PolicyEvaluationDTO;
import com.dinilbositha.smartcity360.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PolicyDAOImpl implements PolicyDAO {

    @Override
    public List<PolicyDTO> getAllPolicies() {
        List<PolicyDTO> list = new ArrayList<>();
        String sql = "SELECT id, policy_code, name, description, dsl_text, category, active, created_at FROM policies ORDER BY id ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PolicyDTO p = new PolicyDTO();
                p.setId(rs.getLong("id"));
                p.setPolicyCode(rs.getString("policy_code"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setDslText(rs.getString("dsl_text"));
                p.setCategory(rs.getString("category"));
                p.setActive(rs.getBoolean("active"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] getAllPolicies error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public PolicyDTO getPolicyById(long id) {
        String sql = "SELECT id, policy_code, name, description, dsl_text, category, active, created_at FROM policies WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PolicyDTO p = new PolicyDTO();
                    p.setId(rs.getLong("id"));
                    p.setPolicyCode(rs.getString("policy_code"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setDslText(rs.getString("dsl_text"));
                    p.setCategory(rs.getString("category"));
                    p.setActive(rs.getBoolean("active"));
                    p.setCreatedAt(rs.getTimestamp("created_at"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] getPolicyById error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public PolicyDTO findByCode(String code) {
        String sql = "SELECT id, policy_code, name, description, dsl_text, category, active, created_at FROM policies WHERE policy_code = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PolicyDTO p = new PolicyDTO();
                    p.setId(rs.getLong("id"));
                    p.setPolicyCode(rs.getString("policy_code"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setDslText(rs.getString("dsl_text"));
                    p.setCategory(rs.getString("category"));
                    p.setActive(rs.getBoolean("active"));
                    p.setCreatedAt(rs.getTimestamp("created_at"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] findByCode error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean createPolicy(PolicyDTO policy) {
        String sql = "INSERT INTO policies (policy_code, name, description, dsl_text, category, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String code = policy.getPolicyCode();
            if (code == null || code.trim().isEmpty()) {
                code = "POL-" + System.currentTimeMillis() % 100000;
            }
            ps.setString(1, code);
            ps.setString(2, policy.getName());
            ps.setString(3, policy.getDescription() != null ? policy.getDescription() : "");
            ps.setString(4, policy.getDslText());
            ps.setString(5, policy.getCategory() != null ? policy.getCategory() : "General");
            ps.setBoolean(6, policy.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        policy.setId(keys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] createPolicy error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updatePolicy(PolicyDTO policy) {
        String sql = "UPDATE policies SET name = ?, description = ?, dsl_text = ?, category = ?, active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, policy.getName());
            ps.setString(2, policy.getDescription());
            ps.setString(3, policy.getDslText());
            ps.setString(4, policy.getCategory());
            ps.setBoolean(5, policy.isActive());
            ps.setLong(6, policy.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] updatePolicy error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deletePolicy(long id) {
        // Also delete associated evaluations to maintain integrity
        String delEvals = "DELETE FROM policy_evaluations WHERE policy_id = ?";
        String delPolicy = "DELETE FROM policies WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psEvals = conn.prepareStatement(delEvals);
                 PreparedStatement psPol = conn.prepareStatement(delPolicy)) {
                psEvals.setLong(1, id);
                psEvals.executeUpdate();

                psPol.setLong(1, id);
                int affected = psPol.executeUpdate();
                conn.commit();
                return affected > 0;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] deletePolicy error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean logEvaluation(long policyId, String contextJson, boolean result) {
        String sql = "INSERT INTO policy_evaluations (policy_id, context_json, result) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (policyId > 0) {
                ps.setLong(1, policyId);
            } else {
                // Default to first active policy if direct ad-hoc rule
                ps.setLong(1, 1);
            }
            ps.setString(2, contextJson != null ? contextJson : "{}");
            ps.setBoolean(3, result);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] logEvaluation error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<PolicyEvaluationDTO> getRecentEvaluations(int limit) {
        List<PolicyEvaluationDTO> list = new ArrayList<>();
        String sql = "SELECT pe.id, pe.policy_id, p.name AS policy_name, pe.context_json, pe.result, pe.evaluated_at " +
                     "FROM policy_evaluations pe " +
                     "LEFT JOIN policies p ON pe.policy_id = p.id " +
                     "ORDER BY pe.id DESC LIMIT ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 10);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PolicyEvaluationDTO dto = new PolicyEvaluationDTO(
                        rs.getLong("id"),
                        rs.getLong("policy_id"),
                        rs.getString("policy_name"),
                        rs.getString("context_json"),
                        rs.getBoolean("result"),
                        rs.getTimestamp("evaluated_at")
                    );
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] getRecentEvaluations error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public int getTotalEvaluationsCount() {
        String sql = "SELECT COUNT(*) FROM policy_evaluations";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] getTotalEvaluationsCount error: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public double getComplianceRate() {
        String sql = "SELECT COUNT(*) AS total, SUM(CASE WHEN result = 1 THEN 1 ELSE 0 END) AS passed FROM policy_evaluations";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int total = rs.getInt("total");
                int passed = rs.getInt("passed");
                if (total == 0) return 100.0;
                return Math.round((passed * 100.0 / total) * 10.0) / 10.0;
            }
        } catch (SQLException e) {
            System.err.println("[PolicyDAO] getComplianceRate error: " + e.getMessage());
        }
        return 100.0;
    }
}
