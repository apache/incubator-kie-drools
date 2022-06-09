package org.optaplanner.quarkus.it.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.quarkus.it.domain.TestdataStringLengthShadowEntity;

public class TestdataStringLengthConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                factory.forEach(TestdataStringLengthShadowEntity.class)
                        .join(TestdataStringLengthShadowEntity.class, Joiners.equal(TestdataStringLengthShadowEntity::getValue))
                        .filter((a, b) -> a != b)
                        .penalize("Don't assign 2 entities the same value.", HardSoftScore.ONE_HARD),
                factory.forEach(TestdataStringLengthShadowEntity.class)
                        .reward("Maximize value length", HardSoftScore.ONE_SOFT,
                                TestdataStringLengthShadowEntity::getLength)
        };
    }

}
