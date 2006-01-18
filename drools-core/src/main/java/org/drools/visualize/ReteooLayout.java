package org.drools.visualize;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserDataContainer;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.VertexLocationFunction;

public class ReteooLayout extends AbstractLayout {

    public final static String COORDS                = "drools.ReteooLayout.coords";

    private static final int   COLUMN_SPACE          = 20;
    private static final int   ROW_HEIGHT_MULTIPLIER = 3;

    private RowList            rowList;

    private VertexFunctions    vertexFunctions;
    private int                columnWidth;
    private int                rowHeight;

    public ReteooLayout(Graph g,
                        VertexFunctions vertexFunctions,
                        RowList rowList) {
        super( g );
        this.vertexFunctions = vertexFunctions;
        this.rowList = rowList;
        computeSize();
    }

    public VertexFunctions getVertexFunctions() {
        return this.vertexFunctions;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public int getPreferredWidth() {
        return rowList.getWidth() * columnWidth;
    }

    public int getPreferredHeight() {
        return rowList.getDepth() * getRowHeight() * ROW_HEIGHT_MULTIPLIER;
    }

    protected void computeSize() {
        Set vertices = getGraph().getVertices();

        for ( Iterator vertexIter = vertices.iterator(); vertexIter.hasNext(); ) {
            Vertex vertex = (Vertex) vertexIter.next();

            int width = vertexFunctions.getShapeDimension( vertex ).width;
            int height = vertexFunctions.getShapeDimension( vertex ).height;

            if ( width > columnWidth ) {
                columnWidth = width;
            }

            if ( height > rowHeight ) {
                rowHeight = height;
            }
        }

        columnWidth = columnWidth + COLUMN_SPACE;
    }

    protected void initialize_local_vertex(Vertex vertex) {
        int row = rowList.getRow( vertex );
        int col = rowList.getColumn( vertex );

        int widthPx = this.getCurrentSize().width;
        int heightPx = this.getCurrentSize().height;

        int rowWidth = rowList.getWidth( row );

        int columnWidthPx = getColumnWidth();
        int rowHeightPx = getRowHeight();

        Coordinates coords = new Coordinates();

        double x = (col * columnWidthPx);
        double y = (row * (rowHeightPx * ROW_HEIGHT_MULTIPLIER));

        x = x + (widthPx / 2) - ((rowWidth - 1) * (columnWidthPx / 2));
        y = y + (rowHeightPx / 2) + 3;

        coords.setX( x );
        coords.setY( y );

        //System.err.println( vertex + " -> " + coords.getX() + "," + coords.getY() + " / " + row + "," + col );

        vertex.setUserDatum( COORDS,
                             coords,
                             new UserDataContainer.CopyAction.Shared() );
    }

    public double getX(Vertex vertex) {
        //System.err.println( "getX" );
        return getCoordinates( vertex ).getX();
    }

    public double getY(Vertex vertex) {
        //System.err.println( "getY" );
        return getCoordinates( vertex ).getY();
    }

    public Coordinates getCoordinates(ArchetypeVertex vertex) {
        //System.err.println( vertex + " --> " + (Coordinates) vertex.getUserDatum( COORDS ) );
        return (Coordinates) vertex.getUserDatum( COORDS );
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
