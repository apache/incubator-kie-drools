package org.optaplanner.core.impl.heuristic.selector.entity.pillar;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfPillarSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingPillarSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertEmptyNeverEndingPillarSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.heuristic.selector.entity.pillar.SubPillarConfigPolicy;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.support.VariableListenerSupport;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;
import org.optaplanner.core.impl.testutil.TestRandom;

class DefaultPillarSelectorTest {

    @Test
    void originalNoSubs() {
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataValue val4 = new TestdataValue("4");

        final TestdataEntity a = new TestdataEntity("a", val1);
        final TestdataEntity b = new TestdataEntity("b", val2);
        final TestdataEntity c = new TestdataEntity("c", val3);
        final TestdataEntity d = new TestdataEntity("d", val2);
        final TestdataEntity e = new TestdataEntity("e", val3);
        final TestdataEntity f = new TestdataEntity("f", val3);

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                a, b, c, d, e, f);

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(entitySelector, List.of(variableDescriptor), false,
                SubPillarConfigPolicy.withoutSubpillars());

        SolverScope<TestdataSolution> solverScope = mockSolverScope();
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        assertAllCodesOfPillarSelector(pillarSelector, "[a]", "[b, d]", "[c, e, f]");
        pillarSelector.stepEnded(stepScopeA1);

