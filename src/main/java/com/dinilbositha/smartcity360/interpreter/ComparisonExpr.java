package com.dinilbositha.smartcity360.interpreter;

import com.google.gson.JsonObject;
import java.util.Objects;


 // Terminal expression evaluating binary comparisons: variable operator literal.

public class ComparisonExpr implements Expression {
    private final String variable;
    private final String operator;
    private final Object literal;

    public ComparisonExpr(String variable, String operator, Object literal) {
        this.variable = Objects.requireNonNull(variable, "Variable cannot be null");
        this.operator = Objects.requireNonNull(operator, "Operator cannot be null");
        this.literal = Objects.requireNonNull(literal, "Literal cannot be null");
    }

    public String getVariable() { return variable; }
    public String getOperator() { return operator; }
    public Object getLiteral() { return literal; }

    @Override
    public boolean interpret(PolicyContext context) {
        if (!context.has(variable)) {
            return false;
        }

        if (literal instanceof Number) {
            Double actual = context.getNumber(variable);
            if (actual == null) return false;
            double target = ((Number) literal).doubleValue();

            switch (operator) {
                case ">":  return actual > target;
                case "<":  return actual < target;
                case ">=": return actual >= target;
                case "<=": return actual <= target;
                case "=":
                case "==": return Math.abs(actual - target) < 0.0001;
                case "!=": return Math.abs(actual - target) >= 0.0001;
                default: return false;
            }
        } else {
            String actual = context.getString(variable);
            String target = literal.toString();
            switch (operator) {
                case "=":
                case "==": return Objects.equals(actual, target);
                case "!=": return !Objects.equals(actual, target);
                default: return false;
            }
        }
    }

    @Override
    public String toTree(int indent) {
        String pad = "  ".repeat(Math.max(0, indent));
        return String.format("%s%s %s %s", pad, variable, operator, literal);
    }

    @Override
    public JsonObject toAstJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "compare");
        json.addProperty("className", "ComparisonExpr");
        json.addProperty("operator", operator);
        json.addProperty("variable", variable);
        json.addProperty("literal", literal.toString());
        return json;
    }
}
