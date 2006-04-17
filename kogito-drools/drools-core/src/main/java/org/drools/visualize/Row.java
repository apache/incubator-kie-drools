package org.drools.visualize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.jung.graph.Vertex;

public class Row {
    private int  depth;

    private List /*Vertex*/vertices;

    public Row(int depth) {
        super();
        this.vertices = new ArrayList();
    }

    public int getDepth() {
        return depth;
    }

    public void add(Vertex vertex) {
        this.vertices.add( vertex );
    }

    public List /*Vertex*/getVertices() {
        return vertices;
    }

    public boolean contains(Vertex vertex) {
        return vertices.contains( vertex );
    }

    public int getWidth() {
        return vertices.size();
    }

    public void optimize() {
        List sorted = new ArrayList( this.vertices );

        Collections.sort( sorted,
                          new Comparator() {
                              public int compare(Object o1,
                                                 Object o2) {
                                  Vertex v1 = (Vertex) o1;
                                  Vertex v2 = (Vertex) o2;

                                  if ( v1.outDegree() < v2.outDegree() ) {
                                      return 1;
                                  }

                                  if ( v1.outDegree() > v2.outDegree() ) {
                                      return -1;
                                  }

                                  return 0;
                              }
                          } );

        LinkedList optimized = new LinkedList();

        boolean front = false;

        for ( Iterator vertexIter = sorted.iterator(); vertexIter.hasNext(); ) {
            Vertex vertex = (Vertex) vertexIter.next();

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
