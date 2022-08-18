package org.optaplanner.examples.coachshuttlegathering.score;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.coachshuttlegathering.domain.BusHub;
import org.optaplanner.examples.coachshuttlegathering.domain.BusOrStop;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocationArc;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class CoachShuttleGatheringConstraintProviderTest {
    private final ConstraintVerifier<CoachShuttleGatheringConstraintProvider, CoachShuttleGatheringSolution> constraintVerifier =
            ConstraintVerifier
                    .build(new CoachShuttleGatheringConstraintProvider(), CoachShuttleGatheringSolution.class,
                            BusOrStop.class, StopOrHub.class, BusStop.class, Shuttle.class, Coach.class);

    @Test
    void coachStopLimit() {
        Coach coach = new Coach();
        coach.setStopLimit(2);
        BusStop stop1 = new BusStop(0L, coach, coach);
        BusStop stop2 = new BusStop(1L, coach, coach);
        BusStop stop3 = new BusStop(2L, coach, coach);
        BusStop stop4 = new BusStop(3L, coach, coach);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachStopLimit)
                .given(coach, stop1)
                .penalizesBy(0L);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachStopLimit)
                .given(coach, stop1, stop2)
                .penalizesBy(0L);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachStopLimit)
                .given(coach, stop1, stop2, stop3)
                .penalizesBy(1L * 1000000);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachStopLimit)
                .given(coach, stop1, stop2, stop3, stop4)
                .penalizesBy(2L * 1000000);
    }

    @Test
    void shuttleCapacity() {
        Shuttle shuttle = new Shuttle();
        BusStop destination = new BusStop();
        shuttle.setDestination(destination);

        shuttle.setCapacity(2);
        shuttle.setPassengerQuantityTotal(1);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleCapacity)
                .given(shuttle)
                .penalizesBy(0);

        shuttle.setPassengerQuantityTotal(2);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleCapacity)
                .given(shuttle)
                .penalizesBy(0);

        shuttle.setPassengerQuantityTotal(3);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleCapacity)
                .given(shuttle)
                .penalizesBy(1L * 1000);

        shuttle.setPassengerQuantityTotal(4);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleCapacity)
                .given(shuttle)
                .penalizesBy(2L * 1000);
    }

    @Test
    void coachCapacity() {
        Coach coach = new Coach();
        Shuttle shuttle = new Shuttle();
        BusStop transferStop = new BusStop(0L, shuttle, coach);
        shuttle.setDestination(transferStop);
        BusStop shuttlePickup1 = new BusStop(1L, transferStop, shuttle, 1);
        BusStop shuttlePickup2 = new BusStop(2L, transferStop, shuttle, 2);
        BusStop shuttlePickup3 = new BusStop(3L, transferStop, shuttle, 3);

        coach.setCapacity(2);
        coach.setPassengerQuantityTotal(0);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacity)
                .given(coach, shuttle, transferStop)
                .penalizesBy(0L);

        coach.setPassengerQuantityTotal(1);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacity)
                .given(coach, shuttle, transferStop, shuttlePickup1)
                .penalizesBy(0L);

        coach.setPassengerQuantityTotal(1);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacity)
                .given(coach, shuttle, transferStop, shuttlePickup1, shuttlePickup2)
                .penalizesBy(2L * 1000);

        coach.setPassengerQuantityTotal(0);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacity)
                .given(coach, shuttle, transferStop, shuttlePickup1, shuttlePickup2)
                .penalizesBy(1L * 1000);

        coach.setPassengerQuantityTotal(0);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacity)
                .given(coach, shuttle, transferStop, shuttlePickup1, shuttlePickup2, shuttlePickup3)
                .penalizesBy(4L * 1000);

        coach.setPassengerQuantityTotal(3);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacity)
                .given(coach, shuttle, transferStop, shuttlePickup1)
                .penalizesBy(1L * 1000);
    }

    @Test
    void coachCapacityShuttleButNoShuttle() {
        Coach coach = new Coach();
        BusHub destination = new BusHub();
        coach.setDestination(destination);

        coach.setCapacity(2);
        coach.setPassengerQuantityTotal(1);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacityShuttleButNoShuttle)
                .given(coach)
                .penalizesBy(0);

        coach.setPassengerQuantityTotal(2);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacityShuttleButNoShuttle)
                .given(coach)
                .penalizesBy(0);

        coach.setPassengerQuantityTotal(3);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacityShuttleButNoShuttle)
                .given(coach)
                .penalizesBy(1L * 1000);

        coach.setPassengerQuantityTotal(4);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::coachCapacityShuttleButNoShuttle)
                .given(coach)
                .penalizesBy(2L * 1000);
    }

    @Test
    void transportTime() {
        Coach bus = new Coach();
        BusStop busStop = new BusStop();
        busStop.setPreviousBusOrStop(bus);
        busStop.setBus(bus);
        busStop.setPassengerQuantity(0);
        busStop.setTransportTimeLimit(5);
        busStop.setTransportTimeToHub(null);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::transportTime)
                .given(busStop)
                .penalizesBy(0L);

        busStop.setTransportTimeToHub(10);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::transportTime)
                .given(busStop)
                .penalizesBy(0L);

        busStop.setPassengerQuantity(1);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::transportTime)
                .given(busStop)
                .penalizesBy(5L);
    }

    @Test
    void shuttleDestinationIsCoachOrHub() {
        Shuttle shuttle = new Shuttle();
        Coach coach = new Coach();
        BusStop destination = new BusStop();
        shuttle.setDestination(destination);
        destination.setPreviousBusOrStop(shuttle);
        destination.setBus(shuttle);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleDestinationIsCoachOrHub)
                .given(shuttle, destination)
                .penalizesBy(1000000000L);

        destination.setBus(coach);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleDestinationIsCoachOrHub)
                .given(shuttle, destination)
                .penalizesBy(0L);
    }

    @Test
    void shuttleSetupCost() {
        Shuttle shuttle = new Shuttle();
        Coach coach = new Coach();
        BusStop destination = new BusStop();

        coach.setNextStop(destination);
        shuttle.setNextStop(destination);

        shuttle.setSetupCost(2);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleSetupCost)
                .given(shuttle)
                .penalizesBy(2L);

        shuttle.setSetupCost(3);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleSetupCost)
                .given(shuttle)
                .penalizesBy(3L);

        shuttle.setNextStop(null);
        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleSetupCost)
                .given(shuttle)
                .penalizesBy(0L);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::shuttleSetupCost)
                .given(coach)
                .penalizesBy(0L);
    }

    @Test
    void distanceFromPrevious() {
        Coach bus = new Coach();
        BusStop busStop = new BusStop();

        RoadLocation busLocation = new RoadLocation(0, 1d, 0d);
        RoadLocation busStopLocation = new RoadLocation(0, 5d, 0d);
        RoadLocationArc distance = new RoadLocationArc();
        distance.setCoachDistance(1);
        distance.setCoachDuration(1);
        distance.setShuttleDistance(1);
        distance.setShuttleDuration(1);
        busStopLocation.setTravelDistanceMap(Collections.singletonMap(busLocation, distance));
        busLocation.setTravelDistanceMap(Collections.singletonMap(busStopLocation, distance));

        bus.setDepartureLocation(busLocation);
        busStop.setLocation(busStopLocation);
        bus.setMileageCost(1);
        busStop.setPreviousBusOrStop(bus);
        busStop.setBus(bus);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::distanceFromPrevious)
                .given(busStop)
                .penalizesBy(1L);
    }

    @Test
    void distanceBusStopToBusDestination() {
        Coach bus = new Coach();
        BusStop busStop = new BusStop();
        BusHub busHub = new BusHub();

        RoadLocation busHubLocation = new RoadLocation(0, 1d, 0d);
        RoadLocation busStopLocation = new RoadLocation(0, 5d, 0d);
        RoadLocationArc distance = new RoadLocationArc();
        distance.setCoachDistance(1);
        distance.setCoachDuration(1);
        distance.setShuttleDistance(1);
        distance.setShuttleDuration(1);
        busStopLocation.setTravelDistanceMap(Collections.singletonMap(busHubLocation, distance));
        busHubLocation.setTravelDistanceMap(Collections.singletonMap(busStopLocation, distance));

        busHub.setLocation(busHubLocation);
        bus.setDestination(busHub);
        bus.setNextStop(busStop);
        busStop.setLocation(busStopLocation);
        bus.setMileageCost(1);
        busStop.setPreviousBusOrStop(bus);
        busStop.setBus(bus);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::distanceBusStopToBusDestination)
                .given(busStop, bus)
                .penalizesBy(1L);
    }

    @Test
    void distanceCoachDirectlyToDestination() {
        Coach bus = new Coach();
        BusHub busHub = new BusHub();

        RoadLocation busHubLocation = new RoadLocation(0, 1d, 0d);
        RoadLocation busLocation = new RoadLocation(0, 5d, 0d);
        RoadLocationArc distance = new RoadLocationArc();
        distance.setCoachDistance(1);
        distance.setCoachDuration(1);
        distance.setShuttleDistance(1);
        distance.setShuttleDuration(1);
        busLocation.setTravelDistanceMap(Collections.singletonMap(busHubLocation, distance));
        busHubLocation.setTravelDistanceMap(Collections.singletonMap(busLocation, distance));

        bus.setDepartureLocation(busLocation);
        busHub.setLocation(busHubLocation);
        bus.setDestination(busHub);
        bus.setMileageCost(1);

        constraintVerifier.verifyThat(CoachShuttleGatheringConstraintProvider::distanceCoachDirectlyToDestination)
                .given(bus)
                .penalizesBy(1L);
    }
}
