/*
 * Copyright 2015 JBoss Inc
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

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableSupply;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Also known as a 2-opt move.
 */
public class TailChainSwapMove extends AbstractMove {

    protected final GenuineVariableDescriptor variableDescriptor;
    protected final SingletonInverseVariableSupply inverseVariableSupply;
    protected final AnchorVariableSupply anchorVariableSupply;

    protected final Object leftEntity;
    protected final Object rightValue;

    public TailChainSwapMove(GenuineVariableDescriptor variableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply, AnchorVariableSupply anchorVariableSupply,
            Object leftEntity, Object rightValue) {
        this.variableDescriptor = variableDescriptor;
        this.inverseVariableSupply = inverseVariableSupply;
        this.anchorVariableSupply = anchorVariableSupply;
        this.leftEntity = leftEntity;
        this.rightValue = rightValue;
    }

    public Object getLeftEntity() {
        return leftEntity;
    }

    public Object getRightValue() {
        return rightValue;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        Object leftValue = variableDescriptor.getValue(leftEntity);
        Object rightEntity = inverseVariableSupply.getInverseSingleton(rightValue);
        if (ObjectUtils.equals(leftValue, rightValue)
                || ObjectUtils.equals(leftEntity, rightValue) || ObjectUtils.equals(rightEntity, leftValue) ) {
            return false;
        }
        if (rightEntity == null) {
            Object leftAnchor = anchorVariableSupply.getAnchor(leftEntity);
            Object rightAnchor = !variableDescriptor.isValueNoPotentialAnchor(rightValue) ? rightValue
                    : anchorVariableSupply.getAnchor(rightValue);
            // TODO Currently unsupported because we fail to create a valid undoMove... even though doMove supports it
            if (leftAnchor == rightAnchor) {
                return false;
            }
        }
        if (!variableDescriptor.isValueRangeEntityIndependent()) {
            ValueRangeDescriptor valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
            Solution workingSolution = scoreDirector.getWorkingSolution();
            if (rightEntity != null) {
                ValueRange rightValueRange = valueRangeDescriptor.extractValueRange(workingSolution, rightEntity);
                if (!rightValueRange.contains(leftValue)) {
                    return false;
                }
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
        Object rightAnchor = !variableDescriptor.isValueNoPotentialAnchor(rightValue) ? rightValue
                : anchorVariableSupply.getAnchor(rightValue);
        Object leftValue = variableDescriptor.getValue(leftEntity);
        if (leftAnchor != rightAnchor) {
            return new TailChainSwapMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply,
                    leftEntity, leftValue);
        } else {
            Object rightEntity = inverseVariableSupply.getInverseSingleton(rightValue);
            if (rightEntity != null) {
                return new TailChainSwapMove(variableDescriptor, inverseVariableSupply, anchorVariableSupply,
                        rightEntity, rightValue);
            } else {
                // TODO Currently unsupported because we fail to create a valid undoMove... even though doMove supports it
                throw new IllegalStateException("Impossible state, because isMoveDoable() should not return true.");
            }
        }
    }

    public void doMove(ScoreDirector scoreDirector) {
        Object leftAnchor = anchorVariableSupply.getAnchor(leftEntity);
        Object rightAnchor = !variableDescriptor.isValueNoPotentialAnchor(rightValue) ? rightValue
                : anchorVariableSupply.getAnchor(rightValue);
        Object leftValue = variableDescriptor.getValue(leftEntity);
        Object rightEntity = inverseVariableSupply.getInverseSingleton(rightValue); // Sometimes null
        if (leftAnchor != rightAnchor) {
            // HACK mixed order to avoid VariableListener trouble - requires predicable VariableListener firing to fix
            scoreDirector.beforeVariableChanged(variableDescriptor, leftEntity);
            if (rightEntity != null) {
                scoreDirector.beforeVariableChanged(variableDescriptor, rightEntity);
            }
            variableDescriptor.setValue(leftEntity, rightValue);
            if (rightEntity != null) {
                variableDescriptor.setValue(rightEntity, leftValue);
            }
            scoreDirector.afterVariableChanged(variableDescriptor, leftEntity);
            if (rightEntity != null) {
                scoreDirector.afterVariableChanged(variableDescriptor, rightEntity);
            }
        } else {
            boolean rightValueIsLater = isRightValueLaterThanLeftEntity(leftAnchor);
            if (!rightValueIsLater) {
                // Reverses loop on the side that doesn't include anchor
                Object leftNextEntity = inverseVariableSupply.getInverseSingleton(leftEntity);
                // HACK mixed order to avoid VariableListener trouble - requires predicable VariableListener firing to fix
                scoreDirector.beforeVariableChanged(variableDescriptor, leftEntity);
                variableDescriptor.setValue(leftEntity, rightValue);
                // Unhook the leftNextEntity before the loop
                if (leftNextEntity != null) {
                    scoreDirector.beforeVariableChanged(variableDescriptor, leftNextEntity);
                }
                Object entity = leftValue;
                Object previousEntity = leftEntity;
                while (previousEntity != rightEntity) {
                    Object value = variableDescriptor.getValue(entity);
                    scoreDirector.changeVariableFacade(variableDescriptor, entity, previousEntity);
                    previousEntity = entity;
                    entity = value;
                }
                // Remainder of the laterEntity handling
                scoreDirector.afterVariableChanged(variableDescriptor, leftEntity);
                // Remainder of the leftNextEntity handling
                if (leftNextEntity != null) {
                    variableDescriptor.setValue(leftNextEntity, previousEntity);
                    scoreDirector.afterVariableChanged(variableDescriptor, leftNextEntity);
                }
            } else {
                // Reverses loop on the side that does include anchor
                Object leftNextEntity = inverseVariableSupply.getInverseSingleton(leftEntity);
                // HACK mixed order to avoid VariableListener trouble - requires predicable VariableListener firing to fix
                scoreDirector.beforeVariableChanged(variableDescriptor, inverseVariableSupply.getInverseSingleton(leftAnchor));
                // Change the tail of the chain
                Object lastEntity = rightValue;
                while (true) {
                    Object next = inverseVariableSupply.getInverseSingleton(lastEntity);
                    if (next == null) {
                        break;
                    }
                    lastEntity = next;
                }
                Object entity = lastEntity;
                Object previousEntity = leftAnchor;
                while (entity != rightValue) {
                    Object value = variableDescriptor.getValue(entity);
                    scoreDirector.changeVariableFacade(variableDescriptor, entity, previousEntity);
                    previousEntity = entity;
                    entity = value;
                }
                // Change the non-anchor loop
                if (leftNextEntity != null) {
                    scoreDirector.changeVariableFacade(variableDescriptor, leftNextEntity, previousEntity);
                }
                // Change the head of the chain
                entity = leftEntity;
                previousEntity = rightValue;
                while (entity != leftAnchor) {
                    Object value = variableDescriptor.getValue(entity);
                    if (value == leftAnchor) {
                        // HACK mixed order to avoid VariableListener trouble - requires predicable VariableListener firing to fix
                        // Remainder of the inverseVariableSupply.getInverseSingleton(leftAnchor) handling
                        variableDescriptor.setValue(entity, previousEntity);
                        scoreDirector.afterVariableChanged(variableDescriptor, entity);
                    } else {
                        scoreDirector.changeVariableFacade(variableDescriptor, entity, previousEntity);
                    }
                    previousEntity = entity;
                    entity = value;
                }
            }
        }
    }

    protected boolean isRightValueLaterThanLeftEntity(Object anchor) {
        // (leftEntity != rightValue) is always true because isMoveDoable() check that
        Object value = rightValue;
        while (value != anchor) {
            if (value == leftEntity) {
                return true;
            }
            value = variableDescriptor.getValue(value);
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
        Object rightEntity = inverseVariableSupply.getInverseSingleton(rightValue);
        return Arrays.asList(leftEntity, rightEntity);
    }

    public Collection<? extends Object> getPlanningValues() {
        Object leftValue = variableDescriptor.getValue(leftEntity);
        return Arrays.asList(leftValue, rightValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TailChainSwapMove) {
            TailChainSwapMove other = (TailChainSwapMove) o;
            return new EqualsBuilder()
                    .append(leftEntity, other.leftEntity)
                    .append(rightValue, other.rightValue)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftEntity)
                .append(rightValue)
                .toHashCode();
    }

    public String toString() {
        return leftEntity + " =tailChainSwap=> " + rightValue;
    }

}
