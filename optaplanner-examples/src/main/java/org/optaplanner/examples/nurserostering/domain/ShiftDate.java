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

package org.optaplanner.examples.nurserostering.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class ShiftDate extends AbstractPersistable implements Labeled, Comparable<ShiftDate> {

    private static final DateTimeFormatter LABEL_FORMATTER = DateTimeFormatter.ofPattern("E d MMM");

    private int dayIndex; // TODO check if still needed/faster now that we use LocalDate instead of java.util.Date
    private LocalDate date;

    private List<Shift> shiftList;

    public ShiftDate() {
    }

    public ShiftDate(long id) {
        super(id);
    }

    public ShiftDate(long id, int dayIndex, LocalDate date) {
        this(id);
        this.dayIndex = dayIndex;
        this.date = date;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @JsonIgnore
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    public List<Shift> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    @JsonIgnore
    public int getWeekendSundayIndex() {
        switch (date.getDayOfWeek()) {
            case MONDAY:
                return dayIndex - 1;
            case TUESDAY:
                return dayIndex - 2;
            case WEDNESDAY:
                return dayIndex - 3;
            case THURSDAY:
                return dayIndex + 3;
            case FRIDAY:
                return dayIndex + 2;
            case SATURDAY:
                return dayIndex + 1;
            case SUNDAY:
                return dayIndex;
            default:
                throw new IllegalArgumentException("The dayOfWeek (" + date.getDayOfWeek() + ") is not valid.");
        }
    }

    @Override
    public String getLabel() {
        return date.format(LABEL_FORMATTER);
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    @Override
    public int compareTo(ShiftDate o) {
        return this.getDate().compareTo(o.getDate());
    }
}
