package org.drools.modelcompiler.util.lambdareplace;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.PrettyPrinter;

public class CreatedClass {

    private final CompilationUnit compilationUnit;
    private final String className;
    private final String packageName;

    public CreatedClass(CompilationUnit compilationUnit, String className, String packageName) {
        this.compilationUnit = compilationUnit;
        this.className = className;
        this.packageName = packageName;
    }

    public String getCompilationUnitAsString() {
        return new PrettyPrinter().print(compilationUnit);
    }

    public String getClassNameWithoutPackage() {
        return className;
    }

    public String getClassNameWithPackage() {
        return String.format("%s.%s", packageName, className);
    }

    public String getClassNamePath() {
        return String.format("%s/%s.java", packageName.replace(".", "/"), className);
    }
}
