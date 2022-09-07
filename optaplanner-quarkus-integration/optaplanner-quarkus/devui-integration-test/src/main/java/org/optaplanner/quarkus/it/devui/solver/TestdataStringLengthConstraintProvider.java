package org.optaplanner.quarkus.it.devui.solver;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.quarkus.it.devui.domain.TestdataStringLengthShadowEntity;

public class TestdataStringLengthConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                factory.forEach(TestdataStringLengthShadowEntity.class)
                        .join(TestdataStringLengthShadowEntity.class, Joiners.equal(TestdataStringLengthShadowEntity::getValue))
                        .filter((a, b) -> a != b)
                        .penalize(HardSoftScore.ONE_HARD)
                        .asConstraint("Don't assign 2 entities the same value."),
                factory.forEach(TestdataStringLengthShadowEntity.class)
                        .reward(HardSoftScore.ONE_SOFT,
                                TestdataStringLengthShadowEntity::getLength)
                        .asConstraint("Maximize value length")
        };
    }

}
