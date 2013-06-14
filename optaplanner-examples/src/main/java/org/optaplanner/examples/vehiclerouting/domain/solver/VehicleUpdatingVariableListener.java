package org.optaplanner.examples.vehiclerouting.domain.solver;

import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.examples.vehiclerouting.domain.VrpCustomer;
import org.optaplanner.examples.vehiclerouting.domain.VrpStandstill;
import org.optaplanner.examples.vehiclerouting.domain.VrpVehicle;

public class VehicleUpdatingVariableListener implements PlanningVariableListener<VrpCustomer> {

    public void beforeEntityAdded(VrpCustomer customer) {
        // Do nothing
    }

    public void afterEntityAdded(VrpCustomer customer) {
        updateVehicle(customer);
    }

    public void beforeVariableChanged(VrpCustomer customer) {
        // Do nothing
    }

    public void afterVariableChanged(VrpCustomer customer) {
        updateVehicle(customer);
    }

    public void beforeEntityRemoved(VrpCustomer customer) {
        // Do nothing
    }

    public void afterEntityRemoved(VrpCustomer customer) {
        // Do nothing
    }

    protected void updateVehicle(VrpCustomer customer) {
        VrpStandstill previousStandstill = customer.getPreviousStandstill();
        VrpVehicle vehicle = previousStandstill == null ? null : previousStandstill.getVehicle();
        VrpCustomer staleCustomer = customer;
        while (staleCustomer != null && staleCustomer.getVehicle() != vehicle) {
            staleCustomer.setVehicle(vehicle);
            staleCustomer = customer.getNextCustomer();
        }
    }

}
