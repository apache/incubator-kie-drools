package org.drools.examples.conway;

import org.drools.examples.conway.Cell;
import org.drools.examples.conway.CellState;

import junit.framework.TestCase;

/**
 *
 */
public class CellTest extends TestCase {

    public void testDefaultStateIsDead() {
    	Cell c1 = new Cell();

        assertEquals("c1 had wrong inital state", CellState.DEAD, c1.getCellState());
    }

    public void testAddNeighbor() {
    	Cell c1 = new Cell();
    	Cell c2 = new Cell();
        assertEquals("c1 had wrong number of neighbors before adding neighbor", 0, c1.getNumberOfNeighboringCells());
        assertEquals("c2 had wrong number of neighbors before adding neighbor", 0, c2.getNumberOfNeighboringCells());


        c1.addNeighbor(c2);

        assertEquals("c1 had wrong number of neighbors after adding neighbor", 1, c1.getNumberOfNeighboringCells());
        assertEquals("c2 had wrong number of neighbors after adding neighbor", 1, c2.getNumberOfNeighboringCells());
    }

    public void testAddingNeighborMultipleTimes() {
    	Cell c1 = new Cell();
    	Cell c2 = new Cell();
        assertEquals("c1 had wrong number of neighbors before adding neighbor", 0, c1.getNumberOfNeighboringCells());
        assertEquals("c2 had wrong number of neighbors before adding neighbor", 0, c2.getNumberOfNeighboringCells());


        c1.addNeighbor(c2);

        assertEquals("c1 had wrong number of neighbors after adding neighbor", 1, c1.getNumberOfNeighboringCells());
        assertEquals("c2 had wrong number of neighbors after adding neighbor", 1, c2.getNumberOfNeighboringCells());

        c1.addNeighbor(c2);
        c1.addNeighbor(c2);
        c2.addNeighbor(c1);

        assertEquals("c1 had wrong number of neighbors after adding neighbor again", 1, c1.getNumberOfNeighboringCells());
        assertEquals("c2 had wrong number of neighbors after adding neighbor again", 1, c2.getNumberOfNeighboringCells());
    }

    public void testNumberOfLiveNeighbors() {
    	Cell c1 = new Cell();
    	Cell c2 = new Cell();
    	Cell c3 = new Cell();

        c1.addNeighbor(c2);

        assertEquals("c1 had wrong number of live neighbors initally", 0, c1.getNumberOfLiveNeighbors());

        c2.setCellState(CellState.LIVE);

        assertEquals("c1 had wrong number of live neighbors", 1, c1.getNumberOfLiveNeighbors());
        c3.setCellState(CellState.LIVE);
        c1.addNeighbor(c3);
        assertEquals("c1 had wrong number of live neighbors", 2, c1.getNumberOfLiveNeighbors());

    }

    public void testStateQueuing() {
    	Cell c1 = new Cell();
        assertEquals("c1 had wrong inital state", CellState.DEAD, c1.getCellState());

        c1.setCellState(CellState.LIVE);
        assertEquals("c1 had wrong state", CellState.LIVE, c1.getCellState());

        c1.queueNextCellState(CellState.DEAD);
        assertEquals("c1 had wrong state", CellState.LIVE, c1.getCellState());

        c1.transitionState();
        assertEquals("c1 had wrong state", CellState.DEAD, c1.getCellState());

        c1.queueNextCellState(CellState.LIVE);
        assertEquals("c1 had wrong state", CellState.DEAD, c1.getCellState());

        c1.transitionState();
        assertEquals("c1 had wrong state", CellState.LIVE, c1.getCellState());

    }

}
