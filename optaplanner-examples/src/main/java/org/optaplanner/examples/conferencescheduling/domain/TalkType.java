/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

public class TalkType extends AbstractPersistable {

    private String name;

    private Set<Timeslot> compatibleTimeslotSet;
    private Set<Room> compatibleRoomSet;

    public TalkType() {
    }

    public TalkType(long id) {
        super(id);
    }

    public TalkType(long id, String name) {
        super(id);
        this.name = name;
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

    public Set<Timeslot> getCompatibleTimeslotSet() {
        return compatibleTimeslotSet;
    }

    public void setCompatibleTimeslotSet(Set<Timeslot> compatibleTimeslotSet) {
        this.compatibleTimeslotSet = compatibleTimeslotSet;
    }

    public Set<Room> getCompatibleRoomSet() {
        return compatibleRoomSet;
    }

    public void setCompatibleRoomSet(Set<Room> compatibleRoomSet) {
        this.compatibleRoomSet = compatibleRoomSet;
    }

}
