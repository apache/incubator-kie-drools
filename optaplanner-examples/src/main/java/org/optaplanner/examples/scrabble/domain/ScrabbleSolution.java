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

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoft.HardMediumSoftScoreXStreamConverter;

@PlanningSolution
@XStreamAlias("ScrabbleSolution")
public class ScrabbleSolution extends AbstractPersistable {

    private int gridWidth;
    private int gridHeight;

    @ValueRangeProvider(id = "startCellRange")
    @ProblemFactCollectionProperty
    private List<ScrabbleCell> cellList;

    @PlanningEntityCollectionProperty
    private List<ScrabbleWordAssignment> wordList;

    @XStreamConverter(HardMediumSoftScoreXStreamConverter.class)
    private HardMediumSoftScore score;

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public List<ScrabbleCell> getCellList() {
        return cellList;
    }

    public void setCellList(List<ScrabbleCell> cellList) {
        this.cellList = cellList;
    }

    public List<ScrabbleWordAssignment> getWordList() {
        return wordList;
    }

    public void setWordList(List<ScrabbleWordAssignment> wordList) {
        this.wordList = wordList;
    }

    @PlanningScore
    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ValueRangeProvider(id = "directionRange")
    public ScrabbleWordDirection[] getDirectionRange() {
        return ScrabbleWordDirection.values();
    }

    public ScrabbleCell getCell(int x, int y) {
        return cellList.get(x * gridWidth + y);
    }

}
