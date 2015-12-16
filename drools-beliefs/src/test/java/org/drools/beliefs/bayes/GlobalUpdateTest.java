/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.drools.beliefs.bayes.GraphTest.addNode;
import static org.drools.beliefs.bayes.GraphTest.bitSet;
import static org.junit.Assert.assertEquals;

/**
 * This class tests that the iteration order for collect and distribute evidence is correct.
 * It tests from 4 different positions on the same network.  First individually for collect and then distribute, and then through globalUpdate request
 * Then it calls the globalUpdate that recurses the network and calls globalUpdate for each clique
 */
public class GlobalUpdateTest {
    Graph<BayesVariable> graph = new BayesNetwork();
    GraphNode            x0    = addNode(graph);

    //          0
    //          |
    //     3_2__1
    //       |  |
    //       4  5
    //          |
    //       7__6__8
    JunctionTreeClique n0 = new JunctionTreeClique(0, graph, bitSet("1"));
    JunctionTreeClique n1 = new JunctionTreeClique(1, graph, bitSet("1"));
    JunctionTreeClique n2 = new JunctionTreeClique(2, graph, bitSet("1"));
    JunctionTreeClique n3 = new JunctionTreeClique(3, graph, bitSet("1"));
    JunctionTreeClique n4 = new JunctionTreeClique(4, graph, bitSet("1"));
    JunctionTreeClique n5 = new JunctionTreeClique(5, graph, bitSet("1"));
    JunctionTreeClique n6 = new JunctionTreeClique(6, graph, bitSet("1"));
    JunctionTreeClique n7 = new JunctionTreeClique(7, graph, bitSet("1"));
    JunctionTreeClique n8 = new JunctionTreeClique(8, graph, bitSet("1"));

    JunctionTree  tree;
    BayesInstance bayesInstance;

    final List<String> messageResults      = new ArrayList<String>();
    final List<String> globalUpdateResults = new ArrayList<String>();

    @Before
    public void startUp() {
        int i = 0;
        List<JunctionTreeSeparator> list = new ArrayList<JunctionTreeSeparator>();
        connectChildren(graph, n0, list, n1);
        connectChildren(graph, n1, list, n2, n5);
        connectChildren(graph, n2, list, n3, n4);
        connectChildren(graph, n5, list, n6);
        connectChildren(graph, n6, list, n7, n8);

        tree = new JunctionTree(graph, n0, new JunctionTreeClique[]{n0, n1, n2, n3, n4, n5, n6, n7, n8}, list.toArray(new JunctionTreeSeparator[list.size()]));

        bayesInstance = new BayesInstance(tree);

        bayesInstance.setPassMessageListener(new PassMessageListener() {
            @Override
            public void beforeProjectAndAbsorb(JunctionTreeClique sourceNode, JunctionTreeSeparator sep, JunctionTreeClique targetNode, double[] oldSeparatorPotentials) {
                // System.out.print("\"" + sourceNode.getId() + ":" + targetNode.getId() + "\", ");
                messageResults.add(sourceNode.getId() + ":" + targetNode.getId());
            }

            @Override
            public void afterProject(JunctionTreeClique sourceNode, JunctionTreeSeparator sep, JunctionTreeClique targetNode, double[] oldSeparatorPotentials) {

            }

            @Override
            public void afterAbsorb(JunctionTreeClique sourceNode, JunctionTreeSeparator sep, JunctionTreeClique targetNode, double[] oldSeparatorPotentials) {

            }
        });

        bayesInstance.setGlobalUpdateListener(new GlobalUpdateListener() {
            @Override
            public void beforeGlobalUpdate(CliqueState clique) {
                globalUpdateResults.add("" + clique.getJunctionTreeClique().getId());
            }

            @Override
            public void afterGlobalUpdate(CliqueState clique) {

            }
        });
    }

    @Test
    public void testCollectFromRootClique() {
        bayesInstance.collectEvidence(n0);
        assertEquals(asList("3:2", "4:2", "2:1", "7:6", "8:6", "6:5", "5:1", "1:0" ), messageResults);
    }

