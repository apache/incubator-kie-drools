/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.variable.inverserelation;

import java.util.Collection;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class CollectionInverseVariableListener implements VariableListener<Object>, CollectionInverseVariableSupply {

    protected final InverseRelationShadowVariableDescriptor shadowVariableDescriptor;
    protected final VariableDescriptor sourceVariableDescriptor;

    public CollectionInverseVariableListener(InverseRelationShadowVariableDescriptor shadowVariableDescriptor,
            VariableDescriptor sourceVariableDescriptor) {
        this.shadowVariableDescriptor = shadowVariableDescriptor;
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Object entity) {
        insert((InnerScoreDirector) scoreDirector, entity);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Object entity) {
        retract((InnerScoreDirector) scoreDirector, entity);
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Object entity) {
        insert((InnerScoreDirector) scoreDirector, entity);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        retract((InnerScoreDirector) scoreDirector, entity);
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        // Do nothing
    }

    protected void insert(InnerScoreDirector scoreDirector, Object entity) {
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

    protected void retract(InnerScoreDirector scoreDirector, Object entity) {
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
