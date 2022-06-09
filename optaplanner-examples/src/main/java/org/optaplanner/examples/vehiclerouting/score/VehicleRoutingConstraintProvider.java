package org.optaplanner.examples.vehiclerouting.score;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

public class VehicleRoutingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                vehicleCapacity(factory),
                distanceToPreviousStandstill(factory),
                distanceFromLastCustomerToDepot(factory),
                arrivalAfterDueTime(factory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    protected Constraint vehicleCapacity(ConstraintFactory factory) {
        return factory.forEach(Customer.class)
                .groupBy(Customer::getVehicle, sum(Customer::getDemand))
                .filter((vehicle, demand) -> demand > vehicle.getCapacity())
                .penalizeLong("vehicleCapacity",
                        HardSoftLongScore.ONE_HARD,
                        (vehicle, demand) -> demand - vehicle.getCapacity());
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    protected Constraint distanceToPreviousStandstill(ConstraintFactory factory) {
        return factory.forEach(Customer.class)
                .penalizeLong("distanceToPreviousStandstill",
                        HardSoftLongScore.ONE_SOFT,
                        Customer::getDistanceFromPreviousStandstill);
    }

    protected Constraint distanceFromLastCustomerToDepot(ConstraintFactory factory) {
        return factory.forEach(Customer.class)
                .filter(customer -> customer.getNextCustomer() == null)
                .penalizeLong("distanceFromLastCustomerToDepot",
                        HardSoftLongScore.ONE_SOFT,
                        customer -> customer.getDistanceTo(customer.getVehicle()));
    }

    // ************************************************************************
    // TimeWindowed: additional hard constraints
    // ************************************************************************

    protected Constraint arrivalAfterDueTime(ConstraintFactory factory) {
        return factory.forEach(TimeWindowedCustomer.class)
                .filter(customer -> customer.getArrivalTime() > customer.getDueTime())
                .penalizeLong("arrivalAfterDueTime",
                        HardSoftLongScore.ONE_HARD,
                        customer -> customer.getArrivalTime() - customer.getDueTime());
    }

}
