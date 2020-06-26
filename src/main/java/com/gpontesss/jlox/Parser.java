package com.gpontesss.jlox;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private List<String> errors = new ArrayList<>();
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Expr parse() {
        try {
            return expression();
        } catch(ParseError error) {
            return null;
        }
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr left = comparison();

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
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
            Token operator = previous();
            Expr right = comparison();
            left = new Expr.Binary(left, operator, right);
        }
        return left;
    }

    private Expr addition() {
        Expr left = multiplication();
        while(match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            left = new Expr.Binary(left, operator, right);
        }
        return left;
    }

    private Expr multiplication() {
        Expr left = unary();
        while(match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expr right = unary();
            left = new Expr.Binary(left, operator, right);
        }
        return left;
    }

    private Expr unary() {
        if (match(TokenType.BANG,  TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        Token token = advance();

        if (token == null) {
            throw error(token, "Unexpected");
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
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
            default:
            throw error(peek(), "Expected expression");
        }
    }

    private boolean match(TokenType ...types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean istAtEnd() {
        return current >= tokens.size() || peek().type == TokenType.EOF;
    }

    private Token advance() {
        if (istAtEnd()) return null;
        current++;
        return tokens.get(current-1);
    }

    private Token peek() {
        if (current >= tokens.size()) return null;
        return tokens.get(current);
    }

    private boolean check(TokenType type) {
        if (istAtEnd()) return false;
        return peek().type == type;
    }

    private Token previous() {
        return tokens.get(current-1);
    }

    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while(!istAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;

            switch(peek().type) {
                case CLASS:
                case IF:
                case FOR:
                case FUN:
                case VAR:
                case WHILE:
                case PRINT:
                    return;
                default:
                    break;
            }

            advance();
        }
    }
}
