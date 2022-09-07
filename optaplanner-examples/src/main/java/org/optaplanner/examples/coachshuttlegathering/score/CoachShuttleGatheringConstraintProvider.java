package org.optaplanner.examples.coachshuttlegathering.score;

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

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                coachStopLimit(constraintFactory),
                shuttleCapacity(constraintFactory),
                coachCapacity(constraintFactory),
                coachCapacityShuttleButNoShuttle(constraintFactory),
                transportTime(constraintFactory),
                shuttleDestinationIsCoachOrHub(constraintFactory),
                shuttleSetupCost(constraintFactory),
                distanceFromPrevious(constraintFactory),
                distanceBusStopToBusDestination(constraintFactory),
                distanceCoachDirectlyToDestination(constraintFactory)
        };
    }

    Constraint coachStopLimit(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Coach.class)
                .join(BusStop.class, equal(coach -> coach, BusStop::getBus))
                .groupBy((coach, busStop) -> coach, countBi())
                .filter((coach, stopCount) -> stopCount > coach.getStopLimit())
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        (coach, stopCount) -> (stopCount - coach.getStopLimit()) * 1000000L)
                .asConstraint("coachStopLimit");
    }

    Constraint shuttleCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shuttle.class)
                .filter(bus -> bus.getPassengerQuantityTotal() > bus.getCapacity())
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        bus -> (bus.getPassengerQuantityTotal() - bus.getCapacity()) * 1000L)
                .asConstraint("shuttleCapacity");
    }

    Constraint coachCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Coach.class)
                .join(Shuttle.class)
                .join(BusStop.class, equal((coach, shuttle) -> shuttle.getDestination(), stop -> stop),
                        equal((coach, shuttle) -> coach, BusStop::getBus))
                .join(BusStop.class, equal((coach, shuttle, stop) -> shuttle, BusStop::getBus))
                .groupBy((coach, shuttle, stop1, stop2) -> coach,
                        sum((coach, shuttle, stop1, stop2) -> stop2.getPassengerQuantity()))
                .filter((coach,
                        shuttlePassengerQuantityTotal) -> coach.getPassengerQuantityTotal()
                                + shuttlePassengerQuantityTotal > coach.getCapacity())
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        (coach, shuttlePassengerQuantityTotal) -> {
                            int totalPassengerCount = coach.getPassengerQuantityTotal();
                            int coachCapacity = coach.getCapacity();
                            long penalty =
                                    Math.max(0L, (totalPassengerCount + shuttlePassengerQuantityTotal - coachCapacity) * 1000L);
                            // Correct double-counting.
                            penalty -= Math.max(0L, (totalPassengerCount - coachCapacity) * 1000L);
                            return penalty;
                        })
                .asConstraint("coachCapacity");
    }

    Constraint coachCapacityShuttleButNoShuttle(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Coach.class)
                .filter(coach -> coach.getPassengerQuantityTotal() > coach.getCapacity())
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        coach -> (coach.getPassengerQuantityTotal() - coach.getCapacity()) * 1000L)
                .asConstraint("coachCapacityShuttleButNoShuttle");
    }

    Constraint transportTime(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(BusStop.class)
                .filter(busStop -> busStop.getTransportTimeToHub() != null && busStop.getTransportTimeRemainder() < 0)
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        busStop -> -busStop.getTransportTimeRemainder())
                .asConstraint("transportTime");
    }

    Constraint shuttleDestinationIsCoachOrHub(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shuttle.class)
                .filter(shuttle -> shuttle.getDestination() != null)
                .join(StopOrHub.class, equal(Shuttle::getDestination, Function.identity()))
                .filter((shuttle, stop) -> !stop.isVisitedByCoach())
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        (bus, stop) -> 1000000000L)
                .asConstraint("shuttleDestinationIsCoachOrHub");
    }

    Constraint shuttleSetupCost(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Bus.class)
                .filter(bus -> bus.getNextStop() != null)
                .penalizeLong(HardSoftLongScore.ONE_SOFT, Bus::getSetupCost)
                .asConstraint("shuttleSetupCost");
    }

    Constraint distanceFromPrevious(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(BusStop.class)
                .filter(bus -> bus.getPreviousBusOrStop() != null)
                .penalizeLong(HardSoftLongScore.ONE_SOFT, BusStop::getDistanceFromPreviousCost)
                .asConstraint("distanceFromPrevious");
    }

    Constraint distanceBusStopToBusDestination(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(BusStop.class)
                .filter(busStop -> busStop.getNextStop() == null)
                .join(Bus.class, equal(BusStop::getBus, Function.identity()))
                .filter((busStop, bus) -> bus.getDestination() != null && bus.getNextStop() != null)
                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                        (busStop, bus) -> busStop.getDistanceToDestinationCost(bus.getDestination()))
                .asConstraint("distanceBusStopToBusDestination");
    }

    Constraint distanceCoachDirectlyToDestination(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Coach.class)
                .filter(coach -> coach.getDestination() != null && coach.getNextStop() == null)
                .penalizeLong(HardSoftLongScore.ONE_SOFT, Coach::getDistanceToDestinationCost)
                .asConstraint("distanceCoachDirectlyToDestination");
    }

}
