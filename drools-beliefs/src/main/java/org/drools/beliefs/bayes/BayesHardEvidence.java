package org.drools.beliefs.bayes;

import java.util.Arrays;

public class BayesHardEvidence {
    private double[] distribution;

    public BayesHardEvidence(double[] distribution) {
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
}
