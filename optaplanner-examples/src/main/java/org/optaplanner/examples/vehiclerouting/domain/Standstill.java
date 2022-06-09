package org.optaplanner.examples.vehiclerouting.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

@PlanningEntity
public interface Standstill {

    /**
     * @return never null
     */
    Location getLocation();

    /**
     * @return sometimes null
     */
    Vehicle getVehicle();

    /**
     * @return sometimes null
     */
    @InverseRelationShadowVariable(sourceVariableName = "previousStandstill")
    Customer getNextCustomer();

    void setNextCustomer(Customer nextCustomer);

}
