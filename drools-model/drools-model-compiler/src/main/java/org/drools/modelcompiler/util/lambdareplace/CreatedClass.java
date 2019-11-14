package org.drools.modelcompiler.util.lambdareplace;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.PrettyPrinter;

public class CreatedClass {

    private CompilationUnit compilationUnit;

    public CreatedClass(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public String getCompilationUnitAsString() {
        return new PrettyPrinter().print(compilationUnit);
    }


}
