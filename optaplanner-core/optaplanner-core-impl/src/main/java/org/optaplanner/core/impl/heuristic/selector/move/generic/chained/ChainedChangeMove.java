package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ChainedChangeMove<Solution_> extends ChangeMove<Solution_> {

    protected final Object oldTrailingEntity;
    protected final Object newTrailingEntity;

    public ChainedChangeMove(GenuineVariableDescriptor<Solution_> variableDescriptor, Object entity, Object toPlanningValue,
            SingletonInverseVariableSupply inverseVariableSupply) {
        super(variableDescriptor, entity, toPlanningValue);
        oldTrailingEntity = inverseVariableSupply.getInverseSingleton(entity);
        newTrailingEntity = toPlanningValue == null ? null
                : inverseVariableSupply.getInverseSingleton(toPlanningValue);
    }

    public ChainedChangeMove(GenuineVariableDescriptor<Solution_> variableDescriptor, Object entity, Object toPlanningValue,
            Object oldTrailingEntity, Object newTrailingEntity) {
        super(variableDescriptor, entity, toPlanningValue);
        this.oldTrailingEntity = oldTrailingEntity;
        this.newTrailingEntity = newTrailingEntity;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return super.isMoveDoable(scoreDirector)
                && !Objects.equals(entity, toPlanningValue);
    }

    @Override
    public ChainedChangeMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        Object oldValue = variableDescriptor.getValue(entity);
        return new ChainedChangeMove<>(variableDescriptor, entity, oldValue, newTrailingEntity, oldTrailingEntity);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        Object oldValue = variableDescriptor.getValue(entity);
        // Close the old chain
        if (oldTrailingEntity != null) {
            innerScoreDirector.changeVariableFacade(variableDescriptor, oldTrailingEntity, oldValue);
        }
        // Change the entity
        innerScoreDirector.changeVariableFacade(variableDescriptor, entity, toPlanningValue);
        // Reroute the new chain
        if (newTrailingEntity != null) {
            innerScoreDirector.changeVariableFacade(variableDescriptor, newTrailingEntity, entity);
        }
    }

    @Override
    public ChainedChangeMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new ChainedChangeMove<>(variableDescriptor,
                destinationScoreDirector.lookUpWorkingObject(entity),
                destinationScoreDirector.lookUpWorkingObject(toPlanningValue),
                destinationScoreDirector.lookUpWorkingObject(oldTrailingEntity),
                destinationScoreDirector.lookUpWorkingObject(newTrailingEntity));
    }

}
