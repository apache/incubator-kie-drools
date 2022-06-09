package org.optaplanner.examples.coachshuttlegathering.domain.solver;

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.coachshuttlegathering.domain.BusHub;
import org.optaplanner.examples.coachshuttlegathering.domain.BusOrStop;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;

/**
 * On large datasets, the constructed solution looks like pizza slices.
 */
public class DepotAngleBusStopDifficultyWeightFactory
        implements SelectionSorterWeightFactory<CoachShuttleGatheringSolution, BusOrStop> {

    @Override
    public DepotAngleBusStopDifficultyWeight createSorterWeight(CoachShuttleGatheringSolution solution, BusOrStop busOrStop) {
        BusHub hub = solution.getHub();
        return new DepotAngleBusStopDifficultyWeight(busOrStop,
                busOrStop.getLocation().getAngle(hub.getLocation()),
                busOrStop.getLocation().getMaximumDistanceTo(hub.getLocation())
                        + hub.getLocation().getMaximumDistanceTo(busOrStop.getLocation()));
    }

    public static class DepotAngleBusStopDifficultyWeight implements Comparable<DepotAngleBusStopDifficultyWeight> {

        private static final Comparator<DepotAngleBusStopDifficultyWeight> COMPARATOR = Comparator
                .comparingDouble((DepotAngleBusStopDifficultyWeight w) -> w.hubAngle)
                .thenComparingInt(w -> w.hubRoundTripDistance) // Further from the depot are more difficult.
                .thenComparingLong(w -> w.busOrStop.getId());

        private final BusOrStop busOrStop;
        private final double hubAngle;
        private final int hubRoundTripDistance;

        public DepotAngleBusStopDifficultyWeight(BusOrStop busOrStop, double hubAngle, int hubRoundTripDistance) {
            this.busOrStop = busOrStop;
            this.hubAngle = hubAngle;
            this.hubRoundTripDistance = hubRoundTripDistance;
        }

        @Override
        public int compareTo(DepotAngleBusStopDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
