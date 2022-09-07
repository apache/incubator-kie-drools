package org.optaplanner.examples.tsp.score;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Visit;

public final class TspConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                distanceToPreviousStandstill(constraintFactory),
                distanceFromLastVisitToDomicile(constraintFactory)
        };
    }

    private Constraint distanceToPreviousStandstill(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Visit.class)
                .penalizeLong(SimpleLongScore.ONE, Visit::getDistanceFromPreviousStandstill)
                .asConstraint("Distance to previous standstill");
    }

    private Constraint distanceFromLastVisitToDomicile(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Visit.class)
                .ifNotExists(Visit.class,
                        Joiners.equal(visit -> visit, Visit::getPreviousStandstill))
                .join(Domicile.class)
                .penalizeLong(SimpleLongScore.ONE,
                        Visit::getDistanceTo)
                .asConstraint("Distance from last visit to domicile");
    }

}
