package org.optaplanner.quarkus.testdata.invalid.inverserelation.constraints;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.quarkus.testdata.invalid.inverserelation.domain.TestdataInvalidInverseRelationValue;

public class TestdataInvalidQuarkusConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                factory.forEach(TestdataInvalidInverseRelationValue.class)
                        .filter(room -> room.getEntityList().size() > 1)
                        .penalize("Don't assign 2 entities the same room.", SimpleScore.ONE)
        };
    }

}
