package org.drools.lang;

import org.drools.DroolsRuntimeException;

public class ParseException extends DroolsRuntimeException {
	

    private static final long serialVersionUID = -7500818890340701977L;
    
    private int lineNumber;
    
    /**
     * Thrown if there is an exception related to parsing a line in a drl file.
     * For more generic exception, a different exception class will be used.
     */
    public ParseException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }
    
    /**
     * The line number on which the error occurred.
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    public String getMessage() {
        return super.getMessage() + " Line number: " + lineNumber;
    }
    
    public String toString() {
        return getMessage();
    }
    
}
