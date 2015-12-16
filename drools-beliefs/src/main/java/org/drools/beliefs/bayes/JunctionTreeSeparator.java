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
import org.drools.core.util.bitmask.OpenBitSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JunctionTreeSeparator {
    private int                 id;
    private OpenBitSet bitSet;
    private List<BayesVariable> values;
    private JunctionTreeClique  parent;
    private JunctionTreeClique  child;
    //private double[]            potentials;


    public JunctionTreeSeparator(int id, JunctionTreeClique parent, JunctionTreeClique child, OpenBitSet bitSet, Graph<BayesVariable> graph) {
        this.id = id;
        this.bitSet = bitSet;
        this.parent = parent;
        this.child = child;

        child.setParentSeparator(this);
        parent.getChildren().add( this );

        values = new ArrayList<BayesVariable>((int) bitSet.cardinality());
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            values.add(graph.getNode(i).getContent());
        }
    }

    public OpenBitSet getBitSet() {
        return bitSet;
    }

    public JunctionTreeClique getParent() {
        return parent;
    }

    public JunctionTreeClique getChild() {
        return child;
    }

    public int getId() {
        return id;
    }

    public SeparatorState createState() {
        int numberOfStates = PotentialMultiplier.createNumberOfStates(values);
        double[]  potentials = new double[numberOfStates];
        Arrays.fill(potentials, 1);
        return new SeparatorState(this, potentials);
    }

    public void resetState(SeparatorState sepSet) {
        Arrays.fill(sepSet.getPotentials(), 1);
    }

    public List<BayesVariable> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "JunctionTreeSeparator{" +
               "id=" + id +
               ", bitSet=" + bitSet +
               ", parent=" + parent.getId() +
               ", child=" + child.getId() +
               '}';
    }
}
