package org.optaplanner.spring.boot.autoconfigure.normal.constraints;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.spring.boot.autoconfigure.normal.domain.TestdataSpringEntity;

public class TestdataSpringConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                factory.forEach(TestdataSpringEntity.class)
                        .join(TestdataSpringEntity.class, Joiners.equal(TestdataSpringEntity::getValue))
                        .filter((a, b) -> a != b)
                        .penalize("Don't assign 2 entities the same value.", SimpleScore.ONE)
        };
    }

}
