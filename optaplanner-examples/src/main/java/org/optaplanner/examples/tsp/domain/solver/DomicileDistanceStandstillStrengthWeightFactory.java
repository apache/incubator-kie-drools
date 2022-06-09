package org.optaplanner.examples.tsp.domain.solver;

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;

public class DomicileDistanceStandstillStrengthWeightFactory implements SelectionSorterWeightFactory<TspSolution, Standstill> {

    @Override
    public DomicileDistanceStandstillStrengthWeight createSorterWeight(TspSolution tspSolution, Standstill standstill) {
        Domicile domicile = tspSolution.getDomicile();
        long domicileRoundTripDistance = domicile.getDistanceTo(standstill) + standstill.getDistanceTo(domicile);
        return new DomicileDistanceStandstillStrengthWeight(standstill, domicileRoundTripDistance);
    }

    public static class DomicileDistanceStandstillStrengthWeight
            implements Comparable<DomicileDistanceStandstillStrengthWeight> {

        private static final Comparator<DomicileDistanceStandstillStrengthWeight> COMPARATOR =
                // Decreasing: closer to depot is stronger
                Comparator.comparingLong((DomicileDistanceStandstillStrengthWeight weight) -> -weight.domicileRoundTripDistance)
                        .thenComparingDouble(weight -> weight.standstill.getLocation().getLatitude())
                        .thenComparingDouble(weight -> weight.standstill.getLocation().getLongitude());

        private final Standstill standstill;
        private final long domicileRoundTripDistance;

        public DomicileDistanceStandstillStrengthWeight(Standstill standstill, long domicileRoundTripDistance) {
            this.standstill = standstill;
            this.domicileRoundTripDistance = domicileRoundTripDistance;
        }

        @Override
        public int compareTo(DomicileDistanceStandstillStrengthWeight other) {
            return COMPARATOR.compare(this, other);
        }

    }

}
