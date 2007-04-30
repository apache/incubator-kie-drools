package org.drools.rule.builder.dialect.java;

import org.drools.compiler.DroolsError;

public class JavaDialectError extends DroolsError {
    private String message;

    public JavaDialectError(final String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "[JavaDialectError message='" + this.message + "']";
    }

}
