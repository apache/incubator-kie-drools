/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.curriculumcourse.solver.move;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.curriculumcourse.domain.Lecture;
import org.drools.planner.examples.curriculumcourse.domain.Room;

public class RoomChangeMove implements Move, TabuPropertyEnabled {

    private Lecture lecture;
    private Room toRoom;

    public RoomChangeMove(Lecture lecture, Room toRoom) {
        this.lecture = lecture;
        this.toRoom = toRoom;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(lecture.getRoom(), toRoom);
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new RoomChangeMove(lecture, lecture.getRoom());
    }

    public void doMove(WorkingMemory workingMemory) {
        CurriculumCourseMoveHelper.moveRoom(workingMemory, lecture, toRoom);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Collections.singletonList(lecture);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof RoomChangeMove) {
            RoomChangeMove other = (RoomChangeMove) o;
            return new EqualsBuilder()
                    .append(lecture, other.lecture)
                    .append(toRoom, other.toRoom)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(lecture)
                .append(toRoom)
                .toHashCode();
    }

    public String toString() {
        return lecture + " => " + toRoom;
    }

}
