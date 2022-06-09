package org.optaplanner.core.impl.domain.variable;

import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

public interface ListVariableListener<Solution_, Entity_> extends AbstractVariableListener<Solution_, Entity_> {

    void beforeElementAdded(ScoreDirector<Solution_> scoreDirector, Entity_ entity, int index);

    void afterElementAdded(ScoreDirector<Solution_> scoreDirector, Entity_ entity, int index);

    void beforeElementRemoved(ScoreDirector<Solution_> scoreDirector, Entity_ entity, int index);

    void afterElementRemoved(ScoreDirector<Solution_> scoreDirector, Entity_ entity, int index);

    void beforeElementMoved(ScoreDirector<Solution_> scoreDirector,
            Entity_ sourceEntity, int sourceIndex,
            Entity_ destinationEntity, int destinationIndex);

    void afterElementMoved(ScoreDirector<Solution_> scoreDirector,
            Entity_ sourceEntity, int sourceIndex,
            Entity_ destinationEntity, int destinationIndex);
}
