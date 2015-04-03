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
import org.optaplanner.examples.coachshuttlegathering.domain.BusVisit;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;

/**
 * On large datasets, the constructed solution looks like pizza slices.
 */
public class DepotAngleBusVisitDifficultyWeightFactory
        implements SelectionSorterWeightFactory<CoachShuttleGatheringSolution, BusVisit> {

    public Comparable createSorterWeight(CoachShuttleGatheringSolution solution, BusVisit visit) {
        BusHub hub = solution.getHub();
        return new DepotAngleCustomerDifficultyWeight(visit,
                visit.getLocation().getAngle(hub.getLocation()),
                visit.getLocation().getMaximumDistanceTo(hub.getLocation())
                        + hub.getLocation().getMaximumDistanceTo(visit.getLocation()));
    }

    public static class DepotAngleCustomerDifficultyWeight
            implements Comparable<DepotAngleCustomerDifficultyWeight> {

        private final BusVisit visit;
        private final double hubAngle;
        private final int hubRoundTripDistance;

        public DepotAngleCustomerDifficultyWeight(BusVisit visit,
                double hubAngle, int hubRoundTripDistance) {
            this.visit = visit;
            this.hubAngle = hubAngle;
            this.hubRoundTripDistance = hubRoundTripDistance;
        }

        public int compareTo(DepotAngleCustomerDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(hubAngle, other.hubAngle)
                    .append(hubRoundTripDistance, other.hubRoundTripDistance) // Ascending (further from the depot are more difficult)
                    .append(visit.getId(), other.visit.getId())
                    .toComparison();
        }

    }

}
