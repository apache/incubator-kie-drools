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

package org.drools.planner.examples.nurserostering.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("ShiftDate")
public class ShiftDate extends AbstractPersistable implements Comparable<ShiftDate> {

    private int dayIndex;
    private String dateString;
    private DayOfWeek dayOfWeek;

    private List<Shift> shiftList;

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<Shift> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    public String getLabel() {
        return dateString.substring(5);
    }

    public int compareTo(ShiftDate other) {
        return new CompareToBuilder()
                .append(dayIndex, other.dayIndex)
                .toComparison();
    }

    @Override
    public String toString() {
        return dateString + "(" + dayOfWeek + ")";
    }

    public int getWeekendSundayIndex() {
        switch (dayOfWeek) {
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
                throw new IllegalArgumentException("The dayOfWeek (" + dayOfWeek + ") is not valid.");
        }
    }

}
