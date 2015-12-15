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

package org.optaplanner.core.impl.testdata.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.mockito.Matchers;
import org.optaplanner.core.impl.constructionheuristic.event.ConstructionHeuristicPhaseLifecycleListener;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListener;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.mockito.Mockito.*;

public class PlannerAssert extends Assert {

    public static final long DO_NOT_ASSERT_SIZE = Long.MIN_VALUE;

    // ************************************************************************
    // Missing JUnit methods
    // ************************************************************************

    public static void assertInstanceOf(Class expectedClass, Object actualInstance) {
        assertInstanceOf(null, expectedClass, actualInstance);
    }

    public static void assertInstanceOf(String message, Class expectedClass, Object actualInstance) {
        if (!expectedClass.isInstance(actualInstance)) {
            String cleanMessage = message == null ? "" : message;
            throw new ComparisonFailure(cleanMessage, expectedClass.getName(),
                    actualInstance == null ? "null" : actualInstance.getClass().getName());
        }
    }

    public static void assertNotInstanceOf(Class expectedClass, Object actualInstance) {
        assertNotInstanceOf(null, expectedClass, actualInstance);
    }

    public static void assertNotInstanceOf(String message, Class expectedClass, Object actualInstance) {
        if (expectedClass.isInstance(actualInstance)) {
            String cleanMessage = message == null ? "" : message;
            throw new ComparisonFailure(cleanMessage, "not " + expectedClass.getName(),
                    actualInstance == null ? "null" : actualInstance.getClass().getName());
        }
    }

    public static <E> void assertCollectionContainsExactly(Collection<E> collection, E... elements) {
        assertCollectionContains(collection, elements);
        assertEquals(elements.length, collection.size());
    }

    public static <E> void assertCollectionContains(Collection<E> collection, E... elements) {
        for (int i = 0; i < elements.length; i++) {
            if (!collection.contains(elements[i])) {
                fail("The asserted collection (" + collection
                        + ") does not contain expected element (" + elements[i] + ")");
            }
        }
    }

    // ************************************************************************
    // PhaseLifecycleListener methods
    // ************************************************************************

    public static void verifyPhaseLifecycle(PhaseLifecycleListener phaseLifecycleListener,
            int solvingCount, int phaseCount, int stepCount) {
        verify(phaseLifecycleListener, times(solvingCount)).solvingStarted(Matchers.<DefaultSolverScope>any());
        verify(phaseLifecycleListener, times(phaseCount)).phaseStarted(Matchers.<AbstractPhaseScope>any());
        verify(phaseLifecycleListener, times(stepCount)).stepStarted(Matchers.<AbstractStepScope>any());
        verify(phaseLifecycleListener, times(stepCount)).stepEnded(Matchers.<AbstractStepScope>any());
        verify(phaseLifecycleListener, times(phaseCount)).phaseEnded(Matchers.<AbstractPhaseScope>any());
        verify(phaseLifecycleListener, times(solvingCount)).solvingEnded(Matchers.<DefaultSolverScope>any());
    }

    public static void verifyPhaseLifecycle(ConstructionHeuristicPhaseLifecycleListener phaseLifecycleListener,
            int solvingCount, int phaseCount, int stepCount) {
        verify(phaseLifecycleListener, times(solvingCount)).solvingStarted(Matchers.<DefaultSolverScope>any());
        verify(phaseLifecycleListener, times(phaseCount)).phaseStarted(Matchers.<ConstructionHeuristicPhaseScope>any());
        verify(phaseLifecycleListener, times(stepCount)).stepStarted(Matchers.<ConstructionHeuristicStepScope>any());
        verify(phaseLifecycleListener, times(stepCount)).stepEnded(Matchers.<ConstructionHeuristicStepScope>any());
        verify(phaseLifecycleListener, times(phaseCount)).phaseEnded(Matchers.<ConstructionHeuristicPhaseScope>any());
        verify(phaseLifecycleListener, times(solvingCount)).solvingEnded(Matchers.<DefaultSolverScope>any());
    }

    public static void verifyPhaseLifecycle(LocalSearchPhaseLifecycleListener phaseLifecycleListener,
            int solvingCount, int phaseCount, int stepCount) {
        verify(phaseLifecycleListener, times(solvingCount)).solvingStarted(Matchers.<DefaultSolverScope>any());
        verify(phaseLifecycleListener, times(phaseCount)).phaseStarted(Matchers.<LocalSearchPhaseScope>any());
        verify(phaseLifecycleListener, times(stepCount)).stepStarted(Matchers.<LocalSearchStepScope>any());
        verify(phaseLifecycleListener, times(stepCount)).stepEnded(Matchers.<LocalSearchStepScope>any());
        verify(phaseLifecycleListener, times(phaseCount)).phaseEnded(Matchers.<LocalSearchPhaseScope>any());
        verify(phaseLifecycleListener, times(solvingCount)).solvingEnded(Matchers.<DefaultSolverScope>any());
    }

