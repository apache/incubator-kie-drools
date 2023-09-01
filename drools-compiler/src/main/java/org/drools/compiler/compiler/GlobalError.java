package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.parser.DroolsError;

public class GlobalError extends DroolsError {
    private final GlobalDescr globalDescr;
    private String message;

    public GlobalError(final GlobalDescr globalDescr, final String message) {
        super(globalDescr.getResource());
        this.globalDescr = globalDescr;
        this.message = message;
    }

    @Override
    public String getNamespace() {
        return globalDescr.getNamespace();
    }

    public String getGlobal() {
        return globalDescr.getIdentifier();
    }
    
    public int[] getLines() {
        return new int[] { globalDescr.getLine() };
    }

    public String getMessage() {
        return message;
    }
    
    public String toString() {
        return "GlobalError: " + getGlobal() + " : " + message;
    }

}
