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

package org.drools.beliefs.bayes.example;

import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.BayesVariable;
import org.drools.beliefs.bayes.BayesLikelyhood;
import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.BayesVariableState;
import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.JunctionTreeBuilder;
import org.drools.beliefs.bayes.JunctionTreeClique;
import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.junit.Before;
import org.junit.Test;

import static org.drools.beliefs.bayes.JunctionTreeTest.assertArray;
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

    @Before
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
        assertArray(new double[]{0.90, 0.1, 0.05, 0.95}, scaleDouble( 3, jtNode1.getPotentials() ));


        // maryCalls
        assertArray( new double[]{ 0.7, 0.3, 0.01, 0.99 }, scaleDouble( 3, jtNode2.getPotentials() ));

        // burglary, earthquake, alarm
        assertArray( new double[]{0.0000019, 0.0000001, 0.0009381, 0.0000599, 0.0005794, 0.0014186, 0.0009970, 0.9960050 },
                     scaleDouble( 7, jtNode3.getPotentials() ));
    }

    @Test
    public void testNoEvidence() {
        bayesInstance.globalUpdate();

        assertArray( new double[]{0.052139, 0.947861},  scaleDouble(6, bayesInstance.marginalize("JohnCalls").getDistribution()) );

        assertArray( new double[]{0.011736, 0.988264 },  scaleDouble( 6, bayesInstance.marginalize("MaryCalls").getDistribution() ) );

        assertArray( new double[]{0.001, 0.999},  scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()) );

        assertArray( new double[]{ 0.002, 0.998},  scaleDouble( 3, bayesInstance.marginalize("Earthquake").getDistribution() ) );

        assertArray( new double[]{0.002516, 0.997484},   scaleDouble(6, bayesInstance.marginalize("Alarm").getDistribution()) );
    }

    @Test
    public void testAlarmEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);

        bayesInstance.setLikelyhood( "Alarm", new double[]{1.0, 0.0} );

        bayesInstance.globalUpdate();

        assertArray( new double[]{0.9, 0.1}, scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution()) );

        assertArray( new double[]{0.7, 0.3 }, scaleDouble( 3, bayesInstance.marginalize("MaryCalls").getDistribution() ) );

        assertArray( new double[]{0.374, 0.626}, scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()) );

        assertArray( new double[]{ 0.231, 0.769}, scaleDouble( 3, bayesInstance.marginalize("Earthquake").getDistribution() ) );

        assertArray( new double[]{1.0, 0.0}, scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution()) ); }

    @Test
    public void testEathQuakeEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);

        bayesInstance.setLikelyhood("Earthquake", new double[]{1.0, 0.0});
        bayesInstance.globalUpdate();

        assertArray( new double[]{0.297, 0.703}, scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution()) );

        assertArray( new double[]{0.211, 0.789 }, scaleDouble( 3, bayesInstance.marginalize("MaryCalls").getDistribution() ) );

        assertArray( new double[]{.001, 0.999}, scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()) );

        assertArray( new double[]{1.0, 0.0}, scaleDouble( 3, bayesInstance.marginalize("Earthquake").getDistribution() ) );

        assertArray( new double[]{0.291, 0.709}, scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution()) );
    }

    @Test
    public void testJoinCallsEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);

        bayesInstance.setLikelyhood("JohnCalls", new double[]{1.0, 0.0});
        bayesInstance.globalUpdate();

        assertArray( new double[]{1.0, 0.0}, scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution()) );

        assertArray( new double[]{0.04, 0.96 }, scaleDouble( 3, bayesInstance.marginalize("MaryCalls").getDistribution() ) );

        assertArray( new double[]{0.016, 0.984}, scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()) );

        assertArray( new double[]{0.011, 0.989}, scaleDouble( 3, bayesInstance.marginalize("Earthquake").getDistribution() ) );

        assertArray( new double[]{0.043, 0.957}, scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution()) );
    }

    @Test
    public void testEarthquakeAndJohnCallsEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.setLikelyhood("JohnCalls", new double[]{1.0, 0.0});
        bayesInstance.setLikelyhood("Earthquake", new double[]{1.0, 0.0});

        bayesInstance.globalUpdate();

        assertArray( new double[]{1.0, 0.0}, scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution()) );

        assertArray( new double[]{0.618, 0.382 }, scaleDouble( 3, bayesInstance.marginalize("MaryCalls").getDistribution() ) );

        assertArray( new double[]{0.003, 0.997}, scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()) );

        assertArray( new double[]{ 1.0, 0.0}, scaleDouble( 3, bayesInstance.marginalize("Earthquake").getDistribution() ) );

        assertArray( new double[]{0.881, 0.119}, scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution()) );
    }

}
