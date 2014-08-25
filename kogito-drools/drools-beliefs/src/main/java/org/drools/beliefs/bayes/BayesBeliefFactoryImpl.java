package org.drools.beliefs.bayes;

public class BayesBeliefFactoryImpl implements BayesBeliefFactory<BayesHardEvidence> {
    private BayesBeliefSystem beliefSystem;

    public BayesBeliefFactoryImpl(BayesBeliefSystem beliefSystem) {
        this.beliefSystem = beliefSystem;
    }

    @Override
    public BayesHardEvidence create(double[] distribution) {
        return new BayesHardEvidence(beliefSystem, distribution);
    }

}
