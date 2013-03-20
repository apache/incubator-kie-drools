/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ChainedMoveUtils {

    public static void doChainedChange(ScoreDirector scoreDirector, Object entity,
            PlanningVariableDescriptor variableDescriptor, Object toPlanningValue) {
        Object oldPlanningValue = variableDescriptor.getValue(entity);
        Object oldTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, entity);
        // If chaining == true then toPlanningValue == null guarantees an uninitialized entity
        Object newTrailingEntity = toPlanningValue == null ? null
                : scoreDirector.getTrailingEntity(variableDescriptor, toPlanningValue);

        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
            variableDescriptor.setValue(oldTrailingEntity, oldPlanningValue);
            scoreDirector.afterVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
        }

        // Change the entity
        scoreDirector.beforeVariableChanged(entity, variableDescriptor.getVariableName());
        variableDescriptor.setValue(entity, toPlanningValue);
        scoreDirector.afterVariableChanged(entity, variableDescriptor.getVariableName());

        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
            variableDescriptor.setValue(newTrailingEntity, entity);
            scoreDirector.afterVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
        }
    }


    public static void doSubChainChange(ScoreDirector scoreDirector, SubChain subChain,
            PlanningVariableDescriptor variableDescriptor, Object toPlanningValue) {
        Object firstEntity = subChain.getFirstEntity();
        Object lastEntity = subChain.getLastEntity();
        Object oldFirstPlanningValue = variableDescriptor.getValue(firstEntity);

        Object oldTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, lastEntity);
        Object newTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, toPlanningValue);

        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
            variableDescriptor.setValue(oldTrailingEntity, oldFirstPlanningValue);
            scoreDirector.afterVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
        }

        // Change the entity
        for (Object entity : subChain.getEntityList()) {
            // When firstEntity changes, other entities in the chain can get a new anchor, so they are changed too
            scoreDirector.beforeVariableChanged(entity, variableDescriptor.getVariableName());
        }
        variableDescriptor.setValue(firstEntity, toPlanningValue);
        for (Object entity : subChain.getEntityList()) {
            // When firstEntity changes, other entities in the chain can get a new anchor, so they are changed too
            scoreDirector.afterVariableChanged(entity, variableDescriptor.getVariableName());
        }

        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
            variableDescriptor.setValue(newTrailingEntity, lastEntity);
            scoreDirector.afterVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
        }
    }

    public static void doReverseSubChainChange(ScoreDirector scoreDirector, SubChain subChain,
            PlanningVariableDescriptor variableDescriptor, Object toPlanningValue) {
        Object firstEntity = subChain.getFirstEntity();
        Object lastEntity = subChain.getLastEntity();
        Object oldFirstPlanningValue = variableDescriptor.getValue(firstEntity);

        Object oldTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, lastEntity);
        Object newTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, toPlanningValue);

        if (firstEntity.equals(newTrailingEntity)) {
            // Unmoved reverse
            // Temporary close the old chain
            if (oldTrailingEntity != null) {
                scoreDirector.beforeVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
                variableDescriptor.setValue(oldTrailingEntity, oldFirstPlanningValue);
                scoreDirector.afterVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
            }
        } else {
            // Close the old chain
            if (oldTrailingEntity != null) {
                scoreDirector.beforeVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
                variableDescriptor.setValue(oldTrailingEntity, oldFirstPlanningValue);
                scoreDirector.afterVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
            }
        }
        // Change the entity
        Object nextEntity = toPlanningValue;
        List<Object> entityList = subChain.getEntityList();
        for (ListIterator<Object> it = entityList.listIterator(entityList.size()); it.hasPrevious();) {
            Object entity = it.previous();
            scoreDirector.beforeVariableChanged(entity, variableDescriptor.getVariableName());
            variableDescriptor.setValue(entity, nextEntity);
            scoreDirector.afterVariableChanged(entity, variableDescriptor.getVariableName());
            nextEntity = entity;
        }
        if (firstEntity.equals(newTrailingEntity)) {
            // Unmoved reverse
            // Reroute the old chain
            if (oldTrailingEntity != null) {
                scoreDirector.beforeVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
                variableDescriptor.setValue(oldTrailingEntity, firstEntity);
                scoreDirector.afterVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
            }
        } else {
            // Reroute the new chain
            if (newTrailingEntity != null) {
                scoreDirector.beforeVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
                variableDescriptor.setValue(newTrailingEntity, firstEntity);
                scoreDirector.afterVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
            }
        }
    }

    private ChainedMoveUtils() {
    }

}
