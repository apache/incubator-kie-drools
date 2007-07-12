package org.drools.examples.conway;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.examples.conway.patterns.ConwayPattern;

/**
 * A <code>CellGrid</code> represents a grid of <code>Cell</code> objects.
 * <p/>
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 * @see Cell
 */
public class CellGrid {

    private final Cell[][]  cells;

    private StatefulSession session;

    /**
     * Constructs a CellGrid
     * 
     * @param rows
     *            number of rows in the grid
     * @param columns
     *            number of columns in the grid
     */
    public CellGrid(final int rows,
                    final int columns) {
        this.cells = new Cell[rows][columns];

        final RuleBase ruleBase = ConwayRuleBaseFactory.getRuleBase();
        this.session = ruleBase.newStatefulSession();

        DefaultAgendaEventListener listener = new DefaultAgendaEventListener() {
            public void agendaGroupPopped(AgendaGroupPoppedEvent event,
                                          WorkingMemory workingMemory) {
//                System.out.println( "popped AgendaGroup = '" + event.getAgendaGroup().getName() + "'" );
//                System.out.println( CellGrid.this.toString() );
//                System.out.println( "" );
            }
        };

        this.session.addEventListener( listener );

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
        this.session.setFocus( "register neighbor" );
        this.session.fireAllRules();
    }

    /**
     * @param row
     *            row of the requested cell
     * @param column
     *            column of the requested cell
     * @return the cell at the specified coordinates
     * @see Cell
     */
    public Cell getCellAt(final int row,
                          final int column) {
        return this.cells[row][column];
    }

    /**
     * @return the number of rows in this grid
     * @see #getNumberOfColumns()
     */
    public int getNumberOfRows() {
        return this.cells.length;
    }

    /**
     * @return the number of columns in this grid
     * @see #getNumberOfRows()
     */
    public int getNumberOfColumns() {
        return this.cells[0].length;
    }

    /**
     * Moves this grid to its next generation
     * 
     * @return <code>true</code> if the state changed, otherwise false
     * @see #transitionState()
     */
    public boolean nextGeneration() {
        //System.out.println( "next generation" );
        session.setFocus( "calculate" );
        session.setFocus( "kill" );
        session.setFocus( "birth" );
        session.setFocus( "reset calculate" );
        session.setFocus( "rest" );
        session.setFocus( "evaluate" );
        session.fireAllRules();
        return session.getAgenda().getAgendaGroup( "evaluate" ).size() != 0;
    }

    /**
     * kills all cells in the grid
     */
    public void killAll() {
        this.session.setFocus( "calculate" );
        this.session.setFocus( "kill all" );
        this.session.setFocus( "reset calculate" );
        this.session.fireAllRules();
    }

    /**
     * Populates the grid with a <code>ConwayPattern</code>
     * 
     * @param pattern
     *            pattern to populate the grid with
     * @see ConwayPattern
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

        killAll();

        for ( int column = 0; column < gridWidth; column++ ) {
            for ( int row = 0; row < gridHeight; row++ ) {
                if ( gridData[row][column] ) {
                    final Cell cell = getCellAt( row + rowOffset,
                                                 column + columnOffset );
                    cell.setCellState( CellState.LIVE );
                    this.session.update( this.session.getFactHandle( cell ),
                                         cell );
                }
            }
        }
        session.setFocus( "calculate" );
        session.fireAllRules();
    }

    public void dispose() {
        if ( this.session != null ) {
            this.session.dispose();
        }
    }

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
