package org.optaplanner.examples.vehiclerouting.domain.timewindowed.solver;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.VrpCustomer;
import org.optaplanner.examples.vehiclerouting.domain.VrpStandstill;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.VrpTimeWindowedCustomer;

// TODO When this class is added only for VrpTimeWindowedCustomer, use VrpTimeWindowedCustomer instead of VrpCustomer
public class ArrivalTimeUpdatingVariableListener implements PlanningVariableListener<VrpCustomer> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, VrpCustomer customer) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, VrpCustomer customer) {
        if (customer instanceof VrpTimeWindowedCustomer) {
            updateVehicle(scoreDirector, (VrpTimeWindowedCustomer) customer);
        }
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, VrpCustomer customer) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, VrpCustomer customer) {
        if (customer instanceof VrpTimeWindowedCustomer) {
            updateVehicle(scoreDirector, (VrpTimeWindowedCustomer) customer);
        }
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, VrpCustomer customer) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, VrpCustomer customer) {
        // Do nothing
    }

    protected void updateVehicle(ScoreDirector scoreDirector, VrpTimeWindowedCustomer sourceCustomer) {
        VrpStandstill previousStandstill = sourceCustomer.getPreviousStandstill();
        Integer departureTime = (previousStandstill instanceof VrpTimeWindowedCustomer)
                ? ((VrpTimeWindowedCustomer) previousStandstill).getDepartureTime() : null;
        VrpTimeWindowedCustomer shadowCustomer = sourceCustomer;
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

    private Integer calculateArrivalTime(VrpTimeWindowedCustomer customer, Integer previousDepartureTime) {
        if (customer == null) {
            return null;
        }
        if (previousDepartureTime == null) {
            // PreviousStandstill is the Vehicle, so we leave from the Depot at the best suitable time
            return Math.max(customer.getReadyTime(), customer.getDistanceToPreviousStandstill());
        }
        return previousDepartureTime + customer.getDistanceToPreviousStandstill();
    }

}
