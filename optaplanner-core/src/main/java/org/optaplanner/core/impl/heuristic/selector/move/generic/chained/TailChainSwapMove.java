/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableSupply;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Also known as a 2-opt move.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class TailChainSwapMove<Solution_> extends AbstractMove<Solution_> {

    protected final GenuineVariableDescriptor<Solution_> variableDescriptor;
    protected final SingletonInverseVariableSupply inverseVariableSupply;
    protected final AnchorVariableSupply anchorVariableSupply;

    protected final Object leftEntity;
    protected final Object rightValue;

    public TailChainSwapMove(GenuineVariableDescriptor<Solution_> variableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply, AnchorVariableSupply anchorVariableSupply,
            Object leftEntity, Object rightValue) {
        this.variableDescriptor = variableDescriptor;
        this.inverseVariableSupply = inverseVariableSupply;
        this.anchorVariableSupply = anchorVariableSupply;
        this.leftEntity = leftEntity;
        this.rightValue = rightValue;
    }

    public String getVariableName() {
        return variableDescriptor.getVariableName();
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

    protected Object determineRightAnchor() {
        return variableDescriptor.isValuePotentialAnchor(rightValue) ? rightValue
                : anchorVariableSupply.getAnchor(rightValue);
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        Object leftValue = variableDescriptor.getValue(leftEntity);
        Object rightEntity = inverseVariableSupply.getInverseSingleton(rightValue);
        if (Objects.equals(leftValue, rightValue)
                || Objects.equals(leftEntity, rightValue) || Objects.equals(rightEntity, leftValue)) {
            return false;
        }
        if (rightEntity == null) {
            Object leftAnchor = anchorVariableSupply.getAnchor(leftEntity);
            Object rightAnchor = determineRightAnchor();
            // TODO Currently unsupported because we fail to create a valid undoMove... even though doMove supports it
            if (leftAnchor == rightAnchor) {
                return false;
            }
        }
        if (!variableDescriptor.isValueRangeEntityIndependent()) {
            ValueRangeDescriptor<Solution_> valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
            Solution_ workingSolution = scoreDirector.getWorkingSolution();
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

    @Override
    public TailChainSwapMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        Object leftAnchor = anchorVariableSupply.getAnchor(leftEntity);
        Object rightAnchor = determineRightAnchor();
        Object leftValue = variableDescriptor.getValue(leftEntity);
        if (leftAnchor != rightAnchor) {
            return new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, leftEntity,
                    leftValue);
        } else {
            Object rightEntity = inverseVariableSupply.getInverseSingleton(rightValue);
            if (rightEntity != null) {
                return new TailChainSwapMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply,
                        rightEntity, rightValue);
            } else {
                // TODO Currently unsupported because we fail to create a valid undoMove... even though doMove supports it
                throw new IllegalStateException("Impossible state, because isMoveDoable() should not return true.");
            }
        }
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        Object leftAnchor = anchorVariableSupply.getAnchor(leftEntity);
        Object rightAnchor = determineRightAnchor();
        Object leftValue = variableDescriptor.getValue(leftEntity);
        Object rightEntity = inverseVariableSupply.getInverseSingleton(rightValue); // Sometimes null
        if (leftAnchor != rightAnchor) {
            // Change the left entity
            scoreDirector.changeVariableFacade(variableDescriptor, leftEntity, rightValue);
            // Change the right entity
            if (rightEntity != null) {
                scoreDirector.changeVariableFacade(variableDescriptor, rightEntity, leftValue);
            }
        } else {
            Object lastEntityInChainOrLeftEntity = findLastEntityInChainOrLeftEntity();
            if (lastEntityInChainOrLeftEntity == leftEntity) {
                // Reverses loop on the side that doesn't include the anchor, because rightValue is earlier than leftEntity
                Object leftNextEntity = inverseVariableSupply.getInverseSingleton(leftEntity);
                scoreDirector.changeVariableFacade(variableDescriptor, leftEntity, rightValue);
                reverseChain(scoreDirector, leftValue, leftEntity, rightEntity);
                if (leftNextEntity != null) {
                    scoreDirector.changeVariableFacade(variableDescriptor, leftNextEntity, rightEntity);
                }
            } else {
                // Reverses loop on the side that does include the anchor, because rightValue is later than leftEntity
                Object lastEntityInChain = lastEntityInChainOrLeftEntity;
                Object leftNextEntity = inverseVariableSupply.getInverseSingleton(leftEntity);
                Object entityAfterAnchor = inverseVariableSupply.getInverseSingleton(leftAnchor);
                // Change the head of the chain
                reverseChain(scoreDirector, leftValue, leftEntity, entityAfterAnchor);
                // Change leftEntity
                scoreDirector.changeVariableFacade(variableDescriptor, leftEntity, rightValue);
                // Change the tail of the chain
                reverseChain(scoreDirector, lastEntityInChain, leftAnchor, rightEntity);
                scoreDirector.changeVariableFacade(variableDescriptor, leftNextEntity, rightEntity);
            }
        }
    }

    protected Object findLastEntityInChainOrLeftEntity() {
        Object entity = rightValue;
        while (entity != leftEntity) {
            Object nextEntity = inverseVariableSupply.getInverseSingleton(entity);
            if (nextEntity == null) {
                return entity;
            }
            entity = nextEntity;
        }
        return leftEntity;
    }

    protected void reverseChain(ScoreDirector scoreDirector, Object fromValue, Object fromEntity, Object toEntity) {
        Object entity = fromValue;
        Object newValue = fromEntity;
        while (newValue != toEntity) {
            Object oldValue = variableDescriptor.getValue(entity);
            scoreDirector.changeVariableFacade(variableDescriptor, entity, newValue);
            newValue = entity;
            entity = oldValue;
        }
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        Object rightEntity = inverseVariableSupply.getInverseSingleton(rightValue);
        return Arrays.asList(leftEntity, rightEntity);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        Object leftValue = variableDescriptor.getValue(leftEntity);
        return Arrays.asList(leftValue, rightValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TailChainSwapMove) {
            TailChainSwapMove<?> other = (TailChainSwapMove) o;
            return new EqualsBuilder()
                    .append(leftEntity, other.leftEntity)
                    .append(rightValue, other.rightValue)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftEntity)
                .append(rightValue)
                .toHashCode();
    }

    @Override
    public String toString() {
        Object leftValue = variableDescriptor.getValue(leftEntity);
        Object rightEntity = inverseVariableSupply.getInverseSingleton(rightValue);
        return leftEntity + " {" + leftValue + "} <-tailChainSwap-> " + rightEntity + " {" + rightValue + "}";
    }

}
