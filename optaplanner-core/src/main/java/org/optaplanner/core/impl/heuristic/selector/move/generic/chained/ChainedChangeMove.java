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

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ChainedChangeMove extends ChangeMove {

    public ChainedChangeMove(Object entity, PlanningVariableDescriptor variableDescriptor, Object toPlanningValue) {
        super(entity, variableDescriptor, toPlanningValue);
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
        Object oldPlanningValue = variableDescriptor.getValue(entity);
        return new ChainedChangeMove(entity, variableDescriptor, oldPlanningValue);
    }

    @Override
    public void doMove(ScoreDirector scoreDirector) {
        ChainedMoveUtils.doChainedChange(scoreDirector, entity, variableDescriptor, toPlanningValue);
    }

}
