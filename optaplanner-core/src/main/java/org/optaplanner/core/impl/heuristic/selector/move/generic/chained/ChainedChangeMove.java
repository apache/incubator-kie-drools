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

import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ChainedChangeMove extends ChangeMove {

    protected final SingletonInverseVariableSupply inverseVariableSupply;

    public ChainedChangeMove(Object entity, GenuineVariableDescriptor variableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply, Object toPlanningValue) {
        super(entity, variableDescriptor, toPlanningValue);
        this.inverseVariableSupply = inverseVariableSupply;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return super.isMoveDoable(scoreDirector)
                && !ObjectUtils.equals(entity, toPlanningValue);
    }

    @Override
    public Move createUndoMove(ScoreDirector scoreDirector) {
        Object oldValue = variableDescriptor.getValue(entity);
        return new ChainedChangeMove(entity, variableDescriptor, inverseVariableSupply, oldValue);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector scoreDirector) {
        Object oldValue = variableDescriptor.getValue(entity);
        Object oldTrailingEntity = inverseVariableSupply.getInverseSingleton(entity);
        Object newTrailingEntity = toPlanningValue == null ? null
                : inverseVariableSupply.getInverseSingleton(toPlanningValue);
        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.changeVariableFacade(variableDescriptor, oldTrailingEntity, oldValue);
        }
        // Change the entity
        scoreDirector.changeVariableFacade(variableDescriptor, entity, toPlanningValue);
        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.changeVariableFacade(variableDescriptor, newTrailingEntity, entity);
        }
    }

}
