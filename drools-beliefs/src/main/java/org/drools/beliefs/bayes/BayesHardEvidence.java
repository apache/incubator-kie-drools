package org.drools.beliefs.bayes;

import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.common.LogicalDependency;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.Arrays;

public class BayesHardEvidence<M extends BayesHardEvidence<M>> extends AbstractBaseLinkedListNode<M> implements ModedAssertion<M> {
    private double[] distribution;
    private BeliefSystem<M> beliefSystem;
    private LogicalDependency<M> dep;
    private Mode nextMode;

    public BayesHardEvidence(BeliefSystem<M> beliefSystem,
                             double[] distribution) {
        this.beliefSystem = beliefSystem;
        this.distribution = distribution;
    }

    public BayesHardEvidence(BeliefSystem<M> beliefSystem,
                             double[] distribution,
                             Mode nextMode) {
        this.beliefSystem = beliefSystem;
        this.distribution = distribution;
        this.nextMode = nextMode;
    }

    public LogicalDependency<M> getLogicalDependency() {
        return dep;
    }

    public void setLogicalDependency(LogicalDependency<M> dep) {
        this.dep = dep;
    }

    public double[] getDistribution() {
        return distribution;
    }

    @Override
    public Mode getNextMode() {
        return nextMode;
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
