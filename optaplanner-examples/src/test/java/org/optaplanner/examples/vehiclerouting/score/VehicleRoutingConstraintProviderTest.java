package org.optaplanner.examples.vehiclerouting.score;

import java.util.Arrays;

import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.AirLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedDepot;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class VehicleRoutingConstraintProviderTest
        extends AbstractConstraintProviderTest<VehicleRoutingConstraintProvider, VehicleRoutingSolution> {

    private final Location location1 = new AirLocation(1L, 0.0, 0.0);
    private final Location location2 = new AirLocation(2L, 0.0, 4.0);
    private final Location location3 = new AirLocation(3L, 3.0, 0.0);

    @ConstraintProviderTest
    void vehicleCapacityUnpenalized(
            ConstraintVerifier<VehicleRoutingConstraintProvider, VehicleRoutingSolution> constraintVerifier) {
        Vehicle vehicleA = new Vehicle(1L, 100, new Depot(1L, location1));
        Customer customer1 = new Customer(2L, location2, 80);

        connect(vehicleA, customer1);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity)
                .given(vehicleA, customer1)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void vehicleCapacityPenalized(
            ConstraintVerifier<VehicleRoutingConstraintProvider, VehicleRoutingSolution> constraintVerifier) {
        Customer customer1 = new Customer(2L, location2, 80);
        Customer customer2 = new Customer(3L, location3, 40);
        Vehicle vehicleA = new Vehicle(1L, 100, new Depot(1L, location1));

        connect(vehicleA, customer1, customer2);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::vehicleCapacity)
                .given(vehicleA, customer1, customer2)
                .penalizesBy(20);
    }

    @ConstraintProviderTest
    void distanceToPreviousStandstill(
            ConstraintVerifier<VehicleRoutingConstraintProvider, VehicleRoutingSolution> constraintVerifier) {
        Customer customer1 = new Customer(2L, location2, 80);
        Customer customer2 = new Customer(3L, location3, 40);
        Vehicle vehicleA = new Vehicle(1L, 100, new Depot(1L, location1));

        connect(vehicleA, customer1, customer2);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::distanceToPreviousStandstill)
                .given(vehicleA, customer1, customer2)
                .penalizesBy(9000L);
    }

    @ConstraintProviderTest
    void distanceFromLastCustomerToDepot(
            ConstraintVerifier<VehicleRoutingConstraintProvider, VehicleRoutingSolution> constraintVerifier) {
        Customer customer1 = new Customer(2L, location2, 80);
        Customer customer2 = new Customer(3L, location3, 40);
        Vehicle vehicleA = new Vehicle(1L, 100, new Depot(1L, location1));

        connect(vehicleA, customer1, customer2);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::distanceFromLastCustomerToDepot)
                .given(vehicleA, customer1, customer2)
                .penalizesBy(3000L);
    }

    @ConstraintProviderTest
    void arrivalAfterDueTime(ConstraintVerifier<VehicleRoutingConstraintProvider, VehicleRoutingSolution> constraintVerifier) {
        TimeWindowedCustomer customer1 = new TimeWindowedCustomer(2L, location2, 1, 8_00_00L, 18_00_00L, 1_00_00L);
        customer1.setArrivalTime(8_00_00L + 4000L);
        TimeWindowedCustomer customer2 = new TimeWindowedCustomer(3L, location3, 40, 8_00_00L, 9_00_00L, 1_00_00L);
        customer2.setArrivalTime(8_00_00L + 4000L + 1_00_00L + 5000L);
        Vehicle vehicleA = new Vehicle(1L, 100, new TimeWindowedDepot(1L, location1, 8_00_00L, 18_00_00L));

        connect(vehicleA, customer1, customer2);

        constraintVerifier.verifyThat(VehicleRoutingConstraintProvider::arrivalAfterDueTime)
                .given(vehicleA, customer1, customer2)
                .penalizesBy(90_00L);
    }

    static void connect(Vehicle vehicle, Customer... customers) {
        vehicle.setCustomers(Arrays.asList(customers));
        for (int i = 0; i < customers.length; i++) {
            Customer customer = customers[i];
            customer.setVehicle(vehicle);
            if (i > 0) {
                customer.setPreviousCustomer(customers[i - 1]);
            }
            if (i < customers.length - 1) {
                customer.setNextCustomer(customers[i + 1]);
            }
        }
    }

    @Override
    protected ConstraintVerifier<VehicleRoutingConstraintProvider, VehicleRoutingSolution> createConstraintVerifier() {
        return ConstraintVerifier.build(new VehicleRoutingConstraintProvider(), VehicleRoutingSolution.class, Vehicle.class);
    }
}
