/**
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
package org.drools.beliefs.bayes.example;

import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.BayesVariable;
import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.BayesVariableState;
import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.JunctionTreeBuilder;
import org.drools.beliefs.bayes.JunctionTreeClique;
import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.beliefs.bayes.JunctionTreeTest.scaleDouble;
import static org.drools.beliefs.bayes.example.SprinkerTest.connectParentToChildren;

public class EarthQuakeTest {
    Graph<BayesVariable> graph = new BayesNetwork();

    GraphNode<BayesVariable> burglaryNode   = graph.addNode();
    GraphNode<BayesVariable> earthquakeNode = graph.addNode();
    GraphNode<BayesVariable> alarmNode      = graph.addNode();
    GraphNode<BayesVariable> johnCallsNode  = graph.addNode();
    GraphNode<BayesVariable> maryCallsNode  = graph.addNode();

    BayesVariable burglary   = new BayesVariable<String>("Burglary", burglaryNode.getId(), new String[]{"false", "true"}, new double[][]{{0.001, 0.999}});
    BayesVariable earthquake = new BayesVariable<String>("Earthquake", earthquakeNode.getId(), new String[]{"false", "true"}, new double[][]{{0.002, 0.998}});
    BayesVariable alarm      = new BayesVariable<String>("Alarm", alarmNode.getId(), new String[]{"false", "true"}, new double[][]{{0.95, 0.05}, {0.94, 0.06}, {0.29, 0.71}, {0.001, 0.999}});
    BayesVariable johnCalls  = new BayesVariable<String>("JohnCalls", johnCallsNode.getId(), new String[]{"false", "true"}, new double[][]{{0.90, 0.1}, {0.05, 0.95}});
    BayesVariable maryCalls  = new BayesVariable<String>("MaryCalls", maryCallsNode.getId(), new String[]{"false", "true"}, new double[][]{{0.7, 0.3}, {0.01, 0.99}});

    BayesVariableState burglaryState;
    BayesVariableState earthquakeState;
    BayesVariableState alarmState;
    BayesVariableState johnCallsState;
    BayesVariableState maryCallsState;

    JunctionTreeClique jtNode1;
    JunctionTreeClique jtNode2;
    JunctionTreeClique jtNode3;

    JunctionTree jTree;

    BayesInstance bayesInstance;

    @BeforeEach
    public void setUp() {
        connectParentToChildren(burglaryNode, alarmNode);
        connectParentToChildren(earthquakeNode, alarmNode);
        connectParentToChildren(alarmNode, johnCallsNode, maryCallsNode);

        burglaryNode.setContent(burglary);
        earthquakeNode.setContent(earthquake);
        alarmNode.setContent(alarm);
        johnCallsNode.setContent(johnCalls);
        maryCallsNode.setContent(maryCalls);

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        jTree = jtBuilder.build();
        //jTree.initialize();

        jtNode1 = jTree.getRoot();
        jtNode2 = jtNode1.getChildren().get(0).getChild();
        jtNode3 = jtNode1.getChildren().get(1).getChild();

        bayesInstance = new BayesInstance(jTree);

        burglaryState = bayesInstance.getVarStates()[burglary.getId()];
        earthquakeState = bayesInstance.getVarStates()[earthquake.getId()];
        alarmState = bayesInstance.getVarStates()[alarm.getId()];
        johnCallsState = bayesInstance.getVarStates()[johnCalls.getId()];
        maryCallsState = bayesInstance.getVarStates()[maryCalls.getId()];
    }

    @Test
    public void testInitialize() {
        // johnCalls
        assertThat(scaleDouble(3, jtNode1.getPotentials())).containsExactly(0.90, 0.1, 0.05, 0.95);


        // maryCalls
        assertThat(scaleDouble(3, jtNode2.getPotentials())).containsExactly(0.7, 0.3, 0.01, 0.99);

        // burglary, earthquake, alarm
        assertThat(scaleDouble(7, jtNode3.getPotentials())).containsExactly(0.0000019, 0.0000001, 0.0009381, 0.0000599, 0.0005794, 0.0014186, 0.0009970, 0.9960050);
    }

    @Test
    public void testNoEvidence() {
        bayesInstance.globalUpdate();

        assertThat(scaleDouble(6, bayesInstance.marginalize("JohnCalls").getDistribution())).containsExactly(0.052139, 0.947861);

        assertThat(scaleDouble(6, bayesInstance.marginalize("MaryCalls").getDistribution() )).containsExactly(0.011736, 0.988264);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution())).containsExactly(0.001, 0.999);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution() )).containsExactly(0.002, 0.998);

        assertThat(scaleDouble(6, bayesInstance.marginalize("Alarm").getDistribution())).containsExactly(0.002516, 0.997484);
    }

    @Test
    public void testAlarmEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);

        bayesInstance.setLikelyhood("Alarm", new double[]{1.0, 0.0});

        bayesInstance.globalUpdate();

        assertThat(scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution())).containsExactly(0.9, 0.1);

        assertThat(scaleDouble(3, bayesInstance.marginalize("MaryCalls").getDistribution() )).containsExactly(0.7, 0.3);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution())).containsExactly(0.374, 0.626);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution() )).containsExactly(0.231, 0.769);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution())).containsExactly(1.0, 0.0); 
        
    }

    @Test
    public void testEathQuakeEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);

        bayesInstance.setLikelyhood("Earthquake", new double[]{1.0, 0.0});
        bayesInstance.globalUpdate();

        assertThat(scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution())).containsExactly(0.297, 0.703);

        assertThat(scaleDouble(3, bayesInstance.marginalize("MaryCalls").getDistribution() )).containsExactly(0.211, 0.789);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution())).containsExactly(.001, 0.999);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution() )).containsExactly(1.0, 0.0);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution())).containsExactly(0.291, 0.709);
    }

    @Test
    public void testJoinCallsEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);

        bayesInstance.setLikelyhood("JohnCalls", new double[]{1.0, 0.0});
        bayesInstance.globalUpdate();

        assertThat(scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution())).containsExactly(1.0, 0.0);

        assertThat(scaleDouble(3, bayesInstance.marginalize("MaryCalls").getDistribution() )).containsExactly(0.04, 0.96);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution())).containsExactly(0.016, 0.984);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution() )).containsExactly(0.011, 0.989);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution())).containsExactly(0.043, 0.957);
    }

    @Test
    public void testEarthquakeAndJohnCallsEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.setLikelyhood("JohnCalls", new double[]{1.0, 0.0});
        bayesInstance.setLikelyhood("Earthquake", new double[]{1.0, 0.0});

        bayesInstance.globalUpdate();

        assertThat(scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution())).containsExactly(1.0, 0.0);

        assertThat(scaleDouble(3, bayesInstance.marginalize("MaryCalls").getDistribution() )).containsExactly(0.618, 0.382);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution())).containsExactly(0.003, 0.997);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution() )).containsExactly(1.0, 0.0);

        assertThat(scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution())).containsExactly(0.881, 0.119);
    }

}
