package org.optaplanner.examples.vehiclerouting.domain.timewindowed.solver;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

// TODO When this class is added only for TimeWindowedCustomer, use TimeWindowedCustomer instead of Customer
public class ArrivalTimeUpdatingVariableListener implements VariableListener<Customer> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, Customer customer) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, Customer customer) {
        if (customer instanceof TimeWindowedCustomer) {
            updateVehicle(scoreDirector, (TimeWindowedCustomer) customer);
        }
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, Customer customer) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, Customer customer) {
        if (customer instanceof TimeWindowedCustomer) {
            updateVehicle(scoreDirector, (TimeWindowedCustomer) customer);
        }
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, Customer customer) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, Customer customer) {
        // Do nothing
    }

    protected void updateVehicle(ScoreDirector scoreDirector, TimeWindowedCustomer sourceCustomer) {
        Standstill previousStandstill = sourceCustomer.getPreviousStandstill();
        Integer departureTime = (previousStandstill instanceof TimeWindowedCustomer)
                ? ((TimeWindowedCustomer) previousStandstill).getDepartureTime() : null;
        TimeWindowedCustomer shadowCustomer = sourceCustomer;
        Integer arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
        while (shadowCustomer != null && ObjectUtils.notEqual(shadowCustomer.getArrivalTime(), arrivalTime)) {
            scoreDirector.beforeVariableChanged(shadowCustomer, "arrivalTime");
            shadowCustomer.setArrivalTime(arrivalTime);
            scoreDirector.afterVariableChanged(shadowCustomer, "arrivalTime");
            departureTime = shadowCustomer.getDepartureTime();
            shadowCustomer = shadowCustomer.getNextCustomer();
            arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
        }
    }

    private Integer calculateArrivalTime(TimeWindowedCustomer customer, Integer previousDepartureTime) {
        if (customer == null) {
            return null;
        }
        if (previousDepartureTime == null) {
            // PreviousStandstill is the Vehicle, so we leave from the Depot at the best suitable time
            return Math.max(customer.getReadyTime(), customer.getDistanceFromPreviousStandstill());
        }
        return previousDepartureTime + customer.getDistanceFromPreviousStandstill();
    }

}
