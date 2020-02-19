/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("RoomPenalty")
public class RoomPenalty extends AbstractPersistable {

    private RoomPenaltyType roomPenaltyType;
    private Topic topic;

    public RoomPenaltyType getRoomPenaltyType() {
        return roomPenaltyType;
    }

    public void setRoomPenaltyType(RoomPenaltyType roomPenaltyType) {
        this.roomPenaltyType = roomPenaltyType;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return roomPenaltyType + "@" + topic.getId();
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public RoomPenalty withId(long id) {
        this.setId(id);
        return this;
    }

    public RoomPenalty withRoomPenaltyType(RoomPenaltyType type) {
        this.setRoomPenaltyType(type);
        return this;
    }

    public RoomPenalty withTopic(Topic topic) {
        this.setTopic(topic);
        return this;
    }

}