    public static <O> void assertElementsOfIterator(Iterator<O> iterator, O... elements) {
        assertNotNull(iterator);
        for (O element : elements) {
            assertTrue(iterator.hasNext());
            assertEquals(element, iterator.next());
        }
    }

    public static <O> void assertAllElementsOfIterator(Iterator<O> iterator, O... elements) {
        assertElementsOfIterator(iterator, elements);
        assertFalse(iterator.hasNext());
    }

    // ************************************************************************
    // CodeAssertable methods
    // ************************************************************************

    private static CodeAssertable convertToCodeAssertable(Object o) {
        assertNotNull(o);
        if (o instanceof CodeAssertable) {
            return (CodeAssertable) o;
        } else if (o instanceof ChangeMove) {
            ChangeMove changeMove = (ChangeMove) o;
            final String code = convertToCodeAssertable(changeMove.getEntity()).getCode()
                    + "->" + convertToCodeAssertable(changeMove.getToPlanningValue()).getCode();
            return new CodeAssertable() {
                public String getCode() {
                    return code;
                }
            };
        } else if (o instanceof SwapMove) {
            SwapMove swapMove = (SwapMove) o;
            final String code = convertToCodeAssertable(swapMove.getLeftEntity()).getCode()
                    + "<->" + convertToCodeAssertable(swapMove.getRightEntity()).getCode();
            return new CodeAssertable() {
                public String getCode() {
                    return code;
                }
            };
        } else if (o instanceof CompositeMove) {
            CompositeMove compositeMove = (CompositeMove) o;
            StringBuilder codeBuilder = new StringBuilder(compositeMove.getMoves().length * 80);
            for (Move move : compositeMove.getMoves()) {
                codeBuilder.append("+").append(convertToCodeAssertable(move).getCode());
            }
            final String code = codeBuilder.substring(1);
            return new CodeAssertable() {
                public String getCode() {
                    return code;
                }
            };
        } else if (o instanceof List) {
            List list = (List) o;
            StringBuilder codeBuilder = new StringBuilder("[");
            boolean firstElement = true;
            for (Object element : list) {
                if (firstElement) {
                    firstElement = false;
                } else {
                    codeBuilder.append(", ");
                }
                codeBuilder.append(convertToCodeAssertable(element).getCode());
            }
            codeBuilder.append("]");
            final String code = codeBuilder.toString();
            return new CodeAssertable() {
                public String getCode() {
                    return code;
                }
            };
        } else if (o instanceof SubChain) {
            SubChain subChain = (SubChain) o;
            final String code = convertToCodeAssertable(subChain.getEntityList()).getCode();
            return new CodeAssertable() {
                public String getCode() {
                    return code;
                }
            };
        }
        throw new AssertionError(("o's class (" + o.getClass() + ") cannot be converted to CodeAssertable."));
    }

    public static void assertCode(String expectedCode, Object o) {
        if (expectedCode == null) {
            assertNull(o);
        } else {
            CodeAssertable codeAssertable = convertToCodeAssertable(o);
            assertCode(expectedCode, codeAssertable);
        }
    }

    public static void assertCode(String message, String expectedCode, Object o) {
        CodeAssertable codeAssertable = convertToCodeAssertable(o);
        assertCode(message, expectedCode, codeAssertable);
    }

    public static void assertCode(String expectedCode, CodeAssertable codeAssertable) {
        assertEquals(expectedCode, codeAssertable.getCode());
    }

    public static void assertCode(String message, String expectedCode, CodeAssertable codeAssertable) {
        assertEquals(message, expectedCode, codeAssertable.getCode());
    }

    public static <O> void assertAllCodesOfArray(O[] array, String... codes) {
        assertNotNull(array);
        assertEquals(codes.length, array.length);
        for (int i = 0; i < array.length; i++) {
            assertCode(codes[i], array[i]);
        }
    }

    public static <O> void assertCodesOfIterator(Iterator<O> iterator, String... codes) {
        assertNotNull(iterator);
        for (String code : codes) {
            assertTrue(iterator.hasNext());
            assertCode(code, iterator.next());
        }
    }

    public static <O> void assertAllCodesOfIterator(Iterator<O> iterator, String... codes) {
        assertCodesOfIterator(iterator, codes);
        assertFalse(iterator.hasNext());
    }

    public static void assertAllCodesOfMoveSelector(MoveSelector moveSelector, String... codes) {
        assertAllCodesOfMoveSelector(moveSelector, (long) codes.length, codes);
    }

    public static void assertAllCodesOfMoveSelector(MoveSelector moveSelector, long size, String... codes) {
        assertAllCodesOfIterator(moveSelector.iterator(), codes);
        assertEquals(true, moveSelector.isCountable());
        assertEquals(false, moveSelector.isNeverEnding());
        if (size != DO_NOT_ASSERT_SIZE) {
            assertEquals(size, moveSelector.getSize());
        }
    }

    public static void assertCodesOfNeverEndingMoveSelector(MoveSelector moveSelector, String... codes) {
        assertCodesOfNeverEndingMoveSelector(moveSelector, DO_NOT_ASSERT_SIZE, codes);
    }

