package org.drools.examples.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * Abstract base class for all kinds of groups of related cells.
 */
public abstract class CellGroup extends SetOfNine {

    public static final Set<Integer> ALL_NINE = new CopyOnWriteArraySet<>();
    static {
        for (int i = 1; i <= 9; i++) ALL_NINE.add(i);
    }

    private List<Cell> cells = new ArrayList<>();
    
    /**
     * Constructor.
     */
    protected CellGroup() {
        super();
    }

    /**
     * Add another Cell object to the cells of this group.
     * @param cell a Cell object.
     */
    public void addCell(Cell cell) {
        cells.add(cell);
    }

    /**
     * Returns the Cell objects in this group.
     * @return a List of Cell objects.
     */
    public List<Cell> getCells() {
        return cells;
    }
}
