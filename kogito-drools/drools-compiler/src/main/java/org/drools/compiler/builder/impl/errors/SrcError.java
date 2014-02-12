package org.drools.compiler.builder.impl.errors;

import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.compiler.DroolsError;

public class SrcError extends DroolsError {

    private Object object;
    private String message;
    private int[]  errorLines = new int[0];

    public SrcError(Object object,
                    String message) {
        super(null);
        this.object = object;
        this.message = message;
    }

    public Object getObject() {
        return this.object;
    }

    public int[] getLines() {
        return this.errorLines;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.message);
        buf.append(" : ");
        buf.append("\n");
        if (this.object instanceof CompilationProblem[]) {
            final CompilationProblem[] problem = (CompilationProblem[]) this.object;
            for (CompilationProblem aProblem : problem) {
                buf.append("\t");
                buf.append(aProblem);
                buf.append("\n");
            }
        } else if (this.object != null) {
            buf.append(this.object);
        }
        return buf.toString();
    }
}
