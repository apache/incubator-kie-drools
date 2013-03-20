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

package org.optaplanner.examples.tsp.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.api.domain.value.ValueRangeType;
import org.optaplanner.core.api.domain.value.ValueRanges;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.tsp.domain.solver.LatitudeVisitDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = LatitudeVisitDifficultyComparator.class)
@XStreamAlias("Visit")
public class Visit extends AbstractPersistable implements Appearance {

    private City city; // the destinationCity
    
    // Planning variables: changes during planning, between score calculations.
    private Appearance previousAppearance;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @PlanningVariable(chained = true)
    @ValueRanges({
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "domicileList"),
            @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "visitList",
                    excludeUninitializedPlanningEntity = true)})
    public Appearance getPreviousAppearance() {
        return previousAppearance;
    }

    public void setPreviousAppearance(Appearance previousAppearance) {
        this.previousAppearance = previousAppearance;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getDistanceToPreviousAppearance() {
        if (previousAppearance == null) {
            return 0;
        }
        return getDistanceTo(previousAppearance);
    }

    public int getDistanceTo(Appearance appearance) {
        return city.getDistance(appearance.getCity());
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
                .append(city) // TODO performance leak: not needed?
                .append(previousAppearance) // TODO performance leak: not needed?
                .toHashCode();
    }

    @Override
    public String toString() {
        return city + "(after " + (previousAppearance == null ? "null" : previousAppearance.getCity()) + ")";
    }

}
