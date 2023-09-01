package org.drools.compiler.rule.builder.dialect;

import org.drools.drl.parser.DroolsError;
import org.kie.api.io.Resource;

public class DialectError extends DroolsError {
    private String message;
    private static final int[] line = new int[0];

    public DialectError(final Resource resource, final String message) {
        super(resource);
        this.message = message;
    }

    public int[] getLines() {
        return line;
    }
    
    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "[DialectError message='" + this.message + "']";
    }

}
