package org.drools.beliefs.bayes;

public class SeparatorState {
    private JunctionTreeSeparator jtSeparator;
    private double[]              potentials;

    public SeparatorState(JunctionTreeSeparator jtSeparator, double[] potentials) {
        this.jtSeparator = jtSeparator;
        this.potentials = potentials;
    }

    public JunctionTreeSeparator getJunctionTreeSeparator() {
        return jtSeparator;
    }

    public double[] getPotentials() {
        return potentials;
    }

}
