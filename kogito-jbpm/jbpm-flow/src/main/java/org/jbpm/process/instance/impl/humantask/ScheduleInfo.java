/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.impl.humantask;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

public class ScheduleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Duration duration;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int numRepetitions;

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public int getNumRepetitions() {
        return numRepetitions;
    }

    public void setNumRepetitions(int numRepetions) {
        this.numRepetitions = numRepetions;
    }

    @Override
    public String toString() {
        return "ScheduleInfo [duration=" + duration + ", startDate=" + startDate + ", endDate=" + endDate +
                ", numRepetions=" + numRepetitions + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, endDate, numRepetitions, startDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ScheduleInfo))
            return false;
        ScheduleInfo other = (ScheduleInfo) obj;
        return Objects.equals(duration, other.duration) && Objects.equals(endDate, other.endDate) &&
                numRepetitions == other.numRepetitions && Objects.equals(startDate, other.startDate);
    }

}
