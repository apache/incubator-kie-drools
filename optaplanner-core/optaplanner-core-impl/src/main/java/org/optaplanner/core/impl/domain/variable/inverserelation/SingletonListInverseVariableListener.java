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

import java.util.List;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class SingletonListInverseVariableListener<Solution_>
        implements VariableListener<Solution_, Object>, SingletonInverseVariableSupply {

    protected final InverseRelationShadowVariableDescriptor<Solution_> shadowVariableDescriptor;
    protected final ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    public SingletonListInverseVariableListener(
            InverseRelationShadowVariableDescriptor<Solution_> shadowVariableDescriptor,
            ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.shadowVariableDescriptor = shadowVariableDescriptor;
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    @Override
    public boolean requiresUniqueEntityEvents() {
        // A move on a single entity produces multiple before/after variable changed events for the given entity
        // but the corrupted input problem checks in insert/retract methods require a unique pair of before/after events.
        return true;
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
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        for (Object value : listVariable) {
            Object oldInverseEntity = shadowVariableDescriptor.getValue(value);
            if (oldInverseEntity != null) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a list variable (" + sourceVariableDescriptor.getVariableName()
                        + ") and one of its values (" + value
                        + ") which has a shadow variable (" + shadowVariableDescriptor.getVariableName()
                        + ") has an oldInverseEntity (" + oldInverseEntity + ") which is not null.\n"
                        + "Verify the consistency of your input problem for that shadow variable.");
            }
            scoreDirector.beforeVariableChanged(shadowVariableDescriptor, value);
            shadowVariableDescriptor.setValue(value, entity);
            scoreDirector.afterVariableChanged(shadowVariableDescriptor, value);
        }
    }

    protected void retract(InnerScoreDirector<Solution_, ?> scoreDirector, Object entity) {
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        for (Object value : listVariable) {
            Object oldInverseEntity = shadowVariableDescriptor.getValue(value);
            if (oldInverseEntity != entity) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a list variable (" + sourceVariableDescriptor.getVariableName()
                        + ") and one of its values (" + value
                        + ") which has a shadow variable (" + shadowVariableDescriptor.getVariableName()
                        + ") has an oldInverseEntity (" + oldInverseEntity + ") which is not that entity.\n"
                        + "Verify the consistency of your input problem for that shadow variable.");
            }
            scoreDirector.beforeVariableChanged(shadowVariableDescriptor, value);
            shadowVariableDescriptor.setValue(value, null);
            scoreDirector.afterVariableChanged(shadowVariableDescriptor, value);
        }
    }

    @Override
    public Object getInverseSingleton(Object planningValue) {
        return shadowVariableDescriptor.getValue(planningValue);
    }
}
