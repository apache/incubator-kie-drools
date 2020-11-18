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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;

public class CoachShuttleGatheringEasyScoreCalculator
        implements EasyScoreCalculator<CoachShuttleGatheringSolution, HardSoftLongScore> {

    @Override
    public HardSoftLongScore calculateScore(CoachShuttleGatheringSolution solution) {
        long hardScore = 0L;
        long softScore = 0L;
        List<Bus> busList = solution.getBusList();
        Map<Bus, Integer> busToPassengerTotalMap = new LinkedHashMap<>(busList.size());
        Map<Coach, Integer> coachToStopCountMap = new LinkedHashMap<>(busList.size());
        for (BusStop stop : solution.getStopList()) {
            Bus bus = stop.getBus();
            if (bus != null) {
                // Constraint shuttleCapacity and coachCapacity
                Integer passengerTotal = busToPassengerTotalMap.get(bus);
                if (passengerTotal == null) {
                    passengerTotal = 0;
                }
                passengerTotal += stop.getPassengerQuantity();
                busToPassengerTotalMap.put(bus, passengerTotal);
                if (bus instanceof Shuttle) {
                    Shuttle shuttle = (Shuttle) bus;
                    StopOrHub destination = shuttle.getDestination();
                    if (destination instanceof BusStop) {
                        Bus destinationBus = ((BusStop) destination).getBus();
                        if (destinationBus != null && destinationBus instanceof Coach) {
                            Integer destinationPassengerTotal = busToPassengerTotalMap.get(destinationBus);
                            if (destinationPassengerTotal == null) {
                                destinationPassengerTotal = 0;
                            }
                            destinationPassengerTotal += stop.getPassengerQuantity();
                            busToPassengerTotalMap.put(destinationBus, destinationPassengerTotal);
                        }
                    }
                }
                // Constraint coachStopLimit
                if (bus instanceof Coach) {
                    Coach coach = (Coach) bus;
                    Integer stopCount = coachToStopCountMap.get(coach);
                    stopCount = (stopCount == null) ? 1 : stopCount + 1;
                    coachToStopCountMap.put(coach, stopCount);
                }
            }
            // Constraint transportTime
            Integer transportTimeRemainder = stop.getTransportTimeRemainder();
            if (transportTimeRemainder != null && transportTimeRemainder < 0) {
                hardScore += transportTimeRemainder;
            }
            // Constraint distanceFromPrevious
            if (stop.getPreviousBusOrStop() != null) {
                softScore -= stop.getDistanceFromPreviousCost();
            }
            // Constraint distanceBusStopToBusDestination
            if (stop.getNextStop() == null && bus != null && bus.getDestination() != null) {
                softScore -= stop.getDistanceToDestinationCost(bus.getDestination());
            }
        }
        for (Bus bus : busList) {
            // Constraint shuttleCapacity and coachCapacity
            Integer passengerTotal = busToPassengerTotalMap.get(bus);
            if (passengerTotal != null && passengerTotal > bus.getCapacity()) {
                hardScore += (bus.getCapacity() - passengerTotal) * 1000L;
            }
            if (bus instanceof Coach) {
                // Constraint coachStopLimit
                Coach coach = (Coach) bus;
                Integer stopCount = coachToStopCountMap.get(coach);
                if (stopCount != null && stopCount > coach.getStopLimit()) {
                    hardScore += (coach.getStopLimit() - stopCount) * 1000000L;
                }
                // Constraint distanceCoachDirectlyToDestination
                if (coach.getNextStop() == null) {
                    softScore -= coach.getDistanceToDestinationCost();
                }
            }
            if (bus instanceof Shuttle) {
                // Constraint shuttleDestinationIsCoachOrHub
                Shuttle shuttle = (Shuttle) bus;
                StopOrHub destination = shuttle.getDestination();
                if (destination instanceof BusStop) {
                    Bus destinationBus = ((BusStop) destination).getBus();
                    if (destinationBus instanceof Shuttle) {
                        hardScore -= 1000000000L;
                    }
                }
                // Constraint shuttleSetupCost
                if (shuttle.getNextStop() != null) {
                    softScore -= shuttle.getSetupCost();
                }
            }
        }
        return HardSoftLongScore.of(hardScore, softScore);
    }

}
