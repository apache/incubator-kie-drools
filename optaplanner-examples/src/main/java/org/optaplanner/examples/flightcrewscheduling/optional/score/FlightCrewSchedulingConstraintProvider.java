package org.optaplanner.examples.flightcrewscheduling.optional.score;

import java.util.Objects;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;

public class FlightCrewSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                flightConflict(constraintFactory),
                requiredSkill(constraintFactory),
                employeeUnavailability(constraintFactory),
                transferBetweenTwoFlights(constraintFactory),
                firstAssignmentDepartingFromHome(constraintFactory),
                lastAssignmentArrivingAtHome(constraintFactory)
        };
    }

    private Constraint flightConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUniquePair(FlightAssignment.class)
                .filter((first, second) -> Objects.equals(first.getEmployee(), second.getEmployee()))
                .filter((first, second) -> second.getFlight().overlaps(first.getFlight()))
                .filter((first, second) -> second.getId() > first.getId())
                .penalize("Flight conflict", HardSoftLongScore.ofHard(10));
    }

    private Constraint requiredSkill(ConstraintFactory constraintFactory) {
        return constraintFactory.from(FlightAssignment.class)
                .filter(a -> !a.getEmployee().hasSkill(a.getRequiredSkill()))
                .penalize("Required skill", HardSoftLongScore.ofHard(100));
    }

    private Constraint employeeUnavailability(ConstraintFactory constraintFactory) {
        return constraintFactory.from(FlightAssignment.class)
                .filter(a -> !a.getEmployee().isAvailable(a.getFlight().getDepartureUTCDate()))
                .penalize("Employee unavailable", HardSoftLongScore.ofHard(10));
    }

    private Constraint transferBetweenTwoFlights(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Employee.class)
                .filter(e -> e.countInvalidConnections() > 0)
                .penalizeLong("Transfer between two flights", HardSoftLongScore.ofHard(1), Employee::countInvalidConnections);
    }

    private Constraint firstAssignmentDepartingFromHome(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Employee.class)
                .filter(e -> !e.isFirstAssignmentDepartingFromHome())
                .penalize("First assignment departing from home", HardSoftLongScore.ofSoft(1_000_000));
    }

    private Constraint lastAssignmentArrivingAtHome(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Employee.class)
                .filter(e -> !e.isLastAssignmentArrivingAtHome())
                .penalize("Last assignment arriving at home", HardSoftLongScore.ofSoft(1_000_000));
    }
}
