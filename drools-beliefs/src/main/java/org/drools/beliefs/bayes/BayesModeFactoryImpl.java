package org.drools.beliefs.bayes;

import org.kie.internal.runtime.beliefs.Mode;

public class BayesModeFactoryImpl implements BayesModeFactory<BayesHardEvidence> {
    private BayesBeliefSystem beliefSystem;

    public BayesModeFactoryImpl(BayesBeliefSystem beliefSystem) {
        this.beliefSystem = beliefSystem;
    }

    @Override
    public BayesHardEvidence create(double[] distribution) {
        return new BayesHardEvidence(beliefSystem, distribution);
    }

    @Override
    public BayesHardEvidence create(double[] distribution, Mode mode) {
        return new BayesHardEvidence(beliefSystem, distribution, mode);
    }

}
