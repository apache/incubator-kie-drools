package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.junit.Test;

import java.util.Arrays;

import static org.drools.beliefs.bayes.GraphTest.addNode;
import static org.drools.beliefs.bayes.GraphTest.bitSet;
import static org.drools.beliefs.bayes.JunctionTreeTest.assertArray;
import static org.drools.beliefs.bayes.JunctionTreeTest.scaleDouble;

public class BayesAbsorbtionTest {

    @Test
    public void testDivide1() {
        double[] newD = new double[] { 10, 8, 4 };
        double[] oldD = new double[] { 2, 4, 1 };
        double[] r = BayesAbsorption.dividePotentials(newD, oldD);

        assertArray(new double[]{5, 2, 4}, scaleDouble(3, r));
    }

    @Test
    public void testDivide2() {
        double[] newD = new double[] { 0.5, 1.0, 1.5, 2.0 };
        double[] oldD = new double[] { 0.1, 0.2, 0.3, 0.4 };
        double[] r = BayesAbsorption.dividePotentials(newD, oldD);

        assertArray(new double[]{5.0, 5.0, 5.0, 5.0}, scaleDouble(3, r));
    }

    @Test
    public void testAbsorption1() {
        // Absorbs into node1 into sep. A and B are in node1. A and B are in the sep.
        // this is a straight forward projection
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2"}, null);
        BayesVariable b = new BayesVariable<String>( "B", 1, new String[] {"B1", "B2"}, null);

        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);

        x0.setContent( a );
        x1.setContent( b );


        JunctionTreeClique node1 = new JunctionTreeClique(0, graph, bitSet("0011") );
        JunctionTreeClique node2 = new JunctionTreeClique(1, graph, bitSet("0011")  );
        SeparatorState sep = new JunctionTreeSeparator(0, node1, node2, bitSet("0011"), graph).createState();

        BayesVariable[] vars = new BayesVariable[] {a, b};

        BayesVariable[] sepVars = new BayesVariable[] { a, b };
        int[] sepVarPos = PotentialMultiplier.createSubsetVarPos(vars, sepVars);

        int sepVarNumberOfStates = PotentialMultiplier.createNumberOfStates(sepVars);
        int[] sepVarMultipliers = PotentialMultiplier.createIndexMultipliers(sepVars, sepVarNumberOfStates);

        double v = 0.44;
        for ( int i = 0; i < node1.getPotentials().length; i++ ) {
            node1.getPotentials()[i] = v;
            v += + 0.4;
        }

        double[] oldSepPotentials = new double[ sep.getPotentials().length];
        Arrays.fill( oldSepPotentials, 0.2);

        v = 0.5;
        for ( int i = 0; i < sep.getPotentials().length; i++ ) {
            sep.getPotentials()[i] = v;
            v += + 0.5;
        }

        BayesAbsorption p = new BayesAbsorption(sepVarPos, oldSepPotentials, sep.getPotentials(), sepVarMultipliers, vars, node1.getPotentials());
        p.absorb();

        assertArray(new double[]{0.035, 0.135, 0.3, 0.529}, scaleDouble(3, node1.getPotentials()));
    }

    @Test
    public void testAbsorption2() {
        // Absorbs into node1 into sep. A, B and C are in node1. A and B are in the sep.
        // this tests a non separator var, after the vars
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2"}, null);
        BayesVariable b = new BayesVariable<String>( "B", 1, new String[] {"B1", "B2"}, null);
        BayesVariable c = new BayesVariable<String>( "C", 2, new String[] {"C1", "C2"}, null);

        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);

        x0.setContent( a );
        x1.setContent( b );
        x2.setContent( c );


        JunctionTreeClique node1 = new JunctionTreeClique(0, graph, bitSet("0111") );
        JunctionTreeClique node2 = new JunctionTreeClique(1, graph, bitSet("0011")  );
        SeparatorState sep = new JunctionTreeSeparator(0, node1, node2, bitSet("0011"), graph).createState();

        BayesVariable[] vars = new BayesVariable[] {a, b, c};

        BayesVariable[] sepVars = new BayesVariable[] { a, b };
        int[] sepVarPos = PotentialMultiplier.createSubsetVarPos(vars, sepVars);

        int sepVarNumberOfStates = PotentialMultiplier.createNumberOfStates(sepVars);
        int[] sepVarMultipliers = PotentialMultiplier.createIndexMultipliers(sepVars, sepVarNumberOfStates);

        double v = 0.44;
        for ( int i = 0; i < node1.getPotentials().length; i++ ) {
            node1.getPotentials()[i] = v;
            v += + 0.4;
        }

        double[] oldSepPotentials = new double[ sep.getPotentials().length];
        Arrays.fill( oldSepPotentials, 0.2);

        v = 0.5;
        for ( int i = 0; i < sep.getPotentials().length; i++ ) {
            sep.getPotentials()[i] = v;
            v += + 0.5;
        }

        BayesAbsorption p = new BayesAbsorption(sepVarPos, oldSepPotentials, sep.getPotentials(), sepVarMultipliers, vars, node1.getPotentials());
        p.absorb();

        assertArray(new double[]{ 0.01, 0.019, 0.055, 0.073, 0.137, 0.163, 0.254, 0.289 }, scaleDouble(3, node1.getPotentials()));
    }

    @Test
    public void testAbsorption3() {
        // Projects from node1 into sep. A, B and C are in node1. A and C are in the sep.
        // this tests a non separator var, in the middle of the vars
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2"},  null);
        BayesVariable b = new BayesVariable<String>( "B", 1, new String[] {"B1", "B2"},  null);
        BayesVariable c = new BayesVariable<String>( "C", 2, new String[] {"C1", "C2"},  null);


        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);


        x0.setContent( a );
        x1.setContent( b );
        x2.setContent( c );

        JunctionTreeClique node1 = new JunctionTreeClique(0, graph, bitSet("0111") );
        JunctionTreeClique node2 = new JunctionTreeClique(1, graph, bitSet("0101")  );
        SeparatorState sep = new JunctionTreeSeparator(0, node1, node2, bitSet("0101"), graph).createState();

        BayesVariable[] vars = new BayesVariable[] {a, b, c};

        BayesVariable[] sepVars = new BayesVariable[] { a, c };
        int[] sepVarPos = PotentialMultiplier.createSubsetVarPos(vars, sepVars);

        int sepVarNumberOfStates = PotentialMultiplier.createNumberOfStates(sepVars);
        int[] sepVarMultipliers = PotentialMultiplier.createIndexMultipliers(sepVars, sepVarNumberOfStates);

        double v = 0.44;
        for ( int i = 0; i < node1.getPotentials().length; i++ ) {
            node1.getPotentials()[i] = v;
            v += + 0.4;
        }

        double[] oldSepPotentials = new double[ sep.getPotentials().length];
        Arrays.fill( oldSepPotentials, 0.2);

        v = 0.5;
        for ( int i = 0; i < sep.getPotentials().length; i++ ) {
            sep.getPotentials()[i] = v;
            v += + 0.5;
        }

        BayesAbsorption p = new BayesAbsorption(sepVarPos, oldSepPotentials, sep.getPotentials(), sepVarMultipliers, vars, node1.getPotentials());
        p.absorb();

        assertArray(new double[]{0.01, 0.038, 0.028, 0.075, 0.139, 0.222, 0.194, 0.295}, scaleDouble(3, node1.getPotentials()));
    }
}