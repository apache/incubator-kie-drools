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

package org.drools.planner.examples.examination.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.score.HardAndSoftScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.examination.domain.solver.TopicConflict;

@XStreamAlias("Examination")
public class Examination extends AbstractPersistable implements Solution<HardAndSoftScore> {

    private InstitutionalWeighting institutionalWeighting;

    private List<Student> studentList;
    private List<Topic> topicList;
    private List<Period> periodList;
    private List<Room> roomList;

    private List<PeriodHardConstraint> periodHardConstraintList;
    private List<RoomHardConstraint> roomHardConstraintList;

    private List<Exam> examList;

    private HardAndSoftScore score;

    public InstitutionalWeighting getInstitutionalWeighting() {
        return institutionalWeighting;
    }

    public void setInstitutionalWeighting(InstitutionalWeighting institutionalWeighting) {
        this.institutionalWeighting = institutionalWeighting;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public List<Topic> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<Topic> topicList) {
        this.topicList = topicList;
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

    public List<PeriodHardConstraint> getPeriodHardConstraintList() {
        return periodHardConstraintList;
    }

    public void setPeriodHardConstraintList(List<PeriodHardConstraint> periodHardConstraintList) {
        this.periodHardConstraintList = periodHardConstraintList;
    }

    public List<RoomHardConstraint> getRoomHardConstraintList() {
        return roomHardConstraintList;
    }

    public void setRoomHardConstraintList(List<RoomHardConstraint> roomHardConstraintList) {
        this.roomHardConstraintList = roomHardConstraintList;
    }

    public List<Exam> getExamList() {
        return examList;
    }

    public void setExamList(List<Exam> examList) {
        this.examList = examList;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    public boolean isInitialized() {
        return (examList != null);
    }

    public Collection<? extends Object> getFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.add(institutionalWeighting);
        // Student isn't used in the DRL at the moment
        // Notice that asserting them is not a noticable performance cost, only a memory cost.
        // facts.addAll(studentList);
        facts.addAll(topicList);
        facts.addAll(periodList);
        facts.addAll(roomList);
        facts.addAll(periodHardConstraintList);
        facts.addAll(roomHardConstraintList);
        if (isInitialized()) {
            facts.addAll(examList);
        }
        // A faster alternative to a insertLogicalTopicConflicts rule.
        facts.addAll(calculateTopicConflictList());
        return facts;
    }

    private List<TopicConflict> calculateTopicConflictList() {
        List<TopicConflict> topicConflictList = new ArrayList<TopicConflict>();
        for (Topic leftTopic : topicList) {
            for (Topic rightTopic : topicList) {
                if (leftTopic.getId() < rightTopic.getId()) {
                    int studentSize = 0;
                    for (Student student : leftTopic.getStudentList()) {
                        // TODO performance can be improved hashing leftTopicStudentList?
                        if (rightTopic.getStudentList().contains(student)) {
                            studentSize++;
                        }
                    }
                    if (studentSize > 0) {
                        topicConflictList.add(new TopicConflict(leftTopic, rightTopic, studentSize));
                    }
                }
            }
        }
        return topicConflictList;
    }

    /**
     * Clone will only deep copy the {@link #examList}.
     */
    public Examination cloneSolution() {
        Examination clone = new Examination();
        clone.id = id;
        clone.institutionalWeighting = institutionalWeighting;
        clone.studentList = studentList;
        clone.topicList = topicList;
        clone.periodList = periodList;
        clone.roomList = roomList;
        clone.periodHardConstraintList = periodHardConstraintList;
        clone.roomHardConstraintList = roomHardConstraintList;
        List<Exam> clonedExamList = new ArrayList<Exam>(examList.size());
        for (Exam exam : examList) {
            Exam clonedExam = exam.clone();
            clonedExamList.add(clonedExam);
        }
        clone.examList = clonedExamList;
        clone.score = score;
        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof Examination)) {
            return false;
        } else {
            Examination other = (Examination) o;
            if (examList.size() != other.examList.size()) {
                return false;
            }
            for (Iterator<Exam> it = examList.iterator(), otherIt = other.examList.iterator(); it.hasNext();) {
                Exam exam = it.next();
                Exam otherExam = otherIt.next();
                // Notice: we don't use equals()
                if (!exam.solutionEquals(otherExam)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (Exam exam : examList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(exam.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
