package org.optaplanner.core.impl.testdata.domain;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public final class TestdataConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] { alwaysPenalizingConstraint(constraintFactory) };
    }

    private Constraint alwaysPenalizingConstraint(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TestdataEntity.class)
                .penalize("Always penalize", SimpleScore.ONE);
    }

}
