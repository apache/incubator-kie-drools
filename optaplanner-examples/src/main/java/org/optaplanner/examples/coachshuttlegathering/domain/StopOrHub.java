package org.optaplanner.examples.coachshuttlegathering.domain;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;

@PlanningEntity
public interface StopOrHub {

    String getName();

    /**
     * @return never null
     */
    RoadLocation getLocation();

    boolean isVisitedByCoach();

    @InverseRelationShadowVariable(sourceVariableName = "destination")
    List<Shuttle> getTransferShuttleList();

    void setTransferShuttleList(List<Shuttle> transferShuttleList);

    Integer getTransportTimeToHub();

}
