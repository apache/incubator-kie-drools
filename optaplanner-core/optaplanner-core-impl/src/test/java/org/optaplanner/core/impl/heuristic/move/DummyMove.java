package org.optaplanner.core.impl.heuristic.move;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.util.CodeAssertable;

public class DummyMove extends AbstractMove<TestdataSolution> implements CodeAssertable {

    protected String code;

    public DummyMove() {
    }

    public DummyMove(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector<TestdataSolution> scoreDirector) {
        return true;
    }

    @Override
    public DummyMove createUndoMove(ScoreDirector<TestdataSolution> scoreDirector) {
        return new DummyMove("undo " + code);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<TestdataSolution> scoreDirector) {
        // do nothing
    }

    @Override
    public Collection<? extends TestdataSolution> getPlanningEntities() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends TestdataSolution> getPlanningValues() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return code;
    }

}
