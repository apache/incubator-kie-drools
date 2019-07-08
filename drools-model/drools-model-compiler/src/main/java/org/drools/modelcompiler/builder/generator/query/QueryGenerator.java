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


//        generateQueryDef(arity);
//        generateQueryDefImpl(arity);
//        generateFlowDSL(arity);
//        generatePatternDSL(arity);


        CompilationUnit aggregator = new CompilationUnit();
        ClassOrInterfaceDeclaration clazz = aggregator.addClass(String.format("FlowDSL", arity));

        IntStream.range(1, 11).forEach(arity1 -> generatePatternDSL(clazz, arity1));

    }

    private static void generatePatternDSL(ClassOrInterfaceDeclaration aggregator, int arity) {
        ClassOrInterfaceDeclaration patternDSL = new PatternDSLQueryGenerator(aggregator, arity).generate();
        System.out.println(new PrettyPrinter().print(patternDSL));
    }

    private static void generateFlowDSL(int arity) {
        CompilationUnit flowDSL = new FlowDSLQueryGenerator(arity).generate();
        System.out.println(new PrettyPrinter().print(flowDSL));
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
