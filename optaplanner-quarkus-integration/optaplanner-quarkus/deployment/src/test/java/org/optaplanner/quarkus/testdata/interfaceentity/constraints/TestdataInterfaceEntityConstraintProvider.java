package org.optaplanner.quarkus.testdata.interfaceentity.constraints;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.quarkus.testdata.interfaceentity.domain.TestdataInterfaceEntity;

public class TestdataInterfaceEntityConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                constraintFactory.forEach(TestdataInterfaceEntity.class)
                        .penalize("Minimize value", SimpleScore.ONE, TestdataInterfaceEntity::getValue)
        };
    }
}
