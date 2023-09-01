package org.drools.drl.parser.lang;

import org.drools.drl.parser.DroolsError;


public class ExpanderException extends DroolsError {

    private static final long serialVersionUID = 510l;

    private String            message;
    private int[]             line;

    public ExpanderException(final String message,
                             final int line) {
        this.message = message;
        this.line = new int[] { line };
    }
    
    public int[] getLines() {
        return this.line;
    }

    public String getMessage() {
        return "[" + this.line[0] + "] " + this.message;
    }
    
    public int getLine() {
        return this.line[0];
    }
    
    public String toString() {
        return this.getMessage();
    }

}
