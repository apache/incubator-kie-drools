/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples.conway;

import org.drools.examples.conway.patterns.ConwayPattern;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * A <code>CellGrid</code> represents a grid of <code>Cell</code> objects.
 * <p/>
 * 
 * @see Cell
 */
public class CellGridImpl implements CellGrid {

    private final Cell[][]     cells;

    private final StatefulKnowledgeSession    session;

    private final ConwayRuleDelegate delegate;

    /**
     * Constructs a CellGrid
     * 
     * @param rows
     *            number of rows in the grid
     * @param columns
     *            number of columns in the grid
     */
    public CellGridImpl(final int rows,
                        final int columns,
                        final int executionControl) {

        this.cells = new Cell[rows][columns];

        if ( executionControl == AbstractRunConway.RULEFLOWGROUP ) {
            delegate = new RuleFlowDelegate();
        } else {
            delegate = new AgendaGroupDelegate();
        }
        
        this.session = delegate.getSession();

        this.session.insert( this );

        // populate the array of Cells and hook each
        // cell up with its neighbors...
        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                final Cell newCell = new Cell( column,
                                               row );
                this.cells[row][column] = newCell;
                this.session.insert( newCell );
            }
        }

        delegate.init();
        //delegate.killAll();
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#getCellAt(int, int)
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#getCellAt(int, int)
     */
    public Cell getCellAt(final int row,
                          final int column) {
        return this.cells[row][column];
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#getNumberOfRows()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#getNumberOfRows()
     */
    public int getNumberOfRows() {
        return this.cells.length;
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#getNumberOfColumns()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#getNumberOfColumns()
     */
    public int getNumberOfColumns() {
        return this.cells[0].length;
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#nextGeneration()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#nextGeneration()
     */
    public boolean nextGeneration() {
        return delegate.nextGeneration();
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#killAll()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#killAll()
     */
    public void killAll() {
        this.delegate.killAll();
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#setPattern(org.drools.examples.conway.patterns.ConwayPattern)
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#setPattern(org.drools.examples.conway.patterns.ConwayPattern)
     */
    public void setPattern(final ConwayPattern pattern) {
        final boolean[][] gridData = pattern.getPattern();
        int gridWidth = gridData[0].length;
        int gridHeight = gridData.length;

        int columnOffset = 0;
        int rowOffset = 0;

        if ( gridWidth > getNumberOfColumns() ) {
            gridWidth = getNumberOfColumns();
        } else {
            columnOffset = (getNumberOfColumns() - gridWidth) / 2;
        }

        if ( gridHeight > getNumberOfRows() ) {
            gridHeight = getNumberOfRows();
        } else {
            rowOffset = (getNumberOfRows() - gridHeight) / 2;
        }

        this.delegate.killAll();

        for ( int column = 0; column < gridWidth; column++ ) {
            for ( int row = 0; row < gridHeight; row++ ) {
                if ( gridData[row][column] ) {
                    final Cell cell = getCellAt( row + rowOffset,
                                                 column + columnOffset );
                    updateCell( cell, CellState.LIVE );
                }
            }
        }

        //this.delegate.setPattern();
    }
    
    public void updateCell(Cell cell, CellState state) {
        cell.setCellState( state );
        this.session.update( this.session.getFactHandle( cell ),
                             cell );
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#dispose()
     */
    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#dispose()
     */
    public void dispose() {
        if ( this.session != null ) {
            this.session.dispose();
        }
    }

    /* (non-Javadoc)
     * @see org.drools.examples.conway.CellGrid#toString()
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();

        for ( int i = 0; i < this.cells.length; i++ ) {
            for ( int j = 0; j < this.cells[i].length; j++ ) {
                Cell cell = this.cells[i][j];
                System.out.print( cell.getLiveNeighbors() + ((cell.getCellState() == CellState.DEAD) ? "D" : "L") + " " );
            }
            System.out.println( "" );
        }

        return buf.toString();
    }
}
