package org.drools.drl.parser.lang;

public class ParseException extends RuntimeException {

    private static final long serialVersionUID = 510l;

    private int               lineNumber;

    private Throwable         cause;

    /**
     * Thrown if there is an exception related to parsing a line in a drl file.
     * For more generic exception, a different exception class will be used.
     */
    public ParseException(final String message,
                          final int lineNumber) {
        super( message );
        this.lineNumber = lineNumber;
    }

    /**
     * Allows nesting of misc exceptions, yet preserving the line number
     * that triggered the error.
     */
    public ParseException(final String message,
                          final int lineNumber,
                          final Throwable cause) {
        super( message );
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
    @Override
    public String getMessage() {
        if ( this.cause == null ) {
            return super.getMessage() + " Line number: " + this.lineNumber;
        } else {
            return super.getMessage() + " Line number: " + this.lineNumber + ". Caused by: " + this.cause.getMessage();
        }
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public synchronized Throwable getCause() {
        return this.cause;
    }

}
