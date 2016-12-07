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

package org.optaplanner.examples.scrabble.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.scrabble.domain.solver.ScrabbleWordAssignmentDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = ScrabbleWordAssignmentDifficultyComparator.class)
@XStreamAlias("ScrabbleWord")
public class ScrabbleWordAssignment extends AbstractPersistable {

    private ScrabbleSolution solution;
    private String word;

    @PlanningVariable(valueRangeProviderRefs = {"startCellRange"})
    private ScrabbleCell startCell;
    @PlanningVariable(valueRangeProviderRefs = {"directionRange"})
    private ScrabbleWordDirection direction;

    public ScrabbleSolution getSolution() {
        return solution;
    }

    public void setSolution(ScrabbleSolution solution) {
        this.solution = solution;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ScrabbleCell getStartCell() {
        return startCell;
    }

    public void setStartCell(ScrabbleCell startCell) {
        this.startCell = startCell;
    }

    public ScrabbleWordDirection getDirection() {
        return direction;
    }

    public void setDirection(ScrabbleWordDirection direction) {
        this.direction = direction;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getLabel() {
        return word;
    }

    public boolean isOutOfGrid() {
        if (direction == null || startCell == null) {
            return false;
        }
        switch (direction) {
            case HORIZONTAL:
                return startCell.getX() + word.length() > solution.getGridWidth();
            case VERTICAL:
                return startCell.getY() + word.length() > solution.getGridHeight();
            default:
                throw new IllegalStateException("The direction (" + direction + ") is not implemented.");
        }
    }

    /**
     * @return manhattan distance
     */
    public int getDistanceToCenter() {
        if (direction == null || startCell == null) {
            return 0;
        }
        int centerX = solution.getGridWidth() / 2;
        int centerY = solution.getGridHeight() / 2;
        int x;
        int y;
        switch (direction) {
            case HORIZONTAL:
                x = startCell.getX() + word.length() / 2;
                y = startCell.getY();
                break;
            case VERTICAL:
                x = startCell.getX();
                y = startCell.getY() + word.length() / 2;
                break;
            default:
                throw new IllegalStateException("The direction (" + direction + ") is not implemented.");
        }
        return Math.abs(centerX - x) + Math.abs(centerY - y);
    }

    @Override
    public String toString() {
        return word;
    }

}
