package org.drools.modelcompiler.builder.generator.query;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinter;

/**
 * Used to generate n-arity query model, copy and paste the result in the files in comment
 */
public class QueryGenerator {

    public static void main(String[] args) {
        int arity = 5;


        // QueryDef
        CompilationUnit queryDef = new QueryDefGenerator(arity).generate();
        System.out.println("\n\n\nQueryDef");
        System.out.println(new PrettyPrinter().print(queryDef));


        // QueryDefImpl
        System.out.println("\n\n\nQueryDefImpl");
        CompilationUnit queryDefImpl = new QueryDefImplGenerator(arity).generate();
        System.out.println(new PrettyPrinter().print(queryDefImpl));


        // FlowDSL
        System.out.println("\n\n\nFlowDSL");
        CompilationUnit flowDSL = new FlowDSLGenerator(arity).generate();
        System.out.println(new PrettyPrinter().print(flowDSL));
    }
}
