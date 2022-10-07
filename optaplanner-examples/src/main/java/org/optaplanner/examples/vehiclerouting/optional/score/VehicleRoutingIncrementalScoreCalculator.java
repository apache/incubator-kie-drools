package org.optaplanner.examples.vehiclerouting.optional.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedVehicleRoutingSolution;

public class VehicleRoutingIncrementalScoreCalculator
        implements IncrementalScoreCalculator<VehicleRoutingSolution, HardSoftLongScore> {

    private boolean timeWindowed;
    private Map<Vehicle, Integer> vehicleDemandMap;

    private long hardScore;
    private long softScore;

    @Override
    public void resetWorkingSolution(VehicleRoutingSolution solution) {
        timeWindowed = solution instanceof TimeWindowedVehicleRoutingSolution;
        hardScore = 0L;
        softScore = 0L;
        List<Vehicle> vehicleList = solution.getVehicleList();
        vehicleDemandMap = new HashMap<>(vehicleList.size());
        for (Vehicle vehicle : vehicleList) {
            int demand = 0;
            List<Customer> customers = vehicle.getCustomers();
            for (int index = 0; index < customers.size(); index++) {
                Customer customer = customers.get(index);
                demand += customer.getDemand();
                softScore -= getDistanceFromPreviousStandstill(vehicle, customer, index);
                if (timeWindowed) {
                    insertArrivalTime((TimeWindowedCustomer) customer);
                }
            }
            if (customers.size() > 0) {
                softScore -= getDistanceToDepot(vehicle, customers.size() - 1);
            }
            vehicleDemandMap.put(vehicle, demand);
            hardScore += Math.min(vehicle.getCapacity() - demand, 0);
        }
    }

    @Override
    public void beforeVariableChanged(Object entity, String variableName) {
        if (variableName.equals("arrivalTime")) {
            retractArrivalTime((TimeWindowedCustomer) entity);
        }
    }

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        if (variableName.equals("arrivalTime")) {
            insertArrivalTime((TimeWindowedCustomer) entity);
        }
    }

    @Override
    public void beforeListVariableChanged(Object entity, String variableName, int fromIndex, int toIndex) {
        Vehicle vehicle = (Vehicle) entity;
        for (int index = fromIndex; index < toIndex; index++) {
            Customer customer = vehicle.getCustomers().get(index);
            // Score constraint vehicleCapacity
            int capacity = vehicle.getCapacity();
            int oldDemand = vehicleDemandMap.get(vehicle);
            int newDemand = oldDemand - customer.getDemand();
            hardScore += Math.min(capacity - newDemand, 0) - Math.min(capacity - oldDemand, 0);
            vehicleDemandMap.put(vehicle, newDemand);
            // Score constraint distanceFromPreviousCustomer
            softScore += getDistanceFromPreviousStandstill(vehicle, customer, index);
        }
        if (toIndex < vehicle.getCustomers().size()) {
            softScore += getDistanceFromPreviousStandstill(vehicle, toIndex);
        } else if (toIndex > 0) {
            // Score constraint distanceFromLastCustomerToDepot
            softScore += getDistanceToDepot(vehicle, toIndex - 1);
        }
    }

    @Override
    public void afterListVariableChanged(Object entity, String variableName, int fromIndex, int toIndex) {
        Vehicle vehicle = (Vehicle) entity;
        for (int index = fromIndex; index < toIndex; index++) {
            Customer customer = vehicle.getCustomers().get(index);
            // Score constraint vehicleCapacity
            int capacity = vehicle.getCapacity();
            int oldDemand = vehicleDemandMap.get(vehicle);
            int newDemand = oldDemand + customer.getDemand();
            hardScore += Math.min(capacity - newDemand, 0) - Math.min(capacity - oldDemand, 0);
            vehicleDemandMap.put(vehicle, newDemand);
            // Score constraint distanceFromPreviousCustomer
            softScore -= getDistanceFromPreviousStandstill(vehicle, customer, index);
        }
        if (toIndex < vehicle.getCustomers().size()) {
            softScore -= getDistanceFromPreviousStandstill(vehicle, toIndex);
        } else if (toIndex > 0) {
            // Score constraint distanceFromLastCustomerToDepot
            softScore -= getDistanceToDepot(vehicle, toIndex - 1);
        }
    }

    private long getDistanceToDepot(Vehicle vehicle, int index) {
        return getDistanceToDepot(vehicle, vehicle.getCustomers().get(index), index);
    }

    private long getDistanceToDepot(Vehicle vehicle, Customer customer, int index) {
        if (index == vehicle.getCustomers().size() - 1) {
            return customer.getLocation().getDistanceTo(vehicle.getLocation());
        }
        return 0;
    }

    private long getDistanceFromPreviousStandstill(Vehicle vehicle, int index) {
        return getDistanceFromPreviousStandstill(vehicle, vehicle.getCustomers().get(index), index);
    }

    private long getDistanceFromPreviousStandstill(Vehicle vehicle, Customer customer, int index) {
        if (index == 0) {
            return vehicle.getLocation().getDistanceTo(customer.getLocation());
        }
        return vehicle.getCustomers().get(index - 1).getLocation().getDistanceTo(customer.getLocation());
    }

    private void insertArrivalTime(TimeWindowedCustomer customer) {
        Long arrivalTime = customer.getArrivalTime();
        if (arrivalTime != null) {
            long dueTime = customer.getDueTime();
            if (dueTime < arrivalTime) {
                // Score constraint arrivalAfterDueTime
                hardScore -= (arrivalTime - dueTime);
            }
        }
        // Score constraint arrivalAfterDueTimeAtDepot is a built-in hard constraint in VehicleRoutingImporter
    }

    private void retractArrivalTime(TimeWindowedCustomer customer) {
        Long arrivalTime = customer.getArrivalTime();
        if (arrivalTime != null) {
            long dueTime = customer.getDueTime();
            if (dueTime < arrivalTime) {
                // Score constraint arrivalAfterDueTime
                hardScore += (arrivalTime - dueTime);
            }
        }
    }

    @Override
    public HardSoftLongScore calculateScore() {
        return HardSoftLongScore.of(hardScore, softScore);
    }

    // ************************************************************************
    // Unused methods
    // ************************************************************************

    @Override
    public void beforeEntityAdded(Object entity) {
        throw new UnsupportedOperationException("The VRP example does not support adding vehicles.");
    }

    @Override
    public void afterEntityAdded(Object entity) {
        throw new UnsupportedOperationException("The VRP example does not support adding vehicles.");
    }

    @Override
    public void beforeEntityRemoved(Object entity) {
        throw new UnsupportedOperationException("The VRP example does not support removing vehicles.");
    }

    @Override
    public void afterEntityRemoved(Object entity) {
        throw new UnsupportedOperationException("The VRP example does not support removing vehicles.");
    }

}
