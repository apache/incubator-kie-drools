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
    private List<Domicile> domicileList;

    private List<Visit> visitList;

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

    public List<Domicile> getDomicileList() {
        return domicileList;
    }

    public void setDomicileList(List<Domicile> domicileList) {
        this.domicileList = domicileList;
    }

    @PlanningEntityCollectionProperty
    public List<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }

    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(cityList);
        facts.addAll(domicileList);
        // Do not add the planning entity's (visitList) because that will be done automatically
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #visitList}.
     */
    public TravelingSalesmanTour cloneSolution() {
        TravelingSalesmanTour clone = new TravelingSalesmanTour();
        clone.id = id;
        clone.name = name;
        clone.cityList = cityList;
        clone.domicileList = domicileList;
        List<Visit> clonedVisitList = new ArrayList<Visit>(visitList.size());
        Map<Long, Visit> idToClonedVisitMap = new HashMap<Long, Visit>(
                visitList.size());
        for (Visit visit : visitList) {
            Visit clonedVisit = visit.clone();
            clonedVisitList.add(clonedVisit);
            idToClonedVisitMap.put(clonedVisit.getId(), clonedVisit);
        }
        // Fix: Previous should point to the new clones instead of the old instances
        for (Visit clonedVisit : clonedVisitList) {
            Appearance previousAppearance = clonedVisit.getPreviousAppearance();
            if (previousAppearance instanceof Visit) {
                Long previousVisitId = ((Visit) previousAppearance).getId();
                clonedVisit.setPreviousAppearance(idToClonedVisitMap.get(previousVisitId));
            }
        }
        clone.visitList = clonedVisitList;
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
            if (visitList.size() != other.visitList.size()) {
                return false;
            }
            for (Iterator<Visit> it = visitList.iterator(), otherIt = other.visitList.iterator(); it.hasNext();) {
                Visit visit = it.next();
                Visit otherVisit = otherIt.next();
                // Notice: we don't use equals()
                if (!visit.solutionEquals(otherVisit)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (Visit visit : visitList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(visit.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
