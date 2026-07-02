/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt.KOptUtils.getMultiEntityBetweenPredicate;
import static org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt.KOptUtils.getMultiEntitySuccessorFunction;
import static org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt.KOptUtils.getSuccessorFunction;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class KOptListMoveTest {

    private final ListVariableDescriptor<TestdataListSolution> variableDescriptor =
            TestdataListEntity.buildVariableDescriptorForValueList();

    private final InnerScoreDirector<TestdataListSolution, ?> scoreDirector =
            PlannerTestUtils.mockScoreDirector(variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

    private final TestdataListValue v1 = new TestdataListValue("1");
    private final TestdataListValue v2 = new TestdataListValue("2");
    private final TestdataListValue v3 = new TestdataListValue("3");
    private final TestdataListValue v4 = new TestdataListValue("4");
    private final TestdataListValue v5 = new TestdataListValue("5");
    private final TestdataListValue v6 = new TestdataListValue("6");
    private final TestdataListValue v7 = new TestdataListValue("7");
    private final TestdataListValue v8 = new TestdataListValue("8");
    private final TestdataListValue v9 = new TestdataListValue("9");
    private final TestdataListValue v10 = new TestdataListValue("10");
    private final TestdataListValue v11 = new TestdataListValue("11");
    private final TestdataListValue v12 = new TestdataListValue("12");

    // TODO: It appears the multi-entity approach does not like kopt-affected-elements;
    // (in particular, I found index variable corruption causing a NPE due to incorrect after
    // listVariableChanged happens around local step 1000 when changes notifications are limited
    // to kopt-affected-elements; as a workaround, the full list is notified currently.
    // (the list notifications that are commented out are the correct + precise
    // notifications; they all pass with the kopt-affected-elements, but solving must of hit an
    // edge case that was not tested before.

    @Test
    void test3Opt() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4, v5, v6);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(v6, v1,
                        v2, v3,
                        v4, v5),
                List.of(v1, v3,
                        v2, v5,
                        v4, v6));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v5, v6, v4, v3);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 6);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 6);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4, v5, v6);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 6);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 6);
    }

    @Test
    void testMultiEntity2Opt() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("e2", v5, v6, v7, v8);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(v2, v3,
                        v6, v7),
                List.of(v2, v6,
                        v3, v7));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v6, v5, v4);
        assertThat(e2.getValueList()).containsExactly(v3, v7, v8);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 2, 4);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 2);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 2, 5);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 1);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 4);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 5);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 3);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4);
        assertThat(e2.getValueList()).containsExactly(v5, v6, v7, v8);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 2, 5);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 1);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 2, 4);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 2);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 5);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 3);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 4);
    }

    @Test
    void testMultiEntity3Opt() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v6);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("e2", v4, v5);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(v6, v1,
                        v2, v3,
                        v4, v5),
                List.of(v1, v3,
                        v2, v5,
                        v4, v6));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v5);
        assertThat(e2.getValueList()).containsExactly(v2, v6, v4, v3);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 4);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 2);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 2);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 4);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v6);
        assertThat(e2.getValueList()).containsExactly(v4, v5);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 2);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 2);
    }

    @Test
    void testMultiEntity4Opt() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("e2", v5, v6, v7, v8);
        TestdataListEntity e3 = TestdataListEntity.createWithValues("e3", v9, v10, v11, v12);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(v2, v3,
                        v7, v8,
                        v9, v10,
                        v11, v12),
                List.of(v11, v3,
                        v12, v7,
                        v2, v9,
                        v10, v8));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v9, v8);
        assertThat(e2.getValueList()).containsExactly(v10, v11, v3, v4);
        assertThat(e3.getValueList()).containsExactly(v5, v6, v7, v12);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 2, 4);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 4);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e3, 0, 3);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 2, 4);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 4);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e3, 0, 3);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 4);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 4);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e3, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e3, 0, 4);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4);
        assertThat(e2.getValueList()).containsExactly(v5, v6, v7, v8);
        assertThat(e3.getValueList()).containsExactly(v9, v10, v11, v12);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 2, 4);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 4);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e3, 0, 3);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 2, 4);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 4);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e3, 0, 3);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 4);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 4);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e3, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 4);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e3, 0, 4);
    }

    @Test
    void test3OptLong() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(v10, v1,
                        v3, v4,
                        v8, v9),
                List.of(v1, v4,
                        v3, v9,
                        v8, v10));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v9, v10, v8, v7, v6, v5, v4);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 10);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 10);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 10);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 10);
    }

    @Test
    void test4Opt() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4, v5, v6, v7, v8);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(
                        v1, v2,
                        v4, v3,
                        v7, v8,
                        v6, v5),
                List.of(
                        v2, v4,
                        v3, v7,
                        v8, v6,
                        v5, v1));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v5, v4, v2, v3, v7, v6, v8);

        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 1, 7);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 1, 7);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 8);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 8);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4, v5, v6, v7, v8);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 1, 7);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 1, 7);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 8);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 8);
    }

    @Test
    void test4OptLong() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(v1, v12,
                        v2, v3,
                        v5, v6,
                        v9, v10),
                List.of(
                        v1, v3,
                        v2, v6,
                        v5, v10,
                        v9, v12));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v3, v4, v5, v10, v11, v12, v9, v8, v7, v6, v2);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 12);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 12);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 12);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 12);
    }

    @Test
    void testInverted4OptLong() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);

        // Note: using only endpoints work (removing v4, v7, v8, v11) from the above list works
        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(
                        v2, v3,
                        v6, v5,
                        v10, v9,
                        v12, v1),
                List.of(
                        v2, v5,
                        v6, v10,
                        v9, v1,
                        v12, v3));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v5, v4, v3, v12, v11, v10, v6, v7, v8, v9);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 5, 12);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 0);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 2, 5);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 5, 12);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 0);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 2, 5);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 12);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 12);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 5, 12);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 0);
        //verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 2, 5);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 5, 12);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 0);
        //verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 2, 5);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 12);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 12);
    }

    @Test
    void testDoubleBridge4Opt() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4, v5, v6, v7, v8);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(v8, v1,
                        v4, v5,
                        v2, v3,
                        v6, v7),
                List.of(
                        v1, v4,
                        v6, v3,
                        v5, v8,
                        v7, v2));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v7, v8, v5, v6, v3, v4);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 8);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 8);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4, v5, v6, v7, v8);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 8);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 8);
    }

    @Test
    void testDoubleBridge4OptLong() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(v12, v1,
                        v5, v6,
                        v2, v3,
                        v8, v9),
                List.of(
                        v1, v5,
                        v8, v3,
                        v6, v12,
                        v9, v2));

        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();
        AbstractMove<TestdataListSolution> undoMove = kOptListMove.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v9, v10, v11, v12, v6, v7, v8, v3, v4, v5);

        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 12);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 12);

        reset(scoreDirector);
        undoMove.doMoveOnly(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);

        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 12);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 12);
    }

    @Test
    void testIsFeasible() {
        TestdataListEntity e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4, v5, v6, v7, v8);

        KOptListMove<TestdataListSolution> kOptListMove = fromRemovedAndAddedEdges(scoreDirector,
                variableDescriptor,
                List.of(
                        v1, v2,
                        v4, v3,
                        v7, v8,
                        v6, v5),
                List.of(
                        v2, v4,
                        v3, v7,
                        v8, v6,
                        v5, v1));
        // this move create 1 cycle (v1 -> v5 -> v4 -> v2 -> v3 -> v7 -> v6 -> v8 -> v1 -> ...)
        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isTrue();

        e1 = TestdataListEntity.createWithValues("e1", v1, v2, v3, v4, v8, v7, v5, v6);

        kOptListMove = fromRemovedAndAddedEdges(scoreDirector, variableDescriptor,
                List.of(
                        v1, v2,
                        v3, v4,
                        v7, v8,
                        v6, v5),
                List.of(
                        v2, v3,
                        v4, v7,
                        v8, v6,
                        v5, v1));
        // this move create 2 cycles (v2...v3->t2...) and (v4...v5->v7...v6->v1...v8->v4...)
        assertThat(kOptListMove.isMoveDoable(scoreDirector)).isFalse();
    }

    /**
     * Create a sequential or non-sequential k-opt from the supplied pairs of undirected removed and added edges.
     *
     * @param <Solution_>
     * @param scoreDirector
     * @param listVariableDescriptor
     * @param removedEdgeList The edges to remove. For each pair {@code (edgePairs[2*i], edgePairs[2*i+1])},
     *        it must be the case {@code edgePairs[2*i+1]} is either the successor or predecessor of
     *        {@code edgePairs[2*i]}. Additionally, each edge must belong to the given entity's
     *        list variable.
     * @param addedEdgeList The edges to add. Must contain only endpoints specified in the removedEdgeList.
     * @return A new sequential or non-sequential k-opt move with the specified undirected edges removed and added.
     */
    private static <Solution_> KOptListMove<Solution_> fromRemovedAndAddedEdges(
            InnerScoreDirector<Solution_, ?> scoreDirector,
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            List<TestdataListValue> removedEdgeList,
            List<TestdataListValue> addedEdgeList) {

        if (addedEdgeList.size() != removedEdgeList.size()) {
            throw new IllegalArgumentException(
                    "addedEdgeList (" + addedEdgeList + ") and removedEdgeList (" + removedEdgeList + ") have the same size");
        }

        if ((addedEdgeList.size() % 2) != 0) {
            throw new IllegalArgumentException(
                    "addedEdgeList and removedEdgeList are invalid: there is an odd number of endpoints.");
        }

        if (!addedEdgeList.containsAll(removedEdgeList)) {
            throw new IllegalArgumentException("addedEdgeList (" + addedEdgeList + ") is invalid; it contains endpoints "
                    + "that are not included in the removedEdgeList (" + removedEdgeList + ").");
        }

        TestdataListValue[] pickedValues = removedEdgeList.toArray(TestdataListValue[]::new);

        IndexVariableSupply indexVariableSupply =
                scoreDirector.getSupplyManager().demand(new IndexVariableDemand<>(listVariableDescriptor));

        SingletonInverseVariableSupply inverseVariableSupply =
                scoreDirector.getSupplyManager().demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor));

        Function<TestdataListValue, TestdataListValue> successorFunction =
                getSuccessorFunction(listVariableDescriptor, inverseVariableSupply, indexVariableSupply);

        for (int i = 0; i < removedEdgeList.size(); i += 2) {
            if (successorFunction.apply(removedEdgeList.get(i)) != removedEdgeList.get(i + 1)
                    && successorFunction.apply(removedEdgeList.get(i + 1)) != removedEdgeList.get(i)) {
                throw new IllegalArgumentException("removedEdgeList (" + removedEdgeList + ") contains an invalid edge ((" +
                        removedEdgeList.get(i) + ", " + removedEdgeList.get(i + 1) + ")).");
            }
        }

        TestdataListValue[] tourArray = new TestdataListValue[removedEdgeList.size() + 1];
        int[] incl = new int[removedEdgeList.size() + 1];
        for (int i = 0; i < removedEdgeList.size(); i += 2) {
            tourArray[i + 1] = removedEdgeList.get(i);
            tourArray[i + 2] = removedEdgeList.get(i + 1);
            int addedEdgeIndex = identityIndexOf(addedEdgeList, removedEdgeList.get(i));

            if (addedEdgeIndex % 2 == 0) {
                incl[i + 1] = identityIndexOf(removedEdgeList, addedEdgeList.get(addedEdgeIndex + 1)) + 1;
            } else {
                incl[i + 1] = identityIndexOf(removedEdgeList, addedEdgeList.get(addedEdgeIndex - 1)) + 1;
            }

            addedEdgeIndex = identityIndexOf(addedEdgeList, removedEdgeList.get(i + 1));
            if (addedEdgeIndex % 2 == 0) {
                incl[i + 2] = identityIndexOf(removedEdgeList, addedEdgeList.get(addedEdgeIndex + 1)) + 1;
            } else {
                incl[i + 2] = identityIndexOf(removedEdgeList, addedEdgeList.get(addedEdgeIndex - 1)) + 1;
            }
        }

        KOptDescriptor<TestdataListValue> descriptor = new KOptDescriptor<>(tourArray,
                incl,
                getMultiEntitySuccessorFunction(pickedValues,
                        listVariableDescriptor,
                        inverseVariableSupply,
                        indexVariableSupply),
                getMultiEntityBetweenPredicate(pickedValues,
                        listVariableDescriptor,
                        inverseVariableSupply,
                        indexVariableSupply));
        return descriptor.getKOptListMove(listVariableDescriptor, indexVariableSupply, inverseVariableSupply);
    }

    private static int identityIndexOf(List<TestdataListValue> sourceList, TestdataListValue query) {
        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i) == query) {
                return i;
            }
        }
        return -1;
    }

}
