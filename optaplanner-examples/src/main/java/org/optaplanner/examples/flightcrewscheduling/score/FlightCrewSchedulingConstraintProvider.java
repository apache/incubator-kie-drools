package org.optaplanner.examples.flightcrewscheduling.score;

import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.overlapping;

import java.time.LocalDate;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;
import org.optaplanner.examples.flightcrewscheduling.domain.Skill;

public class FlightCrewSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                requiredSkill(constraintFactory),
                flightConflict(constraintFactory),
                transferBetweenTwoFlights(constraintFactory),
                employeeUnavailability(constraintFactory),
                firstAssignmentNotDepartingFromHome(constraintFactory),
                lastAssignmentNotArrivingAtHome(constraintFactory)
        };
    }

    private Constraint requiredSkill(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(FlightAssignment.class)
                .filter(flightAssignment -> {
                    Skill requiredSkill = flightAssignment.getRequiredSkill();
                    return !flightAssignment.getEmployee().hasSkill(requiredSkill);
                })
                .penalize("Required skill", HardSoftLongScore.ofHard(100));
    }

    private Constraint flightConflict(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(FlightAssignment.class, equal(FlightAssignment::getEmployee),
                overlapping(flightAssignment -> flightAssignment.getFlight().getDepartureUTCDateTime(),
                        flightAssignment -> flightAssignment.getFlight().getArrivalUTCDateTime()))
                .penalize("Flight conflict", HardSoftLongScore.ofHard(10));
    }

    private Constraint transferBetweenTwoFlights(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Employee.class)
                .filter(employee -> employee.countInvalidConnections() > 0)
                .penalizeLong("Transfer between two flights", HardSoftLongScore.ofHard(1), Employee::countInvalidConnections);
    }

    private Constraint employeeUnavailability(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(FlightAssignment.class)
                .filter(flightAssignment -> {
                    LocalDate departureUTCDate = flightAssignment.getFlight().getDepartureUTCDate();
                    return !flightAssignment.getEmployee().isAvailable(departureUTCDate);
                })
                .penalize("Employee unavailable", HardSoftLongScore.ofHard(10));
    }

    private Constraint firstAssignmentNotDepartingFromHome(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Employee.class)
                .filter(employee -> !employee.isFirstAssignmentDepartingFromHome())
                .penalize("First assignment not departing from home", HardSoftLongScore.ofSoft(1_000_000));
    }

    private Constraint lastAssignmentNotArrivingAtHome(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Employee.class)
                .filter(employee -> !employee.isLastAssignmentArrivingAtHome())
                .penalize("Last assignment not arriving at home", HardSoftLongScore.ofSoft(1_000_000));
    }

}
