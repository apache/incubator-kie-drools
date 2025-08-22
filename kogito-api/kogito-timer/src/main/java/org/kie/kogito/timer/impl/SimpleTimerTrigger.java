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
package org.kie.kogito.timer.impl;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.kie.kogito.timer.Trigger;

/**
 * The SimpleTimerTrigger is designed to work as a "timer", that executes at an initial startTime, and then,
 * depending on the configuration parameters it might be repeated a given number of times, this last is indicated by
 * the repeatCount configuration parameter. The initial execution is not considered a repetition. Additionally, an
 * optional endTime can be provided to configure a time based limit for the executions. If configured, when the endTime
 * is reached no more execution will be produced independently of the repeatCount value.
 * e.g:
 * when
 * repeatCount == 0, the timer is executed one time at the startTime.
 * repeatCount == 1, executes two times.
 * repeatCount == 2, executes three times.
 * repeatCount == -1, the timer es repeated forever.
 *
 * The fireTime for all the subsequent executions after the initial startTime, are calculated by considering the
 * timer period and the periodUnit.
 *
 */
public class SimpleTimerTrigger implements Trigger {

    public static final int INDEFINITELY = -1;

    private static Set<ChronoUnit> acceptedUnits() {
        Set<ChronoUnit> units = new TreeSet<>(Enum::compareTo);
        units.add(ChronoUnit.MILLIS);
        units.add(ChronoUnit.SECONDS);
        units.add(ChronoUnit.MINUTES);
        units.add(ChronoUnit.HOURS);
        units.add(ChronoUnit.DAYS);
        return units;
    }

    private static final Set<ChronoUnit> ACCEPTED_CHRONO_UNITS = acceptedUnits();

    private Date startTime;

    private long period;

    private ChronoUnit periodUnit = ChronoUnit.MILLIS;

    private int repeatCount;

    private Date endTime;

    private String zoneId;

    private int currentRepeatCount;

    private Date nextFireTime;

    private boolean endTimeReached;

    public SimpleTimerTrigger() {
        // Marshalling constructor.
    }

    /**
     * @param startTime The trigger start time.
     * @param period The period for the calculation of the subsequent executions.
     * @param periodUnit The time unit in which the period is expressed. For, example, a period of 2 ChronoUnit.SECONDS.
     * @param repeatCount Number of times the trigger should be repeated according to the configured period.
     *        repeatCount == 0, the timer is executed one time at the startTime.
     *        repeatCount == 1, executes two times, at startTime, and at (startTime + 2 seconds).
     *        repeatCount == 2, executes three times, at startTime, at (startTime + 2 seconds), and at (startTime + 4 seconds)
     *        repeatCount == -1, the timer is executed forever.
     *
     * @param endTime An optional value indicating a potential endTime. Independently of the configured repeatCount,
     *        a nextFireTime must never be after the endTime. In situations where the trigger reaches the endTime,
     *        all subsequent invocations of the hasNextFireTime and nextFireTime methods will return null.
     * @param zoneId An optional value indicating a java.time.ZoneId string representation.
     *        While all the trigger calculations and returned fire times are represented with the instant based
     *        java.util.Date, this value can be helpful in situations where the creator of the trigger
     *        wants to register the startTime's and endTime's original zoneId. This value could be used for example to
     *        translate the produced fireTimes to the original zone, however, these calculations are outside the scope
     *        of the trigger. This field must be considered as trigger metadata.
     */
    public SimpleTimerTrigger(Date startTime, long period, ChronoUnit periodUnit,
            int repeatCount, Date endTime, String zoneId) {
        validateStartTime(startTime);
        validatePeriod(period);
        validatePeriodUnit(periodUnit);
        validateRepeatCount(repeatCount);

        this.startTime = startTime;
        this.period = period;
        this.periodUnit = periodUnit;
        this.repeatCount = repeatCount;
        this.endTime = endTime;
        this.zoneId = zoneId;

        this.currentRepeatCount = repeatCount == INDEFINITELY ? -1 : 0;
        if (endTime != null && startTime.after(endTime)) {
            this.nextFireTime = null;
            this.endTimeReached = true;
        } else {
            this.nextFireTime = startTime;
        }
    }

