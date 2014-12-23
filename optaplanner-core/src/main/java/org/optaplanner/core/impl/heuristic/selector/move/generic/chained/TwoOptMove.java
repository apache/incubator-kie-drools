/*
 * Copyright 2014 JBoss Inc
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableSupply;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class TwoOptMove extends AbstractMove {

    protected final GenuineVariableDescriptor variableDescriptor;
    protected final AnchorVariableSupply anchorVariableSupply;

    protected final Object leftEntity;
    protected final Object rightEntity;

    public TwoOptMove(GenuineVariableDescriptor variableDescriptor, AnchorVariableSupply anchorVariableSupply,
            Object leftEntity, Object rightEntity) {
        this.variableDescriptor = variableDescriptor;
        this.anchorVariableSupply = anchorVariableSupply;
        this.leftEntity = leftEntity;
        this.rightEntity = rightEntity;
    }

    public Object getLeftEntity() {
        return leftEntity;
    }

    public Object getRightEntity() {
        return rightEntity;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        Object leftValue = variableDescriptor.getValue(leftEntity);
        Object rightValue = variableDescriptor.getValue(rightEntity);
        if (ObjectUtils.equals(leftValue, rightValue)
                || ObjectUtils.equals(leftEntity, rightValue) || ObjectUtils.equals(rightEntity, leftValue) ) {
            return false;
        }
        if (!variableDescriptor.isValueRangeEntityIndependent()) {
            ValueRangeDescriptor valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
            Solution workingSolution = scoreDirector.getWorkingSolution();
            ValueRange rightValueRange = valueRangeDescriptor.extractValueRange(workingSolution, rightEntity);
            if (!rightValueRange.contains(leftValue)) {
                return false;
            }
            ValueRange leftValueRange = valueRangeDescriptor.extractValueRange(workingSolution, leftEntity);
            if (!leftValueRange.contains(rightValue)) {
                return false;
            }
        }
        return true;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        Object leftAnchor = anchorVariableSupply.getAnchor(leftEntity);
        Object rightAnchor = anchorVariableSupply.getAnchor(rightEntity);
        boolean sameAnchor = leftAnchor == rightAnchor;
        if (!sameAnchor) {
            return new TwoOptMove(variableDescriptor, anchorVariableSupply, rightEntity, leftEntity);
        } else {
            boolean rightEntityIsLater = isRightEntityLater(rightAnchor, rightEntity);
            Object laterEntity = rightEntityIsLater ? rightEntity : leftEntity;
            return new TwoOptMove(variableDescriptor, anchorVariableSupply,
                    laterEntity, variableDescriptor.getValue(laterEntity));
        }
    }

    public void doMove(ScoreDirector scoreDirector) {
        Object leftAnchor = anchorVariableSupply.getAnchor(leftEntity);
        Object rightAnchor = anchorVariableSupply.getAnchor(rightEntity);
        boolean sameAnchor = leftAnchor == rightAnchor;
        Object oldLeftValue = variableDescriptor.getValue(leftEntity);
        Object oldRightValue = variableDescriptor.getValue(rightEntity);
        if (!sameAnchor) {
            // HACK mixed order to avoid VariableListener trouble - requires predicable VariableListener firing to fix
            scoreDirector.beforeVariableChanged(variableDescriptor, leftEntity);
            scoreDirector.beforeVariableChanged(variableDescriptor, rightEntity);
            variableDescriptor.setValue(leftEntity, oldRightValue);
            variableDescriptor.setValue(rightEntity, oldLeftValue);
            scoreDirector.afterVariableChanged(variableDescriptor, leftEntity);
            scoreDirector.afterVariableChanged(variableDescriptor, rightEntity);
        } else {
            boolean rightEntityIsLater = isRightEntityLater(rightAnchor, oldRightValue);
            Object firstEntity = rightEntityIsLater ? leftEntity : rightEntity;
            Object laterEntity = rightEntityIsLater ? rightEntity : leftEntity;
            Object oldFirstValue = rightEntityIsLater ? oldLeftValue : oldRightValue;
            Object oldLaterValue = rightEntityIsLater ? oldRightValue : oldLeftValue;
            // HACK mixed order to avoid VariableListener trouble - requires predicable VariableListener firing to fix
            scoreDirector.beforeVariableChanged(variableDescriptor, laterEntity);
            variableDescriptor.setValue(laterEntity, firstEntity);
            // Unhook the firstEntity before the loop
            scoreDirector.beforeVariableChanged(variableDescriptor, firstEntity);
            Object entity = oldLaterValue;
            Object nextEntity = oldFirstValue;
            while (entity != firstEntity) {
                Object value = variableDescriptor.getValue(entity);
                scoreDirector.changeVariableFacade(variableDescriptor, entity, nextEntity);
                nextEntity = entity;
                entity = value;
            }
            // Remainder of the laterEntity handling
            scoreDirector.afterVariableChanged(variableDescriptor, laterEntity);
            // Remainder of the firstEntity handling
            variableDescriptor.setValue(firstEntity, nextEntity);
            scoreDirector.afterVariableChanged(variableDescriptor, firstEntity);
        }
    }

    protected boolean isRightEntityLater(Object rightAnchor, Object oldRightValue) {
        Object entity = oldRightValue;
        while (entity != rightAnchor) {
            if (entity == leftEntity) {
                return true;
            }
            entity = variableDescriptor.getValue(entity);
        }
        return false;
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftEntity, rightEntity);
    }

    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<Object>(2);
        values.add(variableDescriptor.getValue(leftEntity));
        values.add(variableDescriptor.getValue(rightEntity));
        return values;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TwoOptMove) {
            TwoOptMove other = (TwoOptMove) o;
            return new EqualsBuilder()
                    .append(leftEntity, other.leftEntity)
                    .append(rightEntity, other.rightEntity)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftEntity)
                .append(rightEntity)
                .toHashCode();
    }

    public String toString() {
        return leftEntity + " <=2opt=> " + rightEntity;
    }

}
