package org.drools.drl.parser.lang.dsl;

public class DSLMappingParseException extends Exception {
    private static final long serialVersionUID = 510l;

    public String             message;
    public int                line;

    public DSLMappingParseException(final String message,
                                    final int line) {
        this.message = message;
        this.line = line;
    }

    public String getMessage() {
        return this.message;
    }

    public int getLine() {
        return this.line;
    }

    public String toString() {
        return "[ line " + this.line + " ]" + this.message;
    }

}
