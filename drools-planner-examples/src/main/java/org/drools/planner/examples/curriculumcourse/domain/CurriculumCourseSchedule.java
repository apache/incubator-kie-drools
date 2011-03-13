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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.HardAndSoftScore;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CurriculumCourseSchedule")
public class CurriculumCourseSchedule extends AbstractPersistable implements Solution<HardAndSoftScore> {

    private String name;

    private List<Teacher> teacherList;
    private List<Curriculum> curriculumList;
    private List<Course> courseList;
    private List<Day> dayList;
    private List<Timeslot> timeslotList;
    private List<Period> periodList;
    private List<Room> roomList;

    private List<UnavailablePeriodConstraint> unavailablePeriodConstraintList;

    private List<Lecture> lectureList;

    private HardAndSoftScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }

    public List<Curriculum> getCurriculumList() {
        return curriculumList;
    }

    public void setCurriculumList(List<Curriculum> curriculumList) {
        this.curriculumList = curriculumList;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public List<Day> getDayList() {
        return dayList;
    }

    public void setDayList(List<Day> dayList) {
        this.dayList = dayList;
    }

    public List<Timeslot> getTimeslotList() {
        return timeslotList;
    }

    public void setTimeslotList(List<Timeslot> timeslotList) {
        this.timeslotList = timeslotList;
    }

    public List<Period> getPeriodList() {
        return periodList;
    }

    public void setPeriodList(List<Period> periodList) {
        this.periodList = periodList;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public List<UnavailablePeriodConstraint> getUnavailablePeriodConstraintList() {
        return unavailablePeriodConstraintList;
    }

    public void setUnavailablePeriodConstraintList(List<UnavailablePeriodConstraint> unavailablePeriodConstraintList) {
        this.unavailablePeriodConstraintList = unavailablePeriodConstraintList;
    }

    public List<Lecture> getLectureList() {
        return lectureList;
    }

    public void setLectureList(List<Lecture> lectureList) {
        this.lectureList = lectureList;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    public boolean isInitialized() {
        return (lectureList != null);
    }

    public Collection<? extends Object> getFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.add(teacherList);
        facts.addAll(curriculumList);
        facts.addAll(courseList);
        facts.addAll(dayList);
        facts.addAll(timeslotList);
        facts.addAll(periodList);
        facts.addAll(roomList);
        facts.addAll(unavailablePeriodConstraintList);
        if (isInitialized()) {
            facts.addAll(lectureList);
        }
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #lectureList}.
     */
    public CurriculumCourseSchedule cloneSolution() {
        CurriculumCourseSchedule clone = new CurriculumCourseSchedule();
        clone.id = id;
        clone.name = name;
        clone.teacherList = teacherList;
        clone.curriculumList = curriculumList;
        clone.courseList = courseList;
        clone.dayList = dayList;
        clone.timeslotList = timeslotList;
        clone.periodList = periodList;
        clone.roomList = roomList;
        clone.unavailablePeriodConstraintList = unavailablePeriodConstraintList;
        List<Lecture> clonedLectureList = new ArrayList<Lecture>(lectureList.size());
        for (Lecture lecture : lectureList) {
            Lecture clonedLecture = lecture.clone();
            clonedLectureList.add(clonedLecture);
        }
        clone.lectureList = clonedLectureList;
        clone.score = score;
        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof CurriculumCourseSchedule)) {
            return false;
        } else {
            CurriculumCourseSchedule other = (CurriculumCourseSchedule) o;
            if (lectureList.size() != other.lectureList.size()) {
                return false;
            }
            for (Iterator<Lecture> it = lectureList.iterator(), otherIt = other.lectureList.iterator(); it.hasNext();) {
                Lecture lecture = it.next();
                Lecture otherLecture = otherIt.next();
                // Notice: we don't use equals()
                if (!lecture.solutionEquals(otherLecture)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (Lecture lecture : lectureList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(lecture.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
