package org.optaplanner.examples.vehiclerouting.domain.solver.nearby;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.LocationAware;

public class CustomerNearbyDistanceMeter implements NearbyDistanceMeter<Customer, LocationAware> {

    @Override
    public double getNearbyDistance(Customer origin, LocationAware destination) {
        return origin.getLocation().getDistanceTo(destination.getLocation());
        // If arriving early also inflicts a cost (more than just not using the vehicle more), such as the driver's wage, use this:
        //        if (origin instanceof TimeWindowedCustomer && destination instanceof TimeWindowedCustomer) {
        //            distance += ((TimeWindowedCustomer) origin).getTimeWindowGapTo((TimeWindowedCustomer) destination);
        //        }
        // return distance;
    }

}
