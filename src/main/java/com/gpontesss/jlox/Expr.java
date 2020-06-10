package com.gpontesss.jlox;

abstract class Expr {

    static class Binary {
        final Expr left;
        final Token operator;
        final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    static class Grouping {
        final Expr expr;

        Grouping(Expr expr) {
            this.expr = expr;
        }
    }

    static class Unary {
        final Token operator;
        final Expr expr;

        Unary(Token operator, Expr expr) {
            this.operator = operator;
            this.expr = expr;
        }
    }

    static class Literal {
        final Object value;

        Literal(Object value) {
            this.value = value;
        }
    }
}
