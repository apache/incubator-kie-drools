/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.nqueens.domain;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRangeFromSolutionProperty;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.nqueens.domain.solution.QueenDifficultyWeightFactory;
import org.drools.planner.examples.nqueens.domain.solution.RowStrengthWeightFactory;

@PlanningEntity(difficultyWeightFactoryClass = QueenDifficultyWeightFactory.class)
public class Queen extends AbstractPersistable {

    private Column column;

    // Planning variables: changes during planning, between score calculations.
    private Row row;

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    @PlanningVariable(strengthWeightFactoryClass = RowStrengthWeightFactory.class)
    @ValueRangeFromSolutionProperty(propertyName = "rowList")
    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public int getColumnIndex() {
        return column.getIndex();
    }

    public int getRowIndex() {
        if (row == null) {
            return -1;
        }
        return row.getIndex();
    }

    public int getAscendingDiagonalIndex() {
        return (getColumnIndex() + getRowIndex());
    }

    public int getDescendingDiagonalIndex() {
        return (getColumnIndex() - getRowIndex());
    }

    public Queen clone() {
        Queen clone = new Queen();
        clone.id = id;
        clone.column = column;
        clone.row = row;
        return clone;
    }

    @Override
    public String toString() {
        return column + "@" + row;
    }

}
