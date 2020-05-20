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

package org.optaplanner.examples.conferencescheduling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningSolution
public class ConferenceSolution extends AbstractPersistable {

    private String conferenceName;
    @ConstraintConfigurationProvider
    private ConferenceConstraintConfiguration constraintConfiguration;

    @ProblemFactCollectionProperty
    private List<TalkType> talkTypeList;

    @ProblemFactCollectionProperty
    private List<Timeslot> timeslotList;

    @ProblemFactCollectionProperty
    private List<Room> roomList;

    @ProblemFactCollectionProperty
    private List<Speaker> speakerList;

    @PlanningEntityCollectionProperty
    private List<Talk> talkList;

    @PlanningScore
    private HardMediumSoftScore score = null;

    public ConferenceSolution() {
    }

    public ConferenceSolution(long id) {
        super(id);
    }

    @Override
    public String toString() {
        return conferenceName;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public ConferenceConstraintConfiguration getConstraintConfiguration() {
        return constraintConfiguration;
    }

    public void setConstraintConfiguration(ConferenceConstraintConfiguration constraintConfiguration) {
        this.constraintConfiguration = constraintConfiguration;
    }

    public List<TalkType> getTalkTypeList() {
        return talkTypeList;
    }

    public void setTalkTypeList(List<TalkType> talkTypeList) {
        this.talkTypeList = talkTypeList;
    }

    public List<Timeslot> getTimeslotList() {
        return timeslotList;
    }

    public void setTimeslotList(List<Timeslot> timeslotList) {
        this.timeslotList = timeslotList;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public List<Speaker> getSpeakerList() {
        return speakerList;
    }

    public void setSpeakerList(List<Speaker> speakerList) {
        this.speakerList = speakerList;
    }

    public List<Talk> getTalkList() {
        return talkList;
    }

    public void setTalkList(List<Talk> talkList) {
        this.talkList = talkList;
    }

    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public ConferenceSolution withConstraintConfiguration(ConferenceConstraintConfiguration constraintConfiguration) {
        this.constraintConfiguration = constraintConfiguration;
        return this;
    }

    public ConferenceSolution withTalkTypeList(List<TalkType> talkTypeList) {
        this.talkTypeList = talkTypeList;
        return this;
    }

    public ConferenceSolution withTimeslotList(List<Timeslot> timeslotList) {
        this.timeslotList = timeslotList;
        return this;
    }

    public ConferenceSolution withRoomList(List<Room> roomList) {
        this.roomList = roomList;
        return this;
    }

    public ConferenceSolution withSpeakerList(List<Speaker> speakerList) {
        this.speakerList = speakerList;
        return this;
    }

    public ConferenceSolution withTalkList(List<Talk> talkList) {
        this.talkList = talkList;
        return this;
    }

}
