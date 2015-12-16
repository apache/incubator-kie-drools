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
import org.junit.Test;

import static org.drools.beliefs.bayes.GraphTest.addNode;
import static org.drools.beliefs.bayes.JunctionTreeTest.scaleDouble;

public class MarginalizerTest {

    @Test
    public void test1() {
        BayesVariable a = new BayesVariable<String>( "A", 0, new String[] {"A1", "A2", "A3"}, null);
        BayesVariable b = new BayesVariable<String>( "B", 1, new String[] {"B1", "B2", "B3"}, null);
        BayesVariable c = new BayesVariable<String>( "C", 2, new String[] {"C1", "C2", "C3"}, null);

        Graph<BayesVariable> graph = new BayesNetwork();
        GraphNode x0 = addNode(graph);
        GraphNode x1 = addNode(graph);
        GraphNode x2 = addNode(graph);

        x0.setContent( a );
        x1.setContent( b );
        x2.setContent( c );


        BayesVariable[] srcVars = new BayesVariable[] { a, b, c, };

        int varNumberOfStates = PotentialMultiplier.createNumberOfStates(srcVars);
        System.out.println( varNumberOfStates );

        double[] srcDistribution = new double[varNumberOfStates];
        double x = 0.05;
        for ( int i = 0; i < varNumberOfStates; i++ ) {
            srcDistribution[i] = x;
            x = x + 0.05;
        }
        srcDistribution = scaleDouble(3, srcDistribution);

        for ( int i = 0; i <srcDistribution.length; i++ ) {
            System.out.print(srcDistribution[i] + " ");
        }
        System.out.println("");

//        public void Marginalizer(BayesVariable[]  srcVars, double[] srcPotentials, BayesVariable var,
//        double[] varDistribution,  BayesVariable[]  trgVars) {
        double[] trgDistribution = new double[ b.getOutcomes().length ];

        Marginalizer marginalizer = new Marginalizer(srcVars, srcDistribution, b, trgDistribution);

        for ( int i = 0; i <trgDistribution.length; i++ ) {
            System.out.print(trgDistribution[i] + " ");
        }
        System.out.println("");
    }
}
