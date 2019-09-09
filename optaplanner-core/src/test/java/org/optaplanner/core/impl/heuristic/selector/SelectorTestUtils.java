/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.lang3.ArrayUtils;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedObject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SelectorTestUtils {

    public static SolutionDescriptor mockSolutionDescriptor() {
        SolutionDescriptor solutionDescriptor = mock(SolutionDescriptor.class);
        return solutionDescriptor;
    }

    public static EntityDescriptor mockEntityDescriptor(Class<?> entityClass) {
        EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.getEntityClass()).thenReturn(entityClass);
        return entityDescriptor;
    }

    public static GenuineVariableDescriptor mockVariableDescriptor(Class<?> entityClass, String variableName) {
        EntityDescriptor entityDescriptor = mockEntityDescriptor(entityClass);
        return mockVariableDescriptor(entityDescriptor, variableName);
    }

    public static GenuineVariableDescriptor mockVariableDescriptor(EntityDescriptor entityDescriptor,
            String variableName) {
        GenuineVariableDescriptor variableDescriptor = mock(GenuineVariableDescriptor.class);
        when(variableDescriptor.getEntityDescriptor()).thenReturn(entityDescriptor);
        when(variableDescriptor.getVariableName()).thenReturn(variableName);
        return variableDescriptor;
    }

    public static EntitySelector mockEntitySelector(Class<?> entityClass, Object... entities) {
        EntityDescriptor entityDescriptor = mockEntityDescriptor(entityClass);
        return mockEntitySelector(entityDescriptor, entities);
    }

    public static EntitySelector mockEntitySelector(EntityDescriptor entityDescriptor,
            Object... entities) {
        EntitySelector entitySelector = mock(EntitySelector.class);
        when(entitySelector.getEntityDescriptor()).thenReturn(entityDescriptor);
        final List<Object> entityList = Arrays.<Object>asList(entities);
        when(entitySelector.iterator()).thenAnswer(invocation -> entityList.iterator());
        when(entitySelector.listIterator()).thenAnswer(invocation -> entityList.listIterator());
        when(entitySelector.spliterator()).thenAnswer(invocation -> entityList.spliterator());
        for (int i = 0; i < entityList.size(); i++) {
            final int index = i;
            when(entitySelector.listIterator(index)).thenAnswer(invocation -> entityList.listIterator(index));
        }
        when(entitySelector.endingIterator()).thenAnswer(invocation -> entityList.iterator());
        when(entitySelector.isCountable()).thenReturn(true);
        when(entitySelector.isNeverEnding()).thenReturn(false);
        when(entitySelector.getSize()).thenReturn((long) entityList.size());
        return entitySelector;
    }

    public static ValueSelector mockValueSelector(Class<?> entityClass, String variableName, Object... values) {
        GenuineVariableDescriptor variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        return mockValueSelector(variableDescriptor, values);
    }

    public static ValueSelector mockValueSelector(EntityDescriptor entityDescriptor, String variableName,
            Object... values) {
        GenuineVariableDescriptor variableDescriptor = mockVariableDescriptor(entityDescriptor, variableName);
        return mockValueSelector(variableDescriptor, values);
    }

    public static ValueSelector mockValueSelector(GenuineVariableDescriptor variableDescriptor, Object... values) {
        ValueSelector valueSelector = mock(ValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        final List<Object> valueList = Arrays.<Object>asList(values);
        when(valueSelector.iterator(any())).thenAnswer(invocation -> valueList.iterator());
        when(valueSelector.isCountable()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        when(valueSelector.getSize(any())).thenReturn((long) valueList.size());
        return valueSelector;
    }

    public static ValueSelector mockValueSelectorForEntity(Class<?> entityClass, Object entity, String variableName,
            Object... values) {
        return mockValueSelectorForEntity(entityClass, variableName,
                ImmutableListMultimap.builder().putAll(entity, values).build());
    }

    public static ValueSelector mockValueSelectorForEntity(Class<?> entityClass, String variableName,
            ListMultimap<Object, Object> entityToValues) {
        GenuineVariableDescriptor variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        return mockValueSelectorForEntity(variableDescriptor, entityToValues);
    }

    public static ValueSelector mockValueSelectorForEntity(GenuineVariableDescriptor variableDescriptor,
            ListMultimap<Object, Object> entityToValues) {
        ValueSelector valueSelector = mock(ValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        for (Map.Entry<Object, Collection<Object>> entry : entityToValues.asMap().entrySet()) {
            Object entity = entry.getKey();
            final List<Object> valueList = (List<Object>) entry.getValue();
            when(valueSelector.getSize(entity)).thenAnswer(invocation -> (long) valueList.size());
            when(valueSelector.iterator(entity)).thenAnswer(invocation -> valueList.iterator());
            when(valueSelector.getSize(entity)).thenReturn((long) valueList.size());
        }
        when(valueSelector.isCountable()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        return valueSelector;
    }

    public static EntityIndependentValueSelector mockEntityIndependentValueSelector(Class<?> entityClass,
            String variableName, Object... values) {
        GenuineVariableDescriptor variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        when(variableDescriptor.isValueRangeEntityIndependent()).thenReturn(true);
        return mockEntityIndependentValueSelector(variableDescriptor, values);
    }

    public static EntityIndependentValueSelector mockEntityIndependentValueSelector(
            GenuineVariableDescriptor variableDescriptor, Object... values) {
        EntityIndependentValueSelector valueSelector = mock(EntityIndependentValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        final List<Object> valueList = Arrays.<Object>asList(values);
        when(valueSelector.iterator(any())).thenAnswer(invocation -> valueList.iterator());
        when(valueSelector.endingIterator(any())).thenAnswer(invocation -> valueList.iterator());
        when(valueSelector.iterator()).thenAnswer(invocation -> valueList.iterator());
        when(valueSelector.isCountable()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        when(valueSelector.getSize(any())).thenReturn((long) valueList.size());
        when(valueSelector.getSize()).thenReturn((long) valueList.size());
        return valueSelector;
    }

    public static MoveSelector mockMoveSelector(Class<?> moveClass,
            Move... moves) {
        MoveSelector moveSelector = mock(MoveSelector.class);
        final List<Move> moveList = Arrays.<Move>asList(moves);
        when(moveSelector.iterator()).thenAnswer(invocation -> moveList.iterator());
        when(moveSelector.isCountable()).thenReturn(true);
        when(moveSelector.isNeverEnding()).thenReturn(false);
        when(moveSelector.getCacheType()).thenReturn(SelectionCacheType.JUST_IN_TIME);
        when(moveSelector.getSize()).thenReturn((long) moveList.size());
        when(moveSelector.supportsPhaseAndSolverCaching()).thenReturn(true);
        return moveSelector;
    }

    public static SingletonInverseVariableSupply mockSingletonInverseVariableSupply(
            final TestdataChainedEntity[] allEntities) {
        return planningValue -> {
            for (TestdataChainedEntity entity : allEntities) {
                if (entity.getChainedObject().equals(planningValue)) {
                    return entity;
                }
            }
            return null;
        };
    }

    public static void assertChain(TestdataChainedObject... chainedObjects) {
        TestdataChainedObject chainedObject = chainedObjects[0];
        for (int i = 1; i < chainedObjects.length; i++) {
            TestdataChainedEntity chainedEntity = (TestdataChainedEntity) chainedObjects[i];
            if (!Objects.equals(chainedObject, chainedEntity.getChainedObject())) {
                fail("Chain assertion failed for chainedEntity (" + chainedEntity + ").\n"
                        + "Expected: " + chainedObject + "\n"
                        + "Actual:   "  + chainedEntity.getChainedObject() + "\n"
                        + "Expected chain: " + Arrays.toString(chainedObjects) + "\n"
                        + "Actual chain:   " + Arrays.toString(ArrayUtils.subarray(chainedObjects, 0, i)) + " ... [" + chainedEntity.getChainedObject() + ", " + chainedEntity + "] ...");
            }
            chainedObject = chainedEntity;
        }
    }

    private SelectorTestUtils() {
    }

}
