package org.drools.beliefs.bayes;

import org.drools.core.beliefsystem.BeliefSystem;
import org.kie.internal.runtime.beliefs.Belief;

import java.util.Arrays;

public class BayesHardEvidence implements Belief {
    private double[] distribution;
    private BeliefSystem beliefSystem;

    public BayesHardEvidence(BeliefSystem beliefSystem,
                             double[] distribution) {
        this.beliefSystem = beliefSystem;
        this.distribution = distribution;
    }

    public double[] getDistribution() {
        return distribution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        BayesHardEvidence that = (BayesHardEvidence) o;

        if (!Arrays.equals(distribution, that.distribution)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(distribution);
    }

    @Override
    public Object getBeliefSystem() {
        return beliefSystem;
    }
}
