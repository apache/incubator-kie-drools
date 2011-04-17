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
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CityAssignment")
public class CityAssignment extends AbstractPersistable implements Comparable<CityAssignment> {

    private City city;
    
    // Changed by moves, between score calculations.
    private CityAssignment previousCityAssignment;
    private CityAssignment nextCityAssignment;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public CityAssignment getPreviousCityAssignment() {
        return previousCityAssignment;
    }

    public void setPreviousCityAssignment(CityAssignment previousCityAssignment) {
        this.previousCityAssignment = previousCityAssignment;
    }

    public CityAssignment getNextCityAssignment() {
        return nextCityAssignment;
    }

    public void setNextCityAssignment(CityAssignment nextCityAssignment) {
        this.nextCityAssignment = nextCityAssignment;
    }

    public int compareTo(CityAssignment other) {
        return new CompareToBuilder()
                .append(city, other.city)
                .append(id, other.id)
                .toComparison();
    }

    /**
     * Warning: previous and next do not point to new clones.
     * @return never null
     */
    public CityAssignment clone() {
        CityAssignment clone = new CityAssignment();
        clone.id = id;
        clone.city = city;
        clone.previousCityAssignment = previousCityAssignment;
        clone.nextCityAssignment = nextCityAssignment;
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
        } else if (o instanceof CityAssignment) {
            CityAssignment other = (CityAssignment) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(city, other.city) // TODO performance leak: not needed?
                    .append(previousCityAssignment, other.previousCityAssignment) // TODO performance leak: not needed?
                    .append(nextCityAssignment, other.nextCityAssignment)
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
                .append(previousCityAssignment) // TODO performance leak: not needed?
                .append(nextCityAssignment)
                .toHashCode();
    }

    @Override
    public String toString() {
        return city.toString();
    }

    public double getDistanceToNextCityAssignment() {
        return city.getDistance(nextCityAssignment.getCity());
    }

}
