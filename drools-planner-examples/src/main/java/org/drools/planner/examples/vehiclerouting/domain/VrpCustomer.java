/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.vehiclerouting.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.api.domain.variable.ValueRanges;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.vehiclerouting.domain.solver.VrpCustomerDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = VrpCustomerDifficultyComparator.class)
@XStreamAlias("VrpCustomer")
public class VrpCustomer extends AbstractPersistable implements VrpAppearance {

    private VrpLocation location;
    private int demand;
    
    // Planning variables: changes during planning, between score calculations.
    private VrpAppearance previousAppearance;

    public VrpLocation getLocation() {
        return location;
    }

    public void setLocation(VrpLocation location) {
        this.location = location;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    @PlanningVariable(chained = true)
    @ValueRanges({
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "vehicleList"),
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "customerList",
                    excludeUninitializedPlanningEntity = true)})
    public VrpAppearance getPreviousAppearance() {
        return previousAppearance;
    }

    public void setPreviousAppearance(VrpAppearance previousAppearance) {
        this.previousAppearance = previousAppearance;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public VrpVehicle getVehicle() {
        // HACK TODO Invent a system like DependentPlanningVariable or PlanningVariableListener to cope with this
        VrpAppearance firstAppearance = getPreviousAppearance();
        while (firstAppearance instanceof VrpCustomer) {
            if (firstAppearance == this) {
                throw new IllegalStateException("Impossible state"); // fail fast during infinite loop
            }
            firstAppearance = ((VrpCustomer) firstAppearance).getPreviousAppearance();
        }
        return (VrpVehicle) firstAppearance;
    }

    public int getDistanceToPreviousAppearance() {
        if (previousAppearance == null) {
            return 0;
        }
        return getDistanceTo(previousAppearance);
    }

    public int getDistanceTo(VrpAppearance appearance) {
        return location.getDistance(appearance.getLocation());
    }

    /**
     * Warning: previous and next do not point to new clones.
     * @return never null
     */
    public VrpCustomer clone() {
        VrpCustomer clone = new VrpCustomer();
        clone.id = id;
        clone.location = location;
        clone.demand = demand;
        clone.previousAppearance = previousAppearance;
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
        } else if (o instanceof VrpCustomer) {
            VrpCustomer other = (VrpCustomer) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(location, other.location) // TODO performance leak: not needed?
                    .append(previousAppearance, other.previousAppearance) // TODO performance leak: not needed?
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
                .append(location) // TODO performance leak: not needed?
                .append(previousAppearance) // TODO performance leak: not needed?
                .toHashCode();
    }

    @Override
    public String toString() {
        return location + "(after " + (previousAppearance == null ? "null" : previousAppearance.getLocation()) + ")";
    }

}
