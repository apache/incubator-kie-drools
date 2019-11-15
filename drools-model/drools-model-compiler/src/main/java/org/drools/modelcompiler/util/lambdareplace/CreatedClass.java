package org.drools.modelcompiler.util.lambdareplace;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreatedClass that = (CreatedClass) o;
        return compilationUnit.equals(that.compilationUnit) &&
                className.equals(that.className) &&
                packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compilationUnit, className, packageName);
    }
}
