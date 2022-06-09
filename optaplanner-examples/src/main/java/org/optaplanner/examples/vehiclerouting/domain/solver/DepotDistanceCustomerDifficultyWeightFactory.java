package org.optaplanner.examples.vehiclerouting.domain.solver;

import static java.util.Comparator.comparingLong;

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

/**
 * On large datasets, the constructed solution looks like a Matryoshka doll.
 */
public class DepotDistanceCustomerDifficultyWeightFactory
        implements SelectionSorterWeightFactory<VehicleRoutingSolution, Customer> {

    @Override
    public DepotDistanceCustomerDifficultyWeight createSorterWeight(VehicleRoutingSolution vehicleRoutingSolution,
            Customer customer) {
        Depot depot = vehicleRoutingSolution.getDepotList().get(0);
        return new DepotDistanceCustomerDifficultyWeight(customer,
                customer.getLocation().getDistanceTo(depot.getLocation())
                        + depot.getLocation().getDistanceTo(customer.getLocation()));
    }

    public static class DepotDistanceCustomerDifficultyWeight
            implements Comparable<DepotDistanceCustomerDifficultyWeight> {

        private static final Comparator<DepotDistanceCustomerDifficultyWeight> COMPARATOR =
                // Ascending (further from the depot are more difficult)
                comparingLong((DepotDistanceCustomerDifficultyWeight weight) -> weight.depotRoundTripDistance)
                        .thenComparingInt(weight -> weight.customer.getDemand())
                        .thenComparingDouble(weight -> weight.customer.getLocation().getLatitude())
                        .thenComparingDouble(weight -> weight.customer.getLocation().getLongitude())
                        .thenComparing(weight -> weight.customer, comparingLong(Customer::getId));

        private final Customer customer;
        private final long depotRoundTripDistance;

        public DepotDistanceCustomerDifficultyWeight(Customer customer,
                long depotRoundTripDistance) {
            this.customer = customer;
            this.depotRoundTripDistance = depotRoundTripDistance;
        }

        @Override
        public int compareTo(DepotDistanceCustomerDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
