package org.optaplanner.examples.flightcrewscheduling.domain;

import java.util.Comparator;

public class FlightAssignmentOrderComparator implements Comparator<FlightAssignment> {

    private static final Comparator<FlightAssignment> ASSIGNMENT_COMPARATOR =
            Comparator.comparing((FlightAssignment a) -> a.getFlight().getDepartureUTCDateTime())
                    .thenComparing(a -> a.getFlight().getArrivalUTCDateTime())
                    .thenComparing(FlightAssignment::getIndexInFlight);

    @Override
    public int compare(FlightAssignment o1, FlightAssignment o2) {
        return ASSIGNMENT_COMPARATOR.compare(o1, o2);
    }
}
