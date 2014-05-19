package org.drools.beliefs.bayes;

import junit.framework.AssertionFailedError;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.drools.beliefs.bayes.GraphTest.addNode;
import static org.drools.beliefs.bayes.GraphTest.bitSet;
import static org.drools.beliefs.bayes.GraphTest.connectParentToChildren;
import static org.drools.beliefs.bayes.PotentialMultiplier.indexToKey;
import static org.drools.beliefs.bayes.PotentialMultiplier.keyToIndex;
import static org.junit.Assert.assertEquals;

public class JunctionTreeTest {

    @Test
    public void testIndextoKeyMapping1() {
        // tests simple index to key mapping for a 2x2 array.
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2"}, null);
        BayesVariable b = new BayesVariable<String>( "B", 0, new String[] {"B1", "B2"}, null);

        BayesVariable[] vars = new BayesVariable[] {a, b};
        int numberOfStates = PotentialMultiplier.createNumberOfStates(vars);
        int[] indexMultipliers = PotentialMultiplier.createIndexMultipliers(vars, numberOfStates);

        assertEquals( 4, numberOfStates );
        assertIndexToKeyMapping(numberOfStates, indexMultipliers);
    }

    @Test
    public void testIndextoKeyMapping2() {
        // tests simple index to key mapping for a 2x3 array.
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2", "A3"}, null);
        BayesVariable b = new BayesVariable<String>( "B", 0, new String[] {"B1", "B2", "B3"}, null);

        BayesVariable[] vars = new BayesVariable[] {a, b};
        int numberOfStates = PotentialMultiplier.createNumberOfStates(vars);
        int[] indexMultipliers = PotentialMultiplier.createIndexMultipliers(vars, numberOfStates);

        assertEquals( 9, numberOfStates );
        assertIndexToKeyMapping(numberOfStates, indexMultipliers);
    }

    @Test
    public void testIndextoKeyMapping3() {
        // tests a slightly more complex array, which has different lengths for rows. This maps to the Year2000 problem, which uses this array size and shape.
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2", "A3"}, null);
        BayesVariable b = new BayesVariable<String>( "B", 0, new String[] {"B1", "B2", "B3"}, null);
        BayesVariable c = new BayesVariable<String>( "C", 0, new String[] {"C1", "C2", "C3", "C4"}, null);
        BayesVariable d = new BayesVariable<String>( "D", 0, new String[] {"D1", "D2", "D3"}, null);

        BayesVariable[] vars = new BayesVariable[] {a, b, c, d};
        int numberOfStates = PotentialMultiplier.createNumberOfStates(vars);
        int[] indexMultipliers = PotentialMultiplier.createIndexMultipliers(vars, numberOfStates);

        assertEquals( 108, numberOfStates);
        assertIndexToKeyMapping(numberOfStates, indexMultipliers);
    }

    @Test
    public void testPotentialMultiplication1() {
        // This tests a simple clique, where the variable being multiplied only has one parent.
        // There are no gaps in the variable key, compared to the path
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2"}, null);
        BayesVariable b = new BayesVariable<String>( "B", 0, new String[] {"B1", "B2"}, new double[][] {{0.1, 0.2}, { 0.3, 0.4 }});

        BayesVariable[] vars = new BayesVariable[] {a, b};
        int numberOfStates = PotentialMultiplier.createNumberOfStates(vars);
        int[] multipliers = PotentialMultiplier.createIndexMultipliers(vars, numberOfStates);

        assertEquals( 4, numberOfStates);
        assertIndexToKeyMapping(numberOfStates, multipliers);

        double[] potentials = new double[numberOfStates];
        Arrays.fill(potentials, 1);


        BayesVariable[] parents = new BayesVariable[] { a };
        int[] parentVarPos = PotentialMultiplier.createSubsetVarPos(vars, parents);

        int parentsNumberOfStates = PotentialMultiplier.createNumberOfStates(parents);
        int[] parentIndexMultipliers = PotentialMultiplier.createIndexMultipliers(parents, parentsNumberOfStates);


        PotentialMultiplier m = new PotentialMultiplier(b.getProbabilityTable(), 1, parentVarPos, parentIndexMultipliers, vars, multipliers, potentials);

        m.multiple();
        assertArray(new double[]{0.1, 0.2, 0.3, 0.4}, potentials);

        // test that it's applying variable multiplications correctly ontop of each other. This simulates the application of project variabe multiplications
        m.multiple();
        assertArray(new double[]{0.01, 0.04, 0.09, 0.16}, scaleDouble( 3, potentials ));
    }

