package org.drools.beliefs.bayes;

import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.common.LogicalDependency;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.Arrays;

public class BayesHardEvidence extends AbstractBaseLinkedListNode<BayesHardEvidence> implements Mode {
    private double[] distribution;
    private BeliefSystem beliefSystem;
    private LogicalDependency dep;

    public BayesHardEvidence(BeliefSystem beliefSystem,
                             double[] distribution) {
        this.beliefSystem = beliefSystem;
        this.distribution = distribution;
    }

    public LogicalDependency getLogicalDependency() {
        return dep;
    }

    public void setLogicalDependency(LogicalDependency dep) {
        this.dep = dep;
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
