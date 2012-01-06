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
import org.drools.planner.api.domain.solution.PlanningEntityCollectionProperty;
import org.drools.planner.core.score.buildin.simple.SimpleScore;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("TravelingSalesmanTour")
public class TravelingSalesmanTour extends AbstractPersistable implements Solution<SimpleScore> {

    private String name;
    private List<City> cityList;
    private City startCity;

    private List<Journey> journeyList;
    private Journey startJourney;

    private SimpleScore score;

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

    @PlanningEntityCollectionProperty
    public List<Journey> getJourneyList() {
        return journeyList;
    }

    public void setJourneyList(List<Journey> journeyList) {
        this.journeyList = journeyList;
    }

    // Not annotated as a planning entity because it already included in journeyList
    public Journey getStartJourney() {
        return startJourney;
    }

    public void setStartJourney(Journey startJourney) {
        this.startJourney = startJourney;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(cityList);
        facts.add(startCity);
        // Do not add the planning entity's (journeyList) because that will be done automatically
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #journeyList}.
     */
    public TravelingSalesmanTour cloneSolution() {
        TravelingSalesmanTour clone = new TravelingSalesmanTour();
        clone.id = id;
        clone.name = name;
        clone.cityList = cityList;
        clone.startCity = startCity;
        List<Journey> clonedJourneyList = new ArrayList<Journey>(journeyList.size());
        Map<Long, Journey> idToClonedJourneyMap = new HashMap<Long, Journey>(
                journeyList.size());
        for (Journey journey : journeyList) {
            Journey clonedJourney = journey.clone();
            clonedJourneyList.add(clonedJourney);
            idToClonedJourneyMap.put(clonedJourney.getId(), clonedJourney);
            if (journey == startJourney) {
                clone.startJourney = clonedJourney;
            }
        }
        // Fix: Previous and next should point to the new clones instead of the old instances
        for (Journey clonedJourney : clonedJourneyList) {
            clonedJourney.setPreviousJourney(idToClonedJourneyMap.get(
                    clonedJourney.getPreviousJourney().getId()));
            clonedJourney.setNextJourney(idToClonedJourneyMap.get(
                    clonedJourney.getNextJourney().getId()));
        }
        clone.journeyList = clonedJourneyList;
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
            if (journeyList.size() != other.journeyList.size()) {
                return false;
            }
            for (Iterator<Journey> it = journeyList.iterator(), otherIt = other.journeyList.iterator(); it.hasNext();) {
                Journey journey = it.next();
                Journey otherJourney = otherIt.next();
                // Notice: we don't use equals()
                if (!journey.solutionEquals(otherJourney)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (Journey journey : journeyList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(journey.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
