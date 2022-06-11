package org.optaplanner.constraint.streams.drools.uni;

import org.optaplanner.constraint.streams.common.uni.AbstractUniConstraintStreamTest;
import org.optaplanner.constraint.streams.drools.DroolsConstraintStreamImplSupport;

final class DroolsUniConstraintStreamTest extends AbstractUniConstraintStreamTest {

    public DroolsUniConstraintStreamTest(boolean constraintMatchEnabled) {
        super(new DroolsConstraintStreamImplSupport(constraintMatchEnabled));
    }

}
