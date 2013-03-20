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

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedObject;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SelectorTestUtils {

    public static SolutionDescriptor mockSolutionDescriptor() {
        SolutionDescriptor solutionDescriptor = mock(SolutionDescriptor.class);
        return solutionDescriptor;
    }

    public static PlanningEntityDescriptor mockEntityDescriptor(Class entityClass) {
        PlanningEntityDescriptor entityDescriptor = mock(PlanningEntityDescriptor.class);
        when(entityDescriptor.getPlanningEntityClass()).thenReturn(entityClass);
        return entityDescriptor;
    }

    public static PlanningVariableDescriptor mockVariableDescriptor(Class entityClass, String variableName) {
        PlanningEntityDescriptor entityDescriptor = mockEntityDescriptor(entityClass);
        return mockVariableDescriptor(entityDescriptor, variableName);
    }

    public static PlanningVariableDescriptor mockVariableDescriptor(PlanningEntityDescriptor entityDescriptor,
            String variableName) {
        PlanningVariableDescriptor variableDescriptor = mock(PlanningVariableDescriptor.class);
        when(variableDescriptor.getPlanningEntityDescriptor()).thenReturn(entityDescriptor);
        when(variableDescriptor.getVariableName()).thenReturn(variableName);
        return variableDescriptor;
    }

    public static EntitySelector mockEntitySelector(Class entityClass, Object... entities) {
        PlanningEntityDescriptor entityDescriptor = mockEntityDescriptor(entityClass);
        return mockEntitySelector(entityDescriptor, entities);
    }

    public static EntitySelector mockEntitySelector(PlanningEntityDescriptor entityDescriptor,
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
        when(entitySelector.isContinuous()).thenReturn(false);
        when(entitySelector.isNeverEnding()).thenReturn(false);
        when(entitySelector.getSize()).thenReturn((long) entityList.size());
        return entitySelector;
    }

    public static ValueSelector mockValueSelector(Class entityClass, String variableName, Object... values) {
        PlanningVariableDescriptor variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        return mockValueSelector(variableDescriptor, values);
    }

    public static ValueSelector mockValueSelector(PlanningEntityDescriptor entityDescriptor, String variableName,
            Object... values) {
        PlanningVariableDescriptor variableDescriptor = mockVariableDescriptor(entityDescriptor, variableName);
        return mockValueSelector(variableDescriptor, values);
    }

    public static ValueSelector mockValueSelector(PlanningVariableDescriptor variableDescriptor, Object... values) {
        ValueSelector valueSelector = mock(ValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        final List<Object> valueList = Arrays.<Object>asList(values);
        when(valueSelector.iterator(any())).thenAnswer(new Answer<Iterator<Object>>() {
            public Iterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return valueList.iterator();
            }
        });
        when(valueSelector.isContinuous()).thenReturn(false);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        when(valueSelector.getSize(any())).thenReturn((long) valueList.size());
        return valueSelector;
    }

    public static EntityIndependentValueSelector mockEntityIndependentValueSelector(Class entityClass, String variableName,
            Object... values) {
        PlanningVariableDescriptor variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        return mockEntityIndependentValueSelector(variableDescriptor, values);
    }

    public static EntityIndependentValueSelector mockEntityIndependentValueSelector(
            PlanningVariableDescriptor variableDescriptor, Object... values) {
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
        when(valueSelector.isContinuous()).thenReturn(false);
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
        when(moveSelector.isContinuous()).thenReturn(false);
        when(moveSelector.isNeverEnding()).thenReturn(false);
        when(moveSelector.getCacheType()).thenReturn(SelectionCacheType.JUST_IN_TIME);
        when(moveSelector.getSize()).thenReturn((long) moveList.size());
        return moveSelector;
    }

    public static void mockMethodGetTrailingEntity(ScoreDirector scoreDirector,
            PlanningVariableDescriptor variableDescriptor, final TestdataChainedEntity[] allEntities) {
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
