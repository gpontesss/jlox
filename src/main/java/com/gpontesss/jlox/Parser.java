package com.gpontesss.jlox;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static void main(String[] args) {
        Scanner scanner = new Scanner("2 - (2 * 5 * 3)");
        List<Token> tokens = scanner.scanTokens();
        for (Token token: tokens) {
            System.out.println(token);
        }

        Parser parser = new Parser(tokens);
        Expr expr = parser.expression();

        List<String> errors = parser.getErrors();
        if (errors.size() > 0) {
            System.err.println(errors);
            System.exit(64);
        }
        System.out.println(new AstPrinter().print(expr));
    }

    private final List<Token> tokens;
    private List<String> errors = new ArrayList<>();
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr left = comparison();
        Token operator = peek();
        // if (operator == null || operator.type == TokenType.EOF)
        //      return left;
        if (operator != null && (operator.type == TokenType.BANG_EQUAL || operator.type == TokenType.EQUAL_EQUAL)) {
            advance();
            Expr right = comparison();
            return new Expr.Binary(left, operator, right);
        }
        return left;
        // errors.add("Expected equality token, got "+operator.lexeme);
        // return null;
    }

    private Expr comparison() {
        Expr left = addition();
        Token operator = peek();
        // if (operator == null || operator.type == TokenType.EOF)
        //     return left;
        if (operator != null &&
        (operator.type == TokenType.LESS ||
        operator.type == TokenType.GREATER ||
        operator.type == TokenType.LESS_EQUAL ||
        operator.type == TokenType.GREATER_EQUAL)) {
            advance();
            Expr right = addition();
            return new Expr.Binary(left, operator, right);
        }
        return left;
        // errors.add("Expected comparison token, got "+operator.lexeme);
        // return null;
    }

    private Expr addition() {
        Expr left = multiplication();
        Token operator = peek();
        // if (operator == null || operator.type == TokenType.EOF)
        //     return left;
        // advance();
        if (operator != null && (operator.type == TokenType.MINUS || operator.type == TokenType.PLUS)) {
            advance();
            Expr right = multiplication();
            return new Expr.Binary(left, operator, right);
        }
        return left;
        // errors.add("Expected addition token, got "+operator.lexeme);
        // return null;
    }

    private Expr multiplication() {
        Expr left = unary();
        Token operator = peek();
        // if (operator == null || operator.type == TokenType.EOF)
        //     return left;
        if (operator != null && (operator.type == TokenType.STAR || operator.type == TokenType.SLASH)) {
            advance();
            Expr right = unary();
            return new Expr.Binary(left, operator, right);
        }
        return left;
        // errors.add("Expected multiplication token, got "+operator.lexeme);
        // return null;
    }

    private Expr unary() {
        Token token = peek();
        if (token != null && (token.type == TokenType.BANG || token.type == TokenType.MINUS)) {
            advance();
            Expr right = unary();
            return new Expr.Unary(token, right);
        }
        return primary();
    }

    private Expr primary() {
        Token token = advance();
        if (token == null || token.type == TokenType.EOF)
            return null;
        switch (token.type) {
            case NUMBER:
            case STRING:
            return new Expr.Literal(token.literal);
            case FALSE:
            return new Expr.Literal(false);
            case TRUE:
            return new Expr.Literal(true);
            case NIL:
            return new Expr.Literal(null);
            case LEFT_PAREN:
                Expr expr = expression();
                Token nextToken = peek();
                if (nextToken == null || nextToken.type == TokenType.EOF) {
                    errors.add("Unexpected EOF");
                    return null;
                }
                if (nextToken.type != TokenType.RIGHT_PAREN) {
                    errors.add("Expected ')' token, got "+nextToken.lexeme);
                    return null;
                }
                advance();
                return new Expr.Grouping(expr);
            default:
            errors.add("Unexpected "+token.lexeme+" token");
            return null;
        }
    }

    private Token advance() {
        if (current >= tokens.size()) return null;
        current++;
        return tokens.get(current-1);
    }

    private Token peek() {
        if (current >= tokens.size()) return null;
        return tokens.get(current);
    }
}
