package org.drools.beliefs.bayes;

public interface BayesBeliefFactory<T> {
    T create(double[] distribution);
}
