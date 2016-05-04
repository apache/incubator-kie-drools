/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.scrabble.domain.solver;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.scrabble.domain.ScrabbleSolution;
import org.optaplanner.examples.scrabble.domain.ScrabbleWord;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

public class CellUpdatingVariableListener implements VariableListener<ScrabbleWord> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, ScrabbleWord word) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, ScrabbleWord word) {
        insertWord((ScrabbleSolution) scoreDirector.getWorkingSolution(), word);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, ScrabbleWord word) {
        retractWord((ScrabbleSolution) scoreDirector.getWorkingSolution(), word);
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, ScrabbleWord word) {
        insertWord((ScrabbleSolution) scoreDirector.getWorkingSolution(), word);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, ScrabbleWord word) {
        retractWord((ScrabbleSolution) scoreDirector.getWorkingSolution(), word);
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, ScrabbleWord word) {
        // Do nothing
    }

    private void insertWord(ScrabbleSolution solution, ScrabbleWord word) {

    }

    private void retractWord(ScrabbleSolution solution, ScrabbleWord word) {

    }

//    protected void updateArrivalTime(ScoreDirector scoreDirector, TimeWindowedCustomer sourceCustomer) {
//        Standstill previousStandstill = sourceCustomer.getPreviousStandstill();
//        Long departureTime = (previousStandstill instanceof TimeWindowedCustomer)
//                ? ((TimeWindowedCustomer) previousStandstill).getDepartureTime() : null;
//        TimeWindowedCustomer shadowCustomer = sourceCustomer;
//        Long arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
//        while (shadowCustomer != null && !Objects.equals(shadowCustomer.getArrivalTime(), arrivalTime)) {
//            scoreDirector.beforeVariableChanged(shadowCustomer, "arrivalTime");
//            shadowCustomer.setArrivalTime(arrivalTime);
//            scoreDirector.afterVariableChanged(shadowCustomer, "arrivalTime");
//            departureTime = shadowCustomer.getDepartureTime();
//            shadowCustomer = shadowCustomer.getNextCustomer();
//            arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
//        }
//    }

}
