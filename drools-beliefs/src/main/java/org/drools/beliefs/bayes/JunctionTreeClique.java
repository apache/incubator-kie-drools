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

public class JunctionTreeClique {
    private int                         id;
    private OpenBitSet bitSet;
    private List<BayesVariable>         values;
    private JunctionTreeSeparator       parentSeparator;
    private List<JunctionTreeSeparator> children;

    private double[] potentials;
    private List<BayesVariable> family;

    public JunctionTreeClique(int id, Graph<BayesVariable> graph, OpenBitSet bitSet) {
        this.id = id;
        this.bitSet = bitSet;
        this.children = new ArrayList<JunctionTreeSeparator>();

        values = new ArrayList<BayesVariable>((int) bitSet.cardinality());
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit( i + 1 ) ) {
            values.add(graph.getNode(i).getContent());
        }

        int numberOfStates = PotentialMultiplier.createNumberOfStates(values);
        potentials = new double[numberOfStates];
        Arrays.fill(potentials, 1);
        family = new ArrayList();
    }

    public void addToFamily(BayesVariable var) {
        family.add(var);
    }

    public int getId() {
        return id;
    }

    public OpenBitSet getBitSet() {
        return bitSet;
    }

    public List<BayesVariable> getValues() {
        return values;
    }

    public List<BayesVariable> getFamily() {
        return family;
    }

    public double[] getPotentials() {
        return potentials;
    }

    public JunctionTreeSeparator getParentSeparator() {
        return parentSeparator;
    }

    public void setParentSeparator(JunctionTreeSeparator parentSeparator) {
        this.parentSeparator = parentSeparator;
    }

    public List<JunctionTreeSeparator> getChildren() {
        return children;
    }

    public CliqueState createState() {
        return new CliqueState(this, Arrays.copyOf(potentials, potentials.length));
    }

    public void resetState(CliqueState cliqueState) {
        cliqueState.setPotentials( Arrays.copyOf(potentials, potentials.length) );
    }

    @Override
    public String toString() {
        StringBuilder seps = new StringBuilder();
        for ( JunctionTreeSeparator sep : children) {
            seps.append( "children[node1.id=" );
            seps.append(sep.getParent().getId());
            seps.append( ", node2.id=" );
            seps.append(sep.getChild().getId());
            seps.append( ", bitSet=" );
            seps.append(sep.getBitSet());
        }

        return "JunctionTreeNode{" +
               "id=" + id +
               ", bitSet=" + bitSet +
               ", values=" + values +
               ", children=" + seps +
               '}';
    }


}
