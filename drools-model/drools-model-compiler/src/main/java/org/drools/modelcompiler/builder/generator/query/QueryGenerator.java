package org.drools.modelcompiler.builder.generator.query;

import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.PrettyPrinter;

/**
 * Used to generate n-arity query model, copy and paste the result in the files in comment
 */
public class QueryGenerator {

    public static void main(String[] args) {
        int arity = 10;

        CompilationUnit patternDSL = new CompilationUnit();
        ClassOrInterfaceDeclaration clazzPatternDSL = patternDSL.addClass("PatternDSL");
        range(arity).forEach(arity1 -> new PatternDSLQueryGenerator(clazzPatternDSL, arity1).generate());
        System.out.println(new PrettyPrinter().print(clazzPatternDSL));

        CompilationUnit flowDSL = new CompilationUnit();
        ClassOrInterfaceDeclaration clazzFlowDSL = flowDSL.addClass("FlowDSL");
        range(arity).forEach(arity1 -> new FlowDSLQueryGenerator(clazzFlowDSL, arity1).generate());
        System.out.println(new PrettyPrinter().print(clazzFlowDSL));
    }

    private static IntStream range(int arity) {
        return IntStream.range(1, arity + 1);
    }

    private static void generateQueryDefImpl(int arity) {
        System.out.println("\n\n\nQueryDefImpl");
        CompilationUnit queryDefImpl = new QueryDefImplGenerator(arity).generate();
        System.out.println(new PrettyPrinter().print(queryDefImpl));
    }

    private static void generateQueryDef(int arity) {
        CompilationUnit queryDef = new QueryDefGenerator(arity).generate();
        System.out.println("\n\n\nQueryDef");
        System.out.println(new PrettyPrinter().print(queryDef));
    }
}
