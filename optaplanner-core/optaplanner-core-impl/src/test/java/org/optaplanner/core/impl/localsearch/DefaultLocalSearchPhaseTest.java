package org.optaplanner.core.impl.localsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.domain.list.externalized.TestdataListEntityExternalized;
import org.optaplanner.core.impl.testdata.domain.list.externalized.TestdataListSolutionExternalized;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class DefaultLocalSearchPhaseTest {

    @Test
    void solveWithInitializedEntities() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataEntity("e1", v1),
                new TestdataEntity("e2", v2),
                new TestdataEntity("e3", v1)));

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
        TestdataEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertThat(solvedE1.getValue()).isNotNull();
        TestdataEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertThat(solvedE2.getValue()).isNotNull();
        TestdataEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertThat(solvedE3.getValue()).isNotNull();
    }

    @Test
    void solveWithPinnedEntities() {
        SolverConfig solverConfig =
                PlannerTestUtils.buildSolverConfig(TestdataPinnedSolution.class, TestdataPinnedEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));

        TestdataPinnedSolution solution = new TestdataPinnedSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataPinnedEntity("e1", v1, false, false),
                new TestdataPinnedEntity("e2", v2, true, false),
                new TestdataPinnedEntity("e3", null, false, true)));

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
        TestdataPinnedEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertThat(solvedE1.getValue()).isNotNull();
        TestdataPinnedEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertThat(solvedE2.getValue()).isEqualTo(v2);
        TestdataPinnedEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertThat(solvedE3.getValue()).isEqualTo(null);
    }

    @Test
    void solveWithEmptyEntityList() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Collections.emptyList());

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getEntityList().size()).isEqualTo(0);
    }

    @Test
    void solveTabuSearchWithInitializedEntities() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataEntity("e1", v1),
                new TestdataEntity("e2", v2),
                new TestdataEntity("e3", v1)));

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
        TestdataEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertThat(solvedE1.getValue()).isNotNull();
        TestdataEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertThat(solvedE2.getValue()).isNotNull();
        TestdataEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertThat(solvedE3.getValue()).isNotNull();
    }

    @Test
    void solveTabuSearchWithPinnedEntities() {
        SolverConfig solverConfig =
                PlannerTestUtils.buildSolverConfig(TestdataPinnedSolution.class, TestdataPinnedEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));

        TestdataPinnedSolution solution = new TestdataPinnedSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Arrays.asList(
                new TestdataPinnedEntity("e1", v1, false, false),
                new TestdataPinnedEntity("e2", v2, true, false),
                new TestdataPinnedEntity("e3", null, false, true)));

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
        TestdataPinnedEntity solvedE1 = solution.getEntityList().get(0);
        assertCode("e1", solvedE1);
        assertThat(solvedE1.getValue()).isNotNull();
        TestdataPinnedEntity solvedE2 = solution.getEntityList().get(1);
        assertCode("e2", solvedE2);
        assertThat(solvedE2.getValue()).isEqualTo(v2);
        TestdataPinnedEntity solvedE3 = solution.getEntityList().get(2);
        assertCode("e3", solvedE3);
        assertThat(solvedE3.getValue()).isEqualTo(null);
    }

    @Test
    void solveTabuSearchWithEmptyEntityList() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setLocalSearchType(LocalSearchType.TABU_SEARCH);
        phaseConfig.setTerminationConfig(new TerminationConfig().withScoreCalculationCountLimit(10L));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));

        TestdataSolution solution = new TestdataSolution("s1");
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        solution.setValueList(Arrays.asList(v1, v2, v3));
        solution.setEntityList(Collections.emptyList());

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getEntityList().size()).isEqualTo(0);
    }

    @Test
    void solveListVariable() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataListSolution.class, TestdataListEntity.class, TestdataListValue.class);

        TestdataListSolution solution = TestdataListSolution.generateUninitializedSolution(6, 2);

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
    }

    @Test
    void solveListVariableWithExternalizedInverseAndIndexSupplies() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataListSolutionExternalized.class, TestdataListEntityExternalized.class);

        TestdataListSolutionExternalized solution = TestdataListSolutionExternalized.generateUninitializedSolution(6, 2);

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
    }

}
