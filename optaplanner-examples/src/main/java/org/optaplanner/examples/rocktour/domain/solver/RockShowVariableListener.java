/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.rocktour.domain.solver;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.util.Objects;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockStandstill;
import org.optaplanner.examples.rocktour.domain.RockTimeOfDay;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

public class RockShowVariableListener implements VariableListener<RockTourSolution, RockShow> {

    @Override
    public void beforeEntityAdded(ScoreDirector<RockTourSolution> scoreDirector, RockShow show) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<RockTourSolution> scoreDirector, RockShow show) {
        updateDate(scoreDirector, show);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<RockTourSolution> scoreDirector, RockShow show) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<RockTourSolution> scoreDirector, RockShow show) {
        updateDate(scoreDirector, show);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<RockTourSolution> scoreDirector, RockShow show) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<RockTourSolution> scoreDirector, RockShow show) {
        // Do nothing
    }

    protected void updateDate(ScoreDirector<RockTourSolution> scoreDirector, RockShow sourceShow) {
        RockTourSolution solution = scoreDirector.getWorkingSolution();

        RockStandstill previousStandstill = sourceShow.getPreviousStandstill();
        Arrival arrival = calculateArrival(solution, sourceShow, previousStandstill);
        RockShow shadowShow = sourceShow;
        while (shadowShow != null
                && !(Objects.equals(shadowShow.getDate(), arrival.date)
                        && Objects.equals(shadowShow.getTimeOfDay(), arrival.timeOfDay)
                        && shadowShow.getHosWeekStart() == arrival.hosWeekStart
                        && Objects.equals(shadowShow.getHosWeekDrivingSecondsTotal(), arrival.hosWeekDrivingSecondsTotal))) {
            scoreDirector.beforeVariableChanged(shadowShow, "date");
            shadowShow.setDate(arrival.date);
            scoreDirector.afterVariableChanged(shadowShow, "date");
            scoreDirector.beforeVariableChanged(shadowShow, "timeOfDay");
            shadowShow.setTimeOfDay(arrival.timeOfDay);
            scoreDirector.afterVariableChanged(shadowShow, "timeOfDay");
            scoreDirector.beforeVariableChanged(shadowShow, "hosWeekStart");
            shadowShow.setHosWeekStart(arrival.hosWeekStart);
            scoreDirector.afterVariableChanged(shadowShow, "hosWeekStart");
            scoreDirector.beforeVariableChanged(shadowShow, "hosWeekDrivingSecondsTotal");
            shadowShow.setHosWeekDrivingSecondsTotal(arrival.hosWeekDrivingSecondsTotal);
            scoreDirector.afterVariableChanged(shadowShow, "hosWeekDrivingSecondsTotal");

            RockShow previousShow = shadowShow;
            shadowShow = shadowShow.getNextShow();
            arrival = calculateArrival(solution, shadowShow, previousShow);
        }
    }

    private Arrival calculateArrival(RockTourSolution solution, RockShow show, RockStandstill previousStandstill) {
        if (show == null || previousStandstill == null || previousStandstill.getDepartureDate() == null
                || previousStandstill.getHosWeekStart().getHosWeekStart() == null) {
            return new Arrival(null, null, null, null);
        }

        // HOS = Hours of service (in terms of driving regulations)
        long earlyLateBreakDrivingSecondsBudget = solution.getConstraintConfiguration().getEarlyLateBreakDrivingSecondsBudget();
        long nightDrivingSecondsBudget = solution.getConstraintConfiguration().getNightDrivingSecondsBudget();
        long hosWeekDrivingSecondsBudget = solution.getConstraintConfiguration().getHosWeekDrivingSecondsBudget();
        int hosWeekConsecutiveDrivingDaysBudget = solution.getConstraintConfiguration()
                .getHosWeekConsecutiveDrivingDaysBudget();
        int hosWeekRestDays = solution.getConstraintConfiguration().getHosWeekRestDays();

        RockTimeOfDay timeOfDay = previousStandstill.getDepartureTimeOfDay();
        LocalDate arrivalDate = previousStandstill.getDepartureDate();
        RockStandstill hosWeekStart = previousStandstill.getHosWeekStart();
        Long hosWeekDrivingSecondsTotal = previousStandstill.getHosWeekDrivingSecondsTotal();

        long drivingSeconds = show.getDrivingTimeFromPreviousStandstill();
        // HOS driving time per day limits
        while (drivingSeconds >= 0) {
            if (timeOfDay == RockTimeOfDay.EARLY) {
                drivingSeconds -= earlyLateBreakDrivingSecondsBudget;
                timeOfDay = RockTimeOfDay.LATE;
            } else {
                drivingSeconds -= nightDrivingSecondsBudget;
                arrivalDate = arrivalDate.plusDays(1);
                timeOfDay = RockTimeOfDay.EARLY;
            }
        }

        hosWeekDrivingSecondsTotal += show.getDrivingTimeFromPreviousStandstill();

        // HOS driving time per week limits: add weekend rest period if driving for too many hour or too many days
        if (hosWeekDrivingSecondsTotal > hosWeekDrivingSecondsBudget
                || hosWeekStart.getDepartureDate().until(arrivalDate, DAYS) > hosWeekConsecutiveDrivingDaysBudget) {
            arrivalDate = arrivalDate.plusDays(hosWeekRestDays);
            hosWeekStart = show;
            hosWeekDrivingSecondsTotal -= hosWeekDrivingSecondsBudget;
            timeOfDay = RockTimeOfDay.EARLY;
        }
        if (show.getDurationInHalfDay() % 2 == 0 && timeOfDay != RockTimeOfDay.EARLY) {
            // Don't split up full days
            arrivalDate = arrivalDate.plusDays(1);
            timeOfDay = RockTimeOfDay.EARLY;
        }
        // Fast forward to next available date of venue
        LocalDate showDate = show.getAvailableDateSet().ceiling(arrivalDate);
        if (showDate == null || showDate.compareTo(show.getBus().getEndDate()) >= 0) {
            return new Arrival(null, null, null, null);
        }
        if (!arrivalDate.equals(showDate) && timeOfDay != RockTimeOfDay.EARLY) {
            // If the show date is later than the arrival date, reset back to the early time of day
            timeOfDay = RockTimeOfDay.EARLY;
        }
        // HOS driving time per week limits: reset week
        if (arrivalDate.until(showDate, DAYS) >= 2) {
            hosWeekStart = show;
            hosWeekDrivingSecondsTotal = 0L;
        }
        return new Arrival(showDate, timeOfDay, hosWeekStart, hosWeekDrivingSecondsTotal);
    }

    private static final class Arrival {

        public final LocalDate date;
        public final RockTimeOfDay timeOfDay;
        public final RockStandstill hosWeekStart;
        public final Long hosWeekDrivingSecondsTotal;

        public Arrival(LocalDate date, RockTimeOfDay timeOfDay, RockStandstill hosWeekStart, Long hosWeekDrivingSecondsTotal) {
            this.date = date;
            this.timeOfDay = timeOfDay;
            this.hosWeekStart = hosWeekStart;
            this.hosWeekDrivingSecondsTotal = hosWeekDrivingSecondsTotal;
        }
    }

}