    public static void assertCodesOfNeverEndingMoveSelector(MoveSelector moveSelector, long size, String... codes) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertCodesOfIterator(iterator, codes);
        assertTrue(iterator.hasNext());
        assertEquals(true, moveSelector.isCountable());
        assertEquals(true, moveSelector.isNeverEnding());
        if (size != DO_NOT_ASSERT_SIZE) {
            assertEquals(size, moveSelector.getSize());
        }
    }

    public static void assertEmptyNeverEndingMoveSelector(MoveSelector moveSelector) {
        assertEmptyNeverEndingMoveSelector(moveSelector, 0L);
    }

    public static void assertEmptyNeverEndingMoveSelector(MoveSelector moveSelector, long size) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertFalse(iterator.hasNext());
        assertEquals(true, moveSelector.isCountable());
        assertEquals(true, moveSelector.isNeverEnding());
        if (size != DO_NOT_ASSERT_SIZE) {
            assertEquals(size, moveSelector.getSize());
        }
    }

    public static void assertAllCodesOfEntitySelector(EntitySelector entitySelector, String... codes) {
        assertAllCodesOfEntitySelector(entitySelector, (long) codes.length, codes);
    }

    public static void assertAllCodesOfEntitySelector(EntitySelector entitySelector, long size, String... codes) {
        assertAllCodesOfIterator(entitySelector.iterator(), codes);
        assertEquals(true, entitySelector.isCountable());
        assertEquals(false, entitySelector.isNeverEnding());
        if (size != DO_NOT_ASSERT_SIZE) {
            assertEquals(size, entitySelector.getSize());
        }
    }

    public static void assertCodesOfNeverEndingOfEntitySelector(EntitySelector entitySelector, String... codes) {
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, DO_NOT_ASSERT_SIZE, codes);
    }

    public static void assertCodesOfNeverEndingOfEntitySelector(EntitySelector entitySelector, long size, String... codes) {
        Iterator<Object> iterator = entitySelector.iterator();
        assertCodesOfIterator(iterator, codes);
        assertTrue(iterator.hasNext());
        assertEquals(true, entitySelector.isCountable());
        assertEquals(true, entitySelector.isNeverEnding());
        if (size != DO_NOT_ASSERT_SIZE) {
            assertEquals(size, entitySelector.getSize());
        }
    }

    public static void assertAllCodesOfPillarSelector(PillarSelector pillarSelector, String... codes) {
        assertAllCodesOfPillarSelector(pillarSelector, (long) codes.length, codes);
    }

    public static void assertAllCodesOfPillarSelector(PillarSelector pillarSelector, long size, String... codes) {
        assertAllCodesOfIterator(pillarSelector.iterator(), codes);
        assertEquals(true, pillarSelector.isCountable());
        assertEquals(false, pillarSelector.isNeverEnding());
        if (size != DO_NOT_ASSERT_SIZE) {
            assertEquals(size, pillarSelector.getSize());
        }
    }

    public static void assertAllCodesOfValueSelector(EntityIndependentValueSelector valueSelector,
            String... codes) {
        assertAllCodesOfValueSelector(valueSelector, (long) codes.length, codes);
    }

    public static void assertAllCodesOfValueSelector(EntityIndependentValueSelector valueSelector, long size,
            String... codes) {
        assertAllCodesOfIterator(valueSelector.iterator(), codes);
        assertEquals(true, valueSelector.isCountable());
        assertEquals(false, valueSelector.isNeverEnding());
        if (size != DO_NOT_ASSERT_SIZE) {
            assertEquals(size, valueSelector.getSize());
        }
    }

    public static void assertAllCodesOfValueSelectorForEntity(ValueSelector valueSelector, Object entity,
            String... codes) {
        assertAllCodesOfValueSelectorForEntity(valueSelector, entity, (long) codes.length, codes);
    }

    public static void assertAllCodesOfValueSelectorForEntity(ValueSelector valueSelector, Object entity,
            long size,  String... codes) {
        assertAllCodesOfIterator(valueSelector.iterator(entity), codes);
        assertEquals(true, valueSelector.isCountable());
        assertEquals(false, valueSelector.isNeverEnding());
        if (size != DO_NOT_ASSERT_SIZE) {
            assertEquals(size, valueSelector.getSize(entity));
        }
    }

    public static void assertAllCodesOfSubChainSelector(SubChainSelector subChainSelector,
            String... codes) {
        assertAllCodesOfSubChainSelector(subChainSelector, (long) codes.length, codes);
    }

    public static void assertAllCodesOfSubChainSelector(SubChainSelector subChainSelector, long size,
            String... codes) {
        assertAllCodesOfIterator(subChainSelector.iterator(), codes);
        assertEquals(true, subChainSelector.isCountable());
        assertEquals(false, subChainSelector.isNeverEnding());
        if (size != DO_NOT_ASSERT_SIZE) {
            assertEquals(size, subChainSelector.getSize());
        }
    }

    public static <E> E extractSingleton(List<E> singletonList) {
        assertEquals(1, singletonList.size());
        return singletonList.get(0);
    }

    private PlannerAssert() {
    }

}
