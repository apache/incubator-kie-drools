/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.conferencescheduling.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class Timeslot extends AbstractPersistable {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private Set<TalkType> talkTypeSet;
    private Set<String> tagSet;

    // Cached
    private Integer durationInMinutes;

    public Timeslot() {
    }

    public Timeslot(long id) {
        super(id);
    }

    public LocalDate getDate() {
        return startDateTime.toLocalDate();
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public boolean overlapsTime(Timeslot other) {
        if (this == other) {
            return true;
        }
        return startDateTime.compareTo(other.endDateTime) < 0
                && other.startDateTime.compareTo(endDateTime) < 0;
    }

    public int getOverlapInMinutes(Timeslot other) {
        if (this == other) {
            return durationInMinutes;
        }
        LocalDateTime startMaximum = (startDateTime.compareTo(other.startDateTime) < 0) ? other.startDateTime : startDateTime;
        LocalDateTime endMinimum = (endDateTime.compareTo(other.endDateTime) < 0) ? endDateTime : other.endDateTime;
        return (int) Duration.between(startMaximum, endMinimum).toMinutes();
    }

    public boolean startsAfter(Timeslot other) {
        return other.endDateTime.compareTo(startDateTime) <= 0;
    }

    public boolean endsBefore(Timeslot other) {
        return endDateTime.compareTo(other.startDateTime) <= 0;
    }

    public boolean hasTag(String tag) {
        return tagSet.contains(tag);
    }

    public boolean isOnSameDayAs(Timeslot other) {
        return startDateTime.toLocalDate().equals(other.getStartDateTime().toLocalDate());
    }

    public boolean pauseExists(Timeslot other, int pauseInMinutes) {
        if (this.overlapsTime(other)) {
            return false;
        }
        if (!this.isOnSameDayAs(other)) {
            return true;
        }
        if (this.startsAfter(other)) {
            // TODO use Duration.between(a, b).toMinutes()
            return (this.getStartDateTime().getHour() * 60 + this.getStartDateTime().getMinute())
                    - (other.getEndDateTime().getHour() * 60 + other.getEndDateTime().getMinute()) >= pauseInMinutes;
        } else {
            // TODO use Duration.between(a, b).toMinutes()
            return (other.getStartDateTime().getHour() * 60 + other.getStartDateTime().getMinute())
                    - (this.getEndDateTime().getHour() * 60 + this.getEndDateTime().getMinute()) >= pauseInMinutes;
        }
    }

    @Override
    public String toString() {
        return startDateTime + "-" + endDateTime.toLocalTime();
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        durationInMinutes = (startDateTime == null || endDateTime == null) ? null
                : (int) Duration.between(startDateTime, endDateTime).toMinutes();
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
        durationInMinutes = (startDateTime == null || endDateTime == null) ? null
                : (int) Duration.between(startDateTime, endDateTime).toMinutes();
    }

    public Set<TalkType> getTalkTypeSet() {
        return talkTypeSet;
    }

    public void setTalkTypeSet(Set<TalkType> talkTypeSet) {
        this.talkTypeSet = talkTypeSet;
    }

    public Set<String> getTagSet() {
        return tagSet;
    }

    public void setTagSet(Set<String> tagSet) {
        this.tagSet = tagSet;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public Timeslot withTalkTypeSet(Set<TalkType> talkTypeSet) {
        this.talkTypeSet = talkTypeSet;
        return this;
    }

    public Timeslot withStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        durationInMinutes = (startDateTime == null || endDateTime == null) ? null
                : (int) Duration.between(startDateTime, endDateTime).toMinutes();
        return this;
    }

    public Timeslot withEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
        durationInMinutes = (startDateTime == null || endDateTime == null) ? null
                : (int) Duration.between(startDateTime, endDateTime).toMinutes();
        return this;
    }
}
