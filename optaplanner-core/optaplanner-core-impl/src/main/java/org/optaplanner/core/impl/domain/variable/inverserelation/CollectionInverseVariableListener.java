package org.optaplanner.core.impl.domain.variable.inverserelation;

import java.util.Collection;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class CollectionInverseVariableListener<Solution_>
        implements VariableListener<Solution_, Object>, CollectionInverseVariableSupply {

    protected final InverseRelationShadowVariableDescriptor<Solution_> shadowVariableDescriptor;
    protected final VariableDescriptor<Solution_> sourceVariableDescriptor;

    public CollectionInverseVariableListener(InverseRelationShadowVariableDescriptor<Solution_> shadowVariableDescriptor,
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
            Collection shadowCollection = (Collection) shadowVariableDescriptor.getValue(shadowEntity);
            if (shadowCollection == null) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + sourceVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a sourceVariableName variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowCollection + ") which is null.\n"
                        + "Verify the consistency of your input problem for that bi-directional relationship.\n"
                        + "Every non-singleton inverse variable can never be null. It should at least be an empty "
                        + Collection.class.getSimpleName() + " instead.");
            }
            scoreDirector.beforeVariableChanged(shadowVariableDescriptor, shadowEntity);
            boolean added = shadowCollection.add(entity);
            if (!added) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + sourceVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a sourceVariableName variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowCollection
                        + ") which already contained the entity (" + entity + ").\n"
                        + "Verify the consistency of your input problem for that bi-directional relationship.");
            }
            scoreDirector.afterVariableChanged(shadowVariableDescriptor, shadowEntity);
        }
    }

    protected void retract(InnerScoreDirector<Solution_, ?> scoreDirector, Object entity) {
        Object shadowEntity = sourceVariableDescriptor.getValue(entity);
        if (shadowEntity != null) {
            Collection shadowCollection = (Collection) shadowVariableDescriptor.getValue(shadowEntity);
            if (shadowCollection == null) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + sourceVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a sourceVariableName variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowCollection + ") which is null.\n"
                        + "Verify the consistency of your input problem for that bi-directional relationship.\n"
                        + "Every non-singleton inverse variable can never be null. It should at least be an empty "
                        + Collection.class.getSimpleName() + " instead.");
            }
            scoreDirector.beforeVariableChanged(shadowVariableDescriptor, shadowEntity);
            boolean removed = shadowCollection.remove(entity);
            if (!removed) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + sourceVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a sourceVariableName variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowCollection
                        + ") which did not contain the entity (" + entity + ").\n"
                        + "Verify the consistency of your input problem for that bi-directional relationship.");
            }
            scoreDirector.afterVariableChanged(shadowVariableDescriptor, shadowEntity);
        }
    }

    @Override
    public Collection<?> getInverseCollection(Object planningValue) {
        return (Collection<?>) shadowVariableDescriptor.getValue(planningValue);
    }

}
