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

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.rocktour.solver")
public class RockTourConstraintConfiguration extends AbstractPersistable {

    public static final String EARLY_LATE_BREAK_DRIVING_SECONDS = "Early late break driving seconds budget";
    public static final String NIGHT_DRIVING_SECONDS = "Night driving seconds budget";

    // HOS = Hours of service (in terms of driving regulations)
    public static final String HOS_WEEK_DRIVING_SECONDS_BUDGET = "HOS week driving seconds budget";
    public static final String HOS_WEEK_CONSECUTIVE_DRIVING_DAYS_BUDGET = "HOS week consecutive driving days budget";
    public static final String HOS_WEEK_REST_DAYS = "HOS week rest days";

    public static final String REQUIRED_SHOW = "Required show";
    public static final String UNASSIGNED_SHOW = "Unassigned show";

    public static final String REVENUE_OPPORTUNITY = "Revenue opportunity";
    public static final String DRIVING_TIME_TO_SHOW_PER_SECOND = "Driving time to show per second";
    public static final String DRIVING_TIME_TO_BUS_ARRIVAL_PER_SECOND = "Driving time to bus arrival per second";
    public static final String DELAY_SHOW_COST_PER_DAY = "Delay show cost per day";

    public static final String SHORTEN_DRIVING_TIME_PER_MILLISECOND_SQUARED = "Shorten driving time per millisecond squared";

    private long earlyLateBreakDrivingSecondsBudget = 1L * 60L * 60L;
    private long nightDrivingSecondsBudget = 7L * 60L * 60L;

    // HOS = Hours of service (in terms of driving regulations)
    private long hosWeekDrivingSecondsBudget = 50L * 60L * 60L;
    private int hosWeekConsecutiveDrivingDaysBudget = 7;
    private int hosWeekRestDays = 2;

    @ConstraintWeight(REQUIRED_SHOW)
    private HardMediumSoftLongScore requiredShow = HardMediumSoftLongScore.ofHard(1000);
    @ConstraintWeight(UNASSIGNED_SHOW)
    private HardMediumSoftLongScore unassignedShow = HardMediumSoftLongScore.ofHard(1);

    @ConstraintWeight(REVENUE_OPPORTUNITY)
    private HardMediumSoftLongScore revenueOpportunity = HardMediumSoftLongScore.ofMedium(1);
    @ConstraintWeight(DRIVING_TIME_TO_SHOW_PER_SECOND)
    private HardMediumSoftLongScore drivingTimeToShowPerSecond = HardMediumSoftLongScore.ofMedium(1);
    @ConstraintWeight(DRIVING_TIME_TO_BUS_ARRIVAL_PER_SECOND)
    private HardMediumSoftLongScore drivingTimeToBusArrivalPerSecond = HardMediumSoftLongScore.ZERO;
    @ConstraintWeight(DELAY_SHOW_COST_PER_DAY)
    private HardMediumSoftLongScore delayShowCostPerDay = HardMediumSoftLongScore.ofMedium(30);

    @ConstraintWeight(SHORTEN_DRIVING_TIME_PER_MILLISECOND_SQUARED)
    private HardMediumSoftLongScore shortenDrivingTimePerMillisecondSquared = HardMediumSoftLongScore.ofSoft(1);

    public RockTourConstraintConfiguration() {
    }

    public RockTourConstraintConfiguration(long id) {
        super(id);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

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

    public HardMediumSoftLongScore getRequiredShow() {
        return requiredShow;
    }

    public void setRequiredShow(HardMediumSoftLongScore requiredShow) {
        this.requiredShow = requiredShow;
    }

    public HardMediumSoftLongScore getUnassignedShow() {
        return unassignedShow;
    }

    public void setUnassignedShow(HardMediumSoftLongScore unassignedShow) {
        this.unassignedShow = unassignedShow;
    }

    public HardMediumSoftLongScore getRevenueOpportunity() {
        return revenueOpportunity;
    }

    public void setRevenueOpportunity(HardMediumSoftLongScore revenueOpportunity) {
        this.revenueOpportunity = revenueOpportunity;
    }

    public HardMediumSoftLongScore getDrivingTimeToShowPerSecond() {
        return drivingTimeToShowPerSecond;
    }

    public void setDrivingTimeToShowPerSecond(HardMediumSoftLongScore drivingTimeToShowPerSecond) {
        this.drivingTimeToShowPerSecond = drivingTimeToShowPerSecond;
    }

    public HardMediumSoftLongScore getDrivingTimeToBusArrivalPerSecond() {
        return drivingTimeToBusArrivalPerSecond;
    }

    public void setDrivingTimeToBusArrivalPerSecond(HardMediumSoftLongScore drivingTimeToBusArrivalPerSecond) {
        this.drivingTimeToBusArrivalPerSecond = drivingTimeToBusArrivalPerSecond;
    }

    public HardMediumSoftLongScore getDelayShowCostPerDay() {
        return delayShowCostPerDay;
    }

    public void setDelayShowCostPerDay(HardMediumSoftLongScore delayShowCostPerDay) {
        this.delayShowCostPerDay = delayShowCostPerDay;
    }

    public HardMediumSoftLongScore getShortenDrivingTimePerMillisecondSquared() {
        return shortenDrivingTimePerMillisecondSquared;
    }

    public void setShortenDrivingTimePerMillisecondSquared(HardMediumSoftLongScore shortenDrivingTimePerMillisecondSquared) {
        this.shortenDrivingTimePerMillisecondSquared = shortenDrivingTimePerMillisecondSquared;
    }

}
