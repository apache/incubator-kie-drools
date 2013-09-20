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
        Integer milliDepartureTime = (previousStandstill instanceof VrpTimeWindowedCustomer)
                ? ((VrpTimeWindowedCustomer) previousStandstill).getDepartureTime() : null;
        VrpTimeWindowedCustomer shadowCustomer = sourceCustomer;
        Integer milliArrivalTime = calculateMilliArrivalTime(shadowCustomer, milliDepartureTime);
        while (shadowCustomer != null && ObjectUtils.notEqual(shadowCustomer.getMilliArrivalTime(), milliArrivalTime)) {
            scoreDirector.beforeVariableChanged(shadowCustomer, "milliArrivalTime");
            shadowCustomer.setMilliArrivalTime(milliArrivalTime);
            scoreDirector.afterVariableChanged(shadowCustomer, "milliArrivalTime");
            milliDepartureTime = shadowCustomer.getDepartureTime();
            shadowCustomer = shadowCustomer.getNextCustomer();
            milliArrivalTime = calculateMilliArrivalTime(shadowCustomer, milliDepartureTime);
        }
    }

    private Integer calculateMilliArrivalTime(VrpTimeWindowedCustomer customer, Integer previousMilliDepartureTime) {
        if (customer == null) {
            return null;
        }
        if (previousMilliDepartureTime == null) {
            // PreviousStandstill is the Vehicle, so we leave from the Depot at the best suitable time
            return Math.max(customer.getMilliReadyTime(), customer.getMilliDistanceToPreviousStandstill());
        }
        return previousMilliDepartureTime + customer.getMilliDistanceToPreviousStandstill();
    }

}
