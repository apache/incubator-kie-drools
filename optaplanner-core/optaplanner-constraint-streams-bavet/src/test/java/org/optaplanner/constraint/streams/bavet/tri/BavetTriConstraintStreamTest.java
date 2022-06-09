package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.BavetConstraintStreamImplSupport;
import org.optaplanner.constraint.streams.common.tri.AbstractTriConstraintStreamTest;

final class BavetTriConstraintStreamTest extends AbstractTriConstraintStreamTest {

    public BavetTriConstraintStreamTest(boolean constraintMatchEnabled) {
        super(new BavetConstraintStreamImplSupport(constraintMatchEnabled));
    }

}
