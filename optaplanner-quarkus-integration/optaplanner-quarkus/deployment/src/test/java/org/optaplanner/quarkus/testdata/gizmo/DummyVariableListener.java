package org.optaplanner.quarkus.testdata.gizmo;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public class DummyVariableListener implements VariableListener {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Object o) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Object o) {

    }
}
