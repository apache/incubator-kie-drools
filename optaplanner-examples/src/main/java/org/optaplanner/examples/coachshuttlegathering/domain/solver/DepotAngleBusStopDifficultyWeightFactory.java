/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

        private static final Comparator<DepotAngleBusStopDifficultyWeight> COMPARATOR =
                Comparator.comparingDouble((DepotAngleBusStopDifficultyWeight w) -> w.hubAngle)
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
