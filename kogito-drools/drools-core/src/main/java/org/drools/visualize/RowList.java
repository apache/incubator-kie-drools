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

    public void add(final int depth,
                    final Vertex vertex) {
        if ( this.rows.size() < (depth + 1) ) {
            final int addRows = depth - this.rows.size() + 1;

            for ( int i = 0; i < addRows; ++i ) {
                this.rows.add( new Row( (depth - addRows) + i ) );
            }
        }

        ((Row) this.rows.get( depth )).add( vertex );
    }

    public int getDepth() {
        return this.rows.size();
    }

    public Row get(final int row) {
        return (Row) this.rows.get( row );
    }

    public int getRow(final Vertex vertex) {
        final int numRows = this.rows.size();

        for ( int i = 0; i < numRows; ++i ) {
            if ( ((Row) this.rows.get( i )).contains( vertex ) ) {
                return i;
            }
        }

        return -1;
    }

    public int getWidth() {
        int width = 0;

        for ( final Iterator rowIter = this.rows.iterator(); rowIter.hasNext(); ) {
            final Row row = (Row) rowIter.next();
            final int rowWidth = row.getWidth();

            if ( rowWidth > width ) {
                width = rowWidth;
            }
        }

        return width;
    }

    public int getWidth(final int row) {
        return ((Row) this.rows.get( row )).getWidth();
    }

    public int getColumn(final Vertex vertex) {
        final int row = getRow( vertex );

        if ( row < 0 ) {
            return -1;
        }

        final List rowVertices = get( row ).getVertices();

        final int numCols = rowVertices.size();

        for ( int i = 0; i < numCols; ++i ) {
            if ( rowVertices.get( i ).equals( vertex ) ) {
                return i;
            }
        }

        return -1;
    }

    public void dump() {
        final int numRows = this.rows.size();

        for ( int i = 0; i < numRows; ++i ) {
            System.err.println( i + ": " + get( i ).getVertices() );
        }
    }

    public void optimize() {
        final int numRows = this.rows.size();

        for ( int i = 0; i < numRows; ++i ) {
            get( i ).optimize();
        }
    }
}