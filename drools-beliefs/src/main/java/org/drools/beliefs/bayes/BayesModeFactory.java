package org.drools.beliefs.bayes;

import org.drools.base.beliefsystem.Mode;

public interface BayesModeFactory<T> {
    T create(double[] distribution);

    T create(double[] distribution, Mode mode);

}
