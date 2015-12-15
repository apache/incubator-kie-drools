/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ChainedSwapMove extends SwapMove {

    protected final List<SingletonInverseVariableSupply> inverseVariableSupplyList;

    public ChainedSwapMove(List<GenuineVariableDescriptor> variableDescriptorList,
            List<SingletonInverseVariableSupply> inverseVariableSupplyList, Object leftEntity, Object rightEntity) {
        super(variableDescriptorList, leftEntity, rightEntity);
        this.inverseVariableSupplyList = inverseVariableSupplyList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new ChainedSwapMove(variableDescriptorList, inverseVariableSupplyList, rightEntity, leftEntity);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector scoreDirector) {
        for (int i = 0; i < variableDescriptorList.size(); i++) {
            GenuineVariableDescriptor variableDescriptor = variableDescriptorList.get(i);
            Object oldLeftValue = variableDescriptor.getValue(leftEntity);
            Object oldRightValue = variableDescriptor.getValue(rightEntity);
            if (!ObjectUtils.equals(oldLeftValue, oldRightValue)) {
                if (!variableDescriptor.isChained()) {
                    scoreDirector.changeVariableFacade(variableDescriptor, leftEntity, oldRightValue);
                    scoreDirector.changeVariableFacade(variableDescriptor, rightEntity, oldLeftValue);
                } else {
                    SingletonInverseVariableSupply inverseVariableSupply = inverseVariableSupplyList.get(i);
                    Object oldLeftTrailingEntity = inverseVariableSupply.getInverseSingleton(leftEntity);
                    Object oldRightTrailingEntity = inverseVariableSupply.getInverseSingleton(rightEntity);
                    if (oldRightValue == leftEntity) {
                        // Change the right entity
                        scoreDirector.changeVariableFacade(variableDescriptor, rightEntity, oldLeftValue);
                        // Change the left entity
                        scoreDirector.changeVariableFacade(variableDescriptor, leftEntity, rightEntity);
                        // Reroute the new left chain
                        if (oldRightTrailingEntity != null) {
                            scoreDirector.changeVariableFacade(variableDescriptor, oldRightTrailingEntity, leftEntity);
                        }
                    } else if (oldLeftValue == rightEntity) {
                        // Change the right entity
                        scoreDirector.changeVariableFacade(variableDescriptor, leftEntity, oldRightValue);
                        // Change the left entity
                        scoreDirector.changeVariableFacade(variableDescriptor, rightEntity, leftEntity);
                        // Reroute the new left chain
                        if (oldLeftTrailingEntity != null) {
                            scoreDirector.changeVariableFacade(variableDescriptor, oldLeftTrailingEntity, rightEntity);
                        }
                    } else {
                        // Change the left entity
                        scoreDirector.changeVariableFacade(variableDescriptor, leftEntity, oldRightValue);
                        // Change the right entity
                        scoreDirector.changeVariableFacade(variableDescriptor, rightEntity, oldLeftValue);
                        // Reroute the new left chain
                        if (oldRightTrailingEntity != null) {
                            scoreDirector.changeVariableFacade(variableDescriptor, oldRightTrailingEntity, leftEntity);
                        }
                        // Reroute the new right chain
                        if (oldLeftTrailingEntity != null) {
                            scoreDirector.changeVariableFacade(variableDescriptor, oldLeftTrailingEntity, rightEntity);
                        }
                    }
                }
            }
        }
    }

}
