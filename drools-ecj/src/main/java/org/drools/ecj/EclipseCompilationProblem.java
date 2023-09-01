package org.drools.ecj;

import org.kie.memorycompiler.CompilationProblem;
import org.eclipse.jdt.core.compiler.IProblem;

/**
 * Wrapping an Eclipse compiler problem
 */
public final class EclipseCompilationProblem implements CompilationProblem {

    private final IProblem problem;

    public EclipseCompilationProblem(final IProblem pProblem) {
        problem = pProblem;
    }

    public boolean isError() {
        return problem.isError();
    }

    public String getFileName() {
        return new String(problem.getOriginatingFileName());
    }

    public int getStartLine() {
        return problem.getSourceLineNumber();
    }

    public int getStartColumn() {
        return problem.getSourceStart();
    }

    public int getEndLine() {
        return getStartLine();
    }

    public int getEndColumn() {
        return problem.getSourceEnd();
    }

    public String getMessage() {
        return problem.getMessage();
    }

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

    public int getId() {
        return problem.getID();
    }

}
