package org.drools.beliefs.bayes.runtime;

import org.drools.beliefs.bayes.BayesInstance;

public interface BayesRuntime {
    BayesInstance getInstance(Class cls);
}
