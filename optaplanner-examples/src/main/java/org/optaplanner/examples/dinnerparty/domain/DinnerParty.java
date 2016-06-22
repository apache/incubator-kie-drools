/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.dinnerparty.domain;

import java.util.EnumSet;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.api.score.buildin.simple.SimpleScoreXStreamConverter;

@PlanningSolution
@XStreamAlias("DinnerParty")
public class DinnerParty extends AbstractPersistable {

    private List<Job> jobList;
    private List<Guest> guestList;
    private List<HobbyPractician> hobbyPracticianList;
    private List<Table> tableList;
    private List<Seat> seatList;

    private List<SeatDesignation> seatDesignationList;

    @XStreamConverter(SimpleScoreXStreamConverter.class)
    private SimpleScore score;

    @ProblemFactCollectionProperty
    public EnumSet<Hobby> getHobbySet() {
        return EnumSet.allOf(Hobby.class);
    }

    @ProblemFactCollectionProperty
    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    @ProblemFactCollectionProperty
    public EnumSet<JobType> getJobSet() {
        return EnumSet.allOf(JobType.class);
    }

    @ProblemFactCollectionProperty
    public List<Guest> getGuestList() {
        return guestList;
    }

    public void setGuestList(List<Guest> guestList) {
        this.guestList = guestList;
    }

    @ProblemFactCollectionProperty
    public List<HobbyPractician> getHobbyPracticianList() {
        return hobbyPracticianList;
    }

    public void setHobbyPracticianList(List<HobbyPractician> hobbyPracticianList) {
        this.hobbyPracticianList = hobbyPracticianList;
    }

    @ProblemFactCollectionProperty
    public List<Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

    @ValueRangeProvider(id = "seatRange")
    @ProblemFactCollectionProperty
    public List<Seat> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<Seat> seatList) {
        this.seatList = seatList;
    }

    @PlanningEntityCollectionProperty
    public List<SeatDesignation> getSeatDesignationList() {
        return seatDesignationList;
    }

    public void setSeatDesignationList(List<SeatDesignation> seatDesignationList) {
        this.seatDesignationList = seatDesignationList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
