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

import java.util.Set;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class Room extends AbstractPersistable {

    private String name;
    private int capacity;

    private Set<TalkType> talkTypeSet;
    private Set<Timeslot> unavailableTimeslotSet;
    private Set<String> tagSet;

    public Room() {
    }

    public Room(long id) {
        super(id);
    }

    public boolean hasTag(String tag) {
        return tagSet.contains(tag);
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Set<TalkType> getTalkTypeSet() {
        return talkTypeSet;
    }

    public void setTalkTypeSet(Set<TalkType> talkTypeSet) {
        this.talkTypeSet = talkTypeSet;
    }

    public Set<Timeslot> getUnavailableTimeslotSet() {
        return unavailableTimeslotSet;
    }

    public void setUnavailableTimeslotSet(Set<Timeslot> unavailableTimeslotSet) {
        this.unavailableTimeslotSet = unavailableTimeslotSet;
    }

    public Set<String> getTagSet() {
        return tagSet;
    }

    public void setTagSet(Set<String> tagSet) {
        this.tagSet = tagSet;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public Room withCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public Room withTalkTypeSet(Set<TalkType> talkTypeSet) {
        this.talkTypeSet = talkTypeSet;
        return this;
    }

    public Room withUnavailableTimeslotSet(Set<Timeslot> unavailableTimeslotTest) {
        this.unavailableTimeslotSet = unavailableTimeslotTest;
        return this;
    }

    public Room withTagSet(Set<String> tagSet) {
        this.tagSet = tagSet;
        return this;
    }

}
