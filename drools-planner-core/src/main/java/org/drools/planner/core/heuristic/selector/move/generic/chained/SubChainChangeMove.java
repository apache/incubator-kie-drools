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

package org.drools.planner.core.heuristic.selector.move.generic.chained;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.value.chained.SubChain;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;

public class SubChainChangeMove implements Move {

    private final SubChain subChain;
    private final PlanningVariableDescriptor variableDescriptor;
    private final Object toPlanningValue;

    private final Object firstEntity;
    private final Object lastEntity;

    public SubChainChangeMove(SubChain subChain,
            PlanningVariableDescriptor variableDescriptor, Object toPlanningValue) {
        this.subChain = subChain;
        this.variableDescriptor = variableDescriptor;
        this.toPlanningValue = toPlanningValue;
        firstEntity = this.subChain.getFirstEntity();
        lastEntity = this.subChain.getLastEntity();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        Object oldFirstPlanningValue = variableDescriptor.getValue(firstEntity);
        return !subChain.getEntityList().contains(toPlanningValue) && oldFirstPlanningValue != toPlanningValue;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        Object oldFirstPlanningValue = variableDescriptor.getValue(firstEntity);
        return new SubChainChangeMove(subChain,
                variableDescriptor, oldFirstPlanningValue);
    }

    public void doMove(ScoreDirector scoreDirector) {
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

    public Collection<? extends Object> getPlanningEntities() {
        return subChain.getEntityList();
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toPlanningValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SubChainChangeMove) {
            SubChainChangeMove other = (SubChainChangeMove) o;
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
        return subChain + " => " + toPlanningValue;
    }

}
