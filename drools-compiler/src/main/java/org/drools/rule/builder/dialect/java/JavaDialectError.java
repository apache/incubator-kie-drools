package org.drools.rule.builder.dialect.java;

import org.drools.compiler.DroolsError;

public class JavaDialectError extends DroolsError {
    private String message;

    public JavaDialectError(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
    public String toString() {
        return "[JavaDialectError message='" + this.message + "']";
    }

}
