/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import java.util.Set;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.conferencescheduling.domain.solver.MovableTalkFilter;

@PlanningEntity(movableEntitySelectionFilter = MovableTalkFilter.class)
public class Talk extends AbstractPersistable {

    private String code;
    private String title;
    private String talkType;
    private Set<String> themeTagSet;
    private Set<String> sectorTagSet;
    private String language;
    private List<Speaker> speakerList;
    private Set<String> requiredTimeslotTagSet;
    private Set<String> preferredTimeslotTagSet;
    private Set<String> prohibitedTimeslotTagSet;
    private Set<String> undesiredTimeslotTagSet;
    private Set<String> requiredRoomTagSet;
    private Set<String> preferredRoomTagSet;
    private Set<String> prohibitedRoomTagSet;
    private Set<String> undesiredRoomTagSet;

    private boolean pinnedByUser = false;

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    private Timeslot timeslot;

    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    private Room room;

    public boolean hasUnavailableRoom() {
        if (timeslot == null || room == null) {
            return false;
        }
        return room.getUnavailableTimeslotSet().contains(timeslot);
    }

    public int overlappingThemeCount(Talk other) {
        return (int) themeTagSet.stream().filter(tag -> other.themeTagSet.contains(tag)).count();
    }

    public int overlappingSectorCount(Talk other) {
        return (int) sectorTagSet.stream().filter(tag -> other.sectorTagSet.contains(tag)).count();
    }

    public boolean hasSpeaker(Speaker speaker) {
        return speakerList.contains(speaker);
    }

    public boolean hasAnyUnavailableSpeaker() {
        if (timeslot == null) {
            return false;
        }
        for (Speaker speaker : speakerList) {
            if (speaker.getUnavailableTimeslotSet().contains(timeslot)) {
                return true;
            }
        }
        return false;
    }

