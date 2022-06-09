package org.optaplanner.constraint.streams.bavet.quad;

import org.optaplanner.constraint.streams.bavet.BavetConstraintStreamImplSupport;
import org.optaplanner.constraint.streams.common.quad.AbstractQuadConstraintStreamTest;

final class BavetQuadConstraintStreamTest extends AbstractQuadConstraintStreamTest {

    public BavetQuadConstraintStreamTest(boolean constraintMatchEnabled) {
        super(new BavetConstraintStreamImplSupport(constraintMatchEnabled));
    }

}
