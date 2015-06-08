package org.optaplanner.examples.nqueens.integration.util;

public class QueenCoordinates  {

    public static QueenCoordinates createQueenCoordinates(int columnIndex, int rowIndex) {
        return new QueenCoordinates(columnIndex, rowIndex);
    }

    private final int columnIndex;
    private final int rowIndex;

    public QueenCoordinates(int columnIndex, int rowIndex) {
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

}
