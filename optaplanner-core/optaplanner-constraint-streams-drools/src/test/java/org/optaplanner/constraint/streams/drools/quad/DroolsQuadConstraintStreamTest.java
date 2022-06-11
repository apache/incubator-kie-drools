package org.optaplanner.constraint.streams.drools.quad;

import org.optaplanner.constraint.streams.common.quad.AbstractQuadConstraintStreamTest;
import org.optaplanner.constraint.streams.drools.DroolsConstraintStreamImplSupport;

final class DroolsQuadConstraintStreamTest extends AbstractQuadConstraintStreamTest {

    public DroolsQuadConstraintStreamTest(boolean constraintMatchEnabled) {
        super(new DroolsConstraintStreamImplSupport(constraintMatchEnabled));
    }

}
