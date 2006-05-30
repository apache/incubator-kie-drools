package org.drools.visualize;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserDataContainer;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;

public class ReteooLayout extends AbstractLayout {

    public final static String COORDS                = "drools.ReteooLayout.coords";

    private static final int   COLUMN_SPACE          = 20;
    private static final int   ROW_HEIGHT_MULTIPLIER = 3;

    private RowList            rowList;

    private VertexFunctions    vertexFunctions;
    private int                columnWidth;
    private int                rowHeight;

    public ReteooLayout(final Graph g,
                        final VertexFunctions vertexFunctions,
                        final RowList rowList) {
        super( g );
        this.vertexFunctions = vertexFunctions;
        this.rowList = rowList;
        computeSize();
    }

    public VertexFunctions getVertexFunctions() {
        return this.vertexFunctions;
    }

    public int getColumnWidth() {
        return this.columnWidth;
    }

    public int getRowHeight() {
        return this.rowHeight;
    }

    public int getPreferredWidth() {
        return this.rowList.getWidth() * this.columnWidth;
    }

    public int getPreferredHeight() {
        return this.rowList.getDepth() * getRowHeight() * ReteooLayout.ROW_HEIGHT_MULTIPLIER;
    }

    protected void computeSize() {
        final Set vertices = getGraph().getVertices();

        for ( final Iterator vertexIter = vertices.iterator(); vertexIter.hasNext(); ) {
            final Vertex vertex = (Vertex) vertexIter.next();

            final int width = this.vertexFunctions.getShapeDimension( vertex ).width;
            final int height = this.vertexFunctions.getShapeDimension( vertex ).height;

            if ( width > this.columnWidth ) {
                this.columnWidth = width;
            }

            if ( height > this.rowHeight ) {
                this.rowHeight = height;
            }
        }

        this.columnWidth = this.columnWidth + ReteooLayout.COLUMN_SPACE;
    }

    protected void initialize_local_vertex(final Vertex vertex) {
        final int row = this.rowList.getRow( vertex );
        final int col = this.rowList.getColumn( vertex );

        final int widthPx = this.getCurrentSize().width;
        final int heightPx = this.getCurrentSize().height;

        final int rowWidth = this.rowList.getWidth( row );

        final int columnWidthPx = getColumnWidth();
        final int rowHeightPx = getRowHeight();

        final Coordinates coords = new Coordinates();

        double x = (col * columnWidthPx);
        double y = (row * (rowHeightPx * ReteooLayout.ROW_HEIGHT_MULTIPLIER));

        x = x + (widthPx / 2) - ((rowWidth - 1) * (columnWidthPx / 2));
        y = y + (rowHeightPx / 2) + 3;

        coords.setX( x );
        coords.setY( y );

        //System.err.println( vertex + " -> " + coords.getX() + "," + coords.getY() + " / " + row + "," + col );

        vertex.setUserDatum( ReteooLayout.COORDS,
                             coords,
                             new UserDataContainer.CopyAction.Shared() );
    }

    public double getX(final Vertex vertex) {
        //System.err.println( "getX" );
        return getCoordinates( vertex ).getX();
    }

    public double getY(final Vertex vertex) {
        //System.err.println( "getY" );
        return getCoordinates( vertex ).getY();
    }

    public Coordinates getCoordinates(final ArchetypeVertex vertex) {
        //System.err.println( vertex + " --> " + (Coordinates) vertex.getUserDatum( COORDS ) );
        return (Coordinates) vertex.getUserDatum( ReteooLayout.COORDS );
    }

    public void advancePositions() {
    }

    public boolean isIncremental() {
        return false;
    }

    public boolean incrementsAreDone() {
        return false;
    }

    protected void initialize_local() {
        // nothing
    }

}