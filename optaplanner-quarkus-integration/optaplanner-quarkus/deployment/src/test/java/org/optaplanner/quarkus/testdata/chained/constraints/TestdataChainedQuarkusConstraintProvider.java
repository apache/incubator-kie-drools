package org.optaplanner.quarkus.testdata.chained.constraints;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.quarkus.testdata.chained.domain.TestdataChainedQuarkusAnchor;
import org.optaplanner.quarkus.testdata.chained.domain.TestdataChainedQuarkusEntity;

public class TestdataChainedQuarkusConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                factory.forEach(TestdataChainedQuarkusAnchor.class)
                        .ifNotExists(TestdataChainedQuarkusEntity.class,
                                Joiners.equal((anchor) -> anchor, TestdataChainedQuarkusEntity::getPrevious))
                        .penalize("Assign at least one entity to each anchor.", SimpleScore.ONE)
        };
    }

}
