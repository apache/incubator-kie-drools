package org.drools.examples.conway.ui;


import javax.swing.*;

import org.drools.examples.conway.Cell;
import org.drools.examples.conway.CellGrid;
import org.drools.examples.conway.CellState;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 * @version $Id: CellGridCanvas.java,v 1.3 2005/05/08 19:54:48 mproctor Exp $
 */
public class CellGridCanvas extends Canvas
{
    private Image              offScreenImage;
    private Image              backgroundImage;
    private final int          cellSize;
    private final CellGrid     cellGrid;
    private final Image        liveCellImage    = new ImageIcon( CellGridCanvas.class.getResource( "liveCellImage.gif" ) ).getImage( );

    private static final Color BACKGROUND_COLOR = Color.gray;
    private static final Color GRID_COLOR       = BACKGROUND_COLOR.brighter( );

    /**
     * Constructs a CellGridCanvas.
     * 
     * @param cellGrid
     *            the GoL cellgrid
     */
    public CellGridCanvas(CellGrid cellGrid)
    {
        this.cellGrid = cellGrid;
        this.cellSize = liveCellImage.getWidth( this );

        setBackground( GRID_COLOR );

        addMouseListener( new MouseAdapter( ) {
            /**
             * Invoked when a mouse button has been pressed on a component.
             */
            public void mousePressed(MouseEvent e)
            {
                toggleCellAt( e.getX( ),
                              e.getY( ) );
            }
        } );

        addMouseMotionListener( new MouseMotionAdapter( ) {

            public void mouseDragged(MouseEvent e)
            {
                Cell cell = getCellAtPoint( e.getX( ),
                                            e.getY( ) );
                if ( cell != null )
                {
                    cell.setCellState( CellState.LIVE );
                    repaint( );
                }
            }
        } );
    }

    private void toggleCellAt(int x,
                              int y)
    {
        Cell cell = getCellAtPoint( x,
                                    y );
        if ( cell != null )
        {
            if ( cell.getCellState( ) == CellState.LIVE )
            {
                cell.setCellState( CellState.DEAD );
            }
            else
            {
                cell.setCellState( CellState.LIVE );
            }
            repaint( );
        }
    }

    private Cell getCellAtPoint(int x,
                                int y)
    {
        Cell cell = null;

        int column = x / cellSize;
        int row = y / cellSize;
        final int numberOfColumns = cellGrid.getNumberOfColumns( );
        final int numberOfRows = cellGrid.getNumberOfRows( );

        if ( column >= 0 && column < numberOfColumns && row >= 0 && row < numberOfRows )
        {
            cell = cellGrid.getCellAt( row,
                                       column );
        }

        return cell;
    }

    /**
     * Use double buffering.
     * 
     * @see java.awt.Component#update(java.awt.Graphics)
     */
    public void update(Graphics g)
    {
        Dimension d = getSize( );
        if ( (offScreenImage == null) )
        {
            offScreenImage = createImage( d.width,
                                          d.height );
        }
        paint( offScreenImage.getGraphics( ) );
        g.drawImage( offScreenImage,
                     0,
                     0,
                     null );
    }

    /**
     * Draw this generation.
     * 
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g)
    {
        // Draw grid on background image, which is faster
        final int numberOfColumns = cellGrid.getNumberOfColumns( );
        final int numberOfRows = cellGrid.getNumberOfRows( );
        if ( backgroundImage == null )
        {
            Dimension d = getSize( );
            backgroundImage = createImage( d.width,
                                           d.height );
            Graphics backgroundImageGraphics = backgroundImage.getGraphics( );
            // draw background (MSIE doesn't do that)
            backgroundImageGraphics.setColor( getBackground( ) );
            backgroundImageGraphics.fillRect( 0,
                                              0,
                                              d.width,
                                              d.height );
            backgroundImageGraphics.setColor( BACKGROUND_COLOR );
            backgroundImageGraphics.fillRect( 0,
                                              0,
                                              cellSize * numberOfColumns - 1,
                                              cellSize * numberOfRows - 1 );
            backgroundImageGraphics.setColor( GRID_COLOR );
            for ( int x = 1; x < numberOfColumns; x++ )
            {
                backgroundImageGraphics.drawLine( x * cellSize - 1,
                                                  0,
                                                  x * cellSize - 1,
                                                  cellSize * numberOfRows - 1 );
            }
            for ( int y = 1; y < numberOfRows; y++ )
            {
                backgroundImageGraphics.drawLine( 0,
                                                  y * cellSize - 1,
                                                  cellSize * numberOfColumns - 1,
                                                  y * cellSize - 1 );
            }
        }
        g.drawImage( backgroundImage,
                     0,
                     0,
                     null );

        // draw populated cells
        for ( int row = 0; row < numberOfRows; row++ )
        {
            for ( int column = 0; column < numberOfColumns; column++ )
            {
                Cell cell = cellGrid.getCellAt( row,
                                                column );
                if ( cell.getCellState( ) == CellState.LIVE )
                {
                    g.drawImage( liveCellImage,
                                 column * cellSize,
                                 row * cellSize,
                                 this );
                }
            }
        }
    }

    /**
     * This is the preferred size.
     * 
     * @see java.awt.Component#getPreferredSize()
     */
    public Dimension getPreferredSize()
    {
        final int numberOfColumns = cellGrid.getNumberOfColumns( );
        final int numberOfRows = cellGrid.getNumberOfRows( );
        return new Dimension( cellSize * numberOfColumns,
                              cellSize * numberOfRows );
    }

    /**
     * This is the minimum size (size of one cell).
     * 
     * @see java.awt.Component#getMinimumSize()
     */
    public Dimension getMinimumSize()
    {
        return new Dimension( cellSize,
                              cellSize );
    }

}
