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
    private String talkType;

    private Set<String> tagSet;

    public Timeslot() {
    }

    public Timeslot(long id) {
        super(id);
    }

    public LocalDate getDate() {
        return startDateTime.toLocalDate();
    }

    public long getDurationInMinutes() {
        return Duration.between(startDateTime, endDateTime).toMinutes();
    }

    public boolean overlaps(Timeslot other) {
        if (this == other) {
            return true;
        }
        return startDateTime.compareTo(other.endDateTime) < 0
                && other.startDateTime.compareTo(endDateTime) < 0;
    }

    public boolean hasTag(String tag) {
        return tagSet.contains(tag);
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
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getTalkType() {
        return talkType;
    }

    public void setTalkType(String talkType) {
        this.talkType = talkType;
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

    public Timeslot withTalkType(String talkType) {
        this.talkType = talkType;
        return this;
    }

    public Timeslot withStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public Timeslot withEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

}