    public int missingRequiredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return (int) requiredTimeslotTagSet.stream().filter(tag -> !timeslot.hasTag(tag)).count();
    }

    public int missingPreferredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return (int) preferredTimeslotTagSet.stream().filter(tag -> !timeslot.hasTag(tag)).count();
    }

    public int prevailingProhibitedTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return (int) prohibitedTimeslotTagSet.stream().filter(tag -> timeslot.hasTag(tag)).count();
    }

    public int prevailingUndesiredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return (int) undesiredTimeslotTagSet.stream().filter(tag -> timeslot.hasTag(tag)).count();
    }

    public int missingRequiredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return (int) requiredRoomTagSet.stream().filter(tag -> !room.hasTag(tag)).count();
    }

    public int missingPreferredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return (int) preferredRoomTagSet.stream().filter(tag -> !room.hasTag(tag)).count();
    }

    public int prevailingProhibitedRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return (int) prohibitedRoomTagSet.stream().filter(tag -> room.hasTag(tag)).count();
    }

    public int prevailingUndesiredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return (int) undesiredRoomTagSet.stream().filter(tag -> room.hasTag(tag)).count();
    }

    public int missingSpeakerRequiredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return (int) speakerList.stream().flatMap(speaker -> speaker.getRequiredTimeslotTagSet().stream())
                .filter(tag -> !timeslot.hasTag(tag)).count();
    }

    public int missingSpeakerPreferredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return (int) speakerList.stream().flatMap(speaker -> speaker.getPreferredTimeslotTagSet().stream())
                .filter(tag -> !timeslot.hasTag(tag)).count();
    }

    public int prevailingSpeakerProhibitedTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return (int) speakerList.stream().flatMap(speaker -> speaker.getProhibitedTimeslotTagSet().stream())
                .filter(tag -> timeslot.hasTag(tag)).count();
    }

    public int prevailingSpeakerUndesiredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return (int) speakerList.stream().flatMap(speaker -> speaker.getUndesiredTimeslotTagSet().stream())
                .filter(tag -> timeslot.hasTag(tag)).count();
    }

    public int missingSpeakerRequiredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return (int) speakerList.stream().flatMap(speaker -> speaker.getRequiredRoomTagSet().stream())
                .filter(tag -> !room.hasTag(tag)).count();
    }

    public int missingSpeakerPreferredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return (int) speakerList.stream().flatMap(speaker -> speaker.getPreferredRoomTagSet().stream())
                .filter(tag -> !room.hasTag(tag)).count();
    }

    public int prevailingSpeakerProhibitedRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return (int) speakerList.stream().flatMap(speaker -> speaker.getProhibitedRoomTagSet().stream())
                .filter(tag -> room.hasTag(tag)).count();
    }

    public int prevailingSpeakerUndesiredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return (int) speakerList.stream().flatMap(speaker -> speaker.getUndesiredRoomTagSet().stream())
                .filter(tag -> room.hasTag(tag)).count();
    }

    @Override
    public String toString() {
        return code;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTalkType() {
        return talkType;
    }

    public void setTalkType(String talkType) {
        this.talkType = talkType;
    }

    public Set<String> getThemeTagSet() {
        return themeTagSet;
    }

    public void setThemeTagSet(Set<String> themeTagSet) {
        this.themeTagSet = themeTagSet;
    }

    public Set<String> getSectorTagSet() {
        return sectorTagSet;
    }

    public void setSectorTagSet(Set<String> sectorTagSet) {
        this.sectorTagSet = sectorTagSet;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Speaker> getSpeakerList() {
        return speakerList;
    }

    public void setSpeakerList(List<Speaker> speakerList) {
        this.speakerList = speakerList;
    }

    public Set<String> getRequiredTimeslotTagSet() {
        return requiredTimeslotTagSet;
    }

    public void setRequiredTimeslotTagSet(Set<String> requiredTimeslotTagSet) {
        this.requiredTimeslotTagSet = requiredTimeslotTagSet;
    }

    public Set<String> getPreferredTimeslotTagSet() {
        return preferredTimeslotTagSet;
    }

    public void setPreferredTimeslotTagSet(Set<String> preferredTimeslotTagSet) {
        this.preferredTimeslotTagSet = preferredTimeslotTagSet;
    }

    public Set<String> getProhibitedTimeslotTagSet() {
        return prohibitedTimeslotTagSet;
    }

    public void setProhibitedTimeslotTagSet(Set<String> prohibitedTimeslotTagSet) {
        this.prohibitedTimeslotTagSet = prohibitedTimeslotTagSet;
    }

    public Set<String> getUndesiredTimeslotTagSet() {
        return undesiredTimeslotTagSet;
    }

    public void setUndesiredTimeslotTagSet(Set<String> undesiredTimeslotTagSet) {
        this.undesiredTimeslotTagSet = undesiredTimeslotTagSet;
    }

    public Set<String> getRequiredRoomTagSet() {
        return requiredRoomTagSet;
    }

    public void setRequiredRoomTagSet(Set<String> requiredRoomTagSet) {
        this.requiredRoomTagSet = requiredRoomTagSet;
    }

    public Set<String> getPreferredRoomTagSet() {
        return preferredRoomTagSet;
    }

    public void setPreferredRoomTagSet(Set<String> preferredRoomTagSet) {
        this.preferredRoomTagSet = preferredRoomTagSet;
    }

    public Set<String> getProhibitedRoomTagSet() {
        return prohibitedRoomTagSet;
    }

    public void setProhibitedRoomTagSet(Set<String> prohibitedRoomTagSet) {
        this.prohibitedRoomTagSet = prohibitedRoomTagSet;
    }

    public Set<String> getUndesiredRoomTagSet() {
        return undesiredRoomTagSet;
    }

    public void setUndesiredRoomTagSet(Set<String> undesiredRoomTagSet) {
        this.undesiredRoomTagSet = undesiredRoomTagSet;
    }

    public boolean isPinnedByUser() {
        return pinnedByUser;
    }

    public void setPinnedByUser(boolean pinnedByUser) {
        this.pinnedByUser = pinnedByUser;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

}
