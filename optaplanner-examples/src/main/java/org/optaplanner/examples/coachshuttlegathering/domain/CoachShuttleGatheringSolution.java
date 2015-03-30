/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.coachshuttlegathering.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.examples.coachshuttlegathering.domain.location.RoadLocation;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.impl.score.XStreamScoreConverter;

@PlanningSolution
@XStreamAlias("CsgCoachShuttleGatheringSolution")
public class CoachShuttleGatheringSolution extends AbstractPersistable implements Solution<HardSoftScore> {

    protected String name;
    protected List<RoadLocation> locationList;
    protected List<Coach> coachList;
    protected List<Shuttle> shuttleList;
    protected BusHub busHub;
    protected List<BusStop> busStopList;

//    protected List<Customer> customerList;

    @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftScoreDefinition.class})
    protected HardSoftScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoadLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<RoadLocation> locationList) {
        this.locationList = locationList;
    }

    public List<Coach> getCoachList() {
        return coachList;
    }

    public void setCoachList(List<Coach> coachList) {
        this.coachList = coachList;
    }

    public List<Shuttle> getShuttleList() {
        return shuttleList;
    }

    public void setShuttleList(List<Shuttle> shuttleList) {
        this.shuttleList = shuttleList;
    }

    public BusHub getBusHub() {
        return busHub;
    }

    public void setBusHub(BusHub busHub) {
        this.busHub = busHub;
    }

    public List<BusStop> getBusStopList() {
        return busStopList;
    }

    public void setBusStopList(List<BusStop> busStopList) {
        this.busStopList = busStopList;
    }

//    @PlanningEntityCollectionProperty
//    @ValueRangeProvider(id = "customerRange")
//    public List<Customer> getCustomerList() {
//        return customerList;
//    }
//
//    public void setCustomerList(List<Customer> customerList) {
//        this.customerList = customerList;
//    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(locationList);
        facts.addAll(coachList);
        facts.addAll(shuttleList);
        facts.add(busHub);
        facts.addAll(busStopList);
        // Do not add the planning entities (vehicleList, customerList) because that will be done automatically
        return facts;
    }

}
