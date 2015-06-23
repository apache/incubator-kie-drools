/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.drools.core.util.bitmask.OpenBitSet;

public class EliminationCandidate implements Comparable<EliminationCandidate> {
    private Graph<BayesVariable>     g;
    private boolean[][]              adjMatrix;
    private GraphNode<BayesVariable> v;
    private int                      newEdgesRequired;
    private int                      weightRequired;
    private OpenBitSet cliqueBitSet;


    public EliminationCandidate(Graph g, boolean[][] adjMatrix, GraphNode v) {
        this.g = g;
        this.adjMatrix = adjMatrix;
        this.v = v;
        update();
    }

    public GraphNode getV() {
        return v;
    }

    public void update() {
        // must use the adjacency matrix, and not the vertex.getEdges() for connections,
        // as it gets updated with new connections, during elimination
        weightRequired = (int) Math.abs(v.getContent().getOutcomes().length);
        newEdgesRequired = 0;

        cliqueBitSet = new OpenBitSet(adjMatrix.length);

        cliqueBitSet.set(v.getId());

        // determine new edges added, to ensure all adjacent nodes become neighbours in a cluster
        boolean[] adjList = adjMatrix[ v.getId() ];
        for ( int i = 0; i < adjList.length; i++ ) {
            if ( !adjList[i] ) {
                // not connected to this vertex
                continue;
            }

            GraphNode<BayesVariable> relV = g.getNode(i);
            weightRequired *= Math.abs(relV.getContent().getOutcomes().length);
            cliqueBitSet.set(i);

            for ( int j = i+1; j < adjList.length; j++ ) {
                // i + 1, so it doesn't check if a node is connected with itself
                if ( !adjList[j] || adjMatrix[i][j] ) {
                    // edge already exists
                    continue;
                }

                newEdgesRequired++;
            }
        }
     }

    public OpenBitSet getCliqueBitSit() {
        return cliqueBitSet;
    }


    public int getWeightRequired() {
        return weightRequired;
    }

    public int getNewEdgesRequired() {
        return newEdgesRequired;
    }

    @Override
    public int compareTo(EliminationCandidate o) {
        // compare edges, if they are different
        if ( newEdgesRequired != o.newEdgesRequired) {
            return  newEdgesRequired - o.newEdgesRequired;
        }

        // compare the weight of the induced cluster, if they are different
        if ( weightRequired != o.weightRequired ) {
            return weightRequired - o.weightRequired;
        }

        // nodes are the same, ensure arbitrary is deterministic, us the node id
        return v.getId() - o.v.getId();
    }

    @Override
    public String toString() {
        return "EliminationCandidateVertex{" +
               "v=" + v.getId() +
               ", newEdgesRequired=" + newEdgesRequired +
               ", weightRequired=" + weightRequired +
               ", cliqueBitSet=" + cliqueBitSet +
               '}';
    }
}
