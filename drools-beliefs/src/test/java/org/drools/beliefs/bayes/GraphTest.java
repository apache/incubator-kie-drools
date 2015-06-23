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
import org.drools.beliefs.graph.impl.EdgeImpl;
import org.drools.core.util.bitmask.OpenBitSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GraphTest {


    public static void connectParentToChildren(GraphNode parent, GraphNode... children) {
        for ( GraphNode child : children ) {
            EdgeImpl e = new EdgeImpl();
            e.setOutGraphNode(parent);
            e.setInGraphNode(child);
        }
    }

    public static void connectChildToParents(GraphNode child, GraphNode... parents) {
        for ( GraphNode parent : parents ) {
            EdgeImpl e = new EdgeImpl();
            e.setOutGraphNode(parent);
            e.setInGraphNode(child);
        }
    }

    public static boolean assertLinkedNode(JunctionTreeBuilder graph, int... ints) {
        return assertLinkedVertex( graph.getAdjacencyMatrix(), ints );
    }

    public static boolean assertLinkedVertex(boolean[][] adjMatrix, int... ints) {
        int id = ints[0];

        Collection<Integer> adjVert = JunctionTreeBuilder.getAdjacentVertices(adjMatrix, id);
        assertEquals( ints.length-1, adjVert.size() );
        for ( int i = 1; i < ints.length; i++ ) {
            assertTrue( "link was not true " + id + ", " + i, adjMatrix[id][ints[i]] );
            assertTrue( "link was not true " + i + ", " + id, adjMatrix[ints[i]][id] );
            assertTrue( "does not contain " + ints[i], adjVert.contains(ints[i]) );
        }

        return   false;
    }


    public static GraphNode<BayesVariable> addNode(Graph<BayesVariable> graph) {
        GraphNode<BayesVariable> x = graph.addNode();
        x.setContent( new BayesVariable<String>( "x" + x.getId(), x.getId(), new String[] { "a", "b" }, new double[][] { { 0.1, 0.1 } } ) );
        return x;
    }



    public static List asList(int[] array) {
        List list = new ArrayList(array.length);
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }

    public static OpenBitSet bitSet(String s) {
        OpenBitSet bitSet =  new OpenBitSet(  );
        bitSet.setBits(new long[] { Long.valueOf(s, 2) });
        return bitSet;
    }


}
