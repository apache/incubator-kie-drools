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
import org.drools.planner.api.domain.variable.DependentPlanningVariable;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRangeFromSolutionProperty;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.tsp.solver.variable.PreviousJourneyListener;

@PlanningEntity
@XStreamAlias("Journey")
public class Journey extends AbstractPersistable {

    private City city; // the destinationCity
    
    // Planning variables: changes during planning, between score calculations.
    private Journey previousJourney;
    private Journey nextJourney;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

//    @PlanningVariable(triggerChainCorrection = true)
    @PlanningVariable(listenerClasses = {PreviousJourneyListener.class})
    @ValueRangeFromSolutionProperty(propertyName = "journeyList")
    public Journey getPreviousJourney() {
        return previousJourney;
    }

    public void setPreviousJourney(Journey previousJourney) {
        this.previousJourney = previousJourney;
    }

//    @DependentPlanningVariable(master = "previousJourney", mappedBy = "previousJourney")
    public Journey getNextJourney() {
        return nextJourney;
    }

    public void setNextJourney(Journey nextJourney) {
        this.nextJourney = nextJourney;
    }

    /**
     * Warning: previous and next do not point to new clones.
     * @return never null
     */
    public Journey clone() {
        Journey clone = new Journey();
        clone.id = id;
        clone.city = city;
        clone.previousJourney = previousJourney;
        clone.nextJourney = nextJourney;
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
        } else if (o instanceof Journey) {
            Journey other = (Journey) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(city, other.city) // TODO performance leak: not needed?
                    .append(previousJourney, other.previousJourney) // TODO performance leak: not needed?
                    .append(nextJourney, other.nextJourney)
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
                .append(previousJourney) // TODO performance leak: not needed?
                .append(nextJourney)
                .toHashCode();
    }

    @Override
    public String toString() {
        return city.toString();
    }

    public int getDistanceToNextJourney() {
        return city.getDistance(nextJourney.getCity());
    }

}
