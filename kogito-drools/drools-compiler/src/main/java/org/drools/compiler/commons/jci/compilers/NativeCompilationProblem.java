package org.drools.compiler.commons.jci.compilers;

import org.drools.compiler.commons.jci.problems.CompilationProblem;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class NativeCompilationProblem implements CompilationProblem {

    private final Diagnostic<JavaFileObject> problem;

    public NativeCompilationProblem(Diagnostic<JavaFileObject> problem) {
        this.problem = problem;
    }

    public boolean isError() {
        return problem.getKind() == Diagnostic.Kind.ERROR;
    }

    public String getFileName() {
        return problem.getSource().getName().substring(1);
    }

    public int getStartLine() {
        return (int)problem.getLineNumber();
    }

    public int getStartColumn() {
        return (int)problem.getColumnNumber();
    }

    public int getEndLine() {
        return (int)problem.getLineNumber();
    }

    public int getEndColumn() {
        return (int)problem.getColumnNumber();
    }

    public String getMessage() {
        return problem.getMessage(null);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getFileName()).append(" (");
        sb.append(getStartLine());
        sb.append(":");
        sb.append(getStartColumn());
        sb.append(") : ");
        sb.append(getMessage());
        return sb.toString();
    }
}
