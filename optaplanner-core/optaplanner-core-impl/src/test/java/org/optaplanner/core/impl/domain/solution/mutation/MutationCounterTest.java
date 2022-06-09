package org.optaplanner.core.impl.domain.solution.mutation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

class MutationCounterTest {

    @Test
    void countMutationsNone() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        MutationCounter<TestdataSolution> mutationCounter = new MutationCounter<>(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);

        TestdataEntity a_a = new TestdataEntity("a", val1);
        TestdataEntity a_b = new TestdataEntity("b", val1);
        TestdataEntity a_c = new TestdataEntity("c", val3);
        TestdataEntity a_d = new TestdataEntity("d", val3);
        List<TestdataEntity> aEntityList = Arrays.asList(a_a, a_b, a_c, a_d);

        TestdataSolution a = new TestdataSolution("solution");
        a.setValueList(valueList);
        a.setEntityList(aEntityList);

        TestdataEntity b_a = new TestdataEntity("a", val1);
        TestdataEntity b_b = new TestdataEntity("b", val1);
        TestdataEntity b_c = new TestdataEntity("c", val3);
        TestdataEntity b_d = new TestdataEntity("d", val3);
        List<TestdataEntity> bEntityList = Arrays.asList(b_a, b_b, b_c, b_d);

        TestdataSolution b = new TestdataSolution("solution");
        b.setValueList(valueList);
        b.setEntityList(bEntityList);

        assertThat(mutationCounter.countMutations(a, b)).isEqualTo(0);
    }

    @Test
    void countMutationsSome() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        MutationCounter<TestdataSolution> mutationCounter = new MutationCounter<>(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);

        TestdataEntity a_a = new TestdataEntity("a", val1);
        TestdataEntity a_b = new TestdataEntity("b", val1);
        TestdataEntity a_c = new TestdataEntity("c", val3);
        TestdataEntity a_d = new TestdataEntity("d", val3);
        List<TestdataEntity> aEntityList = Arrays.asList(a_a, a_b, a_c, a_d);

        TestdataSolution a = new TestdataSolution("solution");
        a.setValueList(valueList);
        a.setEntityList(aEntityList);

        TestdataEntity b_a = new TestdataEntity("a", val3); // Mutated
        TestdataEntity b_b = new TestdataEntity("b", val1);
        TestdataEntity b_c = new TestdataEntity("c", val3);
        TestdataEntity b_d = new TestdataEntity("d", val2); // Mutated
        List<TestdataEntity> bEntityList = Arrays.asList(b_a, b_b, b_c, b_d);

        TestdataSolution b = new TestdataSolution("solution");
        b.setValueList(valueList);
        b.setEntityList(bEntityList);

        assertThat(mutationCounter.countMutations(a, b)).isEqualTo(2);
    }

    @Test
    void countMutationsAll() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        MutationCounter<TestdataSolution> mutationCounter = new MutationCounter<>(solutionDescriptor);

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);

        TestdataEntity a_a = new TestdataEntity("a", val1);
        TestdataEntity a_b = new TestdataEntity("b", val1);
        TestdataEntity a_c = new TestdataEntity("c", val3);
        TestdataEntity a_d = new TestdataEntity("d", val3);
        List<TestdataEntity> aEntityList = Arrays.asList(a_a, a_b, a_c, a_d);

        TestdataSolution a = new TestdataSolution("solution");
        a.setValueList(valueList);
        a.setEntityList(aEntityList);

        TestdataEntity b_a = new TestdataEntity("a", val2); // Mutated
        TestdataEntity b_b = new TestdataEntity("b", val2); // Mutated
        TestdataEntity b_c = new TestdataEntity("c", val2); // Mutated
        TestdataEntity b_d = new TestdataEntity("d", val2); // Mutated
        List<TestdataEntity> bEntityList = Arrays.asList(b_a, b_b, b_c, b_d);

        TestdataSolution b = new TestdataSolution("solution");
        b.setValueList(valueList);
        b.setEntityList(bEntityList);

        assertThat(mutationCounter.countMutations(a, b)).isEqualTo(4);
    }

}
