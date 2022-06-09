package org.optaplanner.core.impl.heuristic.move;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public class NotDoableDummyMove extends DummyMove {

    public NotDoableDummyMove() {
    }

    public NotDoableDummyMove(String code) {
        super(code);
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<TestdataSolution> scoreDirector) {
        return false;
    }

}
