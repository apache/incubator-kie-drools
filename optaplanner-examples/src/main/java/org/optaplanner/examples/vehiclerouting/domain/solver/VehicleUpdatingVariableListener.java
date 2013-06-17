package org.optaplanner.examples.vehiclerouting.domain.solver;

import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.VrpCustomer;
import org.optaplanner.examples.vehiclerouting.domain.VrpStandstill;
import org.optaplanner.examples.vehiclerouting.domain.VrpVehicle;

public class VehicleUpdatingVariableListener implements PlanningVariableListener<VrpCustomer> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, VrpCustomer customer) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, VrpCustomer customer) {
        updateVehicle(scoreDirector, customer);
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, VrpCustomer customer) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, VrpCustomer customer) {
        updateVehicle(scoreDirector, customer);
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, VrpCustomer customer) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, VrpCustomer customer) {
        // Do nothing
    }

    protected void updateVehicle(ScoreDirector scoreDirector, VrpCustomer sourceCustomer) {
        VrpStandstill previousStandstill = sourceCustomer.getPreviousStandstill();
        VrpVehicle vehicle = previousStandstill == null ? null : previousStandstill.getVehicle();
        VrpCustomer shadowCustomer = sourceCustomer;
        while (shadowCustomer != null && shadowCustomer.getVehicle() != vehicle) {
            scoreDirector.beforeVariableChanged(shadowCustomer, "vehicle");
            shadowCustomer.setVehicle(vehicle);
            scoreDirector.afterVariableChanged(shadowCustomer, "vehicle");
            shadowCustomer = shadowCustomer.getNextCustomer();
        }
    }

}
