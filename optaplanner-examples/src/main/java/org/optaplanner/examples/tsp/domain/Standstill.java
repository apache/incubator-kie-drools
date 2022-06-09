package org.optaplanner.examples.tsp.domain;

import org.optaplanner.examples.tsp.domain.location.Location;

public interface Standstill {

    /**
     * @return never null
     */
    Location getLocation();

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    long getDistanceTo(Standstill standstill);

}
