package com.gpontesss.jlox;

import com.gpontesss.jlox.Expr.Binary;
import com.gpontesss.jlox.Expr.Grouping;
import com.gpontesss.jlox.Expr.Literal;
import com.gpontesss.jlox.Expr.Unary;

public class RpnAstPrinter implements Expr.Visitor<String> {

    public static void main(String[] args) {
        // -123 * (45.67 + 13)
        Expr expr = new Expr.Binary(
            new Expr.Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Expr.Literal(123)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(new Expr.Binary(
                new Expr.Literal(45.67),
                new Token(TokenType.PLUS, "+", null, 1),
                new Expr.Literal(13))));
        System.out.println(new RpnAstPrinter().print(expr));
    }

    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        return expr.left.accept(this)+" "+expr.right.accept(this)+" "+expr.operator.lexeme;
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return expr.expression.accept(this);
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return expr.right.accept(this)+" "+expr.operator.lexeme;
    }
}
