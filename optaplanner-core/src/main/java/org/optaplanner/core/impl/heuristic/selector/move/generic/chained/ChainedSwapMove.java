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

import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ChainedSwapMove extends SwapMove {

    public ChainedSwapMove(Collection<PlanningVariableDescriptor> variableDescriptors,
            Object leftEntity, Object rightEntity) {
        super(variableDescriptors, leftEntity, rightEntity);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new ChainedSwapMove(variableDescriptors, rightEntity, leftEntity);
    }

    public void doMove(ScoreDirector scoreDirector) {
        for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
            Object oldLeftValue = variableDescriptor.getValue(leftEntity);
            Object oldRightValue = variableDescriptor.getValue(rightEntity);
            if (!ObjectUtils.equals(oldLeftValue, oldRightValue)) {
                if (!variableDescriptor.isChained()) {
                    scoreDirector.beforeVariableChanged(leftEntity, variableDescriptor.getVariableName());
                    variableDescriptor.setValue(leftEntity, oldRightValue);
                    scoreDirector.afterVariableChanged(leftEntity, variableDescriptor.getVariableName());

                    scoreDirector.beforeVariableChanged(rightEntity, variableDescriptor.getVariableName());
                    variableDescriptor.setValue(rightEntity, oldLeftValue);
                    scoreDirector.afterVariableChanged(rightEntity, variableDescriptor.getVariableName());
                } else {
                    if (oldRightValue != leftEntity) {
                        ChainedMoveUtils.doChainedChange(scoreDirector, leftEntity, variableDescriptor, oldRightValue);
                    }
                    if (oldLeftValue != rightEntity) {
                        ChainedMoveUtils.doChainedChange(scoreDirector, rightEntity, variableDescriptor, oldLeftValue);
                    }
                }
            }
        }
    }

}
