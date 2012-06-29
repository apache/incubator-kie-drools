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
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;

public class GenericChainedChangePartMove implements Move {

    private final List<Object> entitiesSubChain;
    private final Object firstEntity;
    private final Object lastEntity;
    private final PlanningVariableDescriptor planningVariableDescriptor;
    private final Object toPlanningValue;
    private final Object oldTrailingEntity;
    private final Object newTrailingEntity;

    public GenericChainedChangePartMove(List<Object> entitiesSubChain,
            PlanningVariableDescriptor planningVariableDescriptor, Object toPlanningValue,
            Object oldTrailingEntity, Object newTrailingEntity) {
        this.entitiesSubChain = entitiesSubChain;
        this.planningVariableDescriptor = planningVariableDescriptor;
        this.toPlanningValue = toPlanningValue;
        this.oldTrailingEntity = oldTrailingEntity;
        this.newTrailingEntity = newTrailingEntity;
        firstEntity = this.entitiesSubChain.get(0);
        lastEntity = this.entitiesSubChain.get(entitiesSubChain.size() - 1);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return true; // Done by GenericChainedChangePartMoveFactory
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        Object oldFirstPlanningValue = planningVariableDescriptor.getValue(firstEntity);
        return new GenericChainedChangePartMove(entitiesSubChain,
                planningVariableDescriptor, oldFirstPlanningValue,
                newTrailingEntity, oldTrailingEntity);
    }

    public void doMove(ScoreDirector scoreDirector) {
        Object oldFirstPlanningValue = planningVariableDescriptor.getValue(firstEntity);

        // Close the old chain
        if (oldTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(oldTrailingEntity, planningVariableDescriptor.getVariableName());
            planningVariableDescriptor.setValue(oldTrailingEntity, oldFirstPlanningValue);
            scoreDirector.afterVariableChanged(oldTrailingEntity, planningVariableDescriptor.getVariableName());
        }

        // Change the entity
        for (Object entity : entitiesSubChain) {
            // When firstEntity changes, other entities in the chain can get a new anchor, so they are changed too
            scoreDirector.beforeVariableChanged(entity, planningVariableDescriptor.getVariableName());
        }
        planningVariableDescriptor.setValue(firstEntity, toPlanningValue);
        for (Object entity : entitiesSubChain) {
            // When firstEntity changes, other entities in the chain can get a new anchor, so they are changed too
            scoreDirector.afterVariableChanged(entity, planningVariableDescriptor.getVariableName());
        }

        // Reroute the new chain
        if (newTrailingEntity != null) {
            scoreDirector.beforeVariableChanged(newTrailingEntity, planningVariableDescriptor.getVariableName());
            planningVariableDescriptor.setValue(newTrailingEntity, lastEntity);
            scoreDirector.afterVariableChanged(newTrailingEntity, planningVariableDescriptor.getVariableName());
        }
    }

    public Collection<? extends Object> getPlanningEntities() {
        return entitiesSubChain;
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toPlanningValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof GenericChainedChangePartMove) {
            GenericChainedChangePartMove other = (GenericChainedChangePartMove) o;
            return new EqualsBuilder()
                    .append(entitiesSubChain, other.entitiesSubChain)
                    .append(planningVariableDescriptor.getVariableName(),
                            other.planningVariableDescriptor.getVariableName())
                    .append(toPlanningValue, other.toPlanningValue)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(entitiesSubChain)
                .append(planningVariableDescriptor.getVariableName())
                .append(toPlanningValue)
                .toHashCode();
    }

    public String toString() {
        return entitiesSubChain + " => " + toPlanningValue;
    }

}
