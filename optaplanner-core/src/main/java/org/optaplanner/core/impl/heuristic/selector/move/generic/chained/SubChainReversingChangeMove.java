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
import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class SubChainReversingChangeMove implements Move {

    private final SubChain subChain;
    private final PlanningVariableDescriptor variableDescriptor;
    private final Object toPlanningValue;

    public SubChainReversingChangeMove(SubChain subChain,
            PlanningVariableDescriptor variableDescriptor, Object toPlanningValue) {
        this.subChain = subChain;
        this.variableDescriptor = variableDescriptor;
        this.toPlanningValue = toPlanningValue;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        if (subChain.getEntityList().contains(toPlanningValue)) {
            return false;
        }
        Object oldFirstPlanningValue = variableDescriptor.getValue(subChain.getFirstEntity());
        return !ObjectUtils.equals(oldFirstPlanningValue, toPlanningValue);
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        Object oldFirstPlanningValue = variableDescriptor.getValue(subChain.getFirstEntity());
        return new SubChainReversingChangeMove(subChain.reverse(), variableDescriptor, oldFirstPlanningValue);
    }

    public void doMove(ScoreDirector scoreDirector) {
        ChainedMoveUtils.doReverseSubChainChange(scoreDirector, subChain, variableDescriptor, toPlanningValue);
    }

    public Collection<? extends Object> getPlanningEntities() {
        return subChain.getEntityList();
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toPlanningValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SubChainReversingChangeMove) {
            SubChainReversingChangeMove other = (SubChainReversingChangeMove) o;
            return new EqualsBuilder()
                    .append(subChain, other.subChain)
                    .append(variableDescriptor.getVariableName(),
                            other.variableDescriptor.getVariableName())
                    .append(toPlanningValue, other.toPlanningValue)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(subChain)
                .append(variableDescriptor.getVariableName())
                .append(toPlanningValue)
                .toHashCode();
    }

    public String toString() {
        return subChain + " reversed => " + toPlanningValue;
    }

}
