package org.drools.examples.conway;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A <code>Cell</code> represents a single cell within a <code>CellGrid</code>.
 * A cell may be either live or dead. <p/>
 *
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 * @see CellState
 * @see CellGrid
 */
public class Cell {

    private Set       neighbors   = new HashSet();

    private CellState state       = CellState.DEAD;

    private CellState queuedState = null;

    /**
     * @return the number of neighbors that this cell has
     * @see #getNumberOfLiveNeighbors()
     */
    public int getNumberOfNeighboringCells() {
        return neighbors.size();
    }

    /**
     * @return the number of live neighbors that this cell has
     * @see #getNumberOfNeighboringCells()
     */
    public int getNumberOfLiveNeighbors() {
        int numberOfLiveNeighbors = 0;
        Iterator it = neighbors.iterator();
        Cell cell = null;
        while ( it.hasNext() ) {
            cell = (Cell) it.next();
            if ( cell.getCellState() == CellState.LIVE ) {
                numberOfLiveNeighbors++;
            }
        }
        return numberOfLiveNeighbors;
    }

    /**
     * ads a new neighbor to this neighbor
     *
     * @param neighbor
     *            new neighbor
     */
    public void addNeighbor(Cell neighbor) {
        neighbors.add( neighbor );
        neighbor.neighbors.add( this );
    }

    /**
     * tell this cell to queue its next live state. this is the state that this
     * cell will be in after the cell is transitioned (after the next
     * iteration). This transition state is necessary because of the 2 phase
     * process involved in evolution.
     *
     * @param nextLiveState
     *            this cell's next live state
     * @see CellState
     * @see #getCellState()
     * @see #transitionState()
     */
    public void queueNextCellState(CellState nextLiveState) {
        if ( nextLiveState != state ) {
            queuedState = nextLiveState;
        }
    }

    /**
     * Transitions this cell to its next state of evolution
     *
     * @return <code>true</code> if the state changed, otherwise false
     * @see #queueNextCellState(CellState)
     */
    public boolean transitionState() {
        boolean stateChanged = false;
        if ( queuedState != null ) {
            state = queuedState;
            queuedState = null;
            stateChanged = true;
        }
        return stateChanged;
    }

    /**
     * @return this cell's current life state
     * @see #queueNextCellState(org.drools.examples.conway.CellState)
     * @see CellState
     */
    public CellState getCellState() {
        return state;
    }

    /**
     * Sets this cells state
     *
     * @param newState
     *            new state for this cell
     * @see CellState
     */
    public void setCellState(CellState newState) {
        state = newState;
    }
}
