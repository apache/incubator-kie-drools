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

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockStandstill;

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
        RockStandstill previousStandstill = sourceShow.getPreviousStandstill();
        LocalDate arrivalDate = calculateArrivalDate(sourceShow,
                previousStandstill == null ? null : previousStandstill.getDepartureDate());
        RockShow shadowShow = sourceShow;
        while (shadowShow != null && !Objects.equals(shadowShow.getDate(), arrivalDate)) {
            scoreDirector.beforeVariableChanged(shadowShow, "date");
            shadowShow.setDate(arrivalDate);
            scoreDirector.afterVariableChanged(shadowShow, "date");
            LocalDate previousDepartureDate = shadowShow.getDepartureDate();
            shadowShow = shadowShow.getNextShow();
            arrivalDate = calculateArrivalDate(shadowShow, previousDepartureDate);
        }
    }

    private LocalDate calculateArrivalDate(RockShow show, LocalDate previousDepartureDate) {
        if (show == null || previousDepartureDate == null) {
            return null;
        }
        LocalDate arrivalDate = previousDepartureDate.plusDays(1);
        arrivalDate = show.getAvailableDateSet().ceiling(arrivalDate);
        return arrivalDate;
    }

}