    @Test
    public void testCollectFromMidTipClique() {
        bayesInstance.collectEvidence(n4);
        assertEquals( asList( "0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "3:2", "2:4" ), messageResults);
    }

    @Test
    public void testCollectFromEndTipClique() {
        bayesInstance.collectEvidence(n7);
        assertEquals( asList( "0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "8:6", "6:7" ), messageResults);
    }

    @Test
    public void testCollectFromMidClique() {
        bayesInstance.collectEvidence(n5);
        assertEquals( asList( "0:1", "3:2", "4:2", "2:1", "1:5", "7:6", "8:6", "6:5" ), messageResults);
    }

    @Test
    public void testDistributeFromRootClique() {
        bayesInstance.distributeEvidence(n0);
        assertEquals( asList( "0:1", "1:2", "2:3", "2:4", "1:5", "5:6", "6:7", "6:8" ), messageResults);
    }

    @Test
    public void testDistributeFromMidTipClique() {
        bayesInstance.distributeEvidence(n4);
        assertEquals( asList( "4:2", "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:3" ), messageResults);
    }

    @Test
    public void testDistributeFromEndTipClique() {
        bayesInstance.distributeEvidence(n7);
        assertEquals( asList( "7:6", "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:8" ), messageResults);
    }

    @Test
    public void testDistributeFromMidClique() {
        bayesInstance.distributeEvidence(n5);
        assertEquals( asList( "5:1", "1:0", "1:2", "2:3", "2:4", "5:6", "6:7", "6:8" ), messageResults);
    }

    @Test
    public void testGlobalUpdateFromRootClique() {
        bayesInstance.globalUpdate(n0);
        assertEquals( asList( "3:2", "4:2", "2:1", "7:6", "8:6", "6:5", "5:1", "1:0", //n0
                              "0:1", "1:2", "2:3", "2:4", "1:5", "5:6", "6:7", "6:8" //n0
                            ), messageResults);
        assertEquals( asList("0"), globalUpdateResults);
    }

    @Test
    public void testGlobalUpdateFromMidTipClique() {
        bayesInstance.globalUpdate(n4);
        assertEquals( asList( "0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "3:2", "2:4", //n4
                              "4:2", "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:3" //n4
                            ), messageResults);
        assertEquals( asList("4"), globalUpdateResults);
    }

    @Test
    public void testGlobalUpdateFromEndTipClique() {
        bayesInstance.globalUpdate(n7);
        assertEquals( asList( "0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "8:6", "6:7", //n7
                              "7:6", "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:8" //n7
                            ), messageResults);
        assertEquals( asList("7"), globalUpdateResults);
    }

    @Test
    public void testGlobalUpdateFromMidClique() {
        bayesInstance.globalUpdate(n5);
        assertEquals( asList( "0:1", "3:2", "4:2", "2:1", "1:5", "7:6", "8:6", "6:5", //n5
                              "5:1", "1:0", "1:2", "2:3", "2:4", "5:6", "6:7", "6:8" //n5
                            ), messageResults);
        assertEquals( asList("5"), globalUpdateResults);
    }


    @Test
    public void testDistributeFromGlobalUpdate() {
        bayesInstance.globalUpdate();
        assertEquals( asList( "3:2", "4:2", "2:1", "7:6", "8:6", "6:5", "5:1", "1:0", //n0
                              "0:1", "1:2", "2:3", "2:4", "1:5", "5:6", "6:7", "6:8" //n0
//                              "0:1", "3:2", "4:2", "2:1", "7:6", "8:6", "6:5", "5:1", //n1
//                              "1:0", "1:2", "2:3", "2:4", "1:5", "5:6", "6:7", "6:8", //n1
//                              "0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "3:2", "4:2", //n2
//                              "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:3", "2:4", //n2
//                              "0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "4:2", "2:3", //n3
//                              "3:2", "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:4", //n3
//                              "0:1", "7:6", "8:6", "6:5", "5:1", "1:2", "3:2", "2:4", //n4
//                              "4:2", "2:1", "1:0", "1:5", "5:6", "6:7", "6:8", "2:3", //n4
//                              "0:1", "3:2", "4:2", "2:1", "1:5", "7:6", "8:6", "6:5", //n5
//                              "5:1", "1:0", "1:2", "2:3", "2:4", "5:6", "6:7", "6:8", //n5
//                              "0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "7:6", "8:6", //n6
//                              "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:7", "6:8", //n6
//                              "0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "8:6", "6:7", //n7
//                              "7:6", "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:8", //n7
//                              "0:1", "3:2", "4:2", "2:1", "1:5", "5:6", "7:6", "6:8", //n8
//                              "8:6", "6:5", "5:1", "1:0", "1:2", "2:3", "2:4", "6:7"  //n8
                            ), messageResults);

//        assertEquals( asList( "0", "1", "2", "3", "4", "5", "6", "7", "8"), globalUpdateResults);
        assertEquals( asList( "0" ), globalUpdateResults);
    }

    public void testGlobalUpdate() {
        bayesInstance.globalUpdate();
    }

    public List asList(String... items) {
        List<String> list = new ArrayList<String>();
        for ( String s : items ) {
            list.add( s );
        }
        return list;
    }

    public void connectChildren(Graph<BayesVariable> graph, JunctionTreeClique parent, List list, JunctionTreeClique... children) {
        for ( JunctionTreeClique child : children ) {
            list.add( new JunctionTreeSeparator(list.size(), parent, child, bitSet("0"), graph) );
        }
    }


}
