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
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("Lecture")
public class Lecture extends AbstractPersistable implements Comparable<Lecture> {

    private Course course;
    private int lectureIndexInCourse;

    // Changed by moves, between score calculations.
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

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getLabel() {
        return course + "-" + lectureIndexInCourse;
    }

    public int getStudentSize() {
        return course.getStudentSize();
    }

    public List<Curriculum> getCurriculumList() {
        return course.getCurriculumList();
    }

    public Day getDay() {
        return period.getDay();
    }

    public int getTimeslotIndex() {
        return period.getTimeslot().getTimeslotIndex();
    }

    public int compareTo(Lecture other) {
        return new CompareToBuilder()
                .append(period, other.period)
                .append(room, other.room)
                .append(course, other.course)
                .toComparison();
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
