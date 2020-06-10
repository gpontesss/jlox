package com.gpontesss.jlox;

public class AstPrinter implements Expr.Visitor<String> {

    public static void main(String[] args) {
        Expr expr = new Expr.Binary(
            new Expr.Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Expr.Literal(123)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(new Expr.Literal(45.67)));
        System.out.println(new AstPrinter().print(expr));
    }

    public String print(Expr expr) {
        return expr.accept(this);
    }

    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    private String parenthesize(String value, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("("+value);
        for(Expr expr: exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
