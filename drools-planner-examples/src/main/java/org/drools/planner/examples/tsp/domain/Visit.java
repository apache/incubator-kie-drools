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

package org.drools.planner.examples.tsp.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.api.domain.variable.ValueRanges;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@PlanningEntity
@XStreamAlias("Visit")
public class Visit extends AbstractPersistable implements Terminal {

    private City city; // the destinationCity
    
    // Planning variables: changes during planning, between score calculations.
    private Terminal previousTerminal;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @PlanningVariable(triggerChainCorrection = true)
    @ValueRanges({
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "depotList"),
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "visitList",
                    excludeUninitializedPlanningEntity = true)})
    public Terminal getPreviousTerminal() {
        return previousTerminal;
    }

    public void setPreviousTerminal(Terminal previousTerminal) {
        this.previousTerminal = previousTerminal;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getDistanceToPreviousTerminal() {
        if (previousTerminal == null) {
            return 0;
        }
        return getDistanceTo(previousTerminal);
    }

    public int getDistanceTo(Terminal terminal) {
        return city.getDistance(terminal.getCity());
    }

    /**
     * Warning: previous and next do not point to new clones.
     * @return never null
     */
    public Visit clone() {
        Visit clone = new Visit();
        clone.id = id;
        clone.city = city;
        clone.previousTerminal = previousTerminal;
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
        } else if (o instanceof Visit) {
            Visit other = (Visit) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(city, other.city) // TODO performance leak: not needed?
                    .append(previousTerminal, other.previousTerminal) // TODO performance leak: not needed?
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
                .append(city) // TODO performance leak: not needed?
                .append(previousTerminal) // TODO performance leak: not needed?
                .toHashCode();
    }

    @Override
    public String toString() {
        return city + "(after " + (previousTerminal == null ? "null" : previousTerminal.getCity()) + ")";
    }

}
