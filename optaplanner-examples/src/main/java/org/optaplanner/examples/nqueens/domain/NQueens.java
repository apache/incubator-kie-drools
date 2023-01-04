package org.optaplanner.examples.nqueens.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningSolution
public class NQueens extends AbstractPersistable {

    private int n;

    private List<Column> columnList;
    private List<Row> rowList;

    private List<Queen> queenList;

    private SimpleScore score;

    public NQueens() {
    }

    public NQueens(long id) {
        super(id);
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    @ProblemFactCollectionProperty
    public List<Column> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }

    @ValueRangeProvider
    @ProblemFactCollectionProperty
    public List<Row> getRowList() {
        return rowList;
    }

    public void setRowList(List<Row> rowList) {
        this.rowList = rowList;
    }

    @PlanningEntityCollectionProperty
    public List<Queen> getQueenList() {
        return queenList;
    }

    public void setQueenList(List<Queen> queenList) {
        this.queenList = queenList;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
