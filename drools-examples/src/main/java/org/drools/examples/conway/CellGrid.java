package org.drools.examples.conway;

import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.examples.conway.patterns.ConwayPattern;

/**
 * A <code>CellGrid</code> represents a grid of <code>Cell</code> objects.
 * <p/>
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 * @see Cell
 */
public class CellGrid
{

    private final Cell[][] cells;

    /**
     * Constructs a CellGrid
     * 
     * @param rows
     *            number of rows in the grid
     * @param columns
     *            number of columns in the grid
     */
    public CellGrid(int rows,
                    int columns)
    {
        cells = new Cell[rows][columns];

        // populate the array of Cells and hook each
        // cell up with its neighbors...
        for ( int row = 0; row < rows; row++ )
        {
            for ( int column = 0; column < columns; column++ )
            {
                Cell newCell = new Cell( );
                cells[row][column] = newCell;
                if ( row > 0 )
                {
                    // neighbor to the north
                    newCell.addNeighbor( cells[row - 1][column] );
                    if ( column <= (columns - 2) )
                    {
                        // neighbor to the northeast
                        newCell.addNeighbor( cells[row - 1][column + 1] );
                    }
                }
                if ( column > 0 )
                {
                    // neighbor to the west
                    newCell.addNeighbor( cells[row][column - 1] );
                    if ( row > 0 )
                    {
                        // neighbor to the northwest
                        newCell.addNeighbor( cells[row - 1][column - 1] );
                    }
                }
            }
        }
    }

    /**
     * @param row
     *            row of the requested cell
     * @param column
     *            column of the requested cell
     * @return the cell at the specified coordinates
     * @see Cell
     */
    public Cell getCellAt(int row,
                          int column)
    {
        return cells[row][column];
    }

    /**
     * @return the number of rows in this grid
     * @see #getNumberOfColumns()
     */
    public int getNumberOfRows()
    {
        return cells.length;
    }

    /**
     * @return the number of columns in this grid
     * @see #getNumberOfRows()
     */
    public int getNumberOfColumns()
    {
        return cells[0].length;
    }

    /**
     * Moves this grid to its next generation
     * 
     * @return <code>true</code> if the state changed, otherwise false
     * @see #transitionState()
     */
    public boolean nextGeneration()
    {
        boolean didStateChange = false;
        try
        {
            RuleBase ruleBase = ConwayRuleBaseFactory.getRuleBase( );
            WorkingMemory workingMemory = ruleBase.newWorkingMemory( );
            // for (Cell[] rowOfCells : cells) {
            Cell[] rowOfCells = null;
            Cell cell = null;
            for ( int i = 0; i < cells.length; i++ )
            {
                rowOfCells = cells[i];
                for ( int j = 0; j < rowOfCells.length; j++ )
                {
                    cell = rowOfCells[j];
                    workingMemory.assertObject( cell );
                }
            }
            workingMemory.fireAllRules( );
            didStateChange = transitionState( );
        }
        catch ( Exception e )
        {
            e.printStackTrace( );
        }
        return didStateChange;
    }

    /**
     * @return the number of cells in the grid that are alive
     * @see CellState
     */
    public int getNumberOfLiveCells()
    {
        int number = 0;
        Cell[] rowOfCells = null;
        Cell cell = null;
        for ( int i = 0; i < cells.length; i++ )
        {
            rowOfCells = cells[i];
            // for (Cell cell : rowOfCells) {
            for ( int j = 0; j < rowOfCells.length; j++ )
            {
                cell = rowOfCells[j];
                if ( cell.getCellState( ) == CellState.LIVE )
                {
                    number++;
                }
            }
        }
        return number;
    }

    /**
     * kills all cells in the grid
     */
    public void killAll()
    {
        Cell[] rowOfCells = null;
        Cell cell = null;
        for ( int i = 0; i < cells.length; i++ )
        {
            rowOfCells = cells[i];
            // for (Cell cell : rowOfCells) {
            for ( int j = 0; j < rowOfCells.length; j++ )
            {
                cell = rowOfCells[j];
                cell.setCellState( CellState.DEAD );
            }
        }
    }

    /**
     * Transitions this grid to its next state of evolution
     * 
     * @return <code>true</code> if the state changed, otherwise false
     * @see #nextGeneration()
     */
    public boolean transitionState()
    {
        boolean stateChanged = false;
        Cell[] rowOfCells = null;
        Cell cell = null;
        for ( int i = 0; i < cells.length; i++ )
        {
            rowOfCells = cells[i];
            // for (Cell cell : rowOfCells) {
            for ( int j = 0; j < rowOfCells.length; j++ )
            {
                cell = rowOfCells[j];
                stateChanged |= cell.transitionState( );
            }
        }
        return stateChanged;
    }

    /**
     * Populates the grid with a <code>ConwayPattern</code>
     * 
     * @param pattern
     *            pattern to populate the grid with
     * @see ConwayPattern
     */
    public void setPattern(ConwayPattern pattern)
    {
        boolean[][] gridData = pattern.getPattern( );
        int gridWidth = gridData[0].length;
        int gridHeight = gridData.length;

        int columnOffset = 0;
        int rowOffset = 0;

        if ( gridWidth > getNumberOfColumns( ) )
        {
            gridWidth = getNumberOfColumns( );
        }
        else
        {
            columnOffset = (getNumberOfColumns( ) - gridWidth) / 2;
        }

        if ( gridHeight > getNumberOfRows( ) )
        {
            gridHeight = getNumberOfRows( );
        }
        else
        {
            rowOffset = (getNumberOfRows( ) - gridHeight) / 2;
        }

        killAll( );
        for ( int column = 0; column < gridWidth; column++ )
        {
            for ( int row = 0; row < gridHeight; row++ )
            {
                if ( gridData[row][column] )
                {
                    Cell cell = getCellAt( row + rowOffset,
                                           column + columnOffset );
                    cell.setCellState( CellState.LIVE );
                }
            }
        }
    }
}
