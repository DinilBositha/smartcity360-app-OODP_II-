package com.dinilbositha.smartcity360.interpreter;

import com.google.gson.JsonObject;


public interface Expression {

    boolean interpret(PolicyContext context);

     // Returns an indented textual representation of this AST node and its sub-trees.

    String toTree(int indent);

    JsonObject toAstJson();
}
