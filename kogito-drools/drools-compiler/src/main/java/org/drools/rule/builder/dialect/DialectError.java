package org.drools.rule.builder.dialect;

import org.drools.compiler.DroolsError;

public class DialectError extends DroolsError {
    private String message;
    private static final int[] line = new int[0];

    public DialectError(final String message) {
        super();
        this.message = message;
    }

    public int[] getErrorLines() {
        return line;
    }
    
    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "[DialectError message='" + this.message + "']";
    }

}
