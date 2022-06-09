package org.optaplanner.quarkus.testdata.gizmo;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class PrivateNoArgsConstructorConstraintProvider implements ConstraintProvider {

    private PrivateNoArgsConstructorConstraintProvider() {
    }

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                constraintFactory.forEachUniquePair(PrivateNoArgsConstructorEntity.class,
                        Joiners.equal(p -> p.value))
                        .penalize("Same value", SimpleScore.ONE)
        };
    }
}
