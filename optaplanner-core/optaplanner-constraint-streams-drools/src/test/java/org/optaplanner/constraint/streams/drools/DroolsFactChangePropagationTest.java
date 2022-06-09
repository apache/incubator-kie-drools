package org.optaplanner.constraint.streams.drools;

import org.optaplanner.constraint.streams.common.AbstractFactChangePropagationTest;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;

final class DroolsFactChangePropagationTest extends AbstractFactChangePropagationTest {

    public DroolsFactChangePropagationTest() {
        super(ConstraintStreamImplType.DROOLS);
    }
}
