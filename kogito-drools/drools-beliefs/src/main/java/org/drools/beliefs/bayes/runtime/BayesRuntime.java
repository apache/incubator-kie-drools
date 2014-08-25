package org.drools.beliefs.bayes.runtime;

import org.drools.beliefs.bayes.BayesFact;
import org.drools.beliefs.bayes.BayesInstance;

public interface BayesRuntime {
    BayesInstance createInstance(Class cls);
}
