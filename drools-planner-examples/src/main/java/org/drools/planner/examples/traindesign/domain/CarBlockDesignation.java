/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.traindesign.domain;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.traindesign.domain.solver.RailPath;

@PlanningEntity()
@XStreamAlias("CarBlockDesignation")
public class CarBlockDesignation extends AbstractPersistable {

    private CarBlock carBlock;
    private RailPath railPath;

    public CarBlock getCarBlock() {
        return carBlock;
    }

    public void setCarBlock(CarBlock carBlock) {
        this.carBlock = carBlock;
    }

    @PlanningVariable()
    @ValueRange(type = ValueRangeType.FROM_PLANNING_ENTITY_PROPERTY, planningEntityProperty = "possibleRailPathList")
    public RailPath getRailPath() {
        return railPath;
    }

    public void setRailPath(RailPath railPath) {
        this.railPath = railPath;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public List<RailPath> getPossibleRailPathList() {
        // TODO this is too 1 sided
        return carBlock.getOrigin().getShortestPathTo(carBlock.getDestination()).getRailPathList();
    }

    public int calculateCarTravelCost(int carTravelCostPerDistance) {
        int carTravelCost = carBlock.getNumberOfCars()
                * railPath.getDistance() // in miles * 1000
                * carTravelCostPerDistance; // per 1000 miles
        if (carTravelCost % 1000000 != 0) {
            throw new IllegalStateException("The carTravelCost (" + carTravelCost + ") / 1000000 is not an integer.");
        }
        return carTravelCost / 1000000;
    }

    public CarBlockDesignation clone() {
        CarBlockDesignation clone = new CarBlockDesignation();
        clone.id = id;
        clone.carBlock = carBlock;
        clone.railPath = railPath;
        return clone;
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionHashCode()
     */
    public boolean solutionEquals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CarBlockDesignation) {
            CarBlockDesignation other = (CarBlockDesignation) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(carBlock, other.carBlock)
                    .append(railPath, other.railPath)
                    .isEquals();
        } else {
            return false;
        }
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionEquals(Object)
     */
    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(carBlock)
                .append(railPath)
                .toHashCode();
    }

    @Override
    public String toString() {
        return carBlock + " @ " + railPath;
    }

}
