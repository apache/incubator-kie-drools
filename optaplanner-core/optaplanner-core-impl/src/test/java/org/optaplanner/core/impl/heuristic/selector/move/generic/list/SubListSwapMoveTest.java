package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class SubListSwapMoveTest {

    private final TestdataListValue v1 = new TestdataListValue("1");
    private final TestdataListValue v2 = new TestdataListValue("2");
    private final TestdataListValue v3 = new TestdataListValue("3");
    private final TestdataListValue v4 = new TestdataListValue("4");
    private final TestdataListValue v5 = new TestdataListValue("5");
    private final TestdataListValue v6 = new TestdataListValue("6");
    private final TestdataListValue v7 = new TestdataListValue("7");

    private final InnerScoreDirector<TestdataListSolution, ?> scoreDirector = mock(InnerScoreDirector.class);
    private final ListVariableDescriptor<TestdataListSolution> variableDescriptor =
            TestdataListEntity.buildVariableDescriptorForValueList();

    @Test
    void isMoveDoable() {
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2, v3, v4);
        TestdataListEntity e2 = new TestdataListEntity("e2", v5);

        // same entity, overlap => not doable
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 3, e1, 2, 5, false).isMoveDoable(scoreDirector)).isFalse();
        // same entity, overlap => not doable
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 5, e1, 1, 2, false).isMoveDoable(scoreDirector)).isFalse();
        // same entity, no overlap (with gap) => doable
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 1, e1, 4, 5, false).isMoveDoable(scoreDirector)).isTrue();
        // same entity, no overlap (with touch) => doable
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 3, e1, 3, 5, false).isMoveDoable(scoreDirector)).isTrue();
        // same entity, no overlap (with touch, right below left) => doable
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 2, 5, e1, 0, 2, false).isMoveDoable(scoreDirector)).isTrue();
        // different entities => doable
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 5, e2, 0, 1, false).isMoveDoable(scoreDirector)).isTrue();
    }

    @Test
    void doMove() {
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2, v3, v4);
        TestdataListEntity e2 = new TestdataListEntity("e2", v5);

        SubListSwapMove<TestdataListSolution> move = new SubListSwapMove<>(variableDescriptor, e1, 1, 3, e2, 0, 1, false);

        AbstractMove<TestdataListSolution> undoMove = move.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v5, v4);
        assertThat(e2.getValueList()).containsExactly(v2, v3);

        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 1, 3);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 1, 2);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 1);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 2);
        verify(scoreDirector).triggerVariableListeners();
        verifyNoMoreInteractions(scoreDirector);

        undoMove.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4);
        assertThat(e2.getValueList()).containsExactly(v5);
    }

    @Test
    void doReversingMove() {
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2, v3, v4);
        TestdataListEntity e2 = new TestdataListEntity("e2", v5, v6);

        SubListSwapMove<TestdataListSolution> move = new SubListSwapMove<>(variableDescriptor, e1, 0, 3, e2, 0, 2, true);

        AbstractMove<TestdataListSolution> undoMove = move.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v6, v5, v4);
        assertThat(e2.getValueList()).containsExactly(v3, v2, v1);

        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 3);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 2);
        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e2, 0, 2);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e2, 0, 3);
        verify(scoreDirector).triggerVariableListeners();
        verifyNoMoreInteractions(scoreDirector);

        undoMove.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4);
        assertThat(e2.getValueList()).containsExactly(v5, v6);
    }

    @Test
    void doMoveOnSameEntity() {
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2, v3, v4, v5, v6, v7);

        SubListSwapMove<TestdataListSolution> move = new SubListSwapMove<>(variableDescriptor, e1, 0, 1, e1, 4, 7, false);

        AbstractMove<TestdataListSolution> undoMove = move.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v5, v6, v7, v2, v3, v4, v1);

        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 7);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 7);
        // TODO or this more fine-grained? (Do we allow multiple notifications per entity? (Yes))
        // verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 1);
        // verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 3);
        // verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 4, 7);
        // verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 6, 7);
        verify(scoreDirector).triggerVariableListeners();
        verifyNoMoreInteractions(scoreDirector);

        undoMove.doMove(scoreDirector);

        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4, v5, v6, v7);
    }

    @Test
    void rebase() {
        TestdataListEntity e1 = new TestdataListEntity("e1");
        TestdataListEntity e2 = new TestdataListEntity("e2");

        TestdataListEntity destinationE1 = new TestdataListEntity("e1");
        TestdataListEntity destinationE2 = new TestdataListEntity("e2");

        ScoreDirector<TestdataListSolution> destinationScoreDirector = mockRebasingScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][] {
                        { e1, destinationE1 },
                        { e2, destinationE2 },
                });

        boolean reversing = false;
        int leftFromIndex = 7;
        int leftToIndex = 11;
        int rightFromIndex = 3;
        int rightToIndex = 9;
        assertSameProperties(destinationE1, leftFromIndex, leftToIndex, destinationE2, rightFromIndex, rightToIndex, reversing,
                new SubListSwapMove<>(variableDescriptor, e1, leftFromIndex, leftToIndex, e2, rightFromIndex, rightToIndex,
                        reversing)
                        .rebase(destinationScoreDirector));
    }

    static void assertSameProperties(
            Object leftEntity, int leftFromIndex, int leftToIndex,
            Object rightEntity, int rightFromIndex, int rightToIndex,
            boolean reversing,
            SubListSwapMove<?> move) {
        SubList leftSubList = move.getLeftSubList();
        assertThat(leftSubList.getEntity()).isSameAs(leftEntity);
        assertThat(leftSubList.getFromIndex()).isEqualTo(leftFromIndex);
        assertThat(leftSubList.getToIndex()).isEqualTo(leftToIndex);
        SubList rightSubList = move.getRightSubList();
        assertThat(rightSubList.getEntity()).isSameAs(rightEntity);
        assertThat(rightSubList.getFromIndex()).isEqualTo(rightFromIndex);
        assertThat(rightSubList.getToIndex()).isEqualTo(rightToIndex);
        assertThat(move.isReversing()).isEqualTo(reversing);
    }

    @Test
    void tabuIntrospection_twoEntities() {
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2, v3, v4);
        TestdataListEntity e2 = new TestdataListEntity("e2", v5);
        TestdataListEntity e3 = new TestdataListEntity("e3");

        SubListSwapMove<TestdataListSolution> moveTwoEntities =
                new SubListSwapMove<>(variableDescriptor, e1, 0, 3, e2, 0, 1, false);
        // Do the move first because that might affect the returned values.
        moveTwoEntities.doMoveOnGenuineVariables(scoreDirector);
        assertThat(moveTwoEntities.getPlanningEntities()).containsExactly(e1, e2);
        assertThat(moveTwoEntities.getPlanningValues()).containsExactly(v1, v2, v3, v5);

        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 3, e2, 0, 1, true)).isNotEqualTo(moveTwoEntities);
        //                                                                       ^
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 3, e2, 0, 2, false)).isNotEqualTo(moveTwoEntities);
        //                                                                    ^
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 3, e2, 1, 1, false)).isNotEqualTo(moveTwoEntities);
        //                                                                 ^
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 3, e3, 0, 1, false)).isNotEqualTo(moveTwoEntities);
        //                                                             ^
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 1, 4, e2, 0, 1, false)).isNotEqualTo(moveTwoEntities);
        //                                                       ^  ^
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 1, 3, e2, 0, 1, false)).isNotEqualTo(moveTwoEntities);
        //                                                       ^
        assertThat(new SubListSwapMove<>(variableDescriptor, e3, 0, 3, e2, 0, 1, false)).isNotEqualTo(moveTwoEntities);
        //                                                    ^
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 3, e2, 0, 1, false)).isEqualTo(moveTwoEntities);
    }

    @Test
    void tabuIntrospection_oneEntity() {
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2, v3, v4);

        SubListSwapMove<TestdataListSolution> moveOneEntity =
                new SubListSwapMove<>(variableDescriptor, e1, 3, 4, e1, 0, 2, false);
        // Do the move first because that might affect the returned values.
        moveOneEntity.doMoveOnGenuineVariables(scoreDirector);
        assertThat(moveOneEntity.getPlanningEntities()).containsExactly(e1);
        assertThat(moveOneEntity.getPlanningValues()).containsExactly(v1, v2, v4);

        // Swaps on the same entity are normalized so that the lower index subList is always treated as the left one.
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 2, e1, 3, 4, false)).isEqualTo(moveOneEntity);
    }

    @Test
    void toStringTest() {
        TestdataListEntity e1 = new TestdataListEntity("e1");
        TestdataListEntity e2 = new TestdataListEntity("e2");

        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 1, 4, e1, 0, 1, false))
                .hasToString("{e1[0..1]} <-> {e1[1..4]}");
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 1, e2, 1, 6, false))
                .hasToString("{e1[0..1]} <-> {e2[1..6]}");
        assertThat(new SubListSwapMove<>(variableDescriptor, e1, 0, 1, e2, 1, 6, true))
                .hasToString("{e1[0..1]} <-reversing-> {e2[1..6]}");
    }
}
