package org.drools.beliefs.bayes;

import org.kie.internal.runtime.beliefs.Mode;

public interface BayesModeFactory<T> {
    T create(double[] distribution);

    T create(double[] distribution, Mode mode);

}
