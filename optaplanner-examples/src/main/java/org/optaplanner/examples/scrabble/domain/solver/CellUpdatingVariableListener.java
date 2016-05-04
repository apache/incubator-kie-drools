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

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.scrabble.domain.ScrabbleCell;
import org.optaplanner.examples.scrabble.domain.ScrabbleSolution;
import org.optaplanner.examples.scrabble.domain.ScrabbleWordAssignment;
import org.optaplanner.examples.scrabble.domain.ScrabbleWordDirection;

public class CellUpdatingVariableListener implements VariableListener<ScrabbleWordAssignment> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, ScrabbleWordAssignment wordAssignment) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, ScrabbleWordAssignment wordAssignment) {
        insertWord((ScrabbleSolution) scoreDirector.getWorkingSolution(), wordAssignment);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, ScrabbleWordAssignment wordAssignment) {
        retractWord((ScrabbleSolution) scoreDirector.getWorkingSolution(), wordAssignment);
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, ScrabbleWordAssignment wordAssignment) {
        insertWord((ScrabbleSolution) scoreDirector.getWorkingSolution(), wordAssignment);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, ScrabbleWordAssignment wordAssignment) {
        retractWord((ScrabbleSolution) scoreDirector.getWorkingSolution(), wordAssignment);
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, ScrabbleWordAssignment wordAssignment) {
        // Do nothing
    }

    private void insertWord(ScrabbleSolution solution, ScrabbleWordAssignment wordAssignment) {
        ScrabbleCell startCell = wordAssignment.getStartCell();
        ScrabbleWordDirection direction = wordAssignment.getDirection();
        if (startCell != null && direction != null) {
            int x = startCell.getX();
            int y = startCell.getY();
            String word = wordAssignment.getWord();
            for (int i = 0; i < word.length(); i++) {
                solution.getCell(x, y).insertWordAssignment(wordAssignment, word.charAt(i));
                switch (direction) {
                    case HORIZONTAL:
                        x++;
                        break;
                    case VERTICAL:
                        y++;
                        break;
                    default:
                        throw new IllegalStateException("The direction (" + direction + ") is not implemented.");

                }
                if (x >= solution.getGridWidth() || y >= solution.getGridHeight()) {
                    break;
                }
            }
        }
    }

    private void retractWord(ScrabbleSolution solution, ScrabbleWordAssignment wordAssignment) {
        ScrabbleCell startCell = wordAssignment.getStartCell();
        ScrabbleWordDirection direction = wordAssignment.getDirection();
        if (startCell != null && direction != null) {
            int x = startCell.getX();
            int y = startCell.getY();
            String word = wordAssignment.getWord();
            for (int i = 0; i < word.length(); i++) {
                solution.getCell(x, y).retractWordAssignment(wordAssignment, word.charAt(i));
                switch (direction) {
                    case HORIZONTAL:
                        x++;
                        break;
                    case VERTICAL:
                        y++;
                        break;
                    default:
                        throw new IllegalStateException("The direction (" + direction + ") is not implemented.");

                }
                if (x >= solution.getGridWidth() || y >= solution.getGridHeight()) {
                    break;
                }
            }
        }

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
