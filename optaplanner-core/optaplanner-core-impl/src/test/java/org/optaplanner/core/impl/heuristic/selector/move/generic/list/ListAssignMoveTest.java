package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

class ListAssignMoveTest {

    @Test
    void doMove() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = new TestdataListEntity("e1");

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);
        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        // v1 -> e1[0]
        ListAssignMove<TestdataListSolution> move = new ListAssignMove<>(variableDescriptor, v1, e1, 0);
        AbstractMove<TestdataListSolution> undoMove = move.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1);

        // undo
        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).isEmpty();
        assertThatThrownBy(() -> undoMove.doMove(scoreDirector)).isInstanceOf(UnsupportedOperationException.class);

        // v2 -> e1[0]
        new ListAssignMove<>(variableDescriptor, v2, e1, 0).doMove(scoreDirector);
        // v3 -> e1[1]
        new ListAssignMove<>(variableDescriptor, v3, e1, 1).doMove(scoreDirector);
        // v1 -> e1[0]
        new ListAssignMove<>(variableDescriptor, v1, e1, 0).doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v3);
    }

    @Test
    void rebase() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListEntity e1 = new TestdataListEntity("e1");

        TestdataListValue destinationV1 = new TestdataListValue("1");
        TestdataListEntity destinationE1 = new TestdataListEntity("e1");

        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        ScoreDirector<TestdataListSolution> destinationScoreDirector = mockRebasingScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][] {
                        { v1, destinationV1 },
                        { e1, destinationE1 },
                });

        assertSameProperties(
                destinationV1, destinationE1, 0,
                new ListAssignMove<>(variableDescriptor, v1, e1, 0).rebase(destinationScoreDirector));
    }

    static void assertSameProperties(
            Object movedValue, Object destinationEntity, int destinationIndex,
            ListAssignMove<?> move) {
        assertThat(move.getMovedValue()).isSameAs(movedValue);
        assertThat(move.getDestinationEntity()).isSameAs(destinationEntity);
        assertThat(move.getDestinationIndex()).isEqualTo(destinationIndex);
    }

    @Test
    void toStringTest() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListEntity e1 = new TestdataListEntity("E1");

        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        assertThat(new ListAssignMove<>(variableDescriptor, v1, e1, 15)).hasToString("1 {null -> E1[15]}");
    }
}
