package org.optaplanner.constraint.streams.drools.bi;

import org.optaplanner.constraint.streams.common.bi.AbstractBiConstraintStreamTest;
import org.optaplanner.constraint.streams.drools.DroolsConstraintStreamImplSupport;

final class DroolsBiConstraintStreamTest extends AbstractBiConstraintStreamTest {

    public DroolsBiConstraintStreamTest(boolean constraintMatchEnabled) {
        super(new DroolsConstraintStreamImplSupport(constraintMatchEnabled));
    }

}
