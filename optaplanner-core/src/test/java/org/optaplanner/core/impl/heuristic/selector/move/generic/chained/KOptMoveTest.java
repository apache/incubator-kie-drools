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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertArrayElementsSameExactly;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableDemand;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorVariableSupply;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class KOptMoveTest {

    @Test
    public void doMove3OptWith3Chains() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = variableDescriptor.getEntityDescriptor()
                .getSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        TestdataChainedAnchor c0 = new TestdataChainedAnchor("c0");
        TestdataChainedEntity c1 = new TestdataChainedEntity("c1", c0);
        TestdataChainedEntity c2 = new TestdataChainedEntity("c2", c1);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0, c0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1, b2, c1, c2));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
        SelectorTestUtils.assertChain(c0, c1, c2);

        KOptMove<TestdataChainedSolution> move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply,
                a2, new Object[] { b0, c1 });
        assertThat(move.isMoveDoable(scoreDirector)).isTrue();
        KOptMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, c2);
        SelectorTestUtils.assertChain(b0, a2, a3);
        SelectorTestUtils.assertChain(c0, c1, b1, b2);

        assertThat(undoMove.isMoveDoable(scoreDirector)).isTrue();
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
        SelectorTestUtils.assertChain(c0, c1, c2);

        // To tail value
        move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[] { b2, c2 });
        assertThat(move.isMoveDoable(scoreDirector)).isTrue();
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1);
        SelectorTestUtils.assertChain(b0, b1, b2, a2, a3);
        SelectorTestUtils.assertChain(c0, c1, c2);

        assertThat(undoMove.isMoveDoable(scoreDirector)).isTrue();
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1, b2);
        SelectorTestUtils.assertChain(c0, c1, c2);
    }

    @Test
    public void doMove3OptWithOnly2Chains() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = variableDescriptor.getEntityDescriptor()
                .getSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);
        TestdataChainedEntity b3 = new TestdataChainedEntity("b3", b2);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4, b1, b2, b3));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);

        KOptMove<TestdataChainedSolution> move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply,
                a4, new Object[] { a1, b2 });
        assertThat(move.isMoveDoable(scoreDirector)).isTrue();
        KOptMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, a2, a3, b3);

        assertThat(undoMove.isMoveDoable(scoreDirector)).isTrue();
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);

        // Same move, different order
        move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[] { b2, a3 });
        assertThat(move.isMoveDoable(scoreDirector)).isTrue();
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, a2, a3, b3);

        assertThat(undoMove.isMoveDoable(scoreDirector)).isTrue();
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);

        // Same move, yet another order
        move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, b3, new Object[] { a3, a1 });
        assertThat(move.isMoveDoable(scoreDirector)).isTrue();
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, a2, a3, b3);

        assertThat(undoMove.isMoveDoable(scoreDirector)).isTrue();
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);

        // These moves would create a loop
        move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[] { a3, b2 });
        assertThat(move.isMoveDoable(scoreDirector)).isFalse();
        move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a4, new Object[] { b2, a1 });
        assertThat(move.isMoveDoable(scoreDirector)).isFalse();
        move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, b3, new Object[] { a1, a3 });
        assertThat(move.isMoveDoable(scoreDirector)).isFalse();
    }

    @Test
    @Disabled("Valid 1 chain moves aren't supported yet") // TODO
    public void doMove3OptWithOnly1Chain() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = variableDescriptor.getEntityDescriptor()
                .getSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        TestdataChainedEntity a6 = new TestdataChainedEntity("a6", a5);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, a4, a5, a6));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6);

        KOptMove<TestdataChainedSolution> move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply,
                a6, new Object[] { a3, a1 });
        assertThat(move.isMoveDoable(scoreDirector)).isTrue();
        KOptMove<TestdataChainedSolution> undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4, a5, a2, a3, a6);

        assertThat(undoMove.isMoveDoable(scoreDirector)).isTrue();
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6);

        // Same move, different order
        move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a4, new Object[] { a1, a5 });
        assertThat(move.isMoveDoable(scoreDirector)).isTrue();
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4, a5, a2, a3, a6);

        assertThat(undoMove.isMoveDoable(scoreDirector)).isTrue();
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6);

        // Same move, yet another order
        move = new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a2, new Object[] { a5, a3 });
        assertThat(move.isMoveDoable(scoreDirector)).isTrue();
        undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a4, a5, a2, a3, a6);

        assertThat(undoMove.isMoveDoable(scoreDirector)).isTrue();
        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5, a6);

        // These moves would create a loop
        // TODO
    }

    @Test
    @Disabled("https://issues.redhat.com/browse/PLANNER-1250") // TODO https://issues.redhat.com/browse/PLANNER-1250
    public void rebase() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedAnchor c0 = new TestdataChainedAnchor("c0");
        TestdataChainedEntity c1 = new TestdataChainedEntity("c1", c0);

        TestdataChainedAnchor destinationA0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity destinationA1 = new TestdataChainedEntity("a1", destinationA0);
        TestdataChainedEntity destinationA2 = new TestdataChainedEntity("a2", destinationA1);
        TestdataChainedEntity destinationA3 = new TestdataChainedEntity("a3", destinationA2);
        TestdataChainedAnchor destinationB0 = new TestdataChainedAnchor("b0");
        TestdataChainedAnchor destinationC0 = new TestdataChainedAnchor("c0");
        TestdataChainedEntity destinationC1 = new TestdataChainedEntity("c1", destinationC0);

        ScoreDirector<TestdataChainedSolution> destinationScoreDirector = mockRebasingScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][] {
                        { a0, destinationA0 },
                        { a1, destinationA1 },
                        { a2, destinationA2 },
                        { a3, destinationA3 },
                        { b0, destinationB0 },
                        { c0, destinationC0 },
                        { c1, destinationC1 },
                });
        SingletonInverseVariableSupply inverseVariableSupply = mock(SingletonInverseVariableSupply.class);
        AnchorVariableSupply anchorVariableSupply = mock(AnchorVariableSupply.class);

        assertSameProperties(destinationA1, new Object[] { destinationC1, destinationB0 },
                new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a1, new Object[] { c1, b0 })
                        .rebase(destinationScoreDirector));
        assertSameProperties(destinationA3, new Object[] { destinationA0, destinationB0 },
                new KOptMove<>(variableDescriptor, inverseVariableSupply, anchorVariableSupply, a3, new Object[] { a0, b0 })
                        .rebase(destinationScoreDirector));
    }

    public void assertSameProperties(Object leftentity, Object[] values, KOptMove move) {
        assertThat(move.getEntity()).isSameAs(leftentity);
        assertArrayElementsSameExactly(values, move.getValues());
    }

    @Test
    public void toStringTest() {
        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor = TestdataChainedEntity
                .buildVariableDescriptorForChainedObject();
        SolutionDescriptor<TestdataChainedSolution> solutionDescriptor = variableDescriptor.getEntityDescriptor()
                .getSolutionDescriptor();
        InnerScoreDirector<TestdataChainedSolution> scoreDirector = PlannerTestUtils.mockScoreDirector(solutionDescriptor);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);

        TestdataChainedAnchor c0 = new TestdataChainedAnchor("c0");
        TestdataChainedEntity c1 = new TestdataChainedEntity("c1", c0);
        TestdataChainedEntity c2 = new TestdataChainedEntity("c2", c1);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0, c0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1, b2, c1, c2));

        scoreDirector.setWorkingSolution(solution);
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(variableDescriptor));
        AnchorVariableSupply anchorVariableSupply = scoreDirector.getSupplyManager()
                .demand(new AnchorVariableDemand(variableDescriptor));

        assertThat(new KOptMove<>(variableDescriptor,
                inverseVariableSupply, anchorVariableSupply, a2, new Object[] { b0, c1 }).toString())
                        .isEqualTo("a2 {a1} -kOpt-> b1 {b0} -kOpt-> c2 {c1}");
        assertThat(new KOptMove<>(variableDescriptor,
                inverseVariableSupply, anchorVariableSupply, a2, new Object[] { b2, c2 }).toString())
                        .isEqualTo("a2 {a1} -kOpt-> null {b2} -kOpt-> null {c2}");
    }

}
