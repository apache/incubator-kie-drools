package org.optaplanner.examples.coachshuttlegathering.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;

@PlanningEntity
public interface BusOrStop {

    Long getId();

    /**
     * @return never null
     */
    RoadLocation getLocation();

    /**
     * @return sometimes null
     */
    Bus getBus();

    /**
     * @return sometimes null
     */
    @InverseRelationShadowVariable(sourceVariableName = "previousBusOrStop")
    BusStop getNextStop();

    void setNextStop(BusStop nextStop);

}
