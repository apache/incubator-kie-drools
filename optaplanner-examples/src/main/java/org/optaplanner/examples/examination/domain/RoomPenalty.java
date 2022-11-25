package org.optaplanner.examples.examination.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

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
