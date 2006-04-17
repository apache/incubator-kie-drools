package org.drools.visualize;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

public class ReteooLayoutSolver {

    private Vertex  root;

    private RowList rowList;

    public ReteooLayoutSolver(Vertex root) {
        this.root = root;
        solve();
    }

    protected void solve() {
        rowList = new RowList();

        rowList.add( 0,
                     root );

        int curRow = 0;

        Set seenVertices = new HashSet();
        seenVertices.add( root );

        while ( curRow < rowList.getDepth() ) {
            List rowVertices = rowList.get( curRow ).getVertices();

            for ( Iterator rowVertexIter = rowVertices.iterator(); rowVertexIter.hasNext(); ) {
                Vertex rowVertex = (Vertex) rowVertexIter.next();

                Set edges = rowVertex.getOutEdges();

                for ( Iterator edgeIter = edges.iterator(); edgeIter.hasNext(); ) {

                    Edge edge = (Edge) edgeIter.next();
                    Vertex destVertex = edge.getOpposite( rowVertex );

                    if ( !seenVertices.contains( destVertex ) ) {
                        rowList.add( curRow + 1,
                                     destVertex );
                        seenVertices.add( destVertex );
                    }
                }

                seenVertices.add( rowVertex );
            }

            ++curRow;
        }

        rowList.optimize();

        rowList.dump();
    }

    public RowList getRowList() {
        return rowList;
    }
}
