package org.drools.examples.conway;

import junit.framework.TestCase;

/**
 *
 */
public class CellGridTest extends TestCase {

    private CellGrid         grid    = null;
    private static final int ROWS    = 5;
    private static final int COLUMNS = 10;

    protected void setUp() throws Exception {
        grid = new CellGrid( ROWS,
                             COLUMNS );
    }

    protected void tearDown() throws Exception {
        grid = null;
    }

    public void testGetCellAt() {

        Cell cell = grid.getCellAt( ROWS - 1,
                                    COLUMNS - 1 );

        assertNotNull( "getCellAt returned null",
                       cell );

        assertEquals( "cell had wrong initial state",
                      CellState.DEAD,
                      cell.getCellState() );
    }

    public void testInitialStateOfCell() {
        Cell cell = grid.getCellAt( ROWS - 1,
                                    COLUMNS - 1 );

        assertNotNull( "getCellAt returned null",
                       cell );

        assertEquals( "cell had wrong initial state",
                      CellState.DEAD,
                      cell.getCellState() );
    }

    public void testNumberOfNeighbors() {
        Cell cell = grid.getCellAt( 0,
                                    0 );

        // corner cells should all have 3 neighbors
        assertEquals( "cell(0,0) had wrong number of neighbors",
                      3,
                      cell.getNumberOfNeighboringCells() );
        cell = grid.getCellAt( 0,
                               COLUMNS - 1 );
        assertEquals( "cell(0, COLUMNS-1) had wrong number of neighbors",
                      3,
                      cell.getNumberOfNeighboringCells() );
        cell = grid.getCellAt( ROWS - 1,
                               COLUMNS - 1 );
        assertEquals( "cell(ROWS-1, COLUMNS-1) had wrong number of neighbors",
                      3,
                      cell.getNumberOfNeighboringCells() );
        cell = grid.getCellAt( ROWS - 1,
                               0 );
        assertEquals( "cell(ROWS - 1, 0) had wrong number of neighbors",
                      3,
                      cell.getNumberOfNeighboringCells() );

        // cells in the first and last row (except corners) should all have 5 neighbors
        for ( int column = 1; column < COLUMNS - 1; column++ ) {
            cell = grid.getCellAt( 0,
                                   column );
            assertEquals( "cell had wrong number of neighbors",
                          5,
                          cell.getNumberOfNeighboringCells() );
            cell = grid.getCellAt( ROWS - 1,
                                   column );
            assertEquals( "cell had wrong number of neighbors",
                          5,
                          cell.getNumberOfNeighboringCells() );
        }

        // cells in the first and last column (except corners) should all ahve 5 neighbors
        for ( int row = 1; row < ROWS - 1; row++ ) {
            cell = grid.getCellAt( row,
                                   0 );
            assertEquals( "cell had wrong number of neighbors",
                          5,
                          cell.getNumberOfNeighboringCells() );
            cell = grid.getCellAt( row,
                                   COLUMNS - 1 );
            assertEquals( "cell had wrong number of neighbors",
                          5,
                          cell.getNumberOfNeighboringCells() );
        }

        // cells not in the first row and first column should all have 8 neighbors
        for ( int row = 1; row < ROWS - 1; row++ ) {
            for ( int column = 1; column < COLUMNS - 1; column++ ) {
                cell = grid.getCellAt( row,
                                       column );
                assertEquals( "cell had wrong number of neighbors",
                              8,
                              cell.getNumberOfNeighboringCells() );
            }
        }

    }

    public void testGetNumberOfRows() {

        assertEquals( "grid had wrong number of rows",
                      ROWS,
                      grid.getNumberOfRows() );
    }

    public void testGetNumberOfColumns() {

        assertEquals( "grid had wrong number of columns",
                      COLUMNS,
                      grid.getNumberOfColumns() );
    }

    public void testGettingAllCells() {
        int numberOfRows = grid.getNumberOfRows();
        int numberOfColumns = grid.getNumberOfColumns();
        for ( int row = 0; row < numberOfRows; row++ ) {
            for ( int column = 0; column < numberOfColumns; column++ ) {
                Cell cell = grid.getCellAt( row,
                                            column );
                assertNotNull( "getCellAt returned null",
                               cell );

                assertEquals( "cell had wrong initial state",
                              CellState.DEAD,
                              cell.getCellState() );
            }
        }
    }

    public void testGiveBirth() {
        grid.getCellAt( 0,
                        0 ).setCellState( CellState.LIVE );
        grid.getCellAt( 0,
                        1 ).setCellState( CellState.LIVE );
        grid.getCellAt( 0,
                        2 ).setCellState( CellState.LIVE );
        grid.nextGeneration();
        assertEquals( "Cell should have come to life",
                      CellState.LIVE,
                      grid.getCellAt( 1,
                                      1 ).getCellState() );
    }

    public void testKillingTheLonely() {
        grid.getCellAt( 0,
                        0 ).setCellState( CellState.LIVE );
        grid.nextGeneration();
        assertEquals( "Lonely cell should have been killed",
                      CellState.DEAD,
                      grid.getCellAt( 0,
                                      0 ).getCellState() );
    }

    public void testKillingTheOvercrowded() {
        grid.getCellAt( 0,
                        0 ).setCellState( CellState.LIVE );
        grid.getCellAt( 0,
                        1 ).setCellState( CellState.LIVE );
        grid.getCellAt( 0,
                        2 ).setCellState( CellState.LIVE );
        grid.getCellAt( 1,
                        0 ).setCellState( CellState.LIVE );
        grid.getCellAt( 1,
                        1 ).setCellState( CellState.LIVE );
        grid.nextGeneration();
        assertEquals( "Overcrowded cell should have been killed",
                      CellState.DEAD,
                      grid.getCellAt( 1,
                                      1 ).getCellState() );
    }
}
