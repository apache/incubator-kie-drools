/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.beliefs.bayes.GraphTest.addNode;
import static org.drools.beliefs.bayes.GraphTest.bitSet;
import static org.drools.beliefs.bayes.JunctionTreeTest.scaleDouble;

public class PassMessageTest {

    @Test
    public void testPassMessage1() {
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2"}, null);

        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);

        x0.setContent( a );

        JunctionTreeClique node1 = new JunctionTreeClique(0, graph, bitSet("0001") );
        JunctionTreeClique node2 = new JunctionTreeClique(1, graph, bitSet("0001")  );
        JunctionTreeSeparator sep = new JunctionTreeSeparator(0, node1, node2, bitSet("0001"), graph);

        node1.getPotentials()[0] = 0.2;
        node1.getPotentials()[1] = 0.3;

        node2.getPotentials()[0] = 0.6;
        node2.getPotentials()[1] = 0.7;


        JunctionTree jtree = new JunctionTree(graph, node1, new JunctionTreeClique[] { node1, node2 }, new JunctionTreeSeparator[] { sep});
        BayesInstance bayesInstance = new BayesInstance(jtree);

        SeparatorState sepState = bayesInstance.getSeparatorStates()[0];
        sepState.getPotentials()[0] = 0.4;
        sepState.getPotentials()[1] = 0.5;

        bayesInstance.passMessage(node1, sep, node2);

        assertThat(scaleDouble(3, bayesInstance.getCliqueStates()[node1.getId()].getPotentials())).containsExactly(0.2, 0.3);
        assertThat(scaleDouble(3, sepState.getPotentials())).containsExactly(0.4, 0.6);
        assertThat(scaleDouble(3, bayesInstance.getCliqueStates()[node2.getId()].getPotentials())).containsExactly(0.417, 0.583);
    }

}
