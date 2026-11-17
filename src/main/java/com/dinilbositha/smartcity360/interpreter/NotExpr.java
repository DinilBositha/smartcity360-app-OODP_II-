package com.dinilbositha.smartcity360.interpreter;

import com.google.gson.JsonObject;
import java.util.Objects;


 // Non-terminal expression representing logical NOT.

public class NotExpr implements Expression {
    private final Expression child;

    public NotExpr(Expression child) {
        this.child = Objects.requireNonNull(child, "Child expression cannot be null");
    }

    public Expression getChild() { return child; }

    @Override
    public boolean interpret(PolicyContext context) {
        return !child.interpret(context);
    }

    @Override
    public String toTree(int indent) {
        String pad = "  ".repeat(Math.max(0, indent));
        return pad + "NOT\n" + child.toTree(indent + 1);
    }

    @Override
    public JsonObject toAstJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "logical");
        json.addProperty("className", "NotExpr");
        json.addProperty("operator", "NOT");
        json.add("child", child.toAstJson());
        return json;
    }
}
