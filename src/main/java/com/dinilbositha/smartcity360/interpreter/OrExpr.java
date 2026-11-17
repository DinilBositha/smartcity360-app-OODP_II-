package com.dinilbositha.smartcity360.interpreter;

import com.google.gson.JsonObject;
import java.util.Objects;


 // Non-terminal expression representing logical OR.

public class OrExpr implements Expression {
    private final Expression left;
    private final Expression right;

    public OrExpr(Expression left, Expression right) {
        this.left = Objects.requireNonNull(left, "Left expression cannot be null");
        this.right = Objects.requireNonNull(right, "Right expression cannot be null");
    }

    public Expression getLeft() { return left; }
    public Expression getRight() { return right; }

    @Override
    public boolean interpret(PolicyContext context) {
        return left.interpret(context) || right.interpret(context);
    }

    @Override
    public String toTree(int indent) {
        String pad = "  ".repeat(Math.max(0, indent));
        return pad + "OR\n" + left.toTree(indent + 1) + "\n" + right.toTree(indent + 1);
    }

    @Override
    public JsonObject toAstJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "logical");
        json.addProperty("className", "OrExpr");
        json.addProperty("operator", "OR");
        json.add("left", left.toAstJson());
        json.add("right", right.toAstJson());
        return json;
    }
}
