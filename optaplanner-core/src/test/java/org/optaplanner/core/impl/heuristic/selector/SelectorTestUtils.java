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

package org.optaplanner.core.impl.heuristic.selector;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedObject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SelectorTestUtils {

    public static SolutionDescriptor mockSolutionDescriptor() {
        SolutionDescriptor solutionDescriptor = mock(SolutionDescriptor.class);
        return solutionDescriptor;
    }

    public static EntityDescriptor mockEntityDescriptor(Class entityClass) {
        EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.getEntityClass()).thenReturn(entityClass);
        return entityDescriptor;
    }

    public static GenuineVariableDescriptor mockVariableDescriptor(Class entityClass, String variableName) {
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

    public static EntitySelector mockEntitySelector(Class entityClass, Object... entities) {
        EntityDescriptor entityDescriptor = mockEntityDescriptor(entityClass);
        return mockEntitySelector(entityDescriptor, entities);
    }

    public static EntitySelector mockEntitySelector(EntityDescriptor entityDescriptor,
            Object... entities) {
        EntitySelector entitySelector = mock(EntitySelector.class);
        when(entitySelector.getEntityDescriptor()).thenReturn(entityDescriptor);
        final List<Object> entityList = Arrays.<Object>asList(entities);
        when(entitySelector.iterator()).thenAnswer(new Answer<Iterator<Object>>() {
            public Iterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return entityList.iterator();
            }
        });
        when(entitySelector.listIterator()).thenAnswer(new Answer<ListIterator<Object>>() {
            public ListIterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return entityList.listIterator();
            }
        });
        for (int i = 0; i < entityList.size(); i++) {
            final int index = i;
            when(entitySelector.listIterator(index)).thenAnswer(new Answer<ListIterator<Object>>() {
                public ListIterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                    return entityList.listIterator(index);
                }
            });
        }
        when(entitySelector.endingIterator()).thenAnswer(new Answer<Iterator<Object>>() {
            public Iterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return entityList.iterator();
            }
        });
        when(entitySelector.isCountable()).thenReturn(true);
        when(entitySelector.isNeverEnding()).thenReturn(false);
        when(entitySelector.getSize()).thenReturn((long) entityList.size());
        return entitySelector;
    }

    public static ValueSelector mockValueSelector(Class entityClass, String variableName, Object... values) {
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
        when(valueSelector.iterator(any())).thenAnswer(new Answer<Iterator<Object>>() {
            public Iterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return valueList.iterator();
            }
        });
        when(valueSelector.isCountable()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        when(valueSelector.getSize(any())).thenReturn((long) valueList.size());
        return valueSelector;
    }

    public static ValueSelector mockValueSelectorForEntity(Class entityClass, Object entity, String variableName,
            Object... values) {
        GenuineVariableDescriptor variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        return mockValueSelectorForEntity(variableDescriptor, entity, values);
    }

    public static ValueSelector mockValueSelectorForEntity(GenuineVariableDescriptor variableDescriptor, Object entity,
            Object... values) {
        ValueSelector valueSelector = mock(ValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        final List<Object> valueList = Arrays.<Object>asList(values);
        when(valueSelector.iterator(entity)).thenAnswer(new Answer<Iterator<Object>>() {
            public Iterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return valueList.iterator();
            }
        });
        when(valueSelector.isCountable()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        when(valueSelector.getSize(entity)).thenReturn((long) valueList.size());
        return valueSelector;
    }

    public static EntityIndependentValueSelector mockEntityIndependentValueSelector(Class entityClass, String variableName,
            Object... values) {
        GenuineVariableDescriptor variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        return mockEntityIndependentValueSelector(variableDescriptor, values);
    }

    public static EntityIndependentValueSelector mockEntityIndependentValueSelector(
            GenuineVariableDescriptor variableDescriptor, Object... values) {
        EntityIndependentValueSelector valueSelector = mock(EntityIndependentValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        final List<Object> valueList = Arrays.<Object>asList(values);
        when(valueSelector.iterator(any())).thenAnswer(new Answer<Iterator<Object>>() {
            public Iterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return valueList.iterator();
            }
        });
        when(valueSelector.iterator()).thenAnswer(new Answer<Iterator<Object>>() {
            public Iterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return valueList.iterator();
            }
        });
        when(valueSelector.isCountable()).thenReturn(true);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        when(valueSelector.getSize(any())).thenReturn((long) valueList.size());
        when(valueSelector.getSize()).thenReturn((long) valueList.size());
        return valueSelector;
    }

    public static MoveSelector mockMoveSelector(Class moveClass,
            Move... moves) {
        MoveSelector moveSelector = mock(MoveSelector.class);
        final List<Move> moveList = Arrays.<Move>asList(moves);
        when(moveSelector.iterator()).thenAnswer(new Answer<Iterator<Move>>() {
            public Iterator<Move> answer(InvocationOnMock invocation) throws Throwable {
                return moveList.iterator();
            }
        });
        when(moveSelector.isCountable()).thenReturn(true);
        when(moveSelector.isNeverEnding()).thenReturn(false);
        when(moveSelector.getCacheType()).thenReturn(SelectionCacheType.JUST_IN_TIME);
        when(moveSelector.getSize()).thenReturn((long) moveList.size());
        return moveSelector;
    }

    public static void mockMethodGetTrailingEntity(InnerScoreDirector scoreDirector,
            GenuineVariableDescriptor variableDescriptor, final TestdataChainedEntity[] allEntities) {
        when(scoreDirector.getTrailingEntity(eq(variableDescriptor), anyObject())).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object planningValue = invocation.getArguments()[1];
                for (TestdataChainedEntity entity : allEntities) {
                    if (entity.getChainedObject().equals(planningValue)) {
                        return entity;
                    }
                }
                return null;
            }
        });
    }

    public static void assertChain(TestdataChainedObject... chainedObjects) {
        TestdataChainedObject chainedObject = chainedObjects[0];
        for (int i = 1; i < chainedObjects.length; i++) {
            TestdataChainedEntity chainedEntity = (TestdataChainedEntity) chainedObjects[i];
            assertEquals("Chained entity (" + chainedEntity + ")'s chainedObject",
                    chainedObject, chainedEntity.getChainedObject());
            chainedObject = chainedEntity;
        }
    }

    private SelectorTestUtils() {
    }

}
