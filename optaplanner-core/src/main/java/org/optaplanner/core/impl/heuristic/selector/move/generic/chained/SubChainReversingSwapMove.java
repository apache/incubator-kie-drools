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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Non-cacheable
 */
public class SubChainReversingSwapMove implements Move {

    private final PlanningVariableDescriptor variableDescriptor;

    private final SubChain leftSubChain;
    private final SubChain rightSubChain;

    public SubChainReversingSwapMove(PlanningVariableDescriptor variableDescriptor,
            SubChain leftSubChain, SubChain rightSubChain) {
        this.variableDescriptor = variableDescriptor;
        this.leftSubChain = leftSubChain;
        this.rightSubChain = rightSubChain;
    }

    public SubChain getLeftSubChain() {
        return leftSubChain;
    }

    public SubChain getRightSubChain() {
        return rightSubChain;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        for (Object leftEntity : leftSubChain.getEntityList()) {
            if (rightSubChain.getEntityList().contains(leftEntity)) {
                return false;
            }
        }
        // Because leftFirstEntity and rightFirstEntity are unequal, chained guarantees their values are unequal too.
        return true;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new SubChainReversingSwapMove(variableDescriptor,
                rightSubChain.reverse(), leftSubChain.reverse());
    }

    public void doMove(ScoreDirector scoreDirector) {
        Object oldLeftValue = variableDescriptor.getValue(leftSubChain.getFirstEntity());
        Object oldRightValue = variableDescriptor.getValue(rightSubChain.getFirstEntity());
        ChainedMoveUtils.doReverseSubChainChange(scoreDirector, leftSubChain, variableDescriptor,
                oldRightValue == leftSubChain.getLastEntity() ? oldLeftValue : oldRightValue);
        ChainedMoveUtils.doReverseSubChainChange(scoreDirector, rightSubChain, variableDescriptor,
                oldLeftValue == rightSubChain.getLastEntity() ? leftSubChain.getFirstEntity() : oldLeftValue);
    }

    public Collection<? extends Object> getPlanningEntities() {
        List<Object> entities = new ArrayList<Object>(
                leftSubChain.getSize() + rightSubChain.getSize());
        entities.addAll(leftSubChain.getEntityList());
        entities.addAll(rightSubChain.getEntityList());
        return entities;
    }

    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<Object>(2);
        values.add(variableDescriptor.getValue(leftSubChain.getFirstEntity()));
        values.add(variableDescriptor.getValue(rightSubChain.getFirstEntity()));
        return values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SubChainReversingSwapMove) {
            SubChainReversingSwapMove other = (SubChainReversingSwapMove) o;
            return new EqualsBuilder()
                    .append(variableDescriptor, other.variableDescriptor)
                    .append(leftSubChain, other.leftSubChain)
                    .append(rightSubChain, other.rightSubChain)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(variableDescriptor)
                .append(leftSubChain)
                .append(rightSubChain)
                .toHashCode();
    }

    public String toString() {
        return leftSubChain + " reversed <=> " + rightSubChain;
    }

}
