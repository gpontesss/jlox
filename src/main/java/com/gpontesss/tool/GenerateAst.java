package com.gpontesss.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: generate_ast <output dir>");
            System.exit(64);
        }

        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Unary    : Token operator, Expr right",
            "Literal  : Object value"
        ));
    }

    private static void defineAst(String dir, String className, List<String> types) throws IOException {
        String path = dir + "/" + className + ".java";
        PrintWriter writer = new PrintWriter(path);

        writer.println("package com.gpontesss.jlox;");
        writer.println();
        // writer.println("import java.util.List;");
        // writer.println();
        writer.println("abstract class "+ className + " {");
        defineVisitor(writer, "    ", className, types);

        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        for (String type: types) {
            defineType(writer, "    ", className, type);
        }

        writer.println("}");
        writer.close();
    }

    static void defineType(PrintWriter writer, String indent, String baseName, String type) {
        String className = type.split(":")[0].trim();
        String[] fields = type.split(":")[1].trim().split(", ");

        writer.println();
        writer.println(indent+"static class "+className+" {");

        defineFields(writer, indent+indent, fields);
        writer.println();

        writer.print(indent+indent);
        writer.println(className+"("+String.join(", ", fields)+") {");

        for (String field: fields) {
            String fieldName = field.split(" ")[1];
            writer.print(indent+indent+indent);
            writer.println("this."+fieldName+" = "+fieldName+";");
        }
        writer.println(indent+indent+"}");
        writer.println();

        writer.println(indent+indent+"<R> R accept(Visitor<R> visitor) {");
        writer.println(indent+indent+indent+"return visitor.visit"+className+baseName+"(this);");
        writer.println(indent+indent+"}");

        writer.println(indent+"}");
    }

    static void defineFields(PrintWriter writer, String indent, String[] fields) {
        for (String field: fields) {
            writer.print(indent);
            writer.println("final "+field+";");
        }
    }

    static void defineVisitor(PrintWriter writer, String indent, String className, List<String> types) {
        writer.println(indent+"interface Visitor<R> {");

        for (String type: types) {
            String typeName = type.split(":")[0].trim();
            writer.println(indent+indent+"R visit"+typeName+className+"("+typeName+" "+className.toLowerCase()+");");

        }
        writer.println(indent+"}");
    }
}
