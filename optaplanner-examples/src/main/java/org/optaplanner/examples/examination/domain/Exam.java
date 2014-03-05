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

package org.optaplanner.examples.examination.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.examination.domain.solver.ExamBefore;
import org.optaplanner.examples.examination.domain.solver.ExamCoincidence;

@PlanningEntity
@XStreamAlias("Exam")
public class Exam extends AbstractPersistable implements PlanningCloneable<Exam> {

    private Topic topic;

    // Calculated during initialization, not used for score calculation, used for move creation.
    private ExamCoincidence examCoincidence = null;
    private ExamBefore examBefore = null;

    // Planning variables: changes during planning, between score calculations.
    private Period period;
    private Room room;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public ExamCoincidence getExamCoincidence() {
        return examCoincidence;
    }

    public void setExamCoincidence(ExamCoincidence examCoincidence) {
        this.examCoincidence = examCoincidence;
    }

    public ExamBefore getExamBefore() {
        return examBefore;
    }

    public void setExamBefore(ExamBefore examBefore) {
        this.examBefore = examBefore;
    }

    @PlanningVariable(valueRangeProviderRefs = {"periodRange"})
    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @PlanningVariable(valueRangeProviderRefs = {"roomRange"})
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public boolean isCoincidenceLeader() {
        return examCoincidence == null
                || examCoincidence.getFirstExam() == this;
    }

    public int getTopicDuration() {
        return getTopic().getDuration();
    }

    public int getTopicStudentSize() {
        return getTopic().getStudentSize();
    }

    public int getDayIndex() {
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getDayIndex();
    }

    public int getPeriodIndex() {
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getPeriodIndex();
    }

    public int getPeriodDuration() {
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getDuration();
    }

    public boolean isTopicFrontLoadLarge() {
        return topic.isFrontLoadLarge();
    }

    public boolean isPeriodFrontLoadLast() {
        if (period == null) {
            return false;
        }
        return period.isFrontLoadLast();
    }

    public Exam planningClone() {
        Exam clone = new Exam();
        clone.id = id;
        clone.topic = topic;
        clone.period = period;
        clone.room = room;
        // TODO FIXME examCoincidence and examBefore should be deep cloned
        return clone;
    }

    public String getLabel() {
        return Long.toString(topic.getId());
    }

    @Override
    public String toString() {
        return topic + " @ " + period + " + " + room;
    }

}
