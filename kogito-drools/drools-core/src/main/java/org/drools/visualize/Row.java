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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.jung.graph.Vertex;

public class Row {
    private final int depth;

    private List     /*Vertex*/vertices;

    public Row(final int depth) {
        super();
        this.vertices = new ArrayList();
        this.depth = depth;
    }

    public int getDepth() {
        return this.depth;
    }

    public void add(final Vertex vertex) {
        this.vertices.add( vertex );
    }

    public List /*Vertex*/getVertices() {
        return this.vertices;
    }

    public boolean contains(final Vertex vertex) {
        return this.vertices.contains( vertex );
    }

    public int getWidth() {
        return this.vertices.size();
    }

    public void optimize() {
        final List sorted = new ArrayList( this.vertices );

        Collections.sort( sorted,
                          new Comparator() {
                              public int compare(final Object o1,
                                                 final Object o2) {
                                  final Vertex v1 = (Vertex) o1;
                                  final Vertex v2 = (Vertex) o2;

                                  if ( v1.outDegree() < v2.outDegree() ) {
                                      return 1;
                                  }

                                  if ( v1.outDegree() > v2.outDegree() ) {
                                      return -1;
                                  }

                                  return 0;
                              }
                          } );

        final LinkedList optimized = new LinkedList();

        boolean front = false;

        for ( final Iterator vertexIter = sorted.iterator(); vertexIter.hasNext(); ) {
            final Vertex vertex = (Vertex) vertexIter.next();

            if ( front ) {
                optimized.addFirst( vertex );
            } else {
                optimized.addLast( vertex );
            }

            front = !front;
        }

        this.vertices = optimized;
    }
}