/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collections;
import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableSupply;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * Also known as a 2-opt move.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class TailChainSwapMove<Solution_> extends AbstractMove<Solution_> {

    protected final GenuineVariableDescriptor<Solution_> variableDescriptor;

    protected final Object leftEntity;
    protected final Object leftValue;
    protected final Object leftAnchor;

    protected final Object rightEntity; // Sometimes null
    protected final Object rightValue;
    protected final Object rightAnchor;

    protected final boolean sameAnchor;
    protected final Object leftNextEntity;
    protected final Object rightNextEntity;
    protected final boolean reverseAnchorSide;
    protected final Object lastEntityInChain;
    protected final Object entityAfterAnchor;

    public TailChainSwapMove(GenuineVariableDescriptor<Solution_> variableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply, AnchorVariableSupply anchorVariableSupply,
            Object leftEntity, Object rightValue) {
        this.variableDescriptor = variableDescriptor;
        this.leftEntity = leftEntity;
        leftValue = variableDescriptor.getValue(leftEntity);
        leftAnchor = anchorVariableSupply.getAnchor(leftEntity);
        rightEntity = inverseVariableSupply.getInverseSingleton(rightValue);
        this.rightValue = rightValue;
        rightAnchor = variableDescriptor.isValuePotentialAnchor(rightValue) ? rightValue
                : anchorVariableSupply.getAnchor(rightValue);
        sameAnchor = leftAnchor == rightAnchor;
        if (!sameAnchor) {
            leftNextEntity = null;
            rightNextEntity = null;
            reverseAnchorSide = false;
            lastEntityInChain = null;
            entityAfterAnchor = null;
        } else {
            leftNextEntity = inverseVariableSupply.getInverseSingleton(leftEntity);
            rightNextEntity = rightEntity == null ? null : inverseVariableSupply.getInverseSingleton(rightEntity);
            Object lastEntityInChainOrLeftEntity = findLastEntityInChainOrLeftEntity(inverseVariableSupply);
            reverseAnchorSide = lastEntityInChainOrLeftEntity != leftEntity;
            if (reverseAnchorSide) {
                lastEntityInChain = lastEntityInChainOrLeftEntity;
                entityAfterAnchor = inverseVariableSupply.getInverseSingleton(leftAnchor);
            } else {
                lastEntityInChain = null;
                entityAfterAnchor = null;
            }
        }
    }

    // TODO Workaround until https://issues.redhat.com/browse/PLANNER-1250 is fixed
    protected TailChainSwapMove(GenuineVariableDescriptor<Solution_> variableDescriptor,
            Object leftEntity, Object leftValue, Object leftAnchor,
            Object rightEntity, Object rightValue, Object rightAnchor) {
        this.variableDescriptor = variableDescriptor;
        this.leftEntity = leftEntity;
        this.leftValue = leftValue;
        this.leftAnchor = leftAnchor;
        this.rightEntity = rightEntity;
        this.rightValue = rightValue;
        this.rightAnchor = rightAnchor;
        this.sameAnchor = false;
        this.leftNextEntity = null;
        this.rightNextEntity = null;
        this.reverseAnchorSide = false;
        this.lastEntityInChain = null;
        this.entityAfterAnchor = null;
    }

    // TODO Workaround until https://issues.redhat.com/browse/PLANNER-1250 is fixed
    protected TailChainSwapMove(GenuineVariableDescriptor<Solution_> variableDescriptor,
            Object leftEntity, Object leftValue, Object leftAnchor,
            Object rightEntity, Object rightValue, Object rightAnchor,
            Object leftNextEntity, Object rightNextEntity) {
        this.variableDescriptor = variableDescriptor;
        this.leftEntity = leftEntity;
        this.leftValue = leftValue;
        this.leftAnchor = leftAnchor;
        this.rightEntity = rightEntity;
        this.rightValue = rightValue;
        this.rightAnchor = rightAnchor;
        this.sameAnchor = true;
        this.leftNextEntity = leftNextEntity;
        this.rightNextEntity = rightNextEntity;
        this.reverseAnchorSide = false;
        this.lastEntityInChain = null;
        this.entityAfterAnchor = null;
    }

    // TODO Workaround until https://issues.redhat.com/browse/PLANNER-1250 is fixed
    protected TailChainSwapMove(GenuineVariableDescriptor<Solution_> variableDescriptor,
            Object leftEntity, Object leftValue, Object leftAnchor,
            Object rightEntity, Object rightValue, Object rightAnchor,
            Object leftNextEntity, Object rightNextEntity, Object lastEntityInChain, Object entityAfterAnchor) {
        this.variableDescriptor = variableDescriptor;
        this.leftEntity = leftEntity;
        this.leftValue = leftValue;
        this.leftAnchor = leftAnchor;
        this.rightEntity = rightEntity;
        this.rightValue = rightValue;
        this.rightAnchor = rightAnchor;
        this.sameAnchor = true;
        this.leftNextEntity = leftNextEntity;
        this.rightNextEntity = rightNextEntity;
        this.reverseAnchorSide = true;
        this.lastEntityInChain = lastEntityInChain;
        this.entityAfterAnchor = entityAfterAnchor;
    }

    private Object findLastEntityInChainOrLeftEntity(SingletonInverseVariableSupply inverseVariableSupply) {
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

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        if (Objects.equals(leftValue, rightValue)
                || Objects.equals(leftEntity, rightValue) || Objects.equals(rightEntity, leftValue)) {
            return false;
        }
        if (rightEntity == null) {
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
        if (!sameAnchor) {
            return new TailChainSwapMove<>(variableDescriptor,
                    leftEntity, rightValue, rightAnchor,
                    rightEntity, leftValue, leftAnchor);
        } else {
            if (rightEntity == null) {
                // TODO Currently unsupported because we fail to create a valid undoMove... even though doMove supports it
                // https://issues.redhat.com/browse/PLANNER-1250
                throw new IllegalStateException("Impossible state, because isMoveDoable() should not return true.");
            }
            if (!reverseAnchorSide) {
                return new TailChainSwapMove<>(variableDescriptor,
                        rightEntity, rightNextEntity, leftAnchor,
                        leftEntity, rightValue, rightAnchor,
                        leftNextEntity, leftValue);
            } else {
                return new TailChainSwapMove<>(variableDescriptor,
                        rightEntity, rightNextEntity, leftAnchor,
                        leftEntity, rightValue, rightAnchor,
                        leftNextEntity, leftValue, entityAfterAnchor, lastEntityInChain);
            }
        }
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_> innerScoreDirector = (InnerScoreDirector<Solution_>) scoreDirector;
        if (!sameAnchor) {
            // Change the left entity
            innerScoreDirector.changeVariableFacade(variableDescriptor, leftEntity, rightValue);
            // Change the right entity
            if (rightEntity != null) {
                innerScoreDirector.changeVariableFacade(variableDescriptor, rightEntity, leftValue);
            }
        } else {
            if (!reverseAnchorSide) {
                // Reverses loop on the side that doesn't include the anchor, because rightValue is earlier than leftEntity
                innerScoreDirector.changeVariableFacade(variableDescriptor, leftEntity, rightValue);
                reverseChain(innerScoreDirector, leftValue, leftEntity, rightEntity);
                if (leftNextEntity != null) {
                    innerScoreDirector.changeVariableFacade(variableDescriptor, leftNextEntity, rightEntity);
                }
            } else {
                // Reverses loop on the side that does include the anchor, because rightValue is later than leftEntity
                // Change the head of the chain
                reverseChain(innerScoreDirector, leftValue, leftEntity, entityAfterAnchor);
                // Change leftEntity
                innerScoreDirector.changeVariableFacade(variableDescriptor, leftEntity, rightValue);
                // Change the tail of the chain
                reverseChain(innerScoreDirector, lastEntityInChain, leftAnchor, rightEntity);
                innerScoreDirector.changeVariableFacade(variableDescriptor, leftNextEntity, rightEntity);
            }
        }
    }

    protected void reverseChain(InnerScoreDirector scoreDirector, Object fromValue, Object fromEntity, Object toEntity) {
        Object entity = fromValue;
        Object newValue = fromEntity;
        while (newValue != toEntity) {
            Object oldValue = variableDescriptor.getValue(entity);
            scoreDirector.changeVariableFacade(variableDescriptor, entity, newValue);
            newValue = entity;
            entity = oldValue;
        }
    }

    @Override
    public TailChainSwapMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        if (!sameAnchor) {
            return new TailChainSwapMove<>(variableDescriptor,
                    destinationScoreDirector.lookUpWorkingObject(leftEntity),
                    destinationScoreDirector.lookUpWorkingObject(leftValue),
                    destinationScoreDirector.lookUpWorkingObject(leftAnchor),
                    destinationScoreDirector.lookUpWorkingObject(rightEntity),
                    destinationScoreDirector.lookUpWorkingObject(rightValue),
                    destinationScoreDirector.lookUpWorkingObject(rightAnchor));
        } else {
            if (!reverseAnchorSide) {
                return new TailChainSwapMove<>(variableDescriptor,
                        destinationScoreDirector.lookUpWorkingObject(leftEntity),
                        destinationScoreDirector.lookUpWorkingObject(leftValue),
                        destinationScoreDirector.lookUpWorkingObject(leftAnchor),
                        destinationScoreDirector.lookUpWorkingObject(rightEntity),
                        destinationScoreDirector.lookUpWorkingObject(rightValue),
                        destinationScoreDirector.lookUpWorkingObject(rightAnchor),
                        destinationScoreDirector.lookUpWorkingObject(leftNextEntity),
                        destinationScoreDirector.lookUpWorkingObject(rightNextEntity));
            } else {
                return new TailChainSwapMove<>(variableDescriptor,
                        destinationScoreDirector.lookUpWorkingObject(leftEntity),
                        destinationScoreDirector.lookUpWorkingObject(leftValue),
                        destinationScoreDirector.lookUpWorkingObject(leftAnchor),
                        destinationScoreDirector.lookUpWorkingObject(rightEntity),
                        destinationScoreDirector.lookUpWorkingObject(rightValue),
                        destinationScoreDirector.lookUpWorkingObject(rightAnchor),
                        destinationScoreDirector.lookUpWorkingObject(leftNextEntity),
                        destinationScoreDirector.lookUpWorkingObject(rightNextEntity),
                        destinationScoreDirector.lookUpWorkingObject(lastEntityInChain),
                        destinationScoreDirector.lookUpWorkingObject(entityAfterAnchor));
            }
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
        if (rightEntity == null) {
            return Collections.singleton(leftEntity);
        }
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
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TailChainSwapMove<?> other = (TailChainSwapMove<?>) o;
        return Objects.equals(leftEntity, other.leftEntity) &&
                Objects.equals(rightValue, other.rightValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftEntity, rightValue);
    }

    @Override
    public String toString() {
        return leftEntity + " {" + leftValue + "} <-tailChainSwap-> " + rightEntity + " {" + rightValue + "}";
    }

}
