package org.optaplanner.core.impl.heuristic.selector.value.nearby;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockEntityIndependentValueSelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockEntitySelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.mockReplayingEntitySelector;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.phaseStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.solvingStarted;
import static org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils.stepStarted;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfValueSelectorForEntity;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfNeverEndingValueSelectorForEntity;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockScoreDirector;

import java.util.Random;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.ManualEntityMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedObject;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testutil.TestNearbyRandom;
import org.optaplanner.core.impl.testutil.TestRandom;

class NearEntityNearbyValueSelectorTest {

    @Test
    void randomSelection() {
        final TestdataEntity africa = new TestdataEntity("Africa");
        final TestdataEntity europe = new TestdataEntity("Europe");
        final TestdataEntity oceania = new TestdataEntity("Oceania");
        final TestdataValue morocco = new TestdataValue("Morocco");
        final TestdataValue spain = new TestdataValue("Spain");
        final TestdataValue australia = new TestdataValue("Australia");
        final TestdataValue brazil = new TestdataValue("Brazil");

        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntityIndependentValueSelector<TestdataSolution> childValueSelector = mockEntityIndependentValueSelector(
                variableDescriptor,
                morocco, spain, australia, brazil);
        NearbyDistanceMeter<TestdataEntity, TestdataValue> meter = (origin, destination) -> {
            if (origin == africa) {
                if (destination == morocco) {
                    return 0.0;
                } else if (destination == spain) {
                    return 1.0;
                } else if (destination == australia) {
                    return 100.0;
                } else if (destination == brazil) {
                    return 50.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == europe) {
                if (destination == morocco) {
                    return 1.0;
                } else if (destination == spain) {
                    return 0.0;
                } else if (destination == australia) {
                    return 101.0;
                } else if (destination == brazil) {
                    return 51.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == oceania) {
                if (destination == morocco) {
                    return 100.0;
                } else if (destination == spain) {
                    return 101.0;
                } else if (destination == australia) {
                    return 0.0;
                } else if (destination == brazil) {
                    return 60.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else {
                throw new IllegalStateException("The origin (" + origin + ") is not implemented.");
            }
        };

        MimicReplayingEntitySelector<TestdataSolution> mimicReplayingEntitySelector =
                // The last entity (oceania) is not used, it just makes the selector appear never ending.
                mockReplayingEntitySelector(TestdataEntity.buildEntityDescriptor(), europe, europe, europe, europe, oceania);

        NearEntityNearbyValueSelector<TestdataSolution> valueSelector = new NearEntityNearbyValueSelector<>(
                childValueSelector, mimicReplayingEntitySelector, meter, new TestNearbyRandom(), true);

        TestRandom workingRandom = new TestRandom(3, 0, 2, 1);

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataSolution.buildSolutionDescriptor());
        SolverScope<TestdataSolution> solverScope = solvingStarted(valueSelector, scoreDirector, workingRandom);
        AbstractPhaseScope<TestdataSolution> phaseScopeA = phaseStarted(valueSelector, solverScope);
        AbstractStepScope<TestdataSolution> stepScopeA1 = stepStarted(valueSelector, phaseScopeA);
        assertCodesOfNeverEndingValueSelectorForEntity(valueSelector, europe, childValueSelector.getSize(europe),
                "Australia", "Spain", "Brazil", "Morocco");
        valueSelector.stepEnded(stepScopeA1);
        valueSelector.phaseEnded(phaseScopeA);
        valueSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childValueSelector, 1, 1, 1);
    }

    @Test
    void originalSelection() {
        final TestdataEntity africa = new TestdataEntity("Africa");
        final TestdataEntity europe = new TestdataEntity("Europe");
        final TestdataEntity oceania = new TestdataEntity("Oceania");
        final TestdataValue morocco = new TestdataValue("Morocco");
        final TestdataValue spain = new TestdataValue("Spain");
        final TestdataValue australia = new TestdataValue("Australia");
        final TestdataValue brazil = new TestdataValue("Brazil");

        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntityIndependentValueSelector<TestdataSolution> childValueSelector = mockEntityIndependentValueSelector(
                variableDescriptor,
                morocco, spain, australia, brazil);
        NearbyDistanceMeter<TestdataEntity, TestdataValue> meter = (origin, destination) -> {
            if (origin == africa) {
                if (destination == morocco) {
                    return 0.0;
                } else if (destination == spain) {
                    return 1.0;
                } else if (destination == australia) {
                    return 100.0;
                } else if (destination == brazil) {
                    return 50.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == europe) {
                if (destination == morocco) {
                    return 1.0;
                } else if (destination == spain) {
                    return 0.0;
                } else if (destination == australia) {
                    return 101.0;
                } else if (destination == brazil) {
                    return 51.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == oceania) {
                if (destination == morocco) {
                    return 100.0;
                } else if (destination == spain) {
                    return 101.0;
                } else if (destination == australia) {
                    return 0.0;
                } else if (destination == brazil) {
                    return 60.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else {
                throw new IllegalStateException("The origin (" + origin + ") is not implemented.");
            }
        };
        EntitySelector<TestdataSolution> entitySelector = mockEntitySelector(variableDescriptor.getEntityDescriptor(), africa,
                europe, oceania);
        ManualEntityMimicRecorder<TestdataSolution> entityMimicRecorder = new ManualEntityMimicRecorder<>(entitySelector);
        NearEntityNearbyValueSelector<TestdataSolution> valueSelector = new NearEntityNearbyValueSelector<>(
                childValueSelector, new MimicReplayingEntitySelector<>(entityMimicRecorder), meter, null, false);

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataSolution.buildSolutionDescriptor());
        SolverScope<TestdataSolution> solverScope = solvingStarted(valueSelector, scoreDirector);

        // The movingEntity can be the same (ChangeMove) or different (SwapMove) as the nearby source
        TestdataEntity movingEntity = europe;

        AbstractPhaseScope<TestdataSolution> phaseScopeA = phaseStarted(valueSelector, solverScope);

        AbstractStepScope<TestdataSolution> stepScopeA1 = stepStarted(valueSelector, phaseScopeA);
        entityMimicRecorder.setRecordedEntity(europe);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Spain", "Morocco", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeA1);

        AbstractStepScope<TestdataSolution> stepScopeA2 = stepStarted(valueSelector, phaseScopeA);
        entityMimicRecorder.setRecordedEntity(africa);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Morocco", "Spain", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeA2);

        valueSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope<TestdataSolution> phaseScopeB = phaseStarted(valueSelector, solverScope);

        AbstractStepScope<TestdataSolution> stepScopeB1 = stepStarted(valueSelector, phaseScopeB);
        entityMimicRecorder.setRecordedEntity(africa);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Morocco", "Spain", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeB1);

        AbstractStepScope<TestdataSolution> stepScopeB2 = stepStarted(valueSelector, phaseScopeB);
        entityMimicRecorder.setRecordedEntity(oceania);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Australia", "Brazil", "Morocco", "Spain");
        valueSelector.stepEnded(stepScopeB2);

        AbstractStepScope<TestdataSolution> stepScopeB3 = stepStarted(valueSelector, phaseScopeB);
        entityMimicRecorder.setRecordedEntity(europe);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Spain", "Morocco", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeB3);

        valueSelector.phaseEnded(phaseScopeB);

        valueSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childValueSelector, 1, 2, 5);
    }

    @Test
    void distanceMatrixNotReusedIfDifferentMaximums() {
        final TestdataEntity africa = new TestdataEntity("Africa");
        final TestdataEntity europe = new TestdataEntity("Europe");
        final TestdataEntity oceania = new TestdataEntity("Oceania");
        final TestdataValue morocco = new TestdataValue("Morocco");
        final TestdataValue spain = new TestdataValue("Spain");
        final TestdataValue australia = new TestdataValue("Australia");
        final TestdataValue brazil = new TestdataValue("Brazil");

        GenuineVariableDescriptor<TestdataSolution> variableDescriptor = TestdataEntity.buildVariableDescriptorForValue();
        EntityIndependentValueSelector<TestdataSolution> childValueSelector = mockEntityIndependentValueSelector(
                variableDescriptor,
                morocco, spain, australia, brazil);
        NearbyDistanceMeter<TestdataEntity, TestdataValue> meter = (origin, destination) -> {
            if (origin == africa) {
                if (destination == morocco) {
                    return 0.0;
                } else if (destination == spain) {
                    return 1.0;
                } else if (destination == australia) {
                    return 100.0;
                } else if (destination == brazil) {
                    return 50.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == europe) {
                if (destination == morocco) {
                    return 1.0;
                } else if (destination == spain) {
                    return 0.0;
                } else if (destination == australia) {
                    return 101.0;
                } else if (destination == brazil) {
                    return 51.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == oceania) {
                if (destination == morocco) {
                    return 100.0;
                } else if (destination == spain) {
                    return 101.0;
                } else if (destination == australia) {
                    return 0.0;
                } else if (destination == brazil) {
                    return 60.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else {
                throw new IllegalStateException("The origin (" + origin + ") is not implemented.");
            }
        };

        MimicReplayingEntitySelector<TestdataSolution> mimicReplayingEntitySelector =
                SelectorTestUtils.mockReplayingEntitySelector(TestdataEntity.buildEntityDescriptor(),
                        europe, europe, europe, europe);

        NearEntityNearbyValueSelector<TestdataSolution> valueSelector1 = new NearEntityNearbyValueSelector<>(
                childValueSelector, mimicReplayingEntitySelector, meter, TestNearbyRandom.withDistributionSizeMaximum(2), true);
        NearEntityNearbyValueSelector<TestdataSolution> valueSelector2 = new NearEntityNearbyValueSelector<>(
                childValueSelector, mimicReplayingEntitySelector, meter, TestNearbyRandom.withDistributionSizeMaximum(3), true);
        Assumptions.assumeFalse(valueSelector2.equals(valueSelector1)); // If equal, the matrix has to be reused.

        InnerScoreDirector<TestdataSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataSolution.buildSolutionDescriptor());
        Random workingRandom = new TestRandom(
                // valueSelector1 with distributionSizeMaximum=2
                0, 1, 1, 0,
                // valueSelector1 with distributionSizeMaximum=3
                0, 1, 2, 2);
        SupplyManager supplyManager = scoreDirector.getSupplyManager();
        // Both demands are equal, so the active count is always the same for both. This proves the distance matrix is shared.
        assertThat(supplyManager.getActiveCount(valueSelector1.getNearbyDistanceMatrixDemand())).isEqualTo(0);
        assertThat(supplyManager.getActiveCount(valueSelector2.getNearbyDistanceMatrixDemand())).isEqualTo(0);

        // solvingStarted() is first called on valueSelector1, which uses lower distributionSizeMaximum.
        // The resulting shared distance matrix only has 2 destinations for each origin.
        SolverScope<TestdataSolution> solverScope = solvingStarted(valueSelector1, scoreDirector, workingRandom);
        assertThat(supplyManager.getActiveCount(valueSelector1.getNearbyDistanceMatrixDemand())).isEqualTo(1);
        assertThat(supplyManager.getActiveCount(valueSelector2.getNearbyDistanceMatrixDemand())).isEqualTo(0);
        // solvingStarted() is called on valueSelector2, which uses a different distributionSizeMaximum.
        // The resulting shared distance matrix has 3 destinations for each origin.
        valueSelector2.solvingStarted(solverScope);
        assertThat(supplyManager.getActiveCount(valueSelector1.getNearbyDistanceMatrixDemand())).isEqualTo(1);
        assertThat(supplyManager.getActiveCount(valueSelector2.getNearbyDistanceMatrixDemand())).isEqualTo(1);

        AbstractPhaseScope<TestdataSolution> phaseScopeA = phaseStarted(valueSelector1, solverScope);
        valueSelector2.phaseStarted(phaseScopeA);

        AbstractStepScope<TestdataSolution> stepScopeA1 = stepStarted(valueSelector1, phaseScopeA);
        valueSelector2.stepStarted(stepScopeA1);

        assertCodesOfNeverEndingIterator(valueSelector1.iterator(europe), "Spain", "Morocco", "Morocco", "Spain");
        assertCodesOfNeverEndingIterator(valueSelector2.iterator(europe), "Spain", "Morocco", "Brazil", "Brazil");

        valueSelector1.stepEnded(stepScopeA1);
        valueSelector1.phaseEnded(phaseScopeA);
        valueSelector1.solvingEnded(solverScope);
    }

    @Test
    void originalSelectionChained() {
        final TestdataChainedEntity morocco = new TestdataChainedEntity("Morocco");
        final TestdataChainedEntity spain = new TestdataChainedEntity("Spain");
        final TestdataChainedEntity australia = new TestdataChainedEntity("Australia");
        final TestdataChainedAnchor brazil = new TestdataChainedAnchor("Brazil");

        GenuineVariableDescriptor<TestdataChainedSolution> variableDescriptor =
                TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        EntityIndependentValueSelector<TestdataChainedSolution> childValueSelector = mockEntityIndependentValueSelector(
                variableDescriptor,
                morocco, spain, australia, brazil);
        NearbyDistanceMeter<TestdataChainedEntity, TestdataChainedObject> meter = (origin, destination) -> {
            if (origin == morocco) {
                if (destination == morocco) {
                    return 0.0;
                } else if (destination == spain) {
                    return 1.0;
                } else if (destination == australia) {
                    return 100.0;
                } else if (destination == brazil) {
                    return 50.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == spain) {
                if (destination == morocco) {
                    return 1.0;
                } else if (destination == spain) {
                    return 0.0;
                } else if (destination == australia) {
                    return 101.0;
                } else if (destination == brazil) {
                    return 51.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else if (origin == australia) {
                if (destination == morocco) {
                    return 100.0;
                } else if (destination == spain) {
                    return 101.0;
                } else if (destination == australia) {
                    return 0.0;
                } else if (destination == brazil) {
                    return 60.0;
                } else {
                    throw new IllegalStateException("The destination (" + destination + ") is not implemented.");
                }
            } else {
                throw new IllegalStateException("The origin (" + origin + ") is not implemented.");
            }
        };
        EntitySelector<TestdataChainedSolution> entitySelector = mockEntitySelector(variableDescriptor.getEntityDescriptor(),
                morocco, spain, australia);
        ManualEntityMimicRecorder<TestdataChainedSolution> entityMimicRecorder =
                new ManualEntityMimicRecorder<>(entitySelector);
        NearEntityNearbyValueSelector<TestdataChainedSolution> valueSelector = new NearEntityNearbyValueSelector<>(
                childValueSelector, new MimicReplayingEntitySelector<>(entityMimicRecorder), meter, null, false);

        InnerScoreDirector<TestdataChainedSolution, SimpleScore> scoreDirector =
                mockScoreDirector(TestdataChainedSolution.buildSolutionDescriptor());
        SolverScope<TestdataChainedSolution> solverScope = solvingStarted(valueSelector, scoreDirector);

        // The movingEntity can be the same (ChangeMove) or different (SwapMove) as the nearby source
        TestdataChainedEntity movingEntity = spain;

        AbstractPhaseScope<TestdataChainedSolution> phaseScopeA = phaseStarted(valueSelector, solverScope);

        AbstractStepScope<TestdataChainedSolution> stepScopeA1 = stepStarted(valueSelector, phaseScopeA);
        entityMimicRecorder.setRecordedEntity(spain);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Morocco", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeA1);

        AbstractStepScope<TestdataChainedSolution> stepScopeA2 = stepStarted(valueSelector, phaseScopeA);
        entityMimicRecorder.setRecordedEntity(morocco);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Spain", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeA2);

        valueSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope<TestdataChainedSolution> phaseScopeB = phaseStarted(valueSelector, solverScope);

        AbstractStepScope<TestdataChainedSolution> stepScopeB1 = stepStarted(valueSelector, phaseScopeB);
        entityMimicRecorder.setRecordedEntity(morocco);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Spain", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeB1);

        AbstractStepScope<TestdataChainedSolution> stepScopeB2 = stepStarted(valueSelector, phaseScopeB);
        entityMimicRecorder.setRecordedEntity(australia);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Brazil", "Morocco", "Spain");
        valueSelector.stepEnded(stepScopeB2);

        AbstractStepScope<TestdataChainedSolution> stepScopeB3 = stepStarted(valueSelector, phaseScopeB);
        entityMimicRecorder.setRecordedEntity(spain);
        assertAllCodesOfValueSelectorForEntity(valueSelector, movingEntity, "Morocco", "Brazil", "Australia");
        valueSelector.stepEnded(stepScopeB3);

        valueSelector.phaseEnded(phaseScopeB);

        valueSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childValueSelector, 1, 2, 5);
    }

}
