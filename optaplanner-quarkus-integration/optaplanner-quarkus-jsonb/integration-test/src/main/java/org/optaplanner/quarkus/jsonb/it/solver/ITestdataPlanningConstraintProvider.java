package org.optaplanner.quarkus.jsonb.it.solver;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.quarkus.jsonb.it.domain.ITestdataPlanningEntity;

public class ITestdataPlanningConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                factory.forEach(ITestdataPlanningEntity.class)
                        .join(ITestdataPlanningEntity.class, Joiners.equal(ITestdataPlanningEntity::getValue))
                        .filter((a, b) -> a != b)
                        .penalize("Don't assign 2 entities the same value.", SimpleScore.ONE)
        };
    }

}
