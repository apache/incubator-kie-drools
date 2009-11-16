package org.drools.examples.pacman;

public class CellContents {
    private Cell     cell;
    private CellType cellType;

    public CellContents(Cell cell,
                        CellType cellType) {
        this.cell = cell;
        this.cellType = cellType;
    }

    public Cell getCell() {
        return cell;
    }

    public CellType getCellType() {
        return cellType;
    }

    @Override
    public String toString() {
        return "CellType " + cellType;
    }
}
