package org.optaplanner.examples.flightcrewscheduling.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class FlightCrewParametrization extends AbstractPersistable {

    public static final String REQUIRED_SKILL = "Required skill";
    public static final String FLIGHT_CONFLICT = "Flight conflict";
    public static final String TRANSFER_BETWEEN_TWO_FLIGHTS = "Transfer between two flights";
    public static final String EMPLOYEE_UNAVAILABILITY = "Employee unavailability";

    public static final String LOAD_BALANCE_FLIGHT_DURATION_TOTAL_PER_EMPLOYEE =
            "Load balance flight duration total per employee";

    private long loadBalanceFlightDurationTotalPerEmployee = 1;

    public FlightCrewParametrization() {
    }

    public FlightCrewParametrization(long id) {
        super(id);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public long getLoadBalanceFlightDurationTotalPerEmployee() {
        return loadBalanceFlightDurationTotalPerEmployee;
    }

    public void setLoadBalanceFlightDurationTotalPerEmployee(long loadBalanceFlightDurationTotalPerEmployee) {
        this.loadBalanceFlightDurationTotalPerEmployee = loadBalanceFlightDurationTotalPerEmployee;
    }

}
