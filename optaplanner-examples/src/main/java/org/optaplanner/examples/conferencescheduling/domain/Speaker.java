
package org.optaplanner.examples.conferencescheduling.domain;

import java.util.Set;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class Speaker extends AbstractPersistable {

    private String name;

    private Set<Timeslot> unavailableTimeslotSet;

    private Set<String> requiredTimeslotTagSet;
    private Set<String> preferredTimeslotTagSet;
    private Set<String> prohibitedTimeslotTagSet;
    private Set<String> undesiredTimeslotTagSet;
    private Set<String> requiredRoomTagSet;
    private Set<String> preferredRoomTagSet;
    private Set<String> prohibitedRoomTagSet;
    private Set<String> undesiredRoomTagSet;

    public Speaker() {
    }

    public Speaker(long id) {
        super(id);
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

    public Set<Timeslot> getUnavailableTimeslotSet() {
        return unavailableTimeslotSet;
    }

    public void setUnavailableTimeslotSet(Set<Timeslot> unavailableTimeslotSet) {
        this.unavailableTimeslotSet = unavailableTimeslotSet;
    }

    public Set<String> getRequiredTimeslotTagSet() {
        return requiredTimeslotTagSet;
    }

    public void setRequiredTimeslotTagSet(Set<String> requiredTimeslotTagSet) {
        this.requiredTimeslotTagSet = requiredTimeslotTagSet;
    }

    public Set<String> getPreferredTimeslotTagSet() {
        return preferredTimeslotTagSet;
    }

    public void setPreferredTimeslotTagSet(Set<String> preferredTimeslotTagSet) {
        this.preferredTimeslotTagSet = preferredTimeslotTagSet;
    }

    public Set<String> getProhibitedTimeslotTagSet() {
        return prohibitedTimeslotTagSet;
    }

    public void setProhibitedTimeslotTagSet(Set<String> prohibitedTimeslotTagSet) {
        this.prohibitedTimeslotTagSet = prohibitedTimeslotTagSet;
    }

    public Set<String> getUndesiredTimeslotTagSet() {
        return undesiredTimeslotTagSet;
    }

    public void setUndesiredTimeslotTagSet(Set<String> undesiredTimeslotTagSet) {
        this.undesiredTimeslotTagSet = undesiredTimeslotTagSet;
    }

    public Set<String> getRequiredRoomTagSet() {
        return requiredRoomTagSet;
    }

    public void setRequiredRoomTagSet(Set<String> requiredRoomTagSet) {
        this.requiredRoomTagSet = requiredRoomTagSet;
    }

    public Set<String> getPreferredRoomTagSet() {
        return preferredRoomTagSet;
    }

    public void setPreferredRoomTagSet(Set<String> preferredRoomTagSet) {
        this.preferredRoomTagSet = preferredRoomTagSet;
    }

    public Set<String> getProhibitedRoomTagSet() {
        return prohibitedRoomTagSet;
    }

    public void setProhibitedRoomTagSet(Set<String> prohibitedRoomTagSet) {
        this.prohibitedRoomTagSet = prohibitedRoomTagSet;
    }

    public Set<String> getUndesiredRoomTagSet() {
        return undesiredRoomTagSet;
    }

    public void setUndesiredRoomTagSet(Set<String> undesiredRoomTagSet) {
        this.undesiredRoomTagSet = undesiredRoomTagSet;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public Speaker withUnavailableTimeslotSet(Set<Timeslot> unavailableTimeslotTest) {
        this.unavailableTimeslotSet = unavailableTimeslotTest;
        return this;
    }

    public Speaker withRequiredTimeslotTagSet(Set<String> requiredTimeslotTagSet) {
        this.requiredTimeslotTagSet = requiredTimeslotTagSet;
        return this;
    }

    public Speaker withPreferredTimeslotTagSet(Set<String> preferredTimeslotTagSet) {
        this.preferredTimeslotTagSet = preferredTimeslotTagSet;
        return this;
    }

    public Speaker withProhibitedTimeslotTagSet(Set<String> prohibitedTimeslotTagSet) {
        this.prohibitedTimeslotTagSet = prohibitedTimeslotTagSet;
        return this;
    }

    public Speaker withUndesiredTimeslotTagSet(Set<String> undesiredTimeslotTagSet) {
        this.undesiredTimeslotTagSet = undesiredTimeslotTagSet;
        return this;
    }

    public Speaker withRequiredRoomTagSet(Set<String> requiredRoomTagSet) {
        this.requiredRoomTagSet = requiredRoomTagSet;
        return this;
    }

    public Speaker withPreferredRoomTagSet(Set<String> preferredRoomTagSet) {
        this.preferredRoomTagSet = preferredRoomTagSet;
        return this;
    }

    public Speaker withUndesiredRoomTagSet(Set<String> undesiredRoomTagSet) {
        this.undesiredRoomTagSet = undesiredRoomTagSet;
        return this;
    }

    public Speaker withProhibitedRoomTagSet(Set<String> prohibitedRoomTagSet) {
        this.prohibitedRoomTagSet = prohibitedRoomTagSet;
        return this;
    }
}