        b.setValue(val3);
        f.setValue(val4);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        assertAllCodesOfPillarSelector(pillarSelector, "[a]", "[b, c, e]", "[d]", "[f]");
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        assertAllCodesOfPillarSelector(pillarSelector, "[a]", "[b, c, e]", "[d]", "[f]");
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    private SolverScope<TestdataSolution> mockSolverScope() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector = mock(InnerScoreDirector.class);
        doReturn(solutionDescriptor).when(scoreDirector).getSolutionDescriptor();
        doReturn(VariableListenerSupport.create(scoreDirector)).when(scoreDirector).getSupplyManager();

        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        doReturn(scoreDirector).when(solverScope).getScoreDirector();
        return solverScope;
    }

    @Test
    void emptyEntitySelectorOriginalNoSubs() {
        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor());

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(entitySelector, List.of(variableDescriptor), false,
                SubPillarConfigPolicy.withoutSubpillars());

        SolverScope<TestdataSolution> solverScope = mockSolverScope();
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        assertAllCodesOfPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        assertAllCodesOfPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        assertAllCodesOfPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    @Test
    void randomWithSubs() {
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataValue val4 = new TestdataValue("4");

        final TestdataEntity a = new TestdataEntity("a", val1);
        final TestdataEntity b = new TestdataEntity("b", val2);
        final TestdataEntity c = new TestdataEntity("c", val3);
        final TestdataEntity d = new TestdataEntity("d", val2);
        final TestdataEntity e = new TestdataEntity("e", val3);
        final TestdataEntity f = new TestdataEntity("f", val3);

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                a, b, c, d, e, f);

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), true,
                SubPillarConfigPolicy.withSubpillarsUnlimited());

        TestRandom workingRandom = new TestRandom(0);

        SolverScope<TestdataSolution> solverScope = mockSolverScope();
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = PlannerTestUtils.delegatingPhaseScope(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = PlannerTestUtils.delegatingStepScope(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        // nextInt pattern: pillarIndex, subPillarSize, element 0, element 1, element 2, ...
        // Expected pillar cache: [a], [b, d], [c, e, f]
        workingRandom.reset(
                0, // [a]
                2, 1, 0, 0, // [c, e, f]
                1, 0, 0, // [b, d]
                1, 0, 1, 1, 1); // [b, d]
        assertCodesOfNeverEndingPillarSelector(pillarSelector, "[a]", "[c, e]", "[b]", "[d]");
        pillarSelector.stepEnded(stepScopeA1);

        b.setValue(val3);
        f.setValue(val4);

        AbstractStepScope stepScopeA2 = PlannerTestUtils.delegatingStepScope(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        // nextInt pattern: pillarIndex, subPillarSize, element 0, element 1, element 2, ...
        // Expected pillar cache: [a], [b, c, e], [d], [f]
        workingRandom.reset(
                3, // [f]
                1, 2, // [b, c, e]
                1, 0, 0, // [b, c, e]
                2, 0, 0); // [d]
        assertCodesOfNeverEndingPillarSelector(pillarSelector, "[f]", "[b, c, e]", "[b]", "[d]");
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = PlannerTestUtils.delegatingPhaseScope(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = PlannerTestUtils.delegatingStepScope(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        // nextInt pattern: pillarIndex, subPillarSize, element 0, element 1, element 2, ...
        // Expected pillar cache: [a], [b, c, e], [d], [f]
        workingRandom.reset(
                3, // [f]
                1, 2, // [b, c, e]
                1, 0, 0, // [b, c, e]
                2, 0, 0); // [d]
        assertCodesOfNeverEndingPillarSelector(pillarSelector, "[f]", "[b, c, e]", "[b]", "[d]");
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    @Test
    void randomWithSubs_Size2() {
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");

        final TestdataEntity a = new TestdataEntity("a", val1);
        final TestdataEntity b = new TestdataEntity("b", val2);
        final TestdataEntity c = new TestdataEntity("c", val3);
        final TestdataEntity d = new TestdataEntity("d", val2);
        final TestdataEntity e = new TestdataEntity("e", val3);
        final TestdataEntity f = new TestdataEntity("f", val3);

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                a, b, c, d, e, f);

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), true, SubPillarConfigPolicy.withSubpillars(2, 2));

        // nextInt pattern: pillarIndex, subPillarSize, element 0, element 1, element 2, ...
        // Expected pillar cache: [b, d], [c, e, f]
        Random workingRandom = new TestRandom(
                1, 0, 0, 0, // [c, e]
                0, 0, 0, 0); // [b, d]

        SolverScope<TestdataSolution> solverScope = mockSolverScope();
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = PlannerTestUtils.delegatingPhaseScope(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = PlannerTestUtils.delegatingStepScope(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        assertCodesOfNeverEndingPillarSelector(pillarSelector, "[c, e]", "[b, d]");
        pillarSelector.stepEnded(stepScopeA1);

        pillarSelector.phaseEnded(phaseScopeA);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 1);
    }

    @Test
    void sequentialUnlimited() {
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");

        final TestdataEntity a = new TestdataEntity("a", val1);
        final TestdataEntity b = new TestdataEntity("b", val2);
        final TestdataEntity c = new TestdataEntity("c", val3);
        final TestdataEntity d = new TestdataEntity("d", val2);
        final TestdataEntity e = new TestdataEntity("e", val3);
        final TestdataEntity f = new TestdataEntity("f", val3);

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                a, f, d, e, c, b); // return entities in random order

        Comparator<TestdataEntity> lexicographicComparator = Comparator.comparing(TestdataObject::getCode);
        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(entitySelector,
                Arrays.asList(variableDescriptor), true,
                SubPillarConfigPolicy.sequentialUnlimited(lexicographicComparator));

        // nextInt pattern: pillarIndex, subPillarSize, subPillarStartingIndex
        // Expected pillar cache: [a], [b, d], [c, e, f]
        Random workingRandom = new TestRandom(
                1, 1, // [b, d]
                2, 2, // [c, e, f]
                2, 1, 1, // [c, e, f]
                0, 0 // [a]
        );

        SolverScope<TestdataSolution> solverScope = mockSolverScope();
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = PlannerTestUtils.delegatingPhaseScope(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = PlannerTestUtils.delegatingStepScope(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        assertCodesOfNeverEndingPillarSelector(pillarSelector, "[b, d]", "[c, e, f]", "[e, f]", "[a]");
        pillarSelector.stepEnded(stepScopeA1);

        pillarSelector.phaseEnded(phaseScopeA);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 1, 1);
    }

    @Test
    void emptyEntitySelectorRandomWithSubs() {
        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor());

        DefaultPillarSelector pillarSelector = new DefaultPillarSelector(
                entitySelector, Arrays.asList(variableDescriptor), true,
                SubPillarConfigPolicy.withSubpillarsUnlimited());

        SolverScope<TestdataSolution> solverScope = mockSolverScope();
        pillarSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA1);
        assertEmptyNeverEndingPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        pillarSelector.stepStarted(stepScopeA2);
        assertEmptyNeverEndingPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeA2);

        pillarSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        pillarSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        pillarSelector.stepStarted(stepScopeB1);
        assertEmptyNeverEndingPillarSelector(pillarSelector);
        pillarSelector.stepEnded(stepScopeB1);

        pillarSelector.phaseEnded(phaseScopeB);

        pillarSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(entitySelector, 1, 2, 3);
    }

    @Test
    void reusePillarCache() {
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");

        final TestdataEntity a = new TestdataEntity("a", val1);
        final TestdataEntity b = new TestdataEntity("b", val2);
        final TestdataEntity c = new TestdataEntity("c", val3);
        final TestdataEntity d = new TestdataEntity("d", val2);
        final TestdataEntity e = new TestdataEntity("e", val3);
        final TestdataEntity f = new TestdataEntity("f", val3);

        GenuineVariableDescriptor variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntitySelector entitySelector = SelectorTestUtils.mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                a, b, c, d, e, f);

        DefaultPillarSelector pillarSelector1 = new DefaultPillarSelector(entitySelector, List.of(variableDescriptor), false,
                SubPillarConfigPolicy.withoutSubpillars());
        DefaultPillarSelector pillarSelector2 = new DefaultPillarSelector(entitySelector, List.of(variableDescriptor), false,
                SubPillarConfigPolicy.withoutSubpillars());

        SolverScope<TestdataSolution> solverScope = mockSolverScope();
        pillarSelector1.solvingStarted(solverScope);
        pillarSelector2.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        pillarSelector1.phaseStarted(phaseScopeA);
        pillarSelector2.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);

        /*
         * We do not have a direct way of testing that the pillar cache is reused.
         * But we can check that the pillar supply is properly updated.
         *
         * It starts with active count 0, then first selector creates the supply and active count goes to 1.
         * Second selector increases the count to 2, but the supply stays the same.
         * Then after step end, the same process repeats in reverse, eventually reaching zero active count.
         */
        SupplyManager pillarSupplyManager = solverScope.getScoreDirector().getSupplyManager();
        Assertions.assertThat(pillarSupplyManager.getActiveCount(pillarSelector1.getPillarDemand())).isEqualTo(0);
        pillarSelector1.stepStarted(stepScopeA1);
        Assertions.assertThat(pillarSupplyManager.getActiveCount(pillarSelector1.getPillarDemand())).isEqualTo(1);

        Assertions.assertThat(pillarSupplyManager.getActiveCount(pillarSelector2.getPillarDemand())).isEqualTo(1);
        pillarSelector2.stepStarted(stepScopeA1);
        Assertions.assertThat(pillarSupplyManager.getActiveCount(pillarSelector2.getPillarDemand())).isEqualTo(2);

        Assertions.assertThat(pillarSupplyManager.getActiveCount(pillarSelector1.getPillarDemand())).isEqualTo(2);
        pillarSelector1.stepEnded(stepScopeA1);
        Assertions.assertThat(pillarSupplyManager.getActiveCount(pillarSelector1.getPillarDemand())).isEqualTo(1);

        Assertions.assertThat(pillarSupplyManager.getActiveCount(pillarSelector1.getPillarDemand())).isEqualTo(1);
        pillarSelector2.stepEnded(stepScopeA1);
        Assertions.assertThat(pillarSupplyManager.getActiveCount(pillarSelector1.getPillarDemand())).isEqualTo(0);

        pillarSelector1.phaseEnded(phaseScopeA);
        pillarSelector2.phaseEnded(phaseScopeA);

        pillarSelector1.solvingEnded(solverScope);
        pillarSelector2.solvingEnded(solverScope);
    }

}
