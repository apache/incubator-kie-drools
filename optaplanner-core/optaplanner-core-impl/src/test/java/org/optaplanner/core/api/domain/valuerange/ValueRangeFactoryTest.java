package org.optaplanner.core.api.domain.valuerange;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.testdata.domain.valuerange.TestdataValueRangeEntity;
import org.optaplanner.core.impl.testdata.domain.valuerange.TestdataValueRangeSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class ValueRangeFactoryTest {

    @Test
    void solve() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataValueRangeSolution.class, TestdataValueRangeEntity.class);

        TestdataValueRangeSolution solution = new TestdataValueRangeSolution("s1");
        solution.setEntityList(Arrays.asList(new TestdataValueRangeEntity("e1"), new TestdataValueRangeEntity("e2")));

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
    }

}
