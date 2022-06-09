package org.optaplanner.examples.nqueens.optional.solver.tracking;

public class NQueensStepTracking {

    private final int columnIndex;
    private final int rowIndex;

    public NQueensStepTracking(int columnIndex, int rowIndex) {
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
