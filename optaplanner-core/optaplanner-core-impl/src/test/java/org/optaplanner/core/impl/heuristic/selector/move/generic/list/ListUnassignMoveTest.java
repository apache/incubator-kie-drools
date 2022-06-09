package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class ListUnassignMoveTest {

    @Test
    void doMove() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2, v3);

        InnerScoreDirector<TestdataListSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);
        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        // Unassign last
        ListUnassignMove<TestdataListSolution> move = new ListUnassignMove<>(variableDescriptor, e1, 2);
        move.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2);

        // The unassign move only serves as an undo move of the assign move. It is not supposed to be done as a regular move.
        assertThatThrownBy(() -> move.doMove(scoreDirector)).isInstanceOf(UnsupportedOperationException.class);

        // Unassign the rest
        new ListUnassignMove<>(variableDescriptor, e1, 0).doMoveOnly(scoreDirector);
        new ListUnassignMove<>(variableDescriptor, e1, 0).doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).isEmpty();
    }

    @Test
    void toStringTest() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("E1", v1);

        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();

        assertThat(new ListUnassignMove<>(variableDescriptor, e1, 0)).hasToString("1 {E1[0] -> null}");
    }
}
