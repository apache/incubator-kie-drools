package org.drools.template.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a row in a decision table.
 */
public class Row {
    int rowNum;
    List<Cell> cells = new ArrayList<>();

    public Row() {

    }

    Row(int r, Column[] columns) {
        rowNum = r;
        for (int i = 0; i < columns.length; i++) {
            cells.add(columns[i].createCell(this));
        }
    }

    public int getRowNumber() {
        return rowNum;
    }

    Cell getCell(int columnIndex) {
        return cells.get(columnIndex);
    }

    boolean isEmpty() {
        for (Cell cell : cells) {
            if (!cell.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public String toString() {
        return "Row " + rowNum + cells + "\n";
    }


}
