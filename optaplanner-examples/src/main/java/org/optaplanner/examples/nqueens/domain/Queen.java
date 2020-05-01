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

package org.optaplanner.examples.nqueens.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.nqueens.domain.solution.QueenDifficultyWeightFactory;
import org.optaplanner.examples.nqueens.domain.solution.RowStrengthWeightFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity(difficultyWeightFactoryClass = QueenDifficultyWeightFactory.class)
@XStreamAlias("Queen")
public class Queen extends AbstractPersistable {

    private Column column;

    // Planning variables: changes during planning, between score calculations.
    private Row row;

    public Queen() {
    }

    public Queen(long id, Row row, Column column) {
        super(id);
        this.row = row;
        this.column = column;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    @PlanningVariable(valueRangeProviderRefs = { "rowRange" }, strengthWeightFactoryClass = RowStrengthWeightFactory.class)
    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getColumnIndex() {
        return column.getIndex();
    }

    public int getRowIndex() {
        if (row == null) {
            return Integer.MIN_VALUE;
        }
        return row.getIndex();
    }

    public int getAscendingDiagonalIndex() {
        return (getColumnIndex() + getRowIndex());
    }

    public int getDescendingDiagonalIndex() {
        return (getColumnIndex() - getRowIndex());
    }

    @Override
    public String toString() {
        return "Queen-" + column.getIndex();
    }

}
