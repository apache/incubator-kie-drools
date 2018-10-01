/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.rocktour.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class RockTourParametrization extends AbstractPersistable {

    public static final String EARLY_LATE_BREAK_DRIVING_SECONDS = "Early late break driving seconds budget";
    public static final String NIGHT_DRIVING_SECONDS = "Night driving seconds budget";

    // HOS = Hours of service (in terms of driving regulations)
    public static final String HOS_WEEK_DRIVING_SECONDS_BUDGET = "HOS week driving seconds budget";
    public static final String HOS_WEEK_CONSECUTIVE_DRIVING_DAYS_BUDGET = "HOS week consecutive driving days budget";
    public static final String HOS_WEEK_REST_DAYS = "HOS week rest days";

    public static final String MISSED_SHOW_PENALTY = "Minimize missed shows";
    public static final String REVENUE_OPPORTUNITY = "Maximize revenue opportunity";
    public static final String DRIVING_TIME_COST_PER_SECOND = "Minimize driving time cost";
    public static final String DELAY_COST_PER_DAY = "Visit sooner than later";

    private long earlyLateBreakDrivingSecondsBudget = 1L * 60L * 60L;
    private long nightDrivingSecondsBudget = 7L * 60L * 60L;

    // HOS = Hours of service (in terms of driving regulations)
    private long hosWeekDrivingSecondsBudget = 50L * 60L * 60L;
    private int hosWeekConsecutiveDrivingDaysBudget = 7;
    private int hosWeekRestDays = 2;

    private long missedShowPenalty = 0;
    private long revenueOpportunity = 1;
    private long drivingTimeCostPerSecond = 1;
    private long delayCostPerDay = 30;

    public RockTourParametrization() {
    }

    public RockTourParametrization(long id) {
        super(id);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public long getMissedShowPenalty() {
        return missedShowPenalty;
    }

    public void setMissedShowPenalty(long missedShowPenalty) {
        this.missedShowPenalty = missedShowPenalty;
    }

    public long getEarlyLateBreakDrivingSecondsBudget() {
        return earlyLateBreakDrivingSecondsBudget;
    }

    public void setEarlyLateBreakDrivingSecondsBudget(long earlyLateBreakDrivingSecondsBudget) {
        this.earlyLateBreakDrivingSecondsBudget = earlyLateBreakDrivingSecondsBudget;
    }

    public long getNightDrivingSecondsBudget() {
        return nightDrivingSecondsBudget;
    }

    public void setNightDrivingSecondsBudget(long nightDrivingSecondsBudget) {
        this.nightDrivingSecondsBudget = nightDrivingSecondsBudget;
    }

    public long getHosWeekDrivingSecondsBudget() {
        return hosWeekDrivingSecondsBudget;
    }

    public void setHosWeekDrivingSecondsBudget(long hosWeekDrivingSecondsBudget) {
        this.hosWeekDrivingSecondsBudget = hosWeekDrivingSecondsBudget;
    }

    public int getHosWeekConsecutiveDrivingDaysBudget() {
        return hosWeekConsecutiveDrivingDaysBudget;
    }

    public void setHosWeekConsecutiveDrivingDaysBudget(int hosWeekConsecutiveDrivingDaysBudget) {
        this.hosWeekConsecutiveDrivingDaysBudget = hosWeekConsecutiveDrivingDaysBudget;
    }

    public int getHosWeekRestDays() {
        return hosWeekRestDays;
    }

    public void setHosWeekRestDays(int hosWeekRestDays) {
        this.hosWeekRestDays = hosWeekRestDays;
    }

    public long getRevenueOpportunity() {
        return revenueOpportunity;
    }

    public void setRevenueOpportunity(long revenueOpportunity) {
        this.revenueOpportunity = revenueOpportunity;
    }

    public long getDrivingTimeCostPerSecond() {
        return drivingTimeCostPerSecond;
    }

    public void setDrivingTimeCostPerSecond(long drivingTimeCostPerSecond) {
        this.drivingTimeCostPerSecond = drivingTimeCostPerSecond;
    }

    public long getDelayCostPerDay() {
        return delayCostPerDay;
    }

    public void setDelayCostPerDay(long delayCostPerDay) {
        this.delayCostPerDay = delayCostPerDay;
    }

}
