package org.optaplanner.test.api.score.stream.testdata;

import java.util.Objects;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public final class TestdataConstraintVerifierConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                penalizeEveryEntity(constraintFactory),
                rewardEveryEntity(constraintFactory),
                impactEveryEntity(constraintFactory),
                differentStringEntityHaveDifferentValues(constraintFactory),
        };
    }

    public Constraint penalizeEveryEntity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TestdataConstraintVerifierFirstEntity.class)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Penalize every standard entity");
    }

    public Constraint rewardEveryEntity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TestdataConstraintVerifierFirstEntity.class)
                .reward(HardSoftScore.ofSoft(2))
                .asConstraint("Reward every standard entity");
    }

    public Constraint impactEveryEntity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TestdataConstraintVerifierFirstEntity.class)
                .impact(HardSoftScore.ofHard(4),
                        entity -> Objects.equals(entity.getCode(), "A") ? 1 : -1)
                .asConstraint("Impact every standard entity");
    }

    public Constraint differentStringEntityHaveDifferentValues(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(TestdataConstraintVerifierSecondEntity.class,
                Joiners.equal(TestdataConstraintVerifierSecondEntity::getValue))
                .penalize(HardSoftScore.ofSoft(3))
                .asConstraint("Different String Entity Have Different Values");
    }

}
