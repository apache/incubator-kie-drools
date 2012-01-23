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

package org.drools.planner.examples.curriculumcourse.domain;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.curriculumcourse.domain.solver.LectureDifficultyWeightFactory;
import org.drools.planner.examples.curriculumcourse.domain.solver.PeriodStrengthWeightFactory;
import org.drools.planner.examples.curriculumcourse.domain.solver.RoomStrengthWeightFactory;

@PlanningEntity(difficultyWeightFactoryClass = LectureDifficultyWeightFactory.class)
@XStreamAlias("Lecture")
public class Lecture extends AbstractPersistable {

    private Course course;
    private int lectureIndexInCourse;

    // Planning variables: changes during planning, between score calculations.
    private Period period;
    private Room room;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getLectureIndexInCourse() {
        return lectureIndexInCourse;
    }

    public void setLectureIndexInCourse(int lectureIndexInCourse) {
        this.lectureIndexInCourse = lectureIndexInCourse;
    }

    @PlanningVariable(strengthWeightFactoryClass = PeriodStrengthWeightFactory.class)
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "periodList")
    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @PlanningVariable(strengthWeightFactoryClass = RoomStrengthWeightFactory.class)
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "roomList")
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getStudentSize() {
        return course.getStudentSize();
    }

    public List<Curriculum> getCurriculumList() {
        return course.getCurriculumList();
    }

    public Day getDay() {
        if (period == null) {
            return null;
        }
        return period.getDay();
    }

    public int getTimeslotIndex() {
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getTimeslot().getTimeslotIndex();
    }

    public String getLabel() {
        return course + "-" + lectureIndexInCourse;
    }

    public Lecture clone() {
        Lecture clone = new Lecture();
        clone.id = id;
        clone.course = course;
        clone.lectureIndexInCourse = lectureIndexInCourse;
        clone.period = period;
        clone.room = room;
        return clone;
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionHashCode()
     */
    public boolean solutionEquals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Lecture) {
            Lecture other = (Lecture) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(course, other.course)
                    .append(period, other.period)
                    .append(room, other.room)
                    .isEquals();
        } else {
            return false;
        }
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionEquals(Object)
     */
    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(course)
                .append(period)
                .append(room)
                .toHashCode();
    }

    @Override
    public String toString() {
        return course + "-" + lectureIndexInCourse + " @ " + period + " + " + room;
    }

}
