package org.optaplanner.constraint.streams.bavet;

import org.optaplanner.constraint.streams.common.AbstractFactChangePropagationTest;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;

final class BavetFactChangePropagationTest extends AbstractFactChangePropagationTest {

    public BavetFactChangePropagationTest() {
        super(ConstraintStreamImplType.BAVET);
    }
}
