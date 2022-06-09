package org.optaplanner.spring.boot.autoconfigure.gizmo.constraints;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.spring.boot.autoconfigure.gizmo.domain.TestdataGizmoSpringEntity;

public class TestdataGizmoConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                factory.forEach(TestdataGizmoSpringEntity.class)
                        .join(TestdataGizmoSpringEntity.class, Joiners.equal(TestdataGizmoSpringEntity::getValue))
                        .filter((a, b) -> a != b)
                        .penalize("Don't assign 2 entities the same value.", SimpleScore.ONE)
        };
    }

}
