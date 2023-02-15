package org.optaplanner.constraint.streams.drools;

import org.optaplanner.constraint.streams.common.AbstractSolutionManagerTest;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;

final class DroolsSolutionManagerTest extends AbstractSolutionManagerTest {

    protected DroolsSolutionManagerTest() {
        super(ConstraintStreamImplType.DROOLS);
    }

}