    /**
     * @param startTime The trigger start time.
     * @param period The period for the calculation of the subsequent executions.
     * @param periodUnit The time unit in which the period is expressed. For, example, a period of 2 ChronoUnit.SECONDS.
     * @param repeatCount Number of times the trigger should be repeated according to the configured period.
     *        repeatCount == 0, the timer is executed one time at the startTime.
     *        repeatCount == 1, executes two times, at startTime, and at (startTime + 2 seconds).
     *        repeatCount == 2, executes three times, at startTime, at (startTime + 2 seconds), and at (startTime + 4 seconds)
     *        repeatCount == -1, the timer is executed forever.
     * 
     * @param zoneId An optional value indicating a java.time.ZoneId string representation.
     *        While all the trigger calculations and returned fire times are represented with the instant based
     *        java.util.Date, this value can be helpful in situations where the creator of the trigger
     *        wants to register the startTime's and endTime's original zoneId. This value could be used for example to
     *        translate the produced fireTimes to the original zone, however, these calculations are outside the scope
     *        of the trigger. This field must be considered as trigger metadata.
     */
    public SimpleTimerTrigger(Date startTime, long period, ChronoUnit periodUnit,
            int repeatCount, String zoneId) {
        this(startTime, period, periodUnit, repeatCount, null, zoneId);
    }

    @Override
    public Date hasNextFireTime() {
        return nextFireTime;
    }

    @Override
    public synchronized Date nextFireTime() {
        if (nextFireTime == null) {
            return null;
        }
        final Date current = nextFireTime;
        final Date candidateNextFireTime = new Date(current.getTime() + getPeriodInMillis());
        if (endTime != null && candidateNextFireTime.after(endTime)) {
            this.nextFireTime = null;
            this.endTimeReached = true;
        } else if (repeatCount == INDEFINITELY) {
            this.nextFireTime = candidateNextFireTime;
        } else if (currentRepeatCount == Integer.MAX_VALUE || ((currentRepeatCount + 1) > repeatCount)) {
            this.nextFireTime = null;
        } else {
            this.nextFireTime = candidateNextFireTime;
            currentRepeatCount++;
        }
        return current;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        validateStartTime(startTime);
        this.startTime = startTime;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        validatePeriod(period);
        this.period = period;
    }

    public ChronoUnit getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(ChronoUnit periodUnit) {
        validatePeriodUnit(periodUnit);
        this.periodUnit = periodUnit;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        validateRepeatCount(repeatCount);
        this.repeatCount = repeatCount;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return true if the endTime for the current trigger was reached.
     */
    public boolean isEndTimeReached() {
        return endTimeReached;
    }

    public void setEndTimeReached(boolean endTimeReached) {
        this.endTimeReached = endTimeReached;
    }

    public boolean isIndefinitely() {
        return repeatCount == INDEFINITELY;
    }

    /**
     * @return The number of executed repetitions for this timer, or -1 if the timer was configured with the indefinitely
     *         repeatCount = -1.
     */
    public int getCurrentRepeatCount() {
        return currentRepeatCount;
    }

    public void setCurrentRepeatCount(int currentRepeatCount) {
        this.currentRepeatCount = currentRepeatCount;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    private void validateStartTime(Date startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("The startTime must be a non null value.");
        }
    }

    private void validatePeriod(long period) {
        if (period < 0) {
            throw new IllegalArgumentException("The period must be greater or equal than zero, but is: " + period);
        }
    }

    private void validateRepeatCount(int repeatCount) {
        if (repeatCount < -1) {
            throw new IllegalArgumentException("The repeatCount must be greater or equal than zero, or -1 to indicate an indefinitely repeatCount, but is: " + repeatCount);
        }
    }

    private void validatePeriodUnit(ChronoUnit periodUnit) {
        if (periodUnit == null || !ACCEPTED_CHRONO_UNITS.contains(periodUnit)) {
            throw new IllegalArgumentException("The periodUnit must be one of the following values: " +
                    ACCEPTED_CHRONO_UNITS + ", but is: " + periodUnit);
        }
    }

    private long getPeriodInMillis() {
        return periodUnit.getDuration().multipliedBy(period).toMillis();
    }

    @Override
    public String toString() {
        return "SimpleTimerTrigger [startTime=" + startTime + ", period=" + period + ", periodUnit=" + periodUnit + ", repeatCount=" + repeatCount + ", endTime=" + endTime + "]";
    }
}
