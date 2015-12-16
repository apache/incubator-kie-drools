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

package org.drools.beliefs.bayes.example;

import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.BayesVariable;
import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.BayesVariableState;
import org.drools.beliefs.bayes.CliqueState;
import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.JunctionTreeBuilder;
import org.drools.beliefs.bayes.JunctionTreeClique;
import org.drools.beliefs.bayes.Marginalizer;
import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.drools.beliefs.graph.impl.EdgeImpl;
import org.junit.Before;
import org.junit.Test;

import static org.drools.beliefs.bayes.JunctionTreeTest.assertArray;
import static org.drools.beliefs.bayes.JunctionTreeTest.scaleDouble;

public class SprinkerTest {
    Graph<BayesVariable> graph = new BayesNetwork();

    GraphNode<BayesVariable> cloudyNode = graph.addNode();
    GraphNode<BayesVariable> sprinklerNode = graph.addNode();
    GraphNode<BayesVariable> rainNode = graph.addNode();
    GraphNode<BayesVariable> wetGrassNode = graph.addNode();

    BayesVariable cloudy = new BayesVariable<String>("Cloudy", cloudyNode.getId(), new String[]{"true", "false"}, new double[][]{{0.5, 0.5}});
    BayesVariable sprinkler = new BayesVariable<String>("Sprinkler", sprinklerNode.getId(), new String[]{"true", "false"}, new double[][]{{0.5, 0.5}, {0.9, 0.1}});
    BayesVariable rain =  new BayesVariable<String>( "Rain", rainNode.getId(), new String[] { "true", "false" }, new double[][] { { 0.8, 0.2 }, { 0.2, 0.8 } } );
    BayesVariable wetGrass = new BayesVariable<String>( "WetGrass", wetGrassNode.getId(), new String[] { "true", "false" }, new double[][] { { 1.0, 0.0 }, { 0.1, 0.9 }, { 0.1, 0.9 }, { 0.01, 0.99 } } );

    JunctionTree jTree;

    @Before
    public void setUp() {
        connectParentToChildren( cloudyNode, sprinklerNode, rainNode);
        connectParentToChildren( sprinklerNode, wetGrassNode);
        connectParentToChildren( rainNode, wetGrassNode);

        cloudyNode.setContent(cloudy);
        sprinklerNode.setContent( sprinkler);
        rainNode.setContent( rain );
        wetGrassNode.setContent( wetGrass );

        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        jTree = jtBuilder.build();
    }

    @Test
    public void testInitialize() {
        JunctionTreeClique jtNode = jTree.getRoot();

        // cloud, rain sprinkler
        assertArray(new double[]{0.2, 0.05, 0.2, 0.05, 0.09, 0.36, 0.01, 0.04 }, scaleDouble( 3, jtNode.getPotentials() ));

        // wetGrass
        jtNode = jTree.getRoot().getChildren().get(0).getChild();
        assertArray(new double[]{1.0, 0.0, 0.1, 0.9, 0.1, 0.9, 0.01, 0.99   }, scaleDouble( 3, jtNode.getPotentials() ));
    }

    @Test
    public void testNoEvidence() {
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        JunctionTree jTree = jtBuilder.build();

        JunctionTreeClique jtNode = jTree.getRoot();
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.globalUpdate();

        assertArray(new double[]{0.5, 0.5}, scaleDouble(3, bayesInstance.marginalize("Cloudy").getDistribution()));

        assertArray( new double[]{0.5, 0.5},  scaleDouble( 3,  bayesInstance.marginalize("Rain").getDistribution()  ) );

        assertArray( new double[]{0.7, 0.3},  scaleDouble(3, bayesInstance.marginalize("Sprinkler").getDistribution()) );

        assertArray( new double[]{0.353, 0.647},  scaleDouble( 3,  bayesInstance.marginalize("WetGrass").getDistribution() ) );
    }

    @Test
    public void testGrassWetEvidence() {
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        JunctionTree jTree = jtBuilder.build();

        JunctionTreeClique jtNode = jTree.getRoot();
        BayesInstance bayesInstance = new BayesInstance(jTree);

        bayesInstance.setLikelyhood( "WetGrass", new double[]{1.0, 0.0} );

        bayesInstance.globalUpdate();

        assertArray(new double[]{0.639, 0.361}, scaleDouble(3, bayesInstance.marginalize("Cloudy").getDistribution()));

        assertArray( new double[]{0.881, 0.119},  scaleDouble( 3,  bayesInstance.marginalize("Rain").getDistribution()  ) );

        assertArray( new double[]{0.938, 0.062},  scaleDouble(3, bayesInstance.marginalize("Sprinkler").getDistribution()) );

        assertArray( new double[]{1.0, 0.0},  scaleDouble( 3,  bayesInstance.marginalize("WetGrass").getDistribution() ) );
    }

    @Test
    public void testSprinklerEvidence() {
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder( graph );
        JunctionTree jTree = jtBuilder.build();

        JunctionTreeClique jtNode = jTree.getRoot();
        BayesInstance bayesInstance = new BayesInstance(jTree);

        bayesInstance.setLikelyhood( "Sprinkler", new double[]{1.0, 0.0} );
        bayesInstance.setLikelyhood( "Cloudy", new double[]{1.0, 0.0} );

        bayesInstance.globalUpdate();

        assertArray(new double[]{1.0, 0.0}, scaleDouble(3, bayesInstance.marginalize("Cloudy").getDistribution()));

        assertArray( new double[]{0.8, 0.2},  scaleDouble( 3,  bayesInstance.marginalize("Rain").getDistribution()  ) );

        assertArray( new double[]{1.0, 0.0},  scaleDouble(3, bayesInstance.marginalize("Sprinkler").getDistribution()) );

        assertArray( new double[]{0.82, 0.18},  scaleDouble( 3,  bayesInstance.marginalize("WetGrass").getDistribution() ) );
    }

    public static void marginalize(BayesVariableState varState,  CliqueState cliqueState) {
        JunctionTreeClique jtNode = cliqueState.getJunctionTreeClique();
        new Marginalizer(jtNode.getValues().toArray( new BayesVariable[jtNode.getValues().size()]), cliqueState.getPotentials(), varState.getVariable(), varState.getDistribution() );
        System.out.print( varState.getVariable().getName() + " " );
        for ( double d : varState.getDistribution() ) {
            System.out.print(d);
            System.out.print(" ");
        }
        System.out.println(" ");
    }

    public static GraphNode<BayesVariable> addNode(Graph<BayesVariable> graph) {
        GraphNode<BayesVariable> x = graph.addNode();
        x.setContent( new BayesVariable<String>( "x" + x.getId(), x.getId(), new String[] { "a", "b" }, new double[][] { { 0.1, 0.1 } } ) );
        return x;
    }

    public static void connectParentToChildren(GraphNode parent, GraphNode... children) {
        for ( GraphNode child : children ) {
            EdgeImpl e = new EdgeImpl();
            e.setOutGraphNode(parent);
            e.setInGraphNode(child);
        }
    }

}
