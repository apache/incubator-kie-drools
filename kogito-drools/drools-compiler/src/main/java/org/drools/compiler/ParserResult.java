package org.drools.compiler;

public class ParserResult {
    private int    row;
    private int    col;
    private String message;

    public ParserResult(String message,
                        int row,
                        int col) {
        super();
        this.message = message;
        this.row = row;
        this.col = col;
    }

    public String getMessage() {
        return message;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

}
