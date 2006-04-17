package org.drools.visualize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.Vertex;

public class RowList {

    private List /*Row*/rows;

    public RowList() {
        super();
        this.rows = new ArrayList();
    }

    public void add(int depth,
                    Vertex vertex) {
        if ( rows.size() < (depth + 1) ) {
            int addRows = depth - rows.size() + 1;

            for ( int i = 0; i < addRows; ++i ) {
                this.rows.add( new Row( (depth - addRows) + i ) );
            }
        }

        ((Row) rows.get( depth )).add( vertex );
    }

    public int getDepth() {
        return rows.size();
    }

    public Row get(int row) {
        return (Row) rows.get( row );
    }

    public int getRow(Vertex vertex) {
        int numRows = rows.size();

        for ( int i = 0; i < numRows; ++i ) {
            if ( ((Row) this.rows.get( i )).contains( vertex ) ) {
                return i;
            }
        }

        return -1;
    }

    public int getWidth() {
        int width = 0;

        for ( Iterator rowIter = rows.iterator(); rowIter.hasNext(); ) {
            Row row = (Row) rowIter.next();
            int rowWidth = row.getWidth();

            if ( rowWidth > width ) {
                width = rowWidth;
            }
        }

        return width;
    }

    public int getWidth(int row) {
        return ((Row) this.rows.get( row )).getWidth();
    }

    public int getColumn(Vertex vertex) {
        int row = getRow( vertex );

        if ( row < 0 ) {
            return -1;
        }

        List rowVertices = get( row ).getVertices();

        int numCols = rowVertices.size();

        for ( int i = 0; i < numCols; ++i ) {
            if ( rowVertices.get( i ).equals( vertex ) ) {
                return i;
            }
        }

        return -1;
    }

    public void dump() {
        int numRows = rows.size();

        for ( int i = 0; i < numRows; ++i ) {
            System.err.println( i + ": " + get( i ).getVertices() );
        }
    }

    public void optimize() {
        int numRows = rows.size();

        for ( int i = 0; i < numRows; ++i ) {
            get( i ).optimize();
        }
    }
}
