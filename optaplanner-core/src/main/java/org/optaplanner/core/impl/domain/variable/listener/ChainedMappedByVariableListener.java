/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.domain.variable.listener;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ChainedMappedByVariableListener implements VariableListener<Object> {

    private final ShadowVariableDescriptor shadowVariableDescriptor;
    private final GenuineVariableDescriptor mappedByVariableDescriptor;

    public ChainedMappedByVariableListener(ShadowVariableDescriptor shadowVariableDescriptor) {
        this.shadowVariableDescriptor = shadowVariableDescriptor;
        mappedByVariableDescriptor = shadowVariableDescriptor.getMappedByVariableDescriptor();
    }

    public void beforeEntityAdded(ScoreDirector scoreDirector, Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, Object entity) {
        insert(scoreDirector, entity);
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, Object entity) {
        retract(scoreDirector, entity);
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, Object entity) {
        insert(scoreDirector, entity);
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        retract(scoreDirector, entity);
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, Object entity) {
        // Do nothing
    }

    protected void insert(ScoreDirector scoreDirector, Object entity) {
        Object shadowEntity = mappedByVariableDescriptor.getValue(entity);
        if (shadowEntity != null) {
            Object shadowValue = shadowVariableDescriptor.getValue(shadowEntity);
            if (shadowValue != null) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + mappedByVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a mappedBy variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowValue + ") which is not null.\n"
                        + "Verify the consistency of your input problem for that mappedBy variable.");
            }
            scoreDirector.beforeVariableChanged(shadowEntity, shadowVariableDescriptor.getVariableName());
            shadowVariableDescriptor.setValue(shadowEntity, entity);
            scoreDirector.afterVariableChanged(shadowEntity, shadowVariableDescriptor.getVariableName());
        }
    }

    protected void retract(ScoreDirector scoreDirector, Object entity) {
        Object shadowEntity = mappedByVariableDescriptor.getValue(entity);
        if (shadowEntity != null) {
            Object shadowValue = shadowVariableDescriptor.getValue(shadowEntity);
            if (shadowValue != entity) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + mappedByVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a mappedBy variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowValue + ") which is not that entity.\n"
                        + "Verify the consistency of your input problem for that mappedBy variable.");
            }
            scoreDirector.beforeVariableChanged(shadowEntity, shadowVariableDescriptor.getVariableName());
            shadowVariableDescriptor.setValue(shadowEntity, null);
            scoreDirector.afterVariableChanged(shadowEntity, shadowVariableDescriptor.getVariableName());
        }
    }

}
