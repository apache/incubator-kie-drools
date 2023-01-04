package org.optaplanner.examples.nqueens.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.nqueens.domain.solver.QueenDifficultyWeightFactory;
import org.optaplanner.examples.nqueens.domain.solver.RowStrengthWeightFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningEntity(difficultyWeightFactoryClass = QueenDifficultyWeightFactory.class)
public class Queen extends AbstractPersistable {

    private Column column;

    // Planning variables: changes during planning, between score calculations.
    private Row row;

    public Queen() {
    }

    public Queen(long id) {
        super(id);
    }

    public Queen(long id, Row row, Column column) {
        this(id);
        this.row = row;
        this.column = column;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    @PlanningVariable(strengthWeightFactoryClass = RowStrengthWeightFactory.class)
    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @JsonIgnore
    public int getColumnIndex() {
        return column.getIndex();
    }

    @JsonIgnore
    public int getRowIndex() {
        if (row == null) {
            return Integer.MIN_VALUE;
        }
        return row.getIndex();
    }

    @JsonIgnore
    public int getAscendingDiagonalIndex() {
        return (getColumnIndex() + getRowIndex());
    }

    @JsonIgnore
    public int getDescendingDiagonalIndex() {
        return (getColumnIndex() - getRowIndex());
    }

    @Override
    public String toString() {
        return "Queen-" + column.getIndex();
    }

}