    @Test
    public void testPotentialMultiplication2() {
        // This clique has 4 variables. The variable being multiplied has two parents, directly above it.
        // There is a non parent, after it. While d is not part of the key, it's still part of over all path, iterated through by the cross products,
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2"}, null);
        BayesVariable b = new BayesVariable<String>( "B", 0, new String[] {"B1", "B2"}, null);
        BayesVariable c = new BayesVariable<String>( "C", 0, new String[] {"C1", "C2"}, new double[][] {{0.1, 0.2}, {0.3, 0.4}, {0.5, 0.6}, { 0.7, 0.8 }});
        BayesVariable d = new BayesVariable<String>( "D", 0, new String[] {"D1", "D2"}, null);

        BayesVariable[] vars = new BayesVariable[] {a, b, c, d};
        int numberOfStates = PotentialMultiplier.createNumberOfStates(vars);
        int[] multipliers = PotentialMultiplier.createIndexMultipliers(vars, numberOfStates);

        assertEquals( 16, numberOfStates);
        assertIndexToKeyMapping(numberOfStates, multipliers);

        double[] potentials = new double[numberOfStates];
        Arrays.fill(potentials, 1);

        BayesVariable[] parents = new BayesVariable[] { a, b };
        int[] parentVarPos = PotentialMultiplier.createSubsetVarPos(vars, parents);

        int parentsNumberOfStates = PotentialMultiplier.createNumberOfStates(parents);
        int[] parentIndexMultipliers = PotentialMultiplier.createIndexMultipliers(parents, parentsNumberOfStates);


        PotentialMultiplier m = new PotentialMultiplier(c.getProbabilityTable(), 2, parentVarPos, parentIndexMultipliers, vars, multipliers, potentials);

        m.multiple();
        assertArray(new double[]{0.1, 0.1, 0.2, 0.2, 0.3, 0.3, 0.4, 0.4, 0.5, 0.5, 0.6, 0.6, 0.7, 0.7, 0.8, 0.8}, scaleDouble( 3, potentials ));

        // test that it's applying variable multiplications correctly ontop of each other. This simulates the application of project variabe multiplications
        m.multiple();
        assertArray(new double[]{0.01, 0.01, 0.04, 0.04, 0.09, 0.09, 0.16, 0.16, 0.25, 0.25, 0.36, 0.36, 0.49, 0.49, 0.64, 0.64}, scaleDouble( 3, potentials ) );
    }

    @Test
    public void testPotentialMultiplication3() {
        // This clique has 4 variables. One parent is before and the other parent is after the  variable being multiplied.
        // While a is not part of the parent key, it's still part of over all path, iterated through by the cross products,
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2"}, null);
        BayesVariable b = new BayesVariable<String>( "B", 0, new String[] {"B1", "B2"}, null);
        BayesVariable c = new BayesVariable<String>( "C", 0, new String[] {"C1", "C2"}, new double[][] {{0.1, 0.2}, {0.3, 0.4}, {0.5, 0.6}, { 0.7, 0.8 }});
        BayesVariable d = new BayesVariable<String>( "D", 0, new String[] {"D1", "D2"}, null);

        BayesVariable[] vars = new BayesVariable[] {a, b, c, d};
        int numberOfStates = PotentialMultiplier.createNumberOfStates(vars);
        int[] multipliers = PotentialMultiplier.createIndexMultipliers(vars, numberOfStates);

        assertEquals( 16, numberOfStates);
        assertIndexToKeyMapping(numberOfStates, multipliers);

        double[] potentials = new double[numberOfStates];
        Arrays.fill(potentials, 1);

        BayesVariable[] parents = new BayesVariable[] { b, d };
        int[] parentVarPos = PotentialMultiplier.createSubsetVarPos(vars, parents);

        int parentsNumberOfStates = PotentialMultiplier.createNumberOfStates(parents);
        int[] parentIndexMultipliers = PotentialMultiplier.createIndexMultipliers(parents, parentsNumberOfStates);


        PotentialMultiplier m = new PotentialMultiplier(c.getProbabilityTable(), 2, parentVarPos, parentIndexMultipliers, vars, multipliers, potentials);

        m.multiple();
        assertArray(new double[]{0.1, 0.3, 0.2, 0.4, 0.5, 0.7, 0.6, 0.8, 0.1, 0.3, 0.2, 0.4, 0.5, 0.7, 0.6, 0.8}, potentials);

        // test that it's applying variable multiplications correctly ontop of each other. This simulates the application of project variabe multiplications
        m.multiple();
        assertArray(new double[]{0.01, 0.09, 0.04, 0.16, 0.25, 0.49, 0.36, 0.64, 0.01, 0.09, 0.04, 0.16, 0.25, 0.49, 0.36, 0.64}, scaleDouble( 3, potentials ) );
    }

