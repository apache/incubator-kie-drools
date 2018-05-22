/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
