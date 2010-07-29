/**
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

package org.drools.examples.conway.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;

import org.drools.examples.conway.Cell;
import org.drools.examples.conway.CellGrid;
import org.drools.examples.conway.CellState;

/**
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public class CellGridCanvas extends Canvas {
    
	private static final long serialVersionUID = 510l;
    private static final Color BACKGROUND_COLOR = Color.gray;
    private static final Color GRID_COLOR       = CellGridCanvas.BACKGROUND_COLOR.brighter();
	
	private Image              offScreenImage;
    private Image              backgroundImage;
    private final int          cellSize;
    private final CellGrid     cellGrid;
    private final Image        liveCellImage    = new ImageIcon( CellGridCanvas.class.getResource( "/org/drools/examples/conway/liveCellImage.gif" ) ).getImage();

    /**
     * Constructs a CellGridCanvas.
     * 
     * @param cellGrid
     *            the GoL cellgrid
     */
    public CellGridCanvas(final CellGrid cellGrid) {
        this.cellGrid = cellGrid;
        this.cellSize = this.liveCellImage.getWidth( this );

        setBackground( CellGridCanvas.GRID_COLOR );

        addMouseListener( new MouseAdapter() {
            /**
             * Invoked when a mouse button has been pressed on a component.
             */
            public void mousePressed(final MouseEvent e) {
                toggleCellAt( e.getX(),
                              e.getY() );
            }
        } );

        addMouseMotionListener( new MouseMotionAdapter() {

            public void mouseDragged(final MouseEvent e) {
                final Cell cell = getCellAtPoint( e.getX(),
                                                  e.getY() );
                if ( cell != null ) {
                    cellGrid.updateCell( cell, CellState.LIVE  );
                    repaint();
                }
            }
        } );
    }

    private void toggleCellAt(final int x,
                              final int y) {
        final Cell cell = getCellAtPoint( x,
                                    y );
        
        if ( cell != null ) {
            if ( cell.getCellState() == CellState.LIVE ) {
                this.cellGrid.updateCell( cell, CellState.DEAD );
            } else {
                this.cellGrid.updateCell( cell, CellState.LIVE );
            }
            repaint();
        }
    }

    private Cell getCellAtPoint(final int x,
                                final int y) {
        Cell cell = null;

        final int column = x / this.cellSize;
        final int row = y / this.cellSize;
        final int numberOfColumns = this.cellGrid.getNumberOfColumns();
        final int numberOfRows = this.cellGrid.getNumberOfRows();

        if ( column >= 0 && column < numberOfColumns && row >= 0 && row < numberOfRows ) {
            cell = this.cellGrid.getCellAt( row,
                                       column );
        }

        return cell;
    }

    /**
     * Use double buffering.
     * 
     * @see java.awt.Component#update(java.awt.Graphics)
     */
    public void update(final Graphics g) {
        final Dimension d = getSize();
        if ( (this.offScreenImage == null) ) {
            this.offScreenImage = createImage( d.width,
                                          d.height );
        }
        paint( this.offScreenImage.getGraphics() );
        g.drawImage( this.offScreenImage,
                     0,
                     0,
                     null );
    }

    /**
     * Draw this generation.
     * 
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(final Graphics g) {
        // Draw grid on background image, which is faster
        final int numberOfColumns = this.cellGrid.getNumberOfColumns();
        final int numberOfRows = this.cellGrid.getNumberOfRows();
        if ( this.backgroundImage == null ) {
            final Dimension d = getSize();
            this.backgroundImage = createImage( d.width,
                                           d.height );
            final Graphics backgroundImageGraphics = this.backgroundImage.getGraphics();
            // draw background (MSIE doesn't do that)
            backgroundImageGraphics.setColor( getBackground() );
            backgroundImageGraphics.fillRect( 0,
                                              0,
                                              d.width,
                                              d.height );
            backgroundImageGraphics.setColor( CellGridCanvas.BACKGROUND_COLOR );
            backgroundImageGraphics.fillRect( 0,
                                              0,
                                              this.cellSize * numberOfColumns - 1,
                                              this.cellSize * numberOfRows - 1 );
            backgroundImageGraphics.setColor( CellGridCanvas.GRID_COLOR );
            for ( int x = 1; x < numberOfColumns; x++ ) {
                backgroundImageGraphics.drawLine( x * this.cellSize - 1,
                                                  0,
                                                  x * this.cellSize - 1,
                                                  this.cellSize * numberOfRows - 1 );
            }
            for ( int y = 1; y < numberOfRows; y++ ) {
                backgroundImageGraphics.drawLine( 0,
                                                  y * this.cellSize - 1,
                                                  this.cellSize * numberOfColumns - 1,
                                                  y * this.cellSize - 1 );
            }
        }
        g.drawImage( this.backgroundImage,
                     0,
                     0,
                     null );

        // draw populated cells
        for ( int row = 0; row < numberOfRows; row++ ) {
            for ( int column = 0; column < numberOfColumns; column++ ) {
                final Cell cell = this.cellGrid.getCellAt( row,
                                                column );
                if ( cell.getCellState() == CellState.LIVE ) {
                    g.drawImage( this.liveCellImage,
                                 column * this.cellSize,
                                 row * this.cellSize,
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
    public Dimension getPreferredSize() {
        final int numberOfColumns = this.cellGrid.getNumberOfColumns();
        final int numberOfRows = this.cellGrid.getNumberOfRows();
        return new Dimension( this.cellSize * numberOfColumns,
                              this.cellSize * numberOfRows );
    }

    /**
     * This is the minimum size (size of one cell).
     * 
     * @see java.awt.Component#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        return new Dimension( this.cellSize,
                              this.cellSize );
    }

}
