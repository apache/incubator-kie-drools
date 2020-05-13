/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.tsp.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.examples.tsp.domain.solver.DomicileAngleVisitDifficultyWeightFactory;
import org.optaplanner.examples.tsp.domain.solver.DomicileDistanceStandstillStrengthWeightFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity(difficultyWeightFactoryClass = DomicileAngleVisitDifficultyWeightFactory.class)
@XStreamAlias("Visit")
public class Visit extends AbstractPersistable implements Standstill {

    private Location location;

    // Planning variables: changes during planning, between score calculations.
    private Standstill previousStandstill;

    @Override
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @PlanningVariable(valueRangeProviderRefs = { "domicileRange", "visitRange" },
            graphType = PlanningVariableGraphType.CHAINED,
            strengthWeightFactoryClass = DomicileDistanceStandstillStrengthWeightFactory.class)
    public Standstill getPreviousStandstill() {
        return previousStandstill;
    }

    public void setPreviousStandstill(Standstill previousStandstill) {
        this.previousStandstill = previousStandstill;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceFromPreviousStandstill() {
        if (previousStandstill == null) {
            return 0L;
        }
        return getDistanceFrom(previousStandstill);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceFrom(Standstill standstill) {
        return standstill.getLocation().getDistanceTo(location);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    @Override
    public long getDistanceTo(Standstill standstill) {
        return location.getDistanceTo(standstill.getLocation());
    }

    @Override
    public String toString() {
        if (location.getName() == null) {
            return super.toString();
        }
        return location.getName();
    }

}
