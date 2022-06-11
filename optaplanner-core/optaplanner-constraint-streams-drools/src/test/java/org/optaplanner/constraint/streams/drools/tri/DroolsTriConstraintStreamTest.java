package org.optaplanner.constraint.streams.drools.tri;

import org.optaplanner.constraint.streams.common.tri.AbstractTriConstraintStreamTest;
import org.optaplanner.constraint.streams.drools.DroolsConstraintStreamImplSupport;

final class DroolsTriConstraintStreamTest extends AbstractTriConstraintStreamTest {

    public DroolsTriConstraintStreamTest(boolean constraintMatchEnabled) {
        super(new DroolsConstraintStreamImplSupport(constraintMatchEnabled));
    }

}
