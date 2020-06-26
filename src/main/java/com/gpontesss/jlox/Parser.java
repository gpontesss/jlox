package com.gpontesss.jlox;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static void main(String[] args) {
        Scanner scanner = new Scanner("(2 - 2) * 5 * 3");
        List<Token> tokens = scanner.scanTokens();
        if (Lox.hadError) {
            System.exit(1);
        }

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

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = advance();
            Expr right = comparison();
            left = new Expr.Binary(left, operator, right);
        }

        return left;
    }

    private Expr comparison() {
        Expr left = addition();
        while(match(
            TokenType.LESS,
            TokenType.GREATER,
            TokenType.LESS_EQUAL,
            TokenType.GREATER_EQUAL
        )) {
            Token operator = advance();
            Expr right = comparison();
            left = new Expr.Binary(left, operator, right);
        }
        return left;
    }

    private Expr addition() {
        Expr left = multiplication();
        while(match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = advance();
            Expr right = multiplication();
            left = new Expr.Binary(left, operator, right);
        }
        return left;
    }

    private Expr multiplication() {
        Expr left = unary();
        while(match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = advance();
            Expr right = unary();
            left = new Expr.Binary(left, operator, right);
        }
        return left;
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
        if (token == null || token.type == TokenType.EOF) {
            errors.add("Unexpected EOF");
            return null;
        }
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
            return group();
            default:
            errors.add("Unexpected "+token.lexeme+" token");
            return null;
        }
    }

    private Expr group() {
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
    }

    private boolean match(TokenType ...types) {
        if (istAtEnd())
            return false;
        for (TokenType type : types) {
            if (peek().type == type)
                return true;
        }
        return false;
    }

    private boolean istAtEnd() {
        return current >= tokens.size() || peek().type == TokenType.EOF;
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
