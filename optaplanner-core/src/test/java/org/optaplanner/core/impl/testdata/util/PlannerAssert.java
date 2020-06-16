
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

package org.optaplanner.core.impl.testdata.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

/**
 * @see PlannerTestUtils
 */
public final class PlannerAssert {

    public static final long DO_NOT_ASSERT_SIZE = Long.MIN_VALUE;

    // ************************************************************************
    // Missing JUnit methods
    // ************************************************************************

    @SafeVarargs
    public static <C extends Comparable<C>> void assertObjectsAreEqual(C... objects) {
        for (int i = 0; i < objects.length; i++) {
            for (int j = i + 1; j < objects.length; j++) {
                assertThat(objects[j]).isEqualTo(objects[i]);
                assertThat(objects[j].hashCode()).isEqualTo(objects[i].hashCode());
                assertThat(objects[i].compareTo(objects[j])).isEqualTo(0);
            }
        }
    }

    public static void assertObjectsAreEqual(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            for (int j = i + 1; j < objects.length; j++) {
                assertThat(objects[i]).isEqualTo(objects[j]);
                assertThat(objects[i]).hasSameHashCodeAs(objects[j]);
            }
        }
    }

    @SafeVarargs
    public static <C extends Comparable<C>> void assertObjectsAreNotEqual(C... objects) {
        for (int i = 0; i < objects.length; i++) {
            for (int j = i + 1; j < objects.length; j++) {
                assertThat(objects[j]).isNotEqualTo(objects[i]);
                assertThat(objects[i].compareTo(objects[j])).isNotEqualTo(0);
            }
        }
    }

    public static void assertObjectsAreNotEqual(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            for (int j = i + 1; j < objects.length; j++) {
                assertThat(objects[i]).isNotEqualTo(objects[j]);
            }
        }
    }

    @SafeVarargs
    public static <C extends Comparable<C>> void assertCompareToOrder(C... objects) {
        assertCompareToOrder(Comparator.naturalOrder(), objects);
    }

    @SafeVarargs
    public static <T> void assertCompareToOrder(Comparator<T> comparator, T... objects) {
        for (int i = 0; i < objects.length; i++) {
            for (int j = i + 1; j < objects.length; j++) {
                T a = objects[i];
                T b = objects[j];
                assertSoftly(softly -> {
                    softly.assertThat(comparator.compare(a, b))
                            .as("Object (" + a + ") must be lesser than object (" + b + ").")
                            .isLessThan(0);
                    softly.assertThat(comparator.compare(b, a))
                            .as("Object (" + b + ") must be greater than object (" + a + ").")
                            .isGreaterThan(0);
                });
            }
        }
    }

    @SafeVarargs
    public static <C extends Comparable<C>> void assertCompareToEquals(C... comparables) {
        assertCompareToEquals(Comparator.naturalOrder(), comparables);
    }

    @SafeVarargs
    public static <T> void assertCompareToEquals(Comparator<T> comparator, T... objects) {
        for (int i = 0; i < objects.length; i++) {
            for (int j = i + 1; j < objects.length; j++) {
                T a = objects[i];
                T b = objects[j];
                assertSoftly(softly -> {
                    softly.assertThat(comparator.compare(a, b))
                            .as("Object (" + a + ") must compare equal to object (" + b + ").")
                            .isEqualTo(0);
                    softly.assertThat(comparator.compare(b, a))
                            .as("Object (" + b + ") must compare equal to object (" + a + ").")
                            .isEqualTo(0);
                });
            }
        }
    }

    public static <E> void assertListElementsSameExactly(List<E> expectedList, List<E> actualList) {
        assertThat(actualList.size()).isEqualTo(expectedList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            assertThat(actualList.get(i)).isSameAs(expectedList.get(i));
        }
    }

    public static <E> void assertArrayElementsSameExactly(E[] expectedArray, E[] actualArray) {
        assertThat(actualArray.length).isEqualTo(expectedArray.length);
        for (int i = 0; i < expectedArray.length; i++) {
            assertThat(actualArray[i]).isSameAs(expectedArray[i]);
        }
    }

    // ************************************************************************
    // PhaseLifecycleListener methods
    // ************************************************************************

    public static void verifyPhaseLifecycle(PhaseLifecycleListener phaseLifecycleListener,
            int solvingCount, int phaseCount, int stepCount) {
        verify(phaseLifecycleListener, times(solvingCount)).solvingStarted(any(SolverScope.class));
        verify(phaseLifecycleListener, times(phaseCount)).phaseStarted(any(AbstractPhaseScope.class));
        verify(phaseLifecycleListener, times(stepCount)).stepStarted(any(AbstractStepScope.class));
        verify(phaseLifecycleListener, times(stepCount)).stepEnded(any(AbstractStepScope.class));
        verify(phaseLifecycleListener, times(phaseCount)).phaseEnded(any(AbstractPhaseScope.class));
        verify(phaseLifecycleListener, times(solvingCount)).solvingEnded(any(SolverScope.class));
    }

    public static void verifyPhaseLifecycle(ConstructionHeuristicPhaseLifecycleListener phaseLifecycleListener,
            int solvingCount, int phaseCount, int stepCount) {
        verify(phaseLifecycleListener, times(solvingCount)).solvingStarted(any(SolverScope.class));
        verify(phaseLifecycleListener, times(phaseCount)).phaseStarted(any(ConstructionHeuristicPhaseScope.class));
        verify(phaseLifecycleListener, times(stepCount)).stepStarted(any(ConstructionHeuristicStepScope.class));
        verify(phaseLifecycleListener, times(stepCount)).stepEnded(any(ConstructionHeuristicStepScope.class));
        verify(phaseLifecycleListener, times(phaseCount)).phaseEnded(any(ConstructionHeuristicPhaseScope.class));
        verify(phaseLifecycleListener, times(solvingCount)).solvingEnded(any(SolverScope.class));
    }

    public static void verifyPhaseLifecycle(LocalSearchPhaseLifecycleListener phaseLifecycleListener,
            int solvingCount, int phaseCount, int stepCount) {
        verify(phaseLifecycleListener, times(solvingCount)).solvingStarted(any(SolverScope.class));
        verify(phaseLifecycleListener, times(phaseCount)).phaseStarted(any(LocalSearchPhaseScope.class));
        verify(phaseLifecycleListener, times(stepCount)).stepStarted(any(LocalSearchStepScope.class));
        verify(phaseLifecycleListener, times(stepCount)).stepEnded(any(LocalSearchStepScope.class));
        verify(phaseLifecycleListener, times(phaseCount)).phaseEnded(any(LocalSearchPhaseScope.class));
        verify(phaseLifecycleListener, times(solvingCount)).solvingEnded(any(SolverScope.class));
    }

    @SafeVarargs
    public static <O> void assertElementsOfIterator(Iterator<O> iterator, O... elements) {
        assertThat(iterator).isNotNull();
        for (O element : elements) {
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next()).isEqualTo(element);
        }
    }

    @SafeVarargs
    public static <O> void assertAllElementsOfIterator(Iterator<O> iterator, O... elements) {
        assertElementsOfIterator(iterator, elements);
        assertThat(iterator.hasNext()).isFalse();
        try {
            iterator.next();
            fail("The iterator with hasNext() (" + false + ") is expected to throw a "
                    + NoSuchElementException.class.getSimpleName() + " when calling next().");
        } catch (NoSuchElementException e) {
            // Do nothing
        }
    }

    // ************************************************************************
    // CodeAssertable methods
    // ************************************************************************

    private static CodeAssertable convertToCodeAssertable(Object o) {
        assertThat(o).isNotNull();
        if (o instanceof CodeAssertable) {
            return (CodeAssertable) o;
        } else if (o instanceof ChangeMove) {
            ChangeMove<?> changeMove = (ChangeMove) o;
            final String code = convertToCodeAssertable(changeMove.getEntity()).getCode()
                    + "->" + convertToCodeAssertable(changeMove.getToPlanningValue()).getCode();
            return () -> code;
        } else if (o instanceof SwapMove) {
            SwapMove<?> swapMove = (SwapMove) o;
            final String code = convertToCodeAssertable(swapMove.getLeftEntity()).getCode()
                    + "<->" + convertToCodeAssertable(swapMove.getRightEntity()).getCode();
            return () -> code;
        } else if (o instanceof CompositeMove) {
            CompositeMove<?> compositeMove = (CompositeMove) o;
            StringBuilder codeBuilder = new StringBuilder(compositeMove.getMoves().length * 80);
            for (Move<?> move : compositeMove.getMoves()) {
                codeBuilder.append("+").append(convertToCodeAssertable(move).getCode());
            }
            final String code = codeBuilder.substring(1);
            return () -> code;
        } else if (o instanceof List) {
            List<?> list = (List) o;
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
            return () -> code;
        } else if (o instanceof SubChain) {
            SubChain subChain = (SubChain) o;
            final String code = convertToCodeAssertable(subChain.getEntityList()).getCode();
            return () -> code;
        }
        throw new AssertionError(("o's class (" + o.getClass() + ") cannot be converted to CodeAssertable."));
    }

    public static void assertCode(String expectedCode, Object o) {
        if (expectedCode == null) {
            assertThat(o).isNull();
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
        assertThat(codeAssertable.getCode()).isEqualTo(expectedCode);
    }

    public static void assertCode(String message, String expectedCode, CodeAssertable codeAssertable) {
        assertThat(codeAssertable.getCode())
                .as(message)
                .isEqualTo(expectedCode);
    }

    public static <O> void assertAllCodesOfArray(O[] array, String... codes) {
        assertThat(array).isNotNull();
        assertThat(array.length).isEqualTo(codes.length);
        for (int i = 0; i < array.length; i++) {
            assertCode(codes[i], array[i]);
        }
    }

    public static <O> void assertCodesOfIterator(Iterator<O> iterator, String... codes) {
        assertThat(iterator).isNotNull();
        for (String code : codes) {
            if (!iterator.hasNext()) {
                fail("The asserted iterator ends too soon, instead it should return selection (" + code + ").");
            }
            assertCode(code, iterator.next());
        }
    }

    public static <O> void assertAllCodesOfIterator(Iterator<O> iterator, String... codes) {
        assertCodesOfIterator(iterator, codes);
        assertThat(iterator.hasNext()).isFalse();
    }

    public static <O> void assertAllCodesOfCollection(Collection<O> collection, String... codes) {
        assertAllCodesOfIterator(collection.iterator(), codes);
    }

    public static void assertAllCodesOfMoveSelector(MoveSelector moveSelector, String... codes) {
        assertAllCodesOfMoveSelector(moveSelector, codes.length, codes);
    }

    public static void assertAllCodesOfMoveSelector(MoveSelector moveSelector, long size, String... codes) {
        assertAllCodesOfIterator(moveSelector.iterator(), codes);
        assertThat(moveSelector.isCountable()).isTrue();
        assertThat(moveSelector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(moveSelector.getSize()).isEqualTo(size);
        }
    }

    public static void assertCodesOfNeverEndingMoveSelector(MoveSelector moveSelector, String... codes) {
        assertCodesOfNeverEndingMoveSelector(moveSelector, DO_NOT_ASSERT_SIZE, codes);
    }

    public static void assertCodesOfNeverEndingMoveSelector(MoveSelector moveSelector, long size, String... codes) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertCodesOfIterator(iterator, codes);
        assertThat(iterator.hasNext()).isTrue();
        assertThat(moveSelector.isCountable()).isTrue();
        assertThat(moveSelector.isNeverEnding()).isTrue();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(moveSelector.getSize()).isEqualTo(size);
        }
    }

    public static void assertEmptyNeverEndingMoveSelector(MoveSelector moveSelector) {
        assertEmptyNeverEndingMoveSelector(moveSelector, 0L);
    }

    public static void assertEmptyNeverEndingMoveSelector(MoveSelector moveSelector, long size) {
        Iterator<Move> iterator = moveSelector.iterator();
        assertThat(iterator.hasNext()).isFalse();
        assertThat(moveSelector.isCountable()).isTrue();
        assertThat(moveSelector.isNeverEnding()).isTrue();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(moveSelector.getSize()).isEqualTo(size);
        }
    }

    public static void assertAllCodesOfEntitySelector(EntitySelector entitySelector, String... codes) {
        assertAllCodesOfEntitySelector(entitySelector, codes.length, codes);
    }

    public static void assertAllCodesOfEntitySelector(EntitySelector entitySelector, long size, String... codes) {
        assertAllCodesOfIterator(entitySelector.iterator(), codes);
        assertThat(entitySelector.isCountable()).isTrue();
        assertThat(entitySelector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(entitySelector.getSize()).isEqualTo(size);
        }
    }

    public static void assertCodesOfNeverEndingOfEntitySelector(EntitySelector entitySelector, String... codes) {
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, DO_NOT_ASSERT_SIZE, codes);
    }

    public static void assertCodesOfNeverEndingOfEntitySelector(EntitySelector entitySelector, long size, String... codes) {
        Iterator<Object> iterator = entitySelector.iterator();
        assertCodesOfIterator(iterator, codes);
        assertThat(iterator.hasNext()).isTrue();
        assertThat(entitySelector.isCountable()).isTrue();
        assertThat(entitySelector.isNeverEnding()).isTrue();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(entitySelector.getSize()).isEqualTo(size);
        }
    }

    public static void assertAllCodesOfPillarSelector(PillarSelector pillarSelector, String... codes) {
        assertAllCodesOfPillarSelector(pillarSelector, codes.length, codes);
    }

    public static void assertAllCodesOfPillarSelector(PillarSelector pillarSelector, long size, String... codes) {
        assertAllCodesOfIterator(pillarSelector.iterator(), codes);
        assertThat(pillarSelector.isCountable()).isTrue();
        assertThat(pillarSelector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(pillarSelector.getSize()).isEqualTo(size);
        }
    }

    public static void assertAllCodesOfValueSelector(EntityIndependentValueSelector valueSelector,
            String... codes) {
        assertAllCodesOfValueSelector(valueSelector, codes.length, codes);
    }

    public static void assertAllCodesOfValueSelector(EntityIndependentValueSelector valueSelector, long size,
            String... codes) {
        assertAllCodesOfIterator(valueSelector.iterator(), codes);
        assertThat(valueSelector.isCountable()).isTrue();
        assertThat(valueSelector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(valueSelector.getSize()).isEqualTo(size);
        }
    }

    public static void assertAllCodesOfValueSelectorForEntity(ValueSelector valueSelector, Object entity,
            String... codes) {
        assertAllCodesOfValueSelectorForEntity(valueSelector, entity, codes.length, codes);
    }

    public static void assertAllCodesOfValueSelectorForEntity(ValueSelector valueSelector, Object entity,
            long size, String... codes) {
        assertAllCodesOfIterator(valueSelector.iterator(entity), codes);
        assertThat(valueSelector.isCountable()).isTrue();
        assertThat(valueSelector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(valueSelector.getSize(entity)).isEqualTo(size);
        }
    }

    public static void assertAllCodesOfSubChainSelector(SubChainSelector subChainSelector,
            String... codes) {
        assertAllCodesOfSubChainSelector(subChainSelector, codes.length, codes);
    }

    public static void assertAllCodesOfSubChainSelector(SubChainSelector subChainSelector, long size,
            String... codes) {
        assertAllCodesOfIterator(subChainSelector.iterator(), codes);
        assertThat(subChainSelector.isCountable()).isTrue();
        assertThat(subChainSelector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(subChainSelector.getSize()).isEqualTo(size);
        }
    }

    // ************************************************************************
    // Testdata methods
    // ************************************************************************

    public static void assertSolutionInitialized(TestdataSolution solution) {
        assertThat(solution).isNotNull();
        List<TestdataEntity> entityList = solution.getEntityList();
        assertThat(entityList.isEmpty()).isFalse();
        for (TestdataEntity entity : entityList) {
            assertThat(entity.getValue()).isNotNull();
        }
    }

    public static <E> E extractSingleton(List<E> singletonList) {
        assertThat(singletonList.size()).isEqualTo(1);
        return singletonList.get(0);
    }

    private PlannerAssert() {
    }

}
