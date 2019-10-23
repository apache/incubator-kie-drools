package org.optaplanner.examples.flightcrewscheduling.optional.score;

import java.time.LocalDate;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;
import org.optaplanner.examples.flightcrewscheduling.domain.Skill;

public class FlightCrewSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                requiredSkill(constraintFactory),
                flightConflict(constraintFactory),
                transferBetweenTwoFlights(constraintFactory),
                employeeUnavailability(constraintFactory),
                firstAssignmentDepartingFromHome(constraintFactory),
                lastAssignmentArrivingAtHome(constraintFactory)
        };
    }

    private Constraint requiredSkill(ConstraintFactory constraintFactory) {
        return constraintFactory.from(FlightAssignment.class)
                .filter(flightAssignment -> {
                    Skill requiredSkill = flightAssignment.getRequiredSkill();
                    return !flightAssignment.getEmployee().hasSkill(requiredSkill);
                })
                .penalize("Required skill", HardSoftLongScore.ofHard(100));
    }

    private Constraint flightConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUniquePair(FlightAssignment.class, Joiners.equal(FlightAssignment::getEmployee))
                .filter((first, second) -> second.getFlight().overlaps(first.getFlight()))
                .penalize("Flight conflict", HardSoftLongScore.ofHard(10));
    }

    private Constraint transferBetweenTwoFlights(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Employee.class)
                .filter(employee -> employee.countInvalidConnections() > 0)
                .penalizeLong("Transfer between two flights", HardSoftLongScore.ofHard(1), Employee::countInvalidConnections);
    }

    private Constraint employeeUnavailability(ConstraintFactory constraintFactory) {
        return constraintFactory.from(FlightAssignment.class)
                .filter(flightAssignment -> {
                    LocalDate departureUTCDate = flightAssignment.getFlight().getDepartureUTCDate();
                    return !flightAssignment.getEmployee().isAvailable(departureUTCDate);
                })
                .penalize("Employee unavailable", HardSoftLongScore.ofHard(10));
    }

    private Constraint firstAssignmentDepartingFromHome(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Employee.class)
                .filter(employee -> !employee.isFirstAssignmentDepartingFromHome())
                .penalize("First assignment departing from home", HardSoftLongScore.ofSoft(1_000_000));
    }

    private Constraint lastAssignmentArrivingAtHome(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Employee.class)
                .filter(employee -> !employee.isLastAssignmentArrivingAtHome())
                .penalize("Last assignment arriving at home", HardSoftLongScore.ofSoft(1_000_000));
    }

}
