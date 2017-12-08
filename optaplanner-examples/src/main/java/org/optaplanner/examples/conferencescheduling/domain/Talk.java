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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.conferencescheduling.domain.solver.MovableTalkFilter;

@XStreamAlias("Talk")
@PlanningEntity(movableEntitySelectionFilter = MovableTalkFilter.class)
public class Talk extends AbstractPersistable {

    private String title;
    private List<Speaker> speakerList;

    private boolean lockedByUser = false;

    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    private Room room;

    @Override
    public String toString() {
        return title;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Speaker> getSpeakerList() {
        return speakerList;
    }

    public void setSpeakerList(List<Speaker> speakerList) {
        this.speakerList = speakerList;
    }

    public boolean isLockedByUser() {
        return lockedByUser;
    }

    public void setLockedByUser(boolean lockedByUser) {
        this.lockedByUser = lockedByUser;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

}
