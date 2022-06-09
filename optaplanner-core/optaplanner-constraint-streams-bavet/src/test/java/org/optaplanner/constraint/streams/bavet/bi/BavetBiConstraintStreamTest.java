package org.optaplanner.constraint.streams.bavet.bi;

import org.optaplanner.constraint.streams.bavet.BavetConstraintStreamImplSupport;
import org.optaplanner.constraint.streams.common.bi.AbstractBiConstraintStreamTest;

final class BavetBiConstraintStreamTest extends AbstractBiConstraintStreamTest {

    public BavetBiConstraintStreamTest(boolean constraintMatchEnabled) {
        super(new BavetConstraintStreamImplSupport(constraintMatchEnabled));
    }

}
