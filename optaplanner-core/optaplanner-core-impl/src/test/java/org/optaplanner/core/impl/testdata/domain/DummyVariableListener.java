package org.optaplanner.core.impl.testdata.domain;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public abstract class DummyVariableListener<Solution_, Entity_> implements VariableListener<Solution_, Entity_> {

    @Override
    public void beforeEntityAdded(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void afterVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Solution_> scoreDirector, Entity_ entity_) {
        // Nothing to do.
    }
}
