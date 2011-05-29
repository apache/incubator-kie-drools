/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.trailerrouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.domain.PlanningEntity;
import org.drools.planner.core.domain.PlanningVariable;
import org.drools.planner.core.domain.ValueRangeFromSolutionProperty;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@PlanningEntity
@XStreamAlias("TrailerRoutingOrderAssignment")
public class TrailerRoutingOrderAssignment extends AbstractPersistable implements Comparable<TrailerRoutingOrderAssignment> {

    private TrailerRoutingOrder order;

    // Changed by moves, between score calculations.
    private TrailerRoutingDriver driver;
    private TrailerRoutingTruck truck;
    private TrailerRoutingTrailer primaryTrailer;
    private TrailerRoutingTrailer secondaryTrailer;

    public TrailerRoutingOrder getOrder() {
        return order;
    }

    public void setOrder(TrailerRoutingOrder order) {
        this.order = order;
    }

    @PlanningVariable
    @ValueRangeFromSolutionProperty(propertyName = "driverList")
    public TrailerRoutingDriver getDriver() {
        return driver;
    }

    public void setDriver(TrailerRoutingDriver driver) {
        this.driver = driver;
    }

    @PlanningVariable
    @ValueRangeFromSolutionProperty(propertyName = "truckList")
    public TrailerRoutingTruck getTruck() {
        return truck;
    }

    public void setTruck(TrailerRoutingTruck truck) {
        this.truck = truck;
    }

    @PlanningVariable
    @ValueRangeFromSolutionProperty(propertyName = "trailerList")
    public TrailerRoutingTrailer getPrimaryTrailer() {
        return primaryTrailer;
    }

    public void setPrimaryTrailer(TrailerRoutingTrailer primaryTrailer) {
        this.primaryTrailer = primaryTrailer;
    }

    @PlanningVariable
    @ValueRangeFromSolutionProperty(propertyName = "trailerList")
    public TrailerRoutingTrailer getSecondaryTrailer() {
        return secondaryTrailer;
    }

    public void setSecondaryTrailer(TrailerRoutingTrailer secondaryTrailer) {
        this.secondaryTrailer = secondaryTrailer;
    }

    public int compareTo(TrailerRoutingOrderAssignment other) {
        return new CompareToBuilder()
                .append(order, other.order)
                .toComparison();
    }

    public TrailerRoutingOrderAssignment clone() {
        TrailerRoutingOrderAssignment clone = new TrailerRoutingOrderAssignment();
        clone.id = id;
        clone.order = order;
        clone.driver = driver;
        clone.truck = truck;
        clone.primaryTrailer = primaryTrailer;
        clone.secondaryTrailer = secondaryTrailer;
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
        } else if (o instanceof TrailerRoutingOrderAssignment) {
            TrailerRoutingOrderAssignment other = (TrailerRoutingOrderAssignment) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(order, other.order)
                    .append(driver, other.driver)
                    .append(truck, other.truck)
                    .append(primaryTrailer, other.primaryTrailer)
                    .append(secondaryTrailer, other.secondaryTrailer)
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
                .append(order)
                .append(driver)
                .append(truck)
                .append(primaryTrailer)
                .append(secondaryTrailer)
                .toHashCode();
    }

    @Override
    public String toString() {
        return order + " @ " + driver + " + " + truck + " + "
                + (secondaryTrailer == null ? primaryTrailer : primaryTrailer + " + " + secondaryTrailer);
    }

}
