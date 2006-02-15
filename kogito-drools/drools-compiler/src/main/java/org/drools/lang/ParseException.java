package org.drools.lang;

import org.drools.DroolsRuntimeException;

public class ParseException extends DroolsRuntimeException {
	

    private static final long serialVersionUID = -7500818890340701977L;
    
    private int lineNumber;

    private Throwable cause;
    
    /**
     * Thrown if there is an exception related to parsing a line in a drl file.
     * For more generic exception, a different exception class will be used.
     */
    public ParseException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }
    
    /**
     * Allows nesting of misc exceptions, yet preserving the line number
     * that triggered the error.
     */
    public ParseException(String message, int lineNumber, Throwable cause) {
        super(message);
        this.lineNumber = lineNumber;
        this.cause = cause;
    }
    
    /**
     * The line number on which the error occurred.
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * This will print out a summary, including the line number. 
     * It will also print out the cause message if applicable.
     */
    public String getMessage() {
        if (cause == null) {
            return super.getMessage() + " Line number: " + lineNumber;
        } else {
            return super.getMessage() + " Line number: " + lineNumber + ". Caused by: " + cause.getMessage();
        }
    }
    
    public String toString() {
        return getMessage();
    }
    
    public Throwable getCause() {
        return cause;
    }
    
}
