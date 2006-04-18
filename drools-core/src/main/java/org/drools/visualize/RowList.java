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