
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.assertj.core.util.Streams;
import org.optaplanner.core.impl.constructionheuristic.event.ConstructionHeuristicPhaseLifecycleListener;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;
import org.optaplanner.core.impl.heuristic.selector.ListIterableSelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChainSelector;
import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListener;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
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
                assertThat(objects[j]).hasSameHashCodeAs(objects[i]);
                assertThat(objects[i]).isEqualByComparingTo(objects[j]);
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
                assertThat(objects[i]).isNotEqualByComparingTo(objects[j]);
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
            assertThat(iterator).hasNext();
            assertThat(iterator.next()).isEqualTo(element);
        }
    }

    @SafeVarargs
    public static <O> void assertAllElementsOfIterator(Iterator<O> iterator, O... elements) {
        assertElementsOfIterator(iterator, elements);
        assertThat(iterator).isExhausted();
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

    public static void assertCode(String expectedCode, Object o) {
        if (expectedCode == null) {
            assertThat(o).isNull();
        } else {
            assertCode(expectedCode, CodeAssertable.convert(o));
        }
    }

    public static void assertCode(String expectedCode, CodeAssertable codeAssertable) {
        assertThat(codeAssertable.getCode()).isEqualTo(expectedCode);
    }

    // ************************************************************************
    // Generic sequences
    // ************************************************************************

    public static <O> void assertAllCodesOfArray(O[] array, String... codes) {
        assertThat(array).isNotNull();
        assertThat(array).hasSameSizeAs(codes);
        for (int i = 0; i < array.length; i++) {
            assertCode(codes[i], array[i]);
        }
    }

    private static String codeIfNotNull(Object o) {
        return o == null ? null : CodeAssertable.convert(o).getCode();
    }

    public static <O> void assertCodesOfNeverEndingIterator(Iterator<O> iterator, String... codes) {
        assertThat(iterator).isNotNull();
        assertThat(Streams.stream(iterator)
                .map(PlannerAssert::codeIfNotNull)
                .limit(codes.length)).containsExactly(codes);
    }

    public static <O> void assertCodesOfIterator(Iterator<O> iterator, String... codes) {
        assertThat(iterator).isNotNull();
        assertThat(iterator)
                .toIterable()
                .map(PlannerAssert::codeIfNotNull)
                .containsExactly(codes);
    }

    public static void assertAllCodesOfIterator(Iterator<?> iterator, String... codes) {
        assertCodesOfIterator(iterator, codes);
        assertThat(iterator).isExhausted();
    }

    public static <O> void assertReverseCodesOfListIterator(ListIterator<O> listIterator, String... codes) {
        assertThat(listIterator).isNotNull();
        for (int i = codes.length - 1; i >= 0; i--) {
            assertCode(codes[i], listIterator.previous());
        }
    }

    public static <O> void assertAllReverseCodesOfIterator(ListIterator<O> listIterator, String... codes) {
        assertReverseCodesOfListIterator(listIterator, codes);
        assertThat(listIterator.hasPrevious()).isFalse();
    }

    public static void assertAllCodesOfCollection(Collection<?> collection, String... codes) {
        assertAllCodesOfIterator(collection.iterator(), codes);
    }

    public static void assertAllCodesOfIterableSelector(IterableSelector<?, ?> selector, long size, String... codes) {
        assertAllCodesOfIterator(selector.iterator(), codes);
        assertThat(selector.isCountable()).isTrue();
        assertThat(selector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(selector.getSize()).isEqualTo(size);
        }
    }

    public static void assertAllCodesOfListIterableSelector(ListIterableSelector<?, ?> selector, long size, String... codes) {
        ListIterator<?> listIterator = selector.listIterator();
        assertAllCodesOfIterator(listIterator, codes);
        assertAllReverseCodesOfIterator(listIterator, codes);
        assertThat(selector.isCountable()).isTrue();
        assertThat(selector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(selector.getSize()).isEqualTo(size);
        }
    }

    public static void assertCodesOfNeverEndingIterableSelector(IterableSelector<?, ?> selector, long size, String... codes) {
        Iterator<?> iterator = selector.iterator();
        assertCodesOfNeverEndingIterator(iterator, codes);
        assertThat(iterator).hasNext();
        assertThat(selector.isCountable()).isTrue();
        assertThat(selector.isNeverEnding()).isTrue();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(selector.getSize()).isEqualTo(size);
        }
    }

    public static void assertEmptyNeverEndingIterableSelector(IterableSelector<?, ?> selector, long size) {
        assertThat(selector.iterator()).isExhausted();
        assertThat(selector.isCountable()).isTrue();
        assertThat(selector.isNeverEnding()).isTrue();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(selector.getSize()).isZero();
        }
    }

    // ************************************************************************
    // Aliases for IterableSelector assert
    // ************************************************************************

    // ---- Move

    public static void assertAllCodesOfMoveSelector(MoveSelector<?> moveSelector, String... codes) {
        assertAllCodesOfIterableSelector(moveSelector, codes.length, codes);
    }

    public static void assertAllCodesOfMoveSelector(MoveSelector<?> moveSelector, long size, String... codes) {
        assertAllCodesOfIterableSelector(moveSelector, size, codes);
    }

    public static void assertCodesOfNeverEndingMoveSelector(MoveSelector<?> moveSelector, String... codes) {
        assertCodesOfNeverEndingIterableSelector(moveSelector, DO_NOT_ASSERT_SIZE, codes);
    }

    public static void assertCodesOfNeverEndingMoveSelector(MoveSelector<?> moveSelector, long size, String... codes) {
        assertCodesOfNeverEndingIterableSelector(moveSelector, size, codes);
    }

    public static void assertEmptyNeverEndingMoveSelector(MoveSelector<?> moveSelector) {
        assertEmptyNeverEndingIterableSelector(moveSelector, 0);
    }

    // ---- Entity

    public static void assertAllCodesOfEntitySelector(EntitySelector<?> entitySelector, String... codes) {
        assertAllCodesOfIterableSelector(entitySelector, codes.length, codes);
    }

    public static void assertAllCodesOfEntitySelector(EntitySelector<?> entitySelector, long size, String... codes) {
        assertAllCodesOfIterableSelector(entitySelector, size, codes);
    }

    public static void assertCodesOfNeverEndingOfEntitySelector(EntitySelector<?> entitySelector, long size, String... codes) {
        assertCodesOfNeverEndingIterableSelector(entitySelector, size, codes);
    }

    public static void assertAllCodesOfOrderedEntitySelector(EntitySelector<?> entitySelector, String... codes) {
        assertAllCodesOfOrderedEntitySelector(entitySelector, codes.length, codes);
    }

    public static void assertAllCodesOfOrderedEntitySelector(EntitySelector<?> entitySelector, long size, String... codes) {
        ListIterator<?> listIterator = entitySelector.listIterator();
        assertAllCodesOfIterator(listIterator, codes);
        assertAllReverseCodesOfIterator(listIterator, codes);
        assertThat(entitySelector.isCountable()).isTrue();
        assertThat(entitySelector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(entitySelector.getSize()).isEqualTo(size);
        }
    }

    // ---- Pillar

    public static void assertAllCodesOfPillarSelector(PillarSelector<?> pillarSelector, String... codes) {
        assertAllCodesOfIterableSelector(pillarSelector, codes.length, codes);
    }

    public static void assertCodesOfNeverEndingPillarSelector(PillarSelector<?> pillarSelector, String... codes) {
        assertCodesOfNeverEndingIterableSelector(pillarSelector, DO_NOT_ASSERT_SIZE, codes);
    }

    public static void assertEmptyNeverEndingPillarSelector(PillarSelector<?> pillarSelector) {
        assertEmptyNeverEndingIterableSelector(pillarSelector, DO_NOT_ASSERT_SIZE);
    }

    // ---- Sub Chain

    public static void assertAllCodesOfSubChainSelector(SubChainSelector<?> selector, String... codes) {
        assertAllCodesOfIterableSelector(selector, codes.length, codes);
    }

    // ---- Value

    public static void assertAllCodesOfValueSelector(EntityIndependentValueSelector<?> valueSelector, String... codes) {
        assertAllCodesOfIterableSelector(valueSelector, codes.length, codes);
    }

    public static void assertAllCodesOfValueSelector(EntityIndependentValueSelector<?> valueSelector, long size,
            String... codes) {
        assertAllCodesOfIterableSelector(valueSelector, size, codes);
    }

    // ************************************************************************
    // Entity dependent
    // ************************************************************************

    public static void assertAllCodesOfValueSelectorForEntity(ValueSelector<?> valueSelector, Object entity, String... codes) {
        assertAllCodesOfValueSelectorForEntity(valueSelector, entity, codes.length, codes);
    }

    public static void assertAllCodesOfValueSelectorForEntity(ValueSelector<?> valueSelector, Object entity,
            long size, String... codes) {
        assertAllCodesOfIterator(valueSelector.iterator(entity), codes);
        assertThat(valueSelector.isCountable()).isTrue();
        assertThat(valueSelector.isNeverEnding()).isFalse();
        if (size != DO_NOT_ASSERT_SIZE) {
            assertThat(valueSelector.getSize(entity)).isEqualTo(size);
        }
    }

    // ************************************************************************
    // Testdata methods
    // ************************************************************************

    public static void assertSolutionInitialized(TestdataSolution solution) {
        assertThat(solution).isNotNull();
        assertThat(solution.getEntityList())
                .isNotEmpty()
                .noneMatch(entity -> entity.getValue() == null);
    }

    public static <E> E extractSingleton(List<E> singletonList) {
        assertThat(singletonList).hasSize(1);
        return singletonList.get(0);
    }

    private PlannerAssert() {
    }

}
