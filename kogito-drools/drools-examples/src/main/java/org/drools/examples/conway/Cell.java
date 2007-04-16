package org.drools.examples.conway;

/**
 * A <code>Cell</code> represents a single cell within a <code>CellGrid</code>.
 * A cell may be either live or dead. <p/>
 *
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 * @see CellState
 * @see CellGrid
 */
public class Cell {

    private CellState cellState = CellState.DEAD;

    private int       phase     = Phase.DONE;

    private int       liveNeighbors;

    private int       col;

    private int       row;

    public Cell(int col,
                int row) {
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getPhase() {
        return this.phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getLiveNeighbors() {
        return this.liveNeighbors;
    }

    public void setLiveNeighbors(int liveNeighbors) {
        this.liveNeighbors = liveNeighbors;
    }

    /**
     * @return this cell's current life state
     * @see #queueNextCellState(org.drools.examples.conway.CellState)
     * @see CellState
     */
    public CellState getCellState() {
        return this.cellState;
    }

    /**
     * Sets this cells state
     *
     * @param newState
     *            new state for this cell
     * @see CellState
     */
    public void setCellState(final CellState newState) {
        this.cellState = newState;
    }

    public String toString() {
        return cellState + " col=" + this.col + " row=" + this.row + " phase '" + phase + "' liveNeighbors '" + liveNeighbors + "'";
    }
}
