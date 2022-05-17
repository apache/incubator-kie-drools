/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedObject;

public class SelectorTestUtils {

    public static <Solution_> EntityDescriptor<Solution_> mockEntityDescriptor(Class<?> entityClass) {
        EntityDescriptor<Solution_> entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.getEntityClass()).thenReturn((Class) entityClass);
        return entityDescriptor;
    }

    public static <Solution_> GenuineVariableDescriptor<Solution_> mockVariableDescriptor(Class<?> entityClass,
            String variableName) {
        EntityDescriptor<Solution_> entityDescriptor = mockEntityDescriptor(entityClass);
        return mockVariableDescriptor(entityDescriptor, variableName);
    }

    public static <Solution_> GenuineVariableDescriptor<Solution_> mockVariableDescriptor(
            EntityDescriptor<Solution_> entityDescriptor, String variableName) {
        GenuineVariableDescriptor<Solution_> variableDescriptor = mock(GenuineVariableDescriptor.class);
        when(variableDescriptor.getEntityDescriptor()).thenReturn(entityDescriptor);
        when(variableDescriptor.getVariableName()).thenReturn(variableName);
        return variableDescriptor;
    }

    public static <Solution_> EntitySelector<Solution_> mockEntitySelector(Class<?> entityClass, Object... entities) {
        EntityDescriptor<Solution_> entityDescriptor = mockEntityDescriptor(entityClass);
        return mockEntitySelector(entityDescriptor, entities);
    }

    public static <Solution_> EntitySelector<Solution_> mockEntitySelector(EntityDescriptor<Solution_> entityDescriptor,
            Object... entities) {
        EntitySelector<Solution_> entitySelector = mock(EntitySelector.class);
        when(entitySelector.getEntityDescriptor()).thenReturn(entityDescriptor);
        final List<Object> entityList = Arrays.asList(entities);
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

    public static <Solution_> ValueSelector<Solution_> mockValueSelector(Class<?> entityClass, String variableName,
            Object... values) {
        GenuineVariableDescriptor<Solution_> variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        return mockValueSelector(variableDescriptor, values);
    }

    public static <Solution_> ValueSelector<Solution_> mockValueSelector(
            GenuineVariableDescriptor<Solution_> variableDescriptor, Object... values) {
        ValueSelector<Solution_> valueSelector = mock(ValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        final List<Object> valueList = Arrays.asList(values);
        when(valueSelector.iterator(any())).thenAnswer(invocation -> valueList.iterator());
        when(valueSelector.isCountable()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        when(valueSelector.getSize(any())).thenReturn((long) valueList.size());
        return valueSelector;
    }

    public static <Solution_> ValueSelector<Solution_> mockValueSelectorForEntity(Class<?> entityClass, Object entity,
            String variableName, Object... values) {
        return mockValueSelectorForEntity(entityClass, variableName,
                Collections.singletonMap(entity, Arrays.asList(values)));
    }

    private static <Solution_> ValueSelector<Solution_> mockValueSelectorForEntity(Class<?> entityClass,
            String variableName, Map<Object, List<Object>> entityToValues) {
        GenuineVariableDescriptor<Solution_> variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        return mockValueSelectorForEntity(variableDescriptor, entityToValues);
    }

    private static <Solution_> ValueSelector<Solution_> mockValueSelectorForEntity(
            GenuineVariableDescriptor<Solution_> variableDescriptor, Map<Object, List<Object>> entityToValues) {
        ValueSelector<Solution_> valueSelector = mock(ValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        for (Map.Entry<Object, List<Object>> entry : entityToValues.entrySet()) {
            Object entity = entry.getKey();
            final List<Object> valueList = entry.getValue();
            when(valueSelector.getSize(entity)).thenAnswer(invocation -> (long) valueList.size());
            when(valueSelector.iterator(entity)).thenAnswer(invocation -> valueList.iterator());
            when(valueSelector.getSize(entity)).thenReturn((long) valueList.size());
        }
        when(valueSelector.isCountable()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        return valueSelector;
    }

    public static <Solution_> EntityIndependentValueSelector<Solution_> mockEntityIndependentValueSelector(
            Class<?> entityClass, String variableName, Object... values) {
        GenuineVariableDescriptor<Solution_> variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        when(variableDescriptor.isValueRangeEntityIndependent()).thenReturn(true);
        return mockEntityIndependentValueSelector(variableDescriptor, values);
    }

    public static <Solution_> EntityIndependentValueSelector<Solution_> mockEntityIndependentValueSelector(
            GenuineVariableDescriptor<Solution_> variableDescriptor, Object... values) {
        EntityIndependentValueSelector<Solution_> valueSelector = mock(EntityIndependentValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        final List<Object> valueList = Arrays.asList(values);
        when(valueSelector.iterator(any())).thenAnswer(invocation -> valueList.iterator());
        when(valueSelector.endingIterator(any())).thenAnswer(invocation -> valueList.iterator());
        when(valueSelector.iterator()).thenAnswer(invocation -> valueList.iterator());
        when(valueSelector.isCountable()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        when(valueSelector.getSize(any())).thenReturn((long) valueList.size());
        when(valueSelector.getSize()).thenReturn((long) valueList.size());
        return valueSelector;
    }

    @SafeVarargs
    public static <Solution_> MoveSelector<Solution_> mockMoveSelector(Class<?> moveClass, Move<Solution_>... moves) {
        MoveSelector<Solution_> moveSelector = mock(MoveSelector.class);
        final List<Move<Solution_>> moveList = Arrays.asList(moves);
        when(moveSelector.iterator()).thenAnswer(invocation -> moveList.iterator());
        when(moveSelector.spliterator()).thenAnswer(invocation -> moveList.spliterator());
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
                        + "Actual:   " + chainedEntity.getChainedObject() + "\n"
                        + "Expected chain: " + Arrays.toString(chainedObjects) + "\n"
                        + "Actual chain:   " + Arrays.toString(Arrays.copyOf(chainedObjects, i)) + " ... ["
                        + chainedEntity.getChainedObject() + ", " + chainedEntity + "] ...");
            }
            chainedObject = chainedEntity;
        }
    }

    private SelectorTestUtils() {
    }

}
