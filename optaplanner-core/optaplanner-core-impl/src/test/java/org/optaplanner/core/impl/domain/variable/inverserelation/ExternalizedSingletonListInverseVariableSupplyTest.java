package org.optaplanner.core.impl.domain.variable.inverserelation;

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

class ExternalizedSingletonListInverseVariableSupplyTest {

    @Test
    void listVariable() {
        ListVariableDescriptor<TestdataListSolution> variableDescriptor =
                TestdataListEntity.buildVariableDescriptorForValueList();
        ScoreDirector<TestdataListSolution> scoreDirector = mock(ScoreDirector.class);
        ExternalizedSingletonListInverseVariableSupply<TestdataListSolution> supply =
                new ExternalizedSingletonListInverseVariableSupply<>(variableDescriptor);

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

        // Inverse variable is set immediately after the working solution is reset.
        assertThat(supply.getInverseSingleton(v1)).isSameAs(e1);
        assertThat(supply.getInverseSingleton(v2)).isSameAs(e1);
        assertThat(supply.getInverseSingleton(v3)).isSameAs(e2);

        // Move v1 from e1[0] to e2[1].
        supply.beforeListVariableChanged(scoreDirector, e1, 0, 1);
        e1.getValueList().remove(v1);
        supply.afterListVariableChanged(scoreDirector, e1, 0, 0);
        supply.beforeListVariableChanged(scoreDirector, e2, 1, 1);
        e2.getValueList().add(v1);
        supply.afterListVariableChanged(scoreDirector, e2, 1, 2);

        assertThat(supply.getInverseSingleton(v1)).isSameAs(e2);

        // Remove e2.
        supply.beforeEntityRemoved(scoreDirector, e2);
        solution.getEntityList().remove(e2);
        supply.afterEntityRemoved(scoreDirector, e2);

        assertThat(supply.getInverseSingleton(v1)).isNull();
        assertThat(supply.getInverseSingleton(v3)).isNull();

        // Unassign v2 from e1.
        supply.beforeListVariableElementRemoved(scoreDirector, e1, 0);
        e1.getValueList().remove(v2);
        supply.afterListVariableElementRemoved(scoreDirector, e1, 0);

        assertThat(supply.getInverseSingleton(v2)).isNull();

        // Assign v1 to e1.
        supply.beforeListVariableElementAdded(scoreDirector, e1, 0);
        e1.getValueList().add(v1);
        supply.afterListVariableElementAdded(scoreDirector, e1, 0);

        assertThat(supply.getInverseSingleton(v1)).isEqualTo(e1);

        // Return e1 with v2 and v3.
        e2.getValueList().clear();
        e2.getValueList().add(v2);
        e2.getValueList().add(v3);
        supply.beforeEntityAdded(scoreDirector, e2);
        solution.getEntityList().add(e2);
        supply.afterEntityAdded(scoreDirector, e2);

        assertThat(supply.getInverseSingleton(v1)).isEqualTo(e1);
        assertThat(supply.getInverseSingleton(v2)).isEqualTo(e2);
        assertThat(supply.getInverseSingleton(v3)).isEqualTo(e2);

        // Move subList e2[0..2] to e1[1].
        supply.beforeListVariableChanged(scoreDirector, e2, 0, 0);
        supply.beforeListVariableChanged(scoreDirector, e1, 1, 3);
        e1.getValueList().addAll(e2.getValueList());
        e2.getValueList().clear();
        supply.afterListVariableChanged(scoreDirector, e2, 0, 0);
        supply.afterListVariableChanged(scoreDirector, e1, 1, 3);

        assertThat(supply.getInverseSingleton(v1)).isEqualTo(e1);
        assertThat(supply.getInverseSingleton(v2)).isEqualTo(e1);
        assertThat(supply.getInverseSingleton(v3)).isEqualTo(e1);

        supply.close();
    }
}
