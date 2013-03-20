/*
 * Copyright 2011 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ChangeMove implements Move {

    protected final Object entity;
    protected final PlanningVariableDescriptor variableDescriptor;
    protected final Object toPlanningValue;

    public ChangeMove(Object entity, PlanningVariableDescriptor variableDescriptor,
            Object toPlanningValue) {
        this.entity = entity;
        this.variableDescriptor = variableDescriptor;
        this.toPlanningValue = toPlanningValue;
    }

    public Object getEntity() {
        return entity;
    }

    public Object getToPlanningValue() {
        return toPlanningValue;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        Object oldPlanningValue = variableDescriptor.getValue(entity);
        return !ObjectUtils.equals(oldPlanningValue, toPlanningValue);
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        Object oldPlanningValue = variableDescriptor.getValue(entity);
        return new ChangeMove(entity, variableDescriptor, oldPlanningValue);
    }

    public void doMove(ScoreDirector scoreDirector) {
        scoreDirector.beforeVariableChanged(entity, variableDescriptor.getVariableName());
        variableDescriptor.setValue(entity, toPlanningValue);
        scoreDirector.afterVariableChanged(entity, variableDescriptor.getVariableName());
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(entity);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toPlanningValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ChangeMove) {
            ChangeMove other = (ChangeMove) o;
            return new EqualsBuilder()
                    .append(entity, other.entity)
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
                .append(entity)
                .append(variableDescriptor.getVariableName())
                .append(toPlanningValue)
                .toHashCode();
    }

    public String toString() {
        return entity + " => " + toPlanningValue;
    }

}
