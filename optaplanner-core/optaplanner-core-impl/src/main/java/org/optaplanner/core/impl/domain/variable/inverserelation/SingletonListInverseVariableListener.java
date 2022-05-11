/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.ListVariableListener;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class SingletonListInverseVariableListener<Solution_>
        implements ListVariableListener<Solution_, Object>, SingletonInverseVariableSupply {

    protected final InverseRelationShadowVariableDescriptor<Solution_> shadowVariableDescriptor;
    protected final ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    public SingletonListInverseVariableListener(
            InverseRelationShadowVariableDescriptor<Solution_> shadowVariableDescriptor,
            ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.shadowVariableDescriptor = shadowVariableDescriptor;
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        for (Object element : sourceVariableDescriptor.getListVariable(entity)) {
            setInverse((InnerScoreDirector<Solution_, ?>) scoreDirector, element, entity, null);
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        for (Object element : sourceVariableDescriptor.getListVariable(entity)) {
            setInverse(innerScoreDirector, element, null, entity);
        }
    }

    @Override
    public void beforeElementAdded(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        // Do nothing
    }

    @Override
    public void afterElementAdded(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        // Set entity[index].
        setInverse((InnerScoreDirector<Solution_, ?>) scoreDirector,
                sourceVariableDescriptor.getElement(entity, index), entity, null);
    }

    @Override
    public void beforeElementRemoved(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        // Unset entity[index].
        setInverse((InnerScoreDirector<Solution_, ?>) scoreDirector,
                sourceVariableDescriptor.getElement(entity, index), null, entity);
    }

    @Override
    public void afterElementRemoved(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        // Do nothing
    }

    @Override
    public void beforeElementMoved(ScoreDirector<Solution_> scoreDirector,
            Object sourceEntity, int sourceIndex,
            Object destinationEntity, int destinationIndex) {
        // Do nothing
    }

    @Override
    public void afterElementMoved(ScoreDirector<Solution_> scoreDirector,
            Object sourceEntity, int sourceIndex,
            Object destinationEntity, int destinationIndex) {
        if (sourceEntity == destinationEntity) {
            return;
        }
        setInverse((InnerScoreDirector<Solution_, ?>) scoreDirector,
                sourceVariableDescriptor.getElement(destinationEntity, destinationIndex), destinationEntity, sourceEntity);
    }

    private void setInverse(InnerScoreDirector<Solution_, ?> scoreDirector,
            Object element, Object inverseEntity, Object expectedOldInverseEntity) {
        Object oldInverseEntity = shadowVariableDescriptor.getValue(element);
        if (oldInverseEntity == inverseEntity) {
            return;
        } else if (oldInverseEntity != expectedOldInverseEntity) {
            throw new IllegalStateException("The entity (" + inverseEntity
                    + ") has a list variable (" + sourceVariableDescriptor.getVariableName()
                    + ") and one of its elements (" + element
                    + ") which has a shadow variable (" + shadowVariableDescriptor.getVariableName()
                    + ") has an oldInverseEntity (" + oldInverseEntity + ") which is not that entity.\n"
                    + "Verify the consistency of your input problem for that shadow variable.");

        }
        scoreDirector.beforeVariableChanged(shadowVariableDescriptor, element);
        shadowVariableDescriptor.setValue(element, inverseEntity);
        scoreDirector.afterVariableChanged(shadowVariableDescriptor, element);
    }

    @Override
    public Object getInverseSingleton(Object planningValue) {
        return shadowVariableDescriptor.getValue(planningValue);
    }
}
