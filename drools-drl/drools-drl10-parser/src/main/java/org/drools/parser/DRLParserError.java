package org.drools.parser;

public class DRLParserError {

    private int lineNumber;
    private int column;
    private String message;

    private Exception exception;

    public DRLParserError(int lineNumber, int column, String message) {
        this.lineNumber = lineNumber;
        this.column = column;
        this.message = message;
    }

    public DRLParserError(Exception exception) {
        this.exception = exception;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DRLParserError{" +
                "lineNumber=" + lineNumber +
                ", column=" + column +
                ", message='" + message + '\'' +
                ", exception=" + exception +
                '}';
    }
}
