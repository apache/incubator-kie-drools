package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class ListSwapMoveTest {

    @Test
    void isMoveDoable() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2);
        TestdataListEntity e2 = new TestdataListEntity("e2", v3);

        ScoreDirector<TestdataListSolution> scoreDirector = mock(ScoreDirector.class);
        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        // same entity, same index => not doable because the move doesn't change anything
        assertThat(new ListSwapMove<>(variableDescriptor, e1, 1, e1, 1).isMoveDoable(scoreDirector)).isFalse();
        // same entity, different index => doable
        assertThat(new ListSwapMove<>(variableDescriptor, e1, 0, e1, 1).isMoveDoable(scoreDirector)).isTrue();
        // different entity => doable
        assertThat(new ListSwapMove<>(variableDescriptor, e1, 0, e2, 0).isMoveDoable(scoreDirector)).isTrue();
    }

    @Test
    void doMove() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2);
        TestdataListEntity e2 = new TestdataListEntity("e2", v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);
        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        // Swap Move 1: between two entities
        ListSwapMove<TestdataListSolution> move1 = new ListSwapMove<>(variableDescriptor, e1, 0, e2, 0);

        AbstractMove<TestdataListSolution> undoMove1 = move1.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v3, v2);
        assertThat(e2.getValueList()).containsExactly(v1);

        // undo
        undoMove1.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2);
        assertThat(e2.getValueList()).containsExactly(v3);

        // Swap Move 2: same entity
        ListSwapMove<TestdataListSolution> move2 = new ListSwapMove<>(variableDescriptor, e1, 0, e1, 1);

        AbstractMove<TestdataListSolution> undoMove2 = move2.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v2, v1);

        // undo
        undoMove2.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2);
    }

    @Test
    void rebase() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2);
        TestdataListEntity e2 = new TestdataListEntity("e2", v3);

        TestdataListValue destinationV1 = new TestdataListValue("1");
        TestdataListValue destinationV2 = new TestdataListValue("2");
        TestdataListValue destinationV3 = new TestdataListValue("3");
        TestdataListEntity destinationE1 = new TestdataListEntity("e1", destinationV1, destinationV2);
        TestdataListEntity destinationE2 = new TestdataListEntity("e2", destinationV3);

        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        ScoreDirector<TestdataListSolution> destinationScoreDirector = mockRebasingScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][] {
                        { v1, destinationV1 },
                        { v2, destinationV2 },
                        { v3, destinationV3 },
                        { e1, destinationE1 },
                        { e2, destinationE2 },
                });

        assertSameProperties(
                destinationE1, 1, destinationV2,
                destinationE2, 0, destinationV3,
                new ListSwapMove<>(variableDescriptor, e1, 1, e2, 0).rebase(destinationScoreDirector));
        assertSameProperties(
                destinationE1, 0, destinationV1,
                destinationE1, 1, destinationV2,
                new ListSwapMove<>(variableDescriptor, e1, 0, e1, 1).rebase(destinationScoreDirector));
    }

    static void assertSameProperties(
            Object leftEntity, int leftIndex, Object leftValue,
            Object rightEntity, int rightIndex, Object rightValue,
            ListSwapMove<?> move) {
        assertThat(move.getLeftEntity()).isSameAs(leftEntity);
        assertThat(move.getLeftIndex()).isEqualTo(leftIndex);
        assertThat(move.getLeftValue()).isSameAs(leftValue);
        assertThat(move.getRightEntity()).isSameAs(rightEntity);
        assertThat(move.getRightIndex()).isEqualTo(rightIndex);
        assertThat(move.getRightValue()).isSameAs(rightValue);
    }

    @Test
    void tabuIntrospection() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2);
        TestdataListEntity e2 = new TestdataListEntity("e2", v3);

        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        // Swap Move 1: between two entities
        ListSwapMove<TestdataListSolution> move1 = new ListSwapMove<>(variableDescriptor, e1, 0, e2, 0);
        assertThat(move1.getPlanningEntities()).containsExactly(e1, e2);
        assertThat(move1.getPlanningValues()).containsExactly(v1, v3);

        // Swap Move 2: same entity
        ListSwapMove<TestdataListSolution> move2 = new ListSwapMove<>(variableDescriptor, e1, 0, e1, 1);
        assertThat(move2.getPlanningEntities()).containsExactly(e1);
        assertThat(move2.getPlanningValues()).containsExactly(v1, v2);
    }

    @Test
    void toStringTest() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2);
        TestdataListEntity e2 = new TestdataListEntity("e2", v3);

        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        assertThat(new ListSwapMove<>(variableDescriptor, e1, 0, e1, 1)).hasToString("1 {e1[0]} <-> 2 {e1[1]}");
        assertThat(new ListSwapMove<>(variableDescriptor, e1, 1, e2, 0)).hasToString("2 {e1[1]} <-> 3 {e2[0]}");
    }
}
