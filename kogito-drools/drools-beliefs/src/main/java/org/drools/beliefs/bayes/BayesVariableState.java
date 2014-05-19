package org.drools.beliefs.bayes;

public class BayesVariableState {
    private BayesVariable variable;
    private double[]      distribution;
    private Object[]      outcomes;

    public BayesVariableState(BayesVariable variable, double[] distribution) {
        this.variable = variable;
        this.distribution = distribution;
    }

    public BayesVariable getVariable() {
        return variable;
    }

    public double[] getDistribution() {
        return distribution;
    }

    public Object[] getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(Object[] outcomes) {
        this.outcomes = outcomes;
    }
}
