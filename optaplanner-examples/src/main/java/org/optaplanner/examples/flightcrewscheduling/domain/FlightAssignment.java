package org.optaplanner.examples.flightcrewscheduling.domain;

import java.util.Comparator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity
public class FlightAssignment extends AbstractPersistable implements Comparable<FlightAssignment> {

    // Needs to be kept consistent with equals on account of Employee's flightAssignmentSet, which is a SortedSet.
    private static final Comparator<FlightAssignment> COMPARATOR = Comparator.comparing(FlightAssignment::getFlight)
            .thenComparing(FlightAssignment::getIndexInFlight);

    private Flight flight;
    private int indexInFlight;
    private Skill requiredSkill;

    @PlanningVariable
    private Employee employee;

    public FlightAssignment() {
    }

    public FlightAssignment(long id, Flight flight, int indexInFlight, Skill requiredSkill) {
        super(id);
        this.flight = flight;
        this.indexInFlight = indexInFlight;
        this.requiredSkill = requiredSkill;
    }

    public long getFlightDurationInMinutes() {
        return flight.getDurationInMinutes();
    }

    @Override
    public String toString() {
        return flight + "-" + indexInFlight;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public int getIndexInFlight() {
        return indexInFlight;
    }

    public void setIndexInFlight(int indexInFlight) {
        this.indexInFlight = indexInFlight;
    }

    public Skill getRequiredSkill() {
        return requiredSkill;
    }

    public void setRequiredSkill(Skill requiredSkill) {
        this.requiredSkill = requiredSkill;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public int compareTo(FlightAssignment o) {
        return COMPARATOR.compare(this, o);
    }
}
