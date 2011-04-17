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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.score.SimpleDoubleScore;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("TravelingSalesmanTour")
public class TravelingSalesmanTour extends AbstractPersistable implements Solution<SimpleDoubleScore> {

    private String name;
    private List<City> cityList;
    private City startCity;

    private List<CityAssignment> cityAssignmentList;
    private CityAssignment startCityAssignment;

    private SimpleDoubleScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    public City getStartCity() {
        return startCity;
    }

    public void setStartCity(City startCity) {
        this.startCity = startCity;
    }

    public List<CityAssignment> getCityAssignmentList() {
        return cityAssignmentList;
    }

    public void setCityAssignmentList(List<CityAssignment> cityAssignmentList) {
        this.cityAssignmentList = cityAssignmentList;
    }

    public CityAssignment getStartCityAssignment() {
        return startCityAssignment;
    }

    public void setStartCityAssignment(CityAssignment startCityAssignment) {
        this.startCityAssignment = startCityAssignment;
    }

    public SimpleDoubleScore getScore() {
        return score;
    }

    public void setScore(SimpleDoubleScore score) {
        this.score = score;
    }

    public boolean isInitialized() {
        return (cityAssignmentList != null);
    }

    public Collection<? extends Object> getFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(cityList);
        facts.add(startCity);
        if (isInitialized()) {
            facts.addAll(cityAssignmentList);
            facts.add(startCityAssignment);
        }
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #cityAssignmentList}.
     */
    public TravelingSalesmanTour cloneSolution() {
        TravelingSalesmanTour clone = new TravelingSalesmanTour();
        clone.id = id;
        clone.name = name;
        clone.cityList = cityList;
        clone.startCity = startCity;
        List<CityAssignment> clonedCityAssignmentList = new ArrayList<CityAssignment>(cityAssignmentList.size());
        Map<Long, CityAssignment> idToClonedCityAssignmentMap = new HashMap<Long, CityAssignment>(
                cityAssignmentList.size());
        for (CityAssignment cityAssignment : cityAssignmentList) {
            CityAssignment clonedCityAssignment = cityAssignment.clone();
            clonedCityAssignmentList.add(clonedCityAssignment);
            idToClonedCityAssignmentMap.put(clonedCityAssignment.getId(), clonedCityAssignment);
            if (cityAssignment == startCityAssignment) {
                clone.startCityAssignment = clonedCityAssignment;
            }
        }
        // Fix: Previous and next should point to the new clones instead of the old instances
        for (CityAssignment clonedCityAssignment : clonedCityAssignmentList) {
            clonedCityAssignment.setPreviousCityAssignment(idToClonedCityAssignmentMap.get(
                    clonedCityAssignment.getPreviousCityAssignment().getId()));
            clonedCityAssignment.setNextCityAssignment(idToClonedCityAssignmentMap.get(
                    clonedCityAssignment.getNextCityAssignment().getId()));
        }
        clone.cityAssignmentList = clonedCityAssignmentList;
        clone.score = score;
        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof TravelingSalesmanTour)) {
            return false;
        } else {
            TravelingSalesmanTour other = (TravelingSalesmanTour) o;
            if (cityAssignmentList.size() != other.cityAssignmentList.size()) {
                return false;
            }
            for (Iterator<CityAssignment> it = cityAssignmentList.iterator(), otherIt = other.cityAssignmentList.iterator(); it.hasNext();) {
                CityAssignment cityAssignment = it.next();
                CityAssignment otherCityAssignment = otherIt.next();
                // Notice: we don't use equals()
                if (!cityAssignment.solutionEquals(otherCityAssignment)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (CityAssignment cityAssignment : cityAssignmentList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(cityAssignment.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
