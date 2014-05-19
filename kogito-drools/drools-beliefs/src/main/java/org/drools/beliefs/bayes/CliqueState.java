package org.drools.beliefs.bayes;

public class CliqueState {
    private JunctionTreeClique jtNode;
    private double[]           potentials;

    public CliqueState(JunctionTreeClique jtNode, double[] potentials) {
        this.jtNode = jtNode;
        this.potentials = potentials;
    }

    public JunctionTreeClique getJunctionTreeClique() {
        return jtNode;
    }

    public double[] getPotentials() {
        return potentials;
    }
}
