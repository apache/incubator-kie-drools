package org.optaplanner.constraint.streams.drools;

import org.optaplanner.constraint.streams.common.AbstractScoreManagerTest;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;

final class DroolsScoreManagerTest extends AbstractScoreManagerTest {

    protected DroolsScoreManagerTest() {
        super(ConstraintStreamImplType.DROOLS);
    }

}
