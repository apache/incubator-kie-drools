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

package org.optaplanner.examples.rocktour.domain.solver;

import java.time.LocalDate;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockStandstill;
import org.optaplanner.examples.rocktour.domain.RockTimeOfDay;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

public class RockShowDateUpdatingVariableListener implements VariableListener<RockShow> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, RockShow show) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, RockShow show) {
        updateDate(scoreDirector, show);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, RockShow show) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, RockShow show) {
        updateDate(scoreDirector, show);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, RockShow show) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, RockShow show) {
        // Do nothing
    }

    protected void updateDate(ScoreDirector scoreDirector, RockShow sourceShow) {
        RockTourSolution solution = (RockTourSolution) scoreDirector.getWorkingSolution();

        RockStandstill previousStandstill = sourceShow.getPreviousStandstill();
        Pair<LocalDate, RockTimeOfDay> arrival = calculateArrival(solution, sourceShow, previousStandstill);
        RockShow shadowShow = sourceShow;
        while (shadowShow != null
                && !(Objects.equals(shadowShow.getDate(), arrival.getLeft())
                    && Objects.equals(shadowShow.getTimeOfDay(), arrival.getRight()))) {
            scoreDirector.beforeVariableChanged(shadowShow, "date");
            shadowShow.setDate(arrival.getLeft());
            scoreDirector.afterVariableChanged(shadowShow, "date");
            scoreDirector.beforeVariableChanged(shadowShow, "timeOfDay");
            shadowShow.setTimeOfDay(arrival.getRight());
            scoreDirector.afterVariableChanged(shadowShow, "timeOfDay");
            RockShow previousShow = shadowShow;
            shadowShow = shadowShow.getNextShow();
            arrival = calculateArrival(solution, shadowShow, previousShow);
        }
    }

    private Pair<LocalDate, RockTimeOfDay> calculateArrival(RockTourSolution solution, RockShow show, RockStandstill previousStandstill) {
        if (show == null || previousStandstill == null || previousStandstill.getDepartureDate() == null) {
            return Pair.of(null, null);
        }
        long earlyLateBreakDrivingSecondsBudget = solution.getParametrization().getEarlyLateBreakDrivingSecondsBudget();
        long nightDrivingSecondsBudget = solution.getParametrization().getNightDrivingSecondsBudget();

        long drivingSeconds = show.getDrivingTimeFromPreviousStandstill();
        RockTimeOfDay timeOfDay = previousStandstill.getDepartureTimeOfDay();
        LocalDate date = previousStandstill.getDepartureDate();
        while (drivingSeconds >= 0) {
            if (timeOfDay == RockTimeOfDay.EARLY) {
                drivingSeconds -= earlyLateBreakDrivingSecondsBudget;
                timeOfDay = RockTimeOfDay.LATE;
            } else {
                drivingSeconds -= nightDrivingSecondsBudget;
                date = date.plusDays(1);
                timeOfDay = RockTimeOfDay.EARLY;
            }
        }
        if (show.getDurationInHalfDay() % 2 == 0 && timeOfDay != RockTimeOfDay.EARLY) {
            // Don't split up full days
            date = date.plusDays(1);
            timeOfDay = RockTimeOfDay.EARLY;
        }
        LocalDate arrivalDate = show.getAvailableDateSet().ceiling(date);
        if (!date.equals(arrivalDate)) {
            timeOfDay = RockTimeOfDay.EARLY;
        }
        if (arrivalDate == null || arrivalDate.compareTo(show.getBus().getEndDate()) >= 0) {
            return Pair.of(null, null);
        }
        return Pair.of(arrivalDate, timeOfDay);
    }

}
