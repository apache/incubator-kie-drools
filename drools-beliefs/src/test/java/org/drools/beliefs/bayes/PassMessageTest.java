package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.junit.Test;

import static org.drools.beliefs.bayes.GraphTest.addNode;
import static org.drools.beliefs.bayes.GraphTest.bitSet;
import static org.drools.beliefs.bayes.JunctionTreeTest.assertArray;
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

        SeparatorState sepState = bayesInstance.getSparatorStates()[0];
        sepState.getPotentials()[0] = 0.4;
        sepState.getPotentials()[1] = 0.5;

        bayesInstance.passMessage(node1, sep, node2);

        assertArray(new double[]{0.2, 0.3}, scaleDouble(3, bayesInstance.getCliqueStates()[node1.getId()].getPotentials()));
        assertArray(new double[]{0.4, 0.6}, scaleDouble(3, sepState.getPotentials()));
        assertArray(new double[]{0.417, 0.583}, scaleDouble(3, bayesInstance.getCliqueStates()[node2.getId()].getPotentials()));
    }

}
