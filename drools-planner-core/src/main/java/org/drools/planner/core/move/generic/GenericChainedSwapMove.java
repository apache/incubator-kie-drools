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

package org.drools.planner.core.move.generic;

import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;

public class GenericChainedSwapMove extends GenericSwapMove {

    public GenericChainedSwapMove(Collection<PlanningVariableDescriptor> planningVariableDescriptors,
            Object leftPlanningEntity, Object rightPlanningEntity) {
        super(planningVariableDescriptors, leftPlanningEntity, rightPlanningEntity);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new GenericChainedSwapMove(planningVariableDescriptors,
                rightPlanningEntity, leftPlanningEntity);
    }

    public void doMove(ScoreDirector scoreDirector) {
        for (PlanningVariableDescriptor variableDescriptor : planningVariableDescriptors) {
            Object oldLeftValue = variableDescriptor.getValue(leftPlanningEntity);
            Object oldRightValue = variableDescriptor.getValue(rightPlanningEntity);
            if (!ObjectUtils.equals(oldLeftValue, oldRightValue)) {
                if (!variableDescriptor.isChained()) {
                    scoreDirector.beforeVariableChanged(leftPlanningEntity, variableDescriptor.getVariableName());
                    variableDescriptor.setValue(leftPlanningEntity, oldRightValue);
                    scoreDirector.afterVariableChanged(leftPlanningEntity, variableDescriptor.getVariableName());

                    scoreDirector.beforeVariableChanged(rightPlanningEntity, variableDescriptor.getVariableName());
                    variableDescriptor.setValue(rightPlanningEntity, oldLeftValue);
                    scoreDirector.afterVariableChanged(rightPlanningEntity, variableDescriptor.getVariableName());
                } else {
                    if (oldRightValue != leftPlanningEntity) {
                        doChainedMove(scoreDirector, variableDescriptor, leftPlanningEntity, oldRightValue);
                    }
                    if (oldLeftValue != rightPlanningEntity) {
                        doChainedMove(scoreDirector, variableDescriptor, rightPlanningEntity, oldLeftValue);
                    }
                }
            }
        }
    }

    // TODO DRY with GenericChainedChangeMove
    public void doChainedMove(ScoreDirector scoreDirector, PlanningVariableDescriptor variableDescriptor,
            Object planningEntity, Object toPlanningValue ) {
        Object oldPlanningValue = variableDescriptor.getValue(planningEntity);
        Object oldTrailingEntity = scoreDirector.getTrailingEntity(variableDescriptor, planningEntity);
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
        scoreDirector.beforeVariableChanged(planningEntity, variableDescriptor.getVariableName());
        variableDescriptor.setValue(planningEntity, toPlanningValue);
        scoreDirector.afterVariableChanged(planningEntity, variableDescriptor.getVariableName());

        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
            variableDescriptor.setValue(newTrailingEntity, planningEntity);
            scoreDirector.afterVariableChanged(newTrailingEntity, variableDescriptor.getVariableName());
        }
    }

}
