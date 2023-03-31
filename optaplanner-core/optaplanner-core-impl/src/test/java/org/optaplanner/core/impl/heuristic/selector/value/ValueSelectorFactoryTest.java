package org.optaplanner.core.impl.heuristic.selector.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.heuristic.selector.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.AssignedValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.FilteringValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ProbabilityValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ShufflingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.SortingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.UnassignedValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.ClassInstanceCache;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;

class ValueSelectorFactoryTest {

    @Test
    void phaseOriginal() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withCacheType(SelectionCacheType.PHASE)
                .withSelectionOrder(SelectionOrder.ORIGINAL);
        ValueSelector valueSelector = ValueSelectorFactory.create(valueSelectorConfig).buildValueSelector(configPolicy,
                entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector).isInstanceOf(FromSolutionPropertyValueSelector.class)
                .isNotInstanceOf(ShufflingValueSelector.class);
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    void stepOriginal() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withCacheType(SelectionCacheType.STEP)
                .withSelectionOrder(SelectionOrder.ORIGINAL);
        ValueSelector valueSelector = ValueSelectorFactory.create(valueSelectorConfig).buildValueSelector(configPolicy,
                entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector).isInstanceOf(FromSolutionPropertyValueSelector.class)
                .isNotInstanceOf(ShufflingValueSelector.class);
        // PHASE instead of STEP because these values are cacheable, so there's no reason not to cache them?
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    void justInTimeOriginal() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withCacheType(SelectionCacheType.JUST_IN_TIME)
                .withSelectionOrder(SelectionOrder.ORIGINAL);
        ValueSelector valueSelector = ValueSelectorFactory.create(valueSelectorConfig).buildValueSelector(configPolicy,
                entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector).isInstanceOf(FromSolutionPropertyValueSelector.class);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, valueSelector.getCacheType());
    }

    @Test
    void phaseRandom() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withCacheType(SelectionCacheType.PHASE)
                .withSelectionOrder(SelectionOrder.RANDOM);
        ValueSelector valueSelector = ValueSelectorFactory.create(valueSelectorConfig).buildValueSelector(configPolicy,
                entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector)
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        assertThat(valueSelector)
                .isNotInstanceOf(ShufflingValueSelector.class);
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    void stepRandom() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withCacheType(SelectionCacheType.STEP)
                .withSelectionOrder(SelectionOrder.RANDOM);
        ValueSelector valueSelector = ValueSelectorFactory.create(valueSelectorConfig).buildValueSelector(configPolicy,
                entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector).isInstanceOf(FromSolutionPropertyValueSelector.class)
                .isNotInstanceOf(ShufflingValueSelector.class);
        // PHASE instead of STEP because these values are cacheable, so there's no reason not to cache them?
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    void justInTimeRandom() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withCacheType(SelectionCacheType.JUST_IN_TIME)
                .withSelectionOrder(SelectionOrder.RANDOM);
        ValueSelector valueSelector = ValueSelectorFactory.create(valueSelectorConfig).buildValueSelector(configPolicy,
                entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector).isInstanceOf(FromSolutionPropertyValueSelector.class);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, valueSelector.getCacheType());
    }

    @Test
    void phaseShuffled() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withCacheType(SelectionCacheType.PHASE)
                .withSelectionOrder(SelectionOrder.SHUFFLED);
        ValueSelector valueSelector = ValueSelectorFactory.create(valueSelectorConfig).buildValueSelector(configPolicy,
                entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector).isInstanceOf(ShufflingValueSelector.class);
        assertThat(((ShufflingValueSelector) valueSelector).getChildValueSelector())
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    void stepShuffled() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor = configPolicy.getSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withCacheType(SelectionCacheType.STEP)
                .withSelectionOrder(SelectionOrder.SHUFFLED);
        ValueSelector valueSelector = ValueSelectorFactory.create(valueSelectorConfig).buildValueSelector(configPolicy,
                entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(valueSelector).isInstanceOf(ShufflingValueSelector.class);
        assertThat(((ShufflingValueSelector) valueSelector).getChildValueSelector())
                .isInstanceOf(FromSolutionPropertyValueSelector.class);
        assertThat(valueSelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
    }

    @Test
    void justInTimeShuffled() {
        HeuristicConfigPolicy configPolicy = buildHeuristicConfigPolicy();
        EntityDescriptor entityDescriptor =
                configPolicy.getSolutionDescriptor().findEntityDescriptorOrFail(TestdataEntity.class);
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withCacheType(SelectionCacheType.JUST_IN_TIME)
                .withSelectionOrder(SelectionOrder.SHUFFLED);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ValueSelectorFactory.create(valueSelectorConfig).buildValueSelector(configPolicy,
                        entityDescriptor, SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM));
    }

    @Test
    void applyFiltering_withFilterClass() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withFilterClass(DummyValueFilter.class);
        ValueSelector baseValueSelector =
                SelectorTestUtils.mockValueSelector(TestdataEntity.class, "value", new TestdataValue("v1"));
        ValueSelector resultingValueSelector =
                ValueSelectorFactory.create(valueSelectorConfig).applyFiltering(baseValueSelector, ClassInstanceCache.create());
        assertThat(resultingValueSelector).isExactlyInstanceOf(FilteringValueSelector.class);
    }

    @Test
    void applyProbability_withSelectionProbabilityWeightFactory() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withProbabilityWeightFactoryClass(DummySelectionProbabilityWeightFactory.class);
        ValueSelector baseValueSelector = mock(EntityIndependentValueSelector.class);
        ValueSelectorFactory valueSelectorFactory =
                ValueSelectorFactory.create(valueSelectorConfig);
        valueSelectorFactory.validateProbability(SelectionOrder.PROBABILISTIC);
        ValueSelector resultingValueSelector = valueSelectorFactory.applyProbability(SelectionCacheType.PHASE,
                SelectionOrder.PROBABILISTIC, baseValueSelector, ClassInstanceCache.create());
        assertThat(resultingValueSelector).isExactlyInstanceOf(ProbabilityValueSelector.class);
    }

    @Test
    void applySorting_withSorterComparatorClass() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withSorterComparatorClass(DummyValueComparator.class);
        applySorting(valueSelectorConfig);
    }

    @Test
    void applySorting_withSorterWeightFactoryClass() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withSorterWeightFactoryClass(DummySelectionSorterWeightFactory.class);
        applySorting(valueSelectorConfig);
    }

    private void applySorting(ValueSelectorConfig valueSelectorConfig) {
        ValueSelectorFactory valueSelectorFactory =
                ValueSelectorFactory.create(valueSelectorConfig);
        valueSelectorFactory.validateSorting(SelectionOrder.SORTED);

        ValueSelector baseValueSelector = mock(EntityIndependentValueSelector.class);
        GenuineVariableDescriptor variableDescriptor = mock(GenuineVariableDescriptor.class);
        when(baseValueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        when(variableDescriptor.isValueRangeEntityIndependent()).thenReturn(true);

        ValueSelector resultingValueSelector =
                valueSelectorFactory.applySorting(SelectionCacheType.PHASE, SelectionOrder.SORTED, baseValueSelector,
                        ClassInstanceCache.create());
        assertThat(resultingValueSelector).isExactlyInstanceOf(SortingValueSelector.class);
    }

    @Test
    void applySortingFailsFast_withoutAnySorter() {
        ValueSelectorFactory valueSelectorFactory = ValueSelectorFactory.create(new ValueSelectorConfig());
        ValueSelector baseValueSelector = mock(ValueSelector.class);
        assertThatIllegalArgumentException().isThrownBy(
                () -> valueSelectorFactory.applySorting(SelectionCacheType.PHASE, SelectionOrder.SORTED, baseValueSelector,
                        ClassInstanceCache.create()))
                .withMessageContaining("needs a sorterManner");
    }

    @Test
    void failFast_ifMimicRecordingIsUsedWithOtherProperty() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withSelectedCountLimit(10L)
                .withMimicSelectorRef("someSelectorId");

        assertThatIllegalArgumentException().isThrownBy(
                () -> ValueSelectorFactory.create(valueSelectorConfig)
                        .buildMimicReplaying(mock(HeuristicConfigPolicy.class)))
                .withMessageContaining("has another property");
    }

    @Test
    void failFast_ifNearbyDoesNotHaveOriginEntityOrValueSelector() {
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig()
                .withNearbySelectionConfig(new NearbySelectionConfig()
                        .withOriginSubListSelectorConfig(new SubListSelectorConfig().withMimicSelectorRef("x"))
                        .withNearbyDistanceMeterClass(mock(NearbyDistanceMeter.class).getClass()));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> ValueSelectorFactory.<TestdataListSolution> create(valueSelectorConfig)
                        .buildValueSelector(buildHeuristicConfigPolicy(TestdataListSolution.buildSolutionDescriptor()),
                                TestdataListEntity.buildEntityDescriptor(),
                                SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM))
                .withMessageContaining("requires an originEntitySelector or an originValueSelector");
    }

    static Stream<Arguments> applyListValueFiltering() {
        return Stream.of(
                arguments(true, ValueSelectorFactory.ListValueFilteringType.ACCEPT_ASSIGNED, AssignedValueSelector.class),
                arguments(true, ValueSelectorFactory.ListValueFilteringType.ACCEPT_UNASSIGNED, UnassignedValueSelector.class),
                arguments(true, ValueSelectorFactory.ListValueFilteringType.NONE,
                        DummyEntityIndependentValueSelector.class),
                arguments(false, ValueSelectorFactory.ListValueFilteringType.ACCEPT_ASSIGNED,
                        DummyEntityIndependentValueSelector.class),
                arguments(false, ValueSelectorFactory.ListValueFilteringType.ACCEPT_UNASSIGNED,
                        DummyEntityIndependentValueSelector.class),
                arguments(false, ValueSelectorFactory.ListValueFilteringType.NONE,
                        DummyEntityIndependentValueSelector.class));
    }

    @ParameterizedTest
    @MethodSource
    void applyListValueFiltering(
            boolean isUnassignedValuesAllowed,
            ValueSelectorFactory.ListValueFilteringType listValueFilteringType,
            Class<? extends ValueSelector> expectedValueSelectorClass) {
        HeuristicConfigPolicy<TestdataListSolution> configPolicy = mock(HeuristicConfigPolicy.class);
        when(configPolicy.isUnassignedValuesAllowed()).thenReturn(isUnassignedValuesAllowed);

        DummyEntityIndependentValueSelector baseValueSelector = new DummyEntityIndependentValueSelector();
        GenuineVariableDescriptor<TestdataListSolution> variableDescriptor = baseValueSelector.getVariableDescriptor();

        ValueSelector<TestdataListSolution> valueSelector =
                ValueSelectorFactory.<TestdataListSolution> create(new ValueSelectorConfig())
                        .applyListValueFiltering(configPolicy, listValueFilteringType, variableDescriptor, baseValueSelector);

        assertThat(valueSelector).isExactlyInstanceOf(expectedValueSelectorClass);
    }

    public static class DummyValueFilter implements SelectionFilter<TestdataSolution, TestdataValue> {
        @Override
        public boolean accept(ScoreDirector<TestdataSolution> scoreDirector, TestdataValue selection) {
            return true;
        }
    }

    public static class DummySelectionProbabilityWeightFactory
            implements SelectionProbabilityWeightFactory<TestdataSolution, TestdataValue> {

        @Override
        public double createProbabilityWeight(ScoreDirector<TestdataSolution> scoreDirector, TestdataValue selection) {
            return 0.0;
        }
    }

    public static class DummySelectionSorterWeightFactory
            implements SelectionSorterWeightFactory<TestdataSolution, TestdataValue> {
        @Override
        public Comparable createSorterWeight(TestdataSolution testdataSolution, TestdataValue selection) {
            return 0;
        }
    }

    public static class DummyValueComparator implements Comparator<TestdataValue> {
        @Override
        public int compare(TestdataValue testdataValue, TestdataValue testdataValue2) {
            return 0;
        }
    }

    private static class DummyEntityIndependentValueSelector implements EntityIndependentValueSelector<TestdataListSolution> {

        @Override
        public long getSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<Object> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCountable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNeverEnding() {
            return false;
        }

        @Override
        public SelectionCacheType getCacheType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public GenuineVariableDescriptor<TestdataListSolution> getVariableDescriptor() {
            return TestdataListEntity.buildVariableDescriptorForValueList();
        }

        @Override
        public long getSize(Object entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<Object> iterator(Object entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<Object> endingIterator(Object entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void phaseStarted(AbstractPhaseScope<TestdataListSolution> phaseScope) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void stepStarted(AbstractStepScope<TestdataListSolution> stepScope) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void stepEnded(AbstractStepScope<TestdataListSolution> stepScope) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void phaseEnded(AbstractPhaseScope<TestdataListSolution> phaseScope) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void solvingStarted(SolverScope<TestdataListSolution> solverScope) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void solvingEnded(SolverScope<TestdataListSolution> solverScope) {
            throw new UnsupportedOperationException();
        }
    }
}
