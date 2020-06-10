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
            "Grouping : Expr expr",
            "Unary    : Token operator, Expr expr",
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

        for (String type: types) {
            className = type.split(":")[0].trim();
            String[] fields = type.split(":")[1].trim().split(", ");

            writer.println();
            writer.println("    static class "+className+" {");

            for (String field: fields) {
                writer.print("        ");
                writer.println("final "+field+";");
            }
            writer.println();
            writer.print("        ");
            writer.println(className+"("+String.join(", ", fields)+") {");

            for (String field: fields) {
                String fieldName = field.split(" ")[1];
                writer.print("            ");
                writer.println("this."+fieldName+" = "+fieldName+";");
            }
            writer.println("        }");
            writer.println("    }");
        }

        writer.println("}");
        writer.close();
    }
}
