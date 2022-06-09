package org.optaplanner.examples.rocktour.domain;

import java.time.LocalDate;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface RockStandstill {

    /**
     * @return never null;
     */
    RockLocation getDepartureLocation();

    /**
     * @return sometimes null;
     */
    LocalDate getDepartureDate();

    /**
     * @return sometimes null;
     */
    RockTimeOfDay getDepartureTimeOfDay();

    /**
     * @return sometimes null;
     */
    RockStandstill getHosWeekStart();

    /**
     * @return sometimes null;
     */
    Long getHosWeekDrivingSecondsTotal();

    /**
     * @return never null;
     */
    RockLocation getArrivalLocation();

    /**
     * @param standstill never null
     * @return a positive number, in seconds
     */
    default long getDrivingTimeTo(RockStandstill standstill) {
        return getDepartureLocation().getDrivingTimeTo(standstill.getArrivalLocation());
    }

    @InverseRelationShadowVariable(sourceVariableName = "previousStandstill")
    RockShow getNextShow();

    void setNextShow(RockShow nextShow);
}
