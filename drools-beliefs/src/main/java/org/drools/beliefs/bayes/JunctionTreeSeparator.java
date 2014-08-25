package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JunctionTreeSeparator {
    private int                 id;
    private OpenBitSet          bitSet;
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
