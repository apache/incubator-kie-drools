package org.optaplanner.core.impl.domain.variable.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class ExternalizedIndexVariableSupplyTest {

    @Test
    void listVariable() {
        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();
        ScoreDirector<TestdataListSolution> scoreDirector = mock(ScoreDirector.class);
        ExternalizedIndexVariableSupply<TestdataListSolution> supply =
                new ExternalizedIndexVariableSupply<>(variableDescriptor);

        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = new TestdataListEntity("e1", v1, v2);
        TestdataListEntity e2 = new TestdataListEntity("e2", v3);

        TestdataListSolution solution = new TestdataListSolution();
        solution.setEntityList(new ArrayList<>(Arrays.asList(e1, e2)));
        solution.setValueList(Arrays.asList(v1, v2, v3));

        when(scoreDirector.getWorkingSolution()).thenReturn(solution);
        supply.resetWorkingSolution(scoreDirector);

        // Indexes are set immediately after the working solution is reset.
        assertThat(supply.getIndex(v1)).isEqualTo(0);
        assertThat(supply.getIndex(v2)).isEqualTo(1);
        assertThat(supply.getIndex(v3)).isEqualTo(0);

        // Move v3 from e2[0] to e1[2].
        supply.beforeElementMoved(scoreDirector, e2, 0, e1, 2);
        e2.getValueList().remove(v3);
        e1.getValueList().add(v3);
        supply.afterElementMoved(scoreDirector, e2, 0, e1, 2);

        assertThat(supply.getIndex(v3)).isEqualTo(2);

        // Unassign v1 from e1.
        supply.beforeElementRemoved(scoreDirector, e1, 0);
        e1.getValueList().remove(v1);
        supply.afterElementRemoved(scoreDirector, e1, 0);

        assertThat(supply.getIndex(v1)).isNull();
        assertThat(supply.getIndex(v2)).isEqualTo(0);
        assertThat(supply.getIndex(v3)).isEqualTo(1);

        // Remove e1.
        supply.beforeEntityRemoved(scoreDirector, e1);
        solution.getEntityList().remove(e1);
        supply.afterEntityRemoved(scoreDirector, e1);

        assertThat(supply.getIndex(v2)).isNull();
        assertThat(supply.getIndex(v3)).isNull();

        // Assign v1 to e2.
        supply.beforeElementAdded(scoreDirector, e2, 0);
        e2.getValueList().add(0, v1);
        supply.afterElementAdded(scoreDirector, e2, 0);

        assertThat(supply.getIndex(v1)).isEqualTo(0);

        supply.close();
    }
}
