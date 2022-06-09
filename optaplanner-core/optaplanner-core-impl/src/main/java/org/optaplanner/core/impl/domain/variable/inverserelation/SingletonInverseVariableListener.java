package org.optaplanner.core.impl.domain.variable.inverserelation;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class SingletonInverseVariableListener<Solution_>
        implements VariableListener<Solution_, Object>, SingletonInverseVariableSupply {

    protected final InverseRelationShadowVariableDescriptor<Solution_> shadowVariableDescriptor;
    protected final VariableDescriptor<Solution_> sourceVariableDescriptor;

    public SingletonInverseVariableListener(InverseRelationShadowVariableDescriptor<Solution_> shadowVariableDescriptor,
            VariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.shadowVariableDescriptor = shadowVariableDescriptor;
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        insert((InnerScoreDirector<Solution_, ?>) scoreDirector, entity);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<Solution_> scoreDirector, Object entity) {
        retract((InnerScoreDirector<Solution_, ?>) scoreDirector, entity);
    }

    @Override
    public void afterVariableChanged(ScoreDirector<Solution_> scoreDirector, Object entity) {
        insert((InnerScoreDirector<Solution_, ?>) scoreDirector, entity);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        retract((InnerScoreDirector<Solution_, ?>) scoreDirector, entity);
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    protected void insert(InnerScoreDirector<Solution_, ?> scoreDirector, Object entity) {
        Object shadowEntity = sourceVariableDescriptor.getValue(entity);
        if (shadowEntity != null) {
            Object shadowValue = shadowVariableDescriptor.getValue(shadowEntity);
            if (shadowValue != null) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + sourceVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a sourceVariableName variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowValue + ") which is not null.\n"
                        + "Verify the consistency of your input problem for that sourceVariableName variable.");
            }
            scoreDirector.beforeVariableChanged(shadowVariableDescriptor, shadowEntity);
            shadowVariableDescriptor.setValue(shadowEntity, entity);
            scoreDirector.afterVariableChanged(shadowVariableDescriptor, shadowEntity);
        }
    }

    protected void retract(InnerScoreDirector<Solution_, ?> scoreDirector, Object entity) {
        Object shadowEntity = sourceVariableDescriptor.getValue(entity);
        if (shadowEntity != null) {
            Object shadowValue = shadowVariableDescriptor.getValue(shadowEntity);
            if (shadowValue != entity) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + sourceVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a sourceVariableName variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowValue + ") which is not that entity.\n"
                        + "Verify the consistency of your input problem for that sourceVariableName variable.");
            }
            scoreDirector.beforeVariableChanged(shadowVariableDescriptor, shadowEntity);
            shadowVariableDescriptor.setValue(shadowEntity, null);
            scoreDirector.afterVariableChanged(shadowVariableDescriptor, shadowEntity);
        }
    }

    @Override
    public Object getInverseSingleton(Object planningValue) {
        return shadowVariableDescriptor.getValue(planningValue);
    }

}
