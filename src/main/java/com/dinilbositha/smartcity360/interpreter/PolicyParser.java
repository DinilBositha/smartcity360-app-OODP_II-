package com.dinilbositha.smartcity360.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


 // Recursive descent parser tokenizing and building Abstract Syntax Trees
 // from the SmartCity360 policy DSL.

public class PolicyParser {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
        "\\s*(>=|<=|==|!=|=|>|<|\\bAND\\b|\\bOR\\b|\\bNOT\\b|\\(|\\)|\"[^\"]*\"|[a-zA-Z_][a-zA-Z0-9_]*|\\d+(?:\\.\\d+)?)\\s*",
        Pattern.CASE_INSENSITIVE
    );

    private final List<String> tokens;
    private int pos = 0;

    public PolicyParser(String dsl) {
        this.tokens = tokenize(dsl);
    }

    public static Expression parse(String dsl) {
        if (dsl == null || dsl.trim().isEmpty()) {
            throw new IllegalArgumentException("DSL string cannot be empty");
        }
        return new PolicyParser(dsl).parseExpression();
    }

    private static List<String> tokenize(String input) {
        List<String> list = new ArrayList<>();
        Matcher m = TOKEN_PATTERN.matcher(input);
        while (m.find()) {
            String token = m.group(1);
            if (token != null && !token.isEmpty()) {
                list.add(token);
            }
        }
        return list;
    }

    private Expression parseExpression() {
        return parseOr();
    }

    private Expression parseOr() {
        Expression expr = parseAnd();
        while (matchIgnoreCase("OR")) {
            Expression right = parseAnd();
            expr = new OrExpr(expr, right);
        }
        return expr;
    }

    private Expression parseAnd() {
        Expression expr = parsePrimary();
        while (matchIgnoreCase("AND")) {
            Expression right = parsePrimary();
            expr = new AndExpr(expr, right);
        }
        return expr;
    }

    private Expression parsePrimary() {
        if (matchIgnoreCase("NOT")) {
            return new NotExpr(parsePrimary());
        }
        if (match("(")) {
            Expression expr = parseOr();
            consume(")");
            return expr;
        }
        return parseComparison();
    }

    private Expression parseComparison() {
        String identifier = nextToken();
        String operator = nextToken();

        if (!operator.matches(">=|<=|==|!=|=|>|<")) {
            throw new IllegalArgumentException("Expected comparison operator at: " + operator);
        }

        if ("=".equals(operator)) operator = "==";
        String rawVal = nextToken();
        Object literal;
        if (rawVal.startsWith("\"") && rawVal.endsWith("\"")) {
            literal = rawVal.substring(1, rawVal.length() - 1);
        } else {
            try {
                literal = Double.parseDouble(rawVal);
            } catch (NumberFormatException e) {
                literal = rawVal;
            }
        }
        return new ComparisonExpr(identifier, operator, literal);
    }

    private boolean match(String expected) {
        if (pos < tokens.size() && tokens.get(pos).equals(expected)) {
            pos++;
            return true;
        }
        return false;
    }

    private boolean matchIgnoreCase(String expected) {
        if (pos < tokens.size() && tokens.get(pos).equalsIgnoreCase(expected)) {
            pos++;
            return true;
        }
        return false;
    }

    private void consume(String expected) {
        if (!match(expected)) {
            throw new IllegalArgumentException("Expected '" + expected + "' at position " + pos);
        }
    }

    private String nextToken() {
        if (pos >= tokens.size()) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }
        return tokens.get(pos++);
    }
}
