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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.coachshuttlegathering.domain.BusHub;
import org.optaplanner.examples.coachshuttlegathering.domain.BusOrStop;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;

/**
 * On large datasets, the constructed solution looks like pizza slices.
 */
public class DepotAngleBusStopDifficultyWeightFactory
        implements SelectionSorterWeightFactory<CoachShuttleGatheringSolution, BusOrStop> {

    public Comparable createSorterWeight(CoachShuttleGatheringSolution solution, BusOrStop busOrStop) {
        BusHub hub = solution.getHub();
        return new DepotAngleCustomerDifficultyWeight(busOrStop,
                busOrStop.getLocation().getAngle(hub.getLocation()),
                busOrStop.getLocation().getMaximumDistanceTo(hub.getLocation())
                        + hub.getLocation().getMaximumDistanceTo(busOrStop.getLocation()));
    }

    public static class DepotAngleCustomerDifficultyWeight
            implements Comparable<DepotAngleCustomerDifficultyWeight> {

        private final BusOrStop busOrStop;
        private final double hubAngle;
        private final int hubRoundTripDistance;

        public DepotAngleCustomerDifficultyWeight(BusOrStop busOrStop, double hubAngle, int hubRoundTripDistance) {
            this.busOrStop = busOrStop;
            this.hubAngle = hubAngle;
            this.hubRoundTripDistance = hubRoundTripDistance;
        }

        public int compareTo(DepotAngleCustomerDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(hubAngle, other.hubAngle)
                    .append(hubRoundTripDistance, other.hubRoundTripDistance) // Ascending (further from the depot are more difficult)
                    .append(busOrStop.getId(), other.busOrStop.getId())
                    .toComparison();
        }

    }

}
