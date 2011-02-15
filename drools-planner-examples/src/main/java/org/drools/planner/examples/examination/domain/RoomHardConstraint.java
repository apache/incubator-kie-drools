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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("RoomHardConstraint")
public class RoomHardConstraint extends AbstractPersistable implements Comparable<RoomHardConstraint> {

    private RoomHardConstraintType roomHardConstraintType;
    private Topic topic;

    public RoomHardConstraintType getRoomHardConstraintType() {
        return roomHardConstraintType;
    }

    public void setRoomHardConstraintType(RoomHardConstraintType roomHardConstraintType) {
        this.roomHardConstraintType = roomHardConstraintType;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public int compareTo(RoomHardConstraint other) {
        return new CompareToBuilder()
                .append(roomHardConstraintType, other.roomHardConstraintType)
                .append(topic, other.topic)
                .append(id, other.id)
                .toComparison();
    }

    @Override
    public String toString() {
        return roomHardConstraintType + "@" + topic.getId();
    }

}
