/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.persistence.xstream.api.score.buildin.simplelong.SimpleLongScoreXStreamConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@PlanningSolution
@XStreamAlias("TspSolution")
public class TspSolution extends AbstractPersistable {

    private String name;
    protected DistanceType distanceType;
    protected String distanceUnitOfMeasurement;
    private List<Location> locationList;
    private Domicile domicile;

    private List<Visit> visitList;

    @XStreamConverter(SimpleLongScoreXStreamConverter.class)
    private SimpleLongScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DistanceType getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(DistanceType distanceType) {
        this.distanceType = distanceType;
    }

    public String getDistanceUnitOfMeasurement() {
        return distanceUnitOfMeasurement;
    }

    public void setDistanceUnitOfMeasurement(String distanceUnitOfMeasurement) {
        this.distanceUnitOfMeasurement = distanceUnitOfMeasurement;
    }

    @ProblemFactCollectionProperty
    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    @ProblemFactProperty
    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "visitRange")
    public List<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }

    @PlanningScore
    public SimpleLongScore getScore() {
        return score;
    }

    public void setScore(SimpleLongScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ValueRangeProvider(id = "domicileRange")
    public List<Domicile> getDomicileRange() {
        return Collections.singletonList(domicile);
    }

    public String getDistanceString(NumberFormat numberFormat) {
        if (score == null) {
            return null;
        }
        long distance = -score.getScore();
        if (distanceUnitOfMeasurement == null) {
            return numberFormat.format(((double) distance) / 1000.0);
        }
        switch (distanceUnitOfMeasurement) {
            case "sec": // TODO why are the values 1000 larger?
                long hours = distance / 3600000;
                long minutes = distance % 3600000 / 60000;
                long seconds = distance % 60000 / 1000;
                long milliseconds = distance % 1000;
                return hours + "h " + minutes + "m " + seconds + "s " + milliseconds + "ms";
            case "km": { // TODO why are the values 1000 larger?
                long km = distance / 1000;
                long meter = distance % 1000;
                return km + "km " + meter + "m";
            }
            case "meter": {
                long km = distance / 1000;
                long meter = distance % 1000;
                return km + "km " + meter + "m";
            }
            default:
                return numberFormat.format(((double) distance) / 1000.0) + " " + distanceUnitOfMeasurement;
        }
    }

}
