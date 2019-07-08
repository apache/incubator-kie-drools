package org.drools.modelcompiler.builder.generator.query;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.PrettyPrinter;

/**
 * Used to generate n-arity query model, copy and paste the result in the files in comment
 */
public class QueryGenerator {

    public static void main(String[] args) throws IOException {
        int arity = 10;

        CompilationUnit patternDSL = new CompilationUnit();
        ClassOrInterfaceDeclaration clazzPatternDSL = patternDSL.addClass("PatternDSL");
        range(arity).forEach(arity1 -> new PatternDSLQueryGenerator(clazzPatternDSL, arity1).generate());
        System.out.println(new PrettyPrinter().print(clazzPatternDSL));

        CompilationUnit flowDSL = new CompilationUnit();
        ClassOrInterfaceDeclaration clazzFlowDSL = flowDSL.addClass("FlowDSL");
        range(arity).forEach(arity1 -> new FlowDSLQueryGenerator(clazzFlowDSL, arity1).generate());
        System.out.println(new PrettyPrinter().print(clazzFlowDSL));

        range(arity).forEach(QueryGenerator::generateQueryDef);
        range(arity).forEach(QueryGenerator::generateQueryDefImpl);
    }

    private static IntStream range(int arity) {
        return IntStream.range(1, arity + 1);
    }

    private static void generateQueryDefImpl(int arity) {
        QueryDefImplGenerator queryDefImplGenerator = new QueryDefImplGenerator(arity);
        CompilationUnit queryDefImpl = queryDefImplGenerator.generate();
        String generatedClass = new PrettyPrinter().print(queryDefImpl);
        try {
            Files.write(Paths.get("/tmp/", "queryimpl", queryDefImplGenerator.getClassName() + ".java"), generatedClass.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateQueryDef(int arity) {
        QueryDefGenerator queryDefGenerator = new QueryDefGenerator(arity);
        CompilationUnit queryDef = queryDefGenerator.generate();
        String generatedClass = new PrettyPrinter().print(queryDef);
        try {
            Files.write(Paths.get("/tmp/", "querydef", queryDefGenerator.getClassName() + ".java"), generatedClass.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
