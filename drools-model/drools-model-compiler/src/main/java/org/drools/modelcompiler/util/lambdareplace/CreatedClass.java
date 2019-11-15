package org.drools.modelcompiler.util.lambdareplace;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.PrettyPrinter;

public class CreatedClass {

    private final CompilationUnit compilationUnit;
    private final String className;

    public CreatedClass(CompilationUnit compilationUnit, String className) {
        this.compilationUnit = compilationUnit;
        this.className = className;
    }

    public String getCompilationUnitAsString() {
        return new PrettyPrinter().print(compilationUnit);
    }

    public String getClassName() {
        return className;
    }
}