    @Test
    public void testJunctionTreeInitialisation() {
        // creates  JunctionTree where node1 has only B as a family memory.
        // node 2 has both c and d as family, and c is the parent of d.
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2"},  new double[][] {{0.1, 0.2}});
        BayesVariable b = new BayesVariable<String>( "B", 1, new String[] {"B1", "B2"},  new double[][] {{0.1, 0.2}});
        BayesVariable c = new BayesVariable<String>( "C", 2, new String[] {"C1", "C2"},  new double[][] {{0.1, 0.2}});
        BayesVariable d = new BayesVariable<String>( "D", 3, new String[] {"D1", "D2"},  new double[][] {{0.1, 0.2}, {0.3, 0.4}});


        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);
        GraphNode x3 = addNode(graph);

        //connectParentToChildren(x0, x2);
        connectParentToChildren(x2, x3);

        x0.setContent( a );
        x1.setContent( b );
        x2.setContent( c );
        x3.setContent( d );


        JunctionTreeClique node1 = new JunctionTreeClique(0, graph, bitSet("0011") );
        JunctionTreeClique node2 = new JunctionTreeClique(1, graph, bitSet("1100")  );
        new JunctionTreeSeparator(0, node1, node2, new OpenBitSet(), graph);

        node1.addToFamily( b );
        b.setFamily( node1.getId() );

        node2.addToFamily( c );
        c.setFamily( node2.getId() );

        node2.addToFamily( d );
        d.setFamily( node2.getId() );

        JunctionTree jtree = new JunctionTree(graph, node1, new JunctionTreeClique[] { node1, node2 }, null );

        assertArray(new double[]{0.1, 0.2, 0.1, 0.2}, scaleDouble( 3, node1.getPotentials() ));
        assertArray(new double[]{0.01, 0.02, 0.06, 0.08}, scaleDouble( 3, node2.getPotentials() ));
    }

    public static void assertArray(double[] expected, double[] actual) {
        if ( !Arrays.equals(expected, actual) ) {
            System.err.print( "expected " );
            for ( int i = 0; i <expected.length; i++ ) {
                System.err.format("%.7f ", expected[i]);
            }
            System.err.println("");
            System.err.print( "actual " );
            for ( int i = 0; i <actual.length; i++ ) {
                System.err.format("%.7f ", actual[i]);
            }
            System.err.println("");
            throw new AssertionFailedError("Arrays are not Equal");
        }
    }


    public  static void assertIndexToKeyMapping(int numberOfStates, int[] indexMultipliers) {
        for (int i = 0; i < numberOfStates; i++) {
            int[] key = indexToKey(i, indexMultipliers);
            int index = keyToIndex(key, indexMultipliers);
            assertEquals(i, index);
        }
    }

    public static double scaleDouble(int scale, double d) {
        return new BigDecimal(d).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double[] scaleDouble(int scale, double[] array) {
        for ( int i = 0; i < array.length; i++ ) {
            array[i] = scaleDouble(scale, array[i]);
        }

        return array;
    }
}
