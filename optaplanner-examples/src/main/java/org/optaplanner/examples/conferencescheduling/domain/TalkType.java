package org.optaplanner.examples.conferencescheduling.domain;

import java.util.Set;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class TalkType extends AbstractPersistable {

    private String name;

    private Set<Timeslot> compatibleTimeslotSet;
    private Set<Room> compatibleRoomSet;

    public TalkType() {
    }

    public TalkType(long id) {
        super(id);
    }

    public TalkType(long id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Timeslot> getCompatibleTimeslotSet() {
        return compatibleTimeslotSet;
    }

    public void setCompatibleTimeslotSet(Set<Timeslot> compatibleTimeslotSet) {
        this.compatibleTimeslotSet = compatibleTimeslotSet;
    }

    public Set<Room> getCompatibleRoomSet() {
        return compatibleRoomSet;
    }

    public void setCompatibleRoomSet(Set<Room> compatibleRoomSet) {
        this.compatibleRoomSet = compatibleRoomSet;
    }

}
