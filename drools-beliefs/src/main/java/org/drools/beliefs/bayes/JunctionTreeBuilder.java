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

import org.drools.beliefs.graph.Edge;
import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.drools.core.util.bitmask.OpenBitSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class JunctionTreeBuilder {
    private Graph<BayesVariable> graph;
    private boolean[][]          adjacencyMatrix;

    public Graph<BayesVariable> getGraph() {
        return graph;
    }

    public JunctionTreeBuilder(Graph<BayesVariable> graph) {
        this.graph = graph;
        adjacencyMatrix = new boolean[graph.size()][graph.size()];

        for (GraphNode<BayesVariable> v : graph ) {
            for (Edge e1 : v.getInEdges()) {
                GraphNode pV1 = graph.getNode(e1.getOutGraphNode().getId());
                connect(adjacencyMatrix, pV1.getId(), v.getId());
            }
        }
    }
    public JunctionTree build() {
        return build( true );
    }

    public JunctionTree build(boolean init) {
        moralize();
        List<OpenBitSet>  cliques = triangulate();
        return junctionTree(cliques, init);
    }


    public void moralize() {
        for (GraphNode<BayesVariable> v : graph ) {
            for ( Edge e1 : v.getInEdges() ) {
                GraphNode pV1 = graph.getNode(e1.getOutGraphNode().getId());
                moralize(v, pV1);
            }
        }
    }

    public void moralize(GraphNode<BayesVariable> v, GraphNode v1) {
        for ( Edge e2 : v.getInEdges() ) {
            // moralize, by connecting each parent with each other
            GraphNode v2 = graph.getNode(e2.getOutGraphNode().getId());
            if ( v1 == v2 ) {
                continue; // don't connect to itself
            }
            if ( adjacencyMatrix[v1.getId()][v2.getId()] ) {
                // already connected, continue
                continue;
            }
            connect(getAdjacencyMatrix(), v1.getId(), v2.getId());
        }
    }

    public static void connect(boolean[][] adjMatrix, int v1, int v2) {
        adjMatrix[v1][v2] = true;
        adjMatrix[v2][v1] = true;
    }

    public static void disconnect(boolean[][] adjMatrix, int v1, int v2) {
        adjMatrix[v1][v2] = false;
        adjMatrix[v2][v1] = false;
    }

    public List<OpenBitSet> triangulate() {
        // A PriorityQueue is used, as it needs resorting, as new edges are added due to elimination
        // Build up a Priority Queue of Vertices to be eliminated
        // As their edge counts increase, th PriorityQueue ensures they efficiently maintained in a sorted order.
        boolean[][] clonedAdjMatrix = cloneAdjacencyMarix( adjacencyMatrix );
        PriorityQueue<EliminationCandidate> p = new PriorityQueue<EliminationCandidate>(graph.size());
        Map<Integer, EliminationCandidate> elmVertMap = new HashMap<Integer, EliminationCandidate>();

        for (GraphNode<BayesVariable> v : graph ) {
            EliminationCandidate elmCandVert = new EliminationCandidate(graph, clonedAdjMatrix, v);
            p.add( elmCandVert );
            elmVertMap.put( v.getId(), elmCandVert );
        }

        // Iterate and eliminate each Vertex in turn
        List<OpenBitSet> cliques = new ArrayList<OpenBitSet>();
        while ( !p.isEmpty() ) {
            EliminationCandidate v = p.remove();
            updateCliques(cliques, v.getCliqueBitSit()); // keep track of the maximal cliques formed during elimination

            // Not all vertexes get updated, as they may already be connected. Only track those that have changed edges
            Set<Integer> verticesToUpdate = new HashSet<Integer>();
            boolean[] adjList = clonedAdjMatrix[ v.getV().getId() ];
            createClique(v.getV().getId(), clonedAdjMatrix, verticesToUpdate, adjList);
            eliminateVertex(p, elmVertMap, clonedAdjMatrix, adjList, verticesToUpdate, v);
        }
        return cliques;
    }

    public void eliminateVertex(PriorityQueue<EliminationCandidate> p, Map<Integer, EliminationCandidate> elmVertMap, boolean[][] clonedAdjMatrix, boolean[] adjList, Set<Integer> verticesToUpdate, EliminationCandidate v) {
        // remove the vertex, by disconnecting all it's edges
        int id = v.getV().getId();
        for ( int i = 0; i < adjList.length; i++ ) {
            disconnect(clonedAdjMatrix, id, i);
        }

        // iterate all vertices that had their edges updated, and update their score and re-add to the priority queue
        // must also update everything they touch too
        for (Integer i : verticesToUpdate) {
            EliminationCandidate vertexToUpdate = elmVertMap.get( i );
            p.remove( vertexToUpdate );
            vertexToUpdate.update();
            p.add(vertexToUpdate);
        }
    }

    public void createClique(int v, boolean[][] clonedAdjMatrix, Set<Integer> verticesToUpdate, boolean[] adjList) {
        for ( int i = 0; i < adjList.length; i++ ) {
            if ( !adjList[i] ) {
                // not connected to this vertex
                continue;
            }
            getRelatedVerticesToUpdate(v, clonedAdjMatrix, verticesToUpdate, i);

            boolean needsConnection = false;
            for ( int j = i+1; j < adjList.length; j++ ) {
                // i + 1, so it doesn't check if a node is connected with itself
                if ( !adjList[j] || clonedAdjMatrix[i][j] ) {
                    // edge already exists
                    continue;
                }

                connect(adjacencyMatrix, i, j);
                connect(clonedAdjMatrix, i, j );
                getRelatedVerticesToUpdate(v, clonedAdjMatrix, verticesToUpdate, j);

                needsConnection = true;
            }

            if ( needsConnection ) {
                verticesToUpdate.add( i );
            }
        }
    }

    private void getRelatedVerticesToUpdate(int v, boolean[][] clonedAdjMatrix, Set<Integer> verticesToUpdate, int i) {
        verticesToUpdate.add( i );
        // must also add anything i touches
        for ( Integer k : JunctionTreeBuilder.getAdjacentVertices(clonedAdjMatrix, i) ) {
            if ( k != v ) {
                verticesToUpdate.add( k ); // don't add the v
            }
        }
    }

    public static void updateCliques(List<OpenBitSet> cliques, OpenBitSet newClique) {
        // iterate all the existing cliques, checking for a superset.
        // if no superset is found, then add the cliques
        boolean superSetFound = false;
        for ( OpenBitSet existingCluster : cliques ) {
            // is existingCluster a supserset of newClique, visa-vis is newClique a subset of  existingCluster
            if ( OpenBitSet.andNotCount(newClique, existingCluster) == 0) {
                superSetFound = true;
                break; // superset found
            }
        }
        if ( !superSetFound ) {
            cliques.add(newClique);
        }
    }

    public boolean[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    /**
     * Clones the provided array
     *
     * @param src
     * @return a new clone of the provided array
     */
    public static boolean[][] cloneAdjacencyMarix(boolean[][] src) {
        int length = src.length;
        boolean[][] target = new boolean[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }

    public JunctionTree junctionTree(List<OpenBitSet> cliques, boolean init) {
        List<SeparatorSet> list = new ArrayList<SeparatorSet>();
        for ( int i = 0; i < cliques.size(); i++ ) {
            for ( int j = i+1; j < cliques.size(); j++ ) {
                SeparatorSet separatorSet = new SeparatorSet( cliques.get(i), i, cliques.get(j), j, graph );
                if ( separatorSet.getMass() > 0 ) {
                    list.add(separatorSet);
                }
            }
        }

        list.addAll( list );
        Collections.sort(list);

        SeparatorSet[][][] sepGraphs = new SeparatorSet[cliques.size()][][];
        JunctionTreeClique[] jtNodes = new JunctionTreeClique[cliques.size()];
        JunctionTreeSeparator[] jtSeps = new JunctionTreeSeparator[cliques.size()-1];

        OpenBitSet[] varNodeToCliques = new OpenBitSet[graph.size()];
        for ( int i = 0, length = cliques.size(); i < length; i++ ) {
            // assign each Clique to a JunctionNode and give it a
            JunctionTreeClique node = new JunctionTreeClique( i, graph, cliques.get(i));
            jtNodes[i] = node;
            sepGraphs[i] = new SeparatorSet[graph.size()][graph.size()];
            mapVarNodeToCliques(varNodeToCliques, i, cliques.get(i));
        }

        for ( int i = 0, j = 0, length = cliques.size()-1; i < length; ) {
            SeparatorSet separatorSet = list.get(j++);

            JunctionTreeClique node1 =  jtNodes[separatorSet.getId1()];
            JunctionTreeClique node2 =  jtNodes[separatorSet.getId2()];

            if ( sepGraphs[node1.getId()] == sepGraphs[node2.getId()]) {
                continue;
            }

            //mergeGraphs( graphs, node1, graphs[node2.getId()] );
            mergeGraphs( sepGraphs, separatorSet);
            i++;
        }

        createJunctionTreeGraph( sepGraphs[0], jtNodes[0], jtNodes, jtSeps, 0 );

        mapNodeToCliqueFamily(varNodeToCliques, jtNodes);

        return new JunctionTree(graph, jtNodes[0], jtNodes, jtSeps, init);
    }


    public void mergeGraphs(SeparatorSet[][][] graphs, SeparatorSet separatorSet) {
        SeparatorSet[][] srcGraph = graphs[separatorSet.getId1()];
        SeparatorSet[][] trgGraph = graphs[separatorSet.getId2()];

        // merge the src graph into the trg graph
        for ( int i = 0; i < srcGraph.length; i++ ) {
            SeparatorSet[] row = srcGraph[i];
            for ( int j = 0; j < row.length; j++ ) {
                if ( row[j] != null ) {
                    trgGraph[i][j] = row[j];
                    // map the cliques to the new map
                    graphs[j] = trgGraph;
                }
            }
        }

        // add new connection
        graphs[separatorSet.getId1()] = trgGraph;
        trgGraph[separatorSet.getId1()][separatorSet.getId2()] = separatorSet;
        trgGraph[separatorSet.getId2()][separatorSet.getId1()] = separatorSet;
    }

    public int createJunctionTreeGraph(SeparatorSet[][] sepGraph, JunctionTreeClique parent, JunctionTreeClique[] jtNodes, JunctionTreeSeparator[] jtSeps, int i) {
        SeparatorSet[] row = sepGraph[parent.getId()];
        for ( int j = 0; j < row.length; j++ ) {
            if ( row[j] != null ) {
                SeparatorSet separatorSet = row[j];
                JunctionTreeClique node1 =  jtNodes[separatorSet.getId1()];
                JunctionTreeClique node2 =  jtNodes[separatorSet.getId2()];
                JunctionTreeClique child = ( node1 != parent ) ? node1 : node2; // this ensures we build it in the current parent/child order, based on recursive iteration
                JunctionTreeSeparator sepNode = new JunctionTreeSeparator(i++, parent, child, separatorSet.getIntersection(), graph );
                jtSeps[sepNode.getId()] = sepNode;

                // connection made, remove from the graph, before recursion
                sepGraph[separatorSet.getId1()][separatorSet.getId2()] = null;
                sepGraph[separatorSet.getId2()][separatorSet.getId1()] = null;
                createJunctionTreeGraph( sepGraph, child, jtNodes, jtSeps, i );
            }
        }
        return i;
    }


    /**
     * Given the set of cliques, mapped via ID in a Bitset, for a given bayes node,
     * Find the best clique. Where best clique is one that contains all it's parents
     * with the smallest number of nodes in that clique. When there are no parents
     * then simply pick the clique with the smallest number nodes.
     * @param varNodeToCliques
     * @param jtNodes
     */
    public void mapNodeToCliqueFamily(OpenBitSet[] varNodeToCliques, JunctionTreeClique[] jtNodes) {
        for ( int i = 0; i < varNodeToCliques.length; i++ ) {
            GraphNode<BayesVariable> varNode = graph.getNode( i );

            // Get OpenBitSet for parents
            OpenBitSet parents = new OpenBitSet();
            int count = 0;
            for ( Edge edge : varNode.getInEdges() ) {
                parents.set( edge.getOutGraphNode().getId() );
                count++;
            }

            if ( count == 0 ) {
                // node has no parents, so simply find the smallest clique it's in.
            }

            OpenBitSet cliques = varNodeToCliques[i];
            if  ( cliques == null ) {
                throw new IllegalStateException("Node exists, that is not part of a clique. " + varNode.toString());
            }
            int bestWeight = -1;
            int clique = -1;
            // finds the smallest node, that contains all the parents
            for ( int j = cliques.nextSetBit(0); j >= 0; j = cliques.nextSetBit( j+ 1 ) ) {
                JunctionTreeClique jtNode = jtNodes[j];

                // if the node has parents, we find the small clique it's in.
                // If it has parents then is jtNode a supserset of parents, visa-vis is parents a subset of jtNode
                if ( (count == 0 || OpenBitSet.andNotCount(parents, jtNode.getBitSet()) == 0 ) && ( clique == -1 || jtNode.getBitSet().cardinality() < bestWeight ) ) {
                    bestWeight = (int) jtNode.getBitSet().cardinality();
                    clique = j;
                }
            }

            if ( clique == -1 ) {
                throw new IllegalStateException("No clique for node found." + varNode.toString());
            }
            varNode.getContent().setFamily( clique );
            jtNodes[clique].addToFamily( varNode.getContent() );
        }

    }

    /**
     * Maps each Bayes node to cliques it's in.
     * It uses a BitSet to map the ID of the cliques
     * @param nodeToCliques
     * @param id
     * @param clique
     */
    public void mapVarNodeToCliques(OpenBitSet[] nodeToCliques, int id, OpenBitSet clique) {
        for ( int i = clique.nextSetBit(0); i >= 0; i = clique.nextSetBit( i + 1 ) ) {
             OpenBitSet cliques = nodeToCliques[i];
            if ( cliques == null ) {
                cliques = new OpenBitSet();
                nodeToCliques[i] = cliques;
            }
            cliques.set(id);
        }
    }

    public static List<Integer> getAdjacentVertices(boolean[][] adjacencyMatrix, int i) {
        List<Integer> list = new ArrayList<Integer>(adjacencyMatrix.length);
        for ( int j = 0; j < adjacencyMatrix[i].length; j++ ) {
            if ( adjacencyMatrix[i][j] ) {
                list.add( j );
            }
        }
        return list;
    }


}
