/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.examination.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.swingui.components.Labeled;
import org.optaplanner.examples.examination.domain.solver.ExamDifficultyWeightFactory;
import org.optaplanner.examples.examination.domain.solver.RoomStrengthWeightFactory;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@PlanningEntity(difficultyWeightFactoryClass = ExamDifficultyWeightFactory.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LeadingExam.class, name = "leading"),
        @JsonSubTypes.Type(value = FollowingExam.class, name = "following"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public abstract class Exam extends AbstractPersistable implements Labeled {

    protected Topic topic;

    // Planning variables: changes during planning, between score calculations.
    protected Room room;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @PlanningVariable(strengthWeightFactoryClass = RoomStrengthWeightFactory.class)
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public abstract Period getPeriod();

    @JsonIgnore
    public int getTopicDuration() {
        return getTopic().getDuration();
    }

    @JsonIgnore
    public int getTopicStudentSize() {
        return getTopic().getStudentSize();
    }

    @JsonIgnore
    public int getDayIndex() {
        Period period = getPeriod();
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getDayIndex();
    }

    @JsonIgnore
    public int getPeriodIndex() {
        Period period = getPeriod();
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getPeriodIndex();
    }

    @JsonIgnore
    public int getPeriodDuration() {
        Period period = getPeriod();
        if (period == null) {
            return Integer.MIN_VALUE;
        }
        return period.getDuration();
    }

    @JsonIgnore
    public boolean isTopicFrontLoadLarge() {
        return topic.isFrontLoadLarge();
    }

    @JsonIgnore
    public boolean isPeriodFrontLoadLast() {
        Period period = getPeriod();
        if (period == null) {
            return false;
        }
        return period.isFrontLoadLast();
    }

    @Override
    public String getLabel() {
        return Long.toString(topic.getId());
    }

    @Override
    public String toString() {
        return topic.toString();
    }

}
