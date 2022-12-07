package org.optaplanner.examples.tsp.domain.solver;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;

/**
 * On large datasets, the constructed solution looks like pizza slices.
 */
public class DomicileAngleVisitDifficultyWeightFactory
        implements SelectionSorterWeightFactory<TspSolution, Visit> {

    @Override
    public DomicileAngleVisitDifficultyWeight createSorterWeight(TspSolution vehicleRoutingSolution, Visit visit) {
        Domicile domicile = vehicleRoutingSolution.getDomicile();
        return new DomicileAngleVisitDifficultyWeight(visit,
                visit.getLocation().getAngle(domicile.getLocation()),
                visit.getLocation().getDistanceTo(domicile.getLocation())
                        + domicile.getLocation().getDistanceTo(visit.getLocation()));
    }

    public static class DomicileAngleVisitDifficultyWeight
            implements Comparable<DomicileAngleVisitDifficultyWeight> {

        private static final Comparator<DomicileAngleVisitDifficultyWeight> COMPARATOR = comparingDouble(
                (DomicileAngleVisitDifficultyWeight weight) -> weight.domicileAngle)
                .thenComparingLong(weight -> weight.domicileRoundTripDistance) // Ascending (further from the depot are more difficult)
                .thenComparing(weight -> weight.visit, comparingLong(Visit::getId));

        private final Visit visit;
        private final double domicileAngle;
        private final long domicileRoundTripDistance;

        public DomicileAngleVisitDifficultyWeight(Visit visit,
                double domicileAngle, long domicileRoundTripDistance) {
            this.visit = visit;
            this.domicileAngle = domicileAngle;
            this.domicileRoundTripDistance = domicileRoundTripDistance;
        }

        @Override
        public int compareTo(DomicileAngleVisitDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
