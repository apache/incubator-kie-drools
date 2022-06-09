package org.optaplanner.spring.boot.autoconfigure.chained.constraints;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.spring.boot.autoconfigure.chained.domain.TestdataChainedSpringAnchor;
import org.optaplanner.spring.boot.autoconfigure.chained.domain.TestdataChainedSpringEntity;

public class TestdataChainedSpringConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                factory.forEach(TestdataChainedSpringAnchor.class)
                        .ifNotExists(TestdataChainedSpringEntity.class,
                                Joiners.equal((anchor) -> anchor, TestdataChainedSpringEntity::getPrevious))
                        .penalize("Assign at least one entity to each anchor.", SimpleScore.ONE)
        };
    }

}
