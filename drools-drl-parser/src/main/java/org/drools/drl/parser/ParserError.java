package org.drools.drl.parser;

import org.kie.api.io.Resource;

public class ParserError extends DroolsError {
    private final int    row;
    private final int    col;
    private final String message;
    private final String namespace;

    public ParserError(final String message,
                       final int row,
                       final int col) {
        this(null, message, row, col);
    }

    public ParserError(final Resource resource,
                       final String message,
                       final int row,
                       final int col) {
        this(resource, message, row, col, "");
    }

    public ParserError(final Resource resource,
                       final String message,
                       final int row,
                       final int col,
                       final String namespace) {
        super(resource);
        this.message = message;
        this.row = row;
        this.col = col;
        this.namespace = namespace;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public int[] getLines() {
        return new int[] { this.row };
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    public String toString() {
        return "[" + this.row + "," + this.col + "]: " + this.message;
    }

}
