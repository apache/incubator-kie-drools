package org.optaplanner.constraint.streams.bavet.uni;

import org.optaplanner.constraint.streams.bavet.BavetConstraintStreamImplSupport;
import org.optaplanner.constraint.streams.common.uni.AbstractUniConstraintStreamTest;

final class BavetUniConstraintStreamTest extends AbstractUniConstraintStreamTest {

    public BavetUniConstraintStreamTest(boolean constraintMatchEnabled) {
        super(new BavetConstraintStreamImplSupport(constraintMatchEnabled));
    }

}
