/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.coachshuttlegathering.optional.score;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countBi;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;
import static org.optaplanner.core.api.score.stream.Joiners.equal;

import java.util.function.Function;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;

public class CoachShuttleGatheringConstraintProvider implements ConstraintProvider {
    static final String CONSTRAINT_PACKAGE = "org.optaplanner.examples.coachshuttlegathering.solver";

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                coachStopLimit(constraintFactory),
                shuttleCapacity(constraintFactory),
                coachCapacity(constraintFactory),
                coachCapacityShuttleButNoShuttle(constraintFactory),
                coachCapacityCorrection(constraintFactory),
                transportTime(constraintFactory),
                shuttleDestinationIsCoachOrHub(constraintFactory),
                shuttleSetupCost(constraintFactory),
                distanceFromPrevious(constraintFactory),
                distanceBusStopToBusDestination(constraintFactory),
                distanceCoachDirectlyToDestination(constraintFactory)
        };
    }

    Constraint coachStopLimit(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Coach.class)
                .join(BusStop.class, equal(coach -> coach, BusStop::getBus))
                .groupBy((coach, busStop) -> coach, countBi())
                .filter((coach, stopCount) -> stopCount > coach.getStopLimit())
                .penalizeLong(CONSTRAINT_PACKAGE, "coachStopLimit", HardSoftLongScore.ONE_HARD,
                        (coach, stopCount) -> (stopCount - coach.getStopLimit()) * 1000000L);
    }

    Constraint shuttleCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Shuttle.class)
                .filter(bus -> bus.getPassengerQuantityTotal() > bus.getCapacity())
                .penalizeLong(CONSTRAINT_PACKAGE, "shuttleCapacity", HardSoftLongScore.ONE_HARD,
                        bus -> (bus.getPassengerQuantityTotal() - bus.getCapacity()) * 1000L);
    }

    Constraint coachCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Coach.class)
                .join(Shuttle.class)
                .join(BusStop.class, equal((coach, shuttle) -> shuttle.getDestination(), stop -> stop),
                        equal((coach, shuttle) -> coach, BusStop::getBus))
                .join(BusStop.class, equal((coach, shuttle, stop) -> shuttle, BusStop::getBus))
                .groupBy((coach, shuttle, stop1, stop2) -> coach,
                        sum((coach, shuttle, stop1, stop2) -> stop2.getPassengerQuantity()))
                .filter((coach,
                        shuttlePassengerQuantityTotal) -> coach.getPassengerQuantityTotal()
                                + shuttlePassengerQuantityTotal > coach.getCapacity())
                .penalizeLong(CONSTRAINT_PACKAGE, "coachCapacity", HardSoftLongScore.ONE_HARD,
                        (coach, shuttlePassengerQuantityTotal) -> (coach.getPassengerQuantityTotal()
                                + shuttlePassengerQuantityTotal - coach.getCapacity()) * 1000L);
    }

    /*
     * Correct the double counting
     * Explanation: groupBy is like accumulate, but it doesn't trigger on empty streams.
     * We need something like
     * .accumulate(Function<ConstraintStream, UniConstraintStream<T>>, T defaultValue): ConstraintStream+1
     * To change it from 3 separate constraints (one for the normal case, one in the case of empty stream,
     * one to remove double counting).
     */
    Constraint coachCapacityCorrection(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Coach.class)
                .join(Shuttle.class)
                .join(BusStop.class, equal((coach, shuttle) -> shuttle.getDestination(), stop -> stop),
                        equal((coach, shuttle) -> coach, BusStop::getBus))
                .join(BusStop.class, equal((coach, shuttle, stop) -> shuttle, BusStop::getBus))
                .groupBy((coach, shuttle, stop1, stop2) -> coach,
                        sum((coach, shuttle, stop1, stop2) -> stop2.getPassengerQuantity()))
                .filter((coach,
                        shuttlePassengerQuantityTotal) -> coach.getPassengerQuantityTotal() > coach.getCapacity())
                .rewardLong(CONSTRAINT_PACKAGE, "coachCapacityCorrection", HardSoftLongScore.ONE_HARD,
                        (coach, shuttlePassengerQuantityTotal) -> (coach.getPassengerQuantityTotal() - coach.getCapacity())
                                * 1000L);
    }

    Constraint coachCapacityShuttleButNoShuttle(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Coach.class)
                .filter(coach -> coach.getPassengerQuantityTotal() > coach.getCapacity())
                .penalizeLong(CONSTRAINT_PACKAGE, "coachCapacityShuttleButNoShuttle", HardSoftLongScore.ONE_HARD,
                        coach -> (coach.getPassengerQuantityTotal() - coach.getCapacity()) * 1000L);
    }

    Constraint transportTime(ConstraintFactory constraintFactory) {
        return constraintFactory.from(BusStop.class)
                .filter(busStop -> busStop.getTransportTimeToHub() != null && busStop.getTransportTimeRemainder() < 0)
                .penalizeLong(CONSTRAINT_PACKAGE, "transportTime", HardSoftLongScore.ONE_HARD,
                        busStop -> -busStop.getTransportTimeRemainder());
    }

    Constraint shuttleDestinationIsCoachOrHub(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Shuttle.class)
                .filter(shuttle -> shuttle.getDestination() != null)
                .join(StopOrHub.class, equal(Shuttle::getDestination, Function.identity()))
                .filter((shuttle, stop) -> !stop.isVisitedByCoach())
                .penalizeLong(CONSTRAINT_PACKAGE, "shuttleDestinationIsCoachOrHub", HardSoftLongScore.ONE_HARD,
                        (bus, stop) -> 1000000000L);
    }

    Constraint shuttleSetupCost(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Bus.class)
                .filter(bus -> bus.getNextStop() != null)
                .penalizeLong(CONSTRAINT_PACKAGE, "shuttleSetupCost", HardSoftLongScore.ONE_SOFT, Bus::getSetupCost);
    }

    Constraint distanceFromPrevious(ConstraintFactory constraintFactory) {
        return constraintFactory.from(BusStop.class)
                .filter(bus -> bus.getPreviousBusOrStop() != null)
                .penalizeLong(CONSTRAINT_PACKAGE, "distanceFromPrevious", HardSoftLongScore.ONE_SOFT,
                        BusStop::getDistanceFromPreviousCost);
    }

    Constraint distanceBusStopToBusDestination(ConstraintFactory constraintFactory) {
        return constraintFactory.from(BusStop.class)
                .filter(busStop -> busStop.getNextStop() == null)
                .join(Bus.class, equal(BusStop::getBus, Function.identity()))
                .filter((busStop, bus) -> bus.getDestination() != null && bus.getNextStop() != null)
                .penalizeLong(CONSTRAINT_PACKAGE, "distanceBusStopToBusDestination", HardSoftLongScore.ONE_SOFT,
                        (busStop, bus) -> busStop.getDistanceToDestinationCost(bus.getDestination()));
    }

    Constraint distanceCoachDirectlyToDestination(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Coach.class)
                .filter(coach -> coach.getDestination() != null && coach.getNextStop() == null)
                .penalizeLong(CONSTRAINT_PACKAGE, "distanceCoachDirectlyToDestination", HardSoftLongScore.ONE_SOFT,
                        Coach::getDistanceToDestinationCost);
    }

}
