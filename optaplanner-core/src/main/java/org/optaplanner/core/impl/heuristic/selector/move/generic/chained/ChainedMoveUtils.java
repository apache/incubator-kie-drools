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

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ChainedMoveUtils {

    public static void doChainedChange(ScoreDirector scoreDirector, Object entity,
            GenuineVariableDescriptor variableDescriptor, Object toPlanningValue) {
        Object oldPlanningValue = variableDescriptor.getValue(entity);
        Object oldTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, entity);
        // If chaining == true then toPlanningValue == null guarantees an uninitialized entity
        Object newTrailingEntity = toPlanningValue == null ? null
                : scoreDirector.getTrailingEntity(variableDescriptor, toPlanningValue);

        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
        }
        // Change the entity
        scoreDirector.beforeVariableChanged(entity, variableDescriptor.getVariableName());
        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
        }

        // Close the old chain
        if (oldTrailingEntity != null) {
            variableDescriptor.setValue(oldTrailingEntity, oldPlanningValue);
        }
        // Change the entity
        variableDescriptor.setValue(entity, toPlanningValue);
        // Reroute the new chain
        if (newTrailingEntity != null) {
            variableDescriptor.setValue(newTrailingEntity, entity);
        }

        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.afterVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
        }
        // Change the entity
        scoreDirector.afterVariableChanged(entity, variableDescriptor.getVariableName());
        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.afterVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
        }
    }


    public static void doSubChainChange(ScoreDirector scoreDirector, SubChain subChain,
            GenuineVariableDescriptor variableDescriptor, Object toPlanningValue) {
        Object firstEntity = subChain.getFirstEntity();
        Object lastEntity = subChain.getLastEntity();
        Object oldFirstPlanningValue = variableDescriptor.getValue(firstEntity);

        Object oldTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, lastEntity);
        Object newTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, toPlanningValue);

        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
        }
        // Change the entity
        scoreDirector.beforeVariableChanged(firstEntity, variableDescriptor.getVariableName());
        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
        }

        // Close the old chain
        if (oldTrailingEntity != null) {
            variableDescriptor.setValue(oldTrailingEntity, oldFirstPlanningValue);
        }
        // Change the entity
        variableDescriptor.setValue(firstEntity, toPlanningValue);
        // Reroute the new chain
        if (newTrailingEntity != null) {
            variableDescriptor.setValue(newTrailingEntity, lastEntity);
        }

        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.afterVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
        }
        // Change the entity
        scoreDirector.afterVariableChanged(firstEntity, variableDescriptor.getVariableName());
        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.afterVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
        }
    }

    public static void doReverseSubChainChange(ScoreDirector scoreDirector, SubChain subChain,
            GenuineVariableDescriptor variableDescriptor, Object toPlanningValue) {
        Object firstEntity = subChain.getFirstEntity();
        Object lastEntity = subChain.getLastEntity();
        Object oldFirstPlanningValue = variableDescriptor.getValue(firstEntity);

        Object oldTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, lastEntity);
        Object newTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, toPlanningValue);
        boolean unmovedReverse = firstEntity == newTrailingEntity;
        List<Object> entityList = subChain.getEntityList();

        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
        }
        // Change the entity
        for (ListIterator<Object> it = entityList.listIterator(entityList.size()); it.hasPrevious();) {
            Object entity = it.previous();
            scoreDirector.beforeVariableChanged(entity, variableDescriptor.getVariableName());
        }
        if (!unmovedReverse) {
            // Reroute the new chain
            if (newTrailingEntity != null) {
                scoreDirector.beforeVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
            }
        }

        // Close the old chain
        if (oldTrailingEntity != null) {
            variableDescriptor.setValue(oldTrailingEntity, oldFirstPlanningValue);
        }
        // Change the entity
        Object nextEntity = toPlanningValue;
        for (ListIterator<Object> it = entityList.listIterator(entityList.size()); it.hasPrevious();) {
            Object entity = it.previous();
            variableDescriptor.setValue(entity, nextEntity);
            nextEntity = entity;
        }
        if (unmovedReverse) {
            // Reroute the old chain
            if (oldTrailingEntity != null) {
                variableDescriptor.setValue(oldTrailingEntity, firstEntity);
            }
        } else {
            // Reroute the new chain
            if (newTrailingEntity != null) {
                variableDescriptor.setValue(newTrailingEntity, firstEntity);
            }
        }

        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.afterVariableChanged(oldTrailingEntity, variableDescriptor.getVariableName());
        }
        // Change the entity
        for (ListIterator<Object> it = entityList.listIterator(entityList.size()); it.hasPrevious();) {
            Object entity = it.previous();
            scoreDirector.afterVariableChanged(entity, variableDescriptor.getVariableName());
        }
        if (!unmovedReverse) {
            // Reroute the new chain
            if (newTrailingEntity != null) {
                scoreDirector.afterVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
            }
        }
    }

    private ChainedMoveUtils() {
    }

}
