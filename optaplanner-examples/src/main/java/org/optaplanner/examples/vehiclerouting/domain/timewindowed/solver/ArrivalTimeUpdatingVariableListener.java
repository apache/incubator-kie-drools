package org.optaplanner.examples.vehiclerouting.domain.timewindowed.solver;

import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.VrpCustomer;
import org.optaplanner.examples.vehiclerouting.domain.VrpStandstill;
import org.optaplanner.examples.vehiclerouting.domain.VrpVehicle;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.VrpTimeWindowedCustomer;

public class ArrivalTimeUpdatingVariableListener implements PlanningVariableListener<VrpTimeWindowedCustomer> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, VrpTimeWindowedCustomer customer) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, VrpTimeWindowedCustomer customer) {
        updateVehicle(scoreDirector, customer);
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, VrpTimeWindowedCustomer customer) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, VrpTimeWindowedCustomer customer) {
        updateVehicle(scoreDirector, customer);
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, VrpTimeWindowedCustomer customer) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, VrpTimeWindowedCustomer customer) {
        // Do nothing
    }

    protected void updateVehicle(ScoreDirector scoreDirector, VrpTimeWindowedCustomer sourceCustomer) {
        VrpStandstill previousStandstill = sourceCustomer.getPreviousStandstill();
        Integer departureTime = (previousStandstill instanceof VrpTimeWindowedCustomer)
                ? ((VrpTimeWindowedCustomer) previousStandstill).getDepartureTime() : 0;
        VrpTimeWindowedCustomer shadowCustomer = sourceCustomer;
        Integer arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
        while (shadowCustomer != null && !shadowCustomer.getArrivalTime().equals(arrivalTime)) {
            scoreDirector.beforeVariableChanged(shadowCustomer, "arrivalTime");
            shadowCustomer.setArrivalTime(arrivalTime);
            scoreDirector.afterVariableChanged(shadowCustomer, "arrivalTime");
            departureTime = shadowCustomer.getDepartureTime();
            shadowCustomer = shadowCustomer.getNextCustomer();
            arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
        }
    }

    private int calculateArrivalTime(VrpTimeWindowedCustomer customer, int previousDepartureTime) {
        if (customer == null) {
            return -1;
        }
        int arrivalTime = previousDepartureTime + customer.getDistanceToPreviousStandstill();
        if (arrivalTime < customer.getReadyTime()) {
            arrivalTime = customer.getReadyTime();
        }
        return arrivalTime;
    }

}
