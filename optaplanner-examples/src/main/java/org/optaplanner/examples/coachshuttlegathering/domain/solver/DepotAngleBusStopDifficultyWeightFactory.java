/*
 * Copyright 2015 JBoss Inc
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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.coachshuttlegathering.domain.BusHub;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;

/**
 * On large datasets, the constructed solution looks like pizza slices.
 */
public class DepotAngleBusStopDifficultyWeightFactory
        implements SelectionSorterWeightFactory<CoachShuttleGatheringSolution, BusStop> {

    public Comparable createSorterWeight(CoachShuttleGatheringSolution solution, BusStop stop) {
        BusHub hub = solution.getHub();
        return new DepotAngleCustomerDifficultyWeight(stop,
                stop.getLocation().getAngle(hub.getLocation()),
                stop.getLocation().getMaximumDistanceTo(hub.getLocation())
                        + hub.getLocation().getMaximumDistanceTo(stop.getLocation()));
    }

    public static class DepotAngleCustomerDifficultyWeight
            implements Comparable<DepotAngleCustomerDifficultyWeight> {

        private final BusStop stop;
        private final double hubAngle;
        private final int hubRoundTripDistance;

        public DepotAngleCustomerDifficultyWeight(BusStop stop, double hubAngle, int hubRoundTripDistance) {
            this.stop = stop;
            this.hubAngle = hubAngle;
            this.hubRoundTripDistance = hubRoundTripDistance;
        }

        public int compareTo(DepotAngleCustomerDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(hubAngle, other.hubAngle)
                    .append(hubRoundTripDistance, other.hubRoundTripDistance) // Ascending (further from the depot are more difficult)
                    .append(stop.getId(), other.stop.getId())
                    .toComparison();
        }

    }

}
