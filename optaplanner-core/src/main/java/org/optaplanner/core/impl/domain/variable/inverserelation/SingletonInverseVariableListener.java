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

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class SingletonInverseVariableListener implements VariableListener<Object>, SingletonInverseVariableSupply {

    protected final InverseRelationShadowVariableDescriptor shadowVariableDescriptor;
    protected final VariableDescriptor sourceVariableDescriptor;

    public SingletonInverseVariableListener(InverseRelationShadowVariableDescriptor shadowVariableDescriptor,
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

    protected void retract(InnerScoreDirector scoreDirector, Object entity) {
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
