package org.drools.modelcompiler.builder.generator.query;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.PrettyPrinter;

/**
 * Used to generate n-arity query model, copy and paste the result in the files in comment
 */
public class QueryGenerator {

    public static void main(String[] args) {
        int arity = 5;
        CompilationUnit queryDefImpl = new QueryDefImplGenerator(arity).generate();

        System.out.println("queryDefImpl = " + new PrettyPrinter().print(queryDefImpl));
    }
}
