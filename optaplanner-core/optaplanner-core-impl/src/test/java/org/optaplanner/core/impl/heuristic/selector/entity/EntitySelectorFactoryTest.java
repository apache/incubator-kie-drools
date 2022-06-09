package org.optaplanner.core.impl.heuristic.selector.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelectorFactoryTest;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.ProbabilityEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.ShufflingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.SortingEntitySelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class EntitySelectorFactoryTest extends AbstractSelectorFactoryTest {

    @Test
    void phaseOriginal() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        EntitySelector<TestdataSolution> entitySelector =
                EntitySelectorFactory.<TestdataSolution> create(entitySelectorConfig)
                        .buildEntitySelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector)
                .isNotInstanceOf(ShufflingEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    void stepOriginal() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        EntitySelector<TestdataSolution> entitySelector =
                EntitySelectorFactory.<TestdataSolution> create(entitySelectorConfig)
                        .buildEntitySelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector)
                .isNotInstanceOf(ShufflingEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
    }

    @Test
    void justInTimeOriginal() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.ORIGINAL);
        EntitySelector<TestdataSolution> entitySelector =
                EntitySelectorFactory.<TestdataSolution> create(entitySelectorConfig)
                        .buildEntitySelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, entitySelector.getCacheType());
    }

    @Test
    void phaseRandom() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        EntitySelector<TestdataSolution> entitySelector =
                EntitySelectorFactory.<TestdataSolution> create(entitySelectorConfig)
                        .buildEntitySelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector)
                .isNotInstanceOf(ShufflingEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    void stepRandom() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        EntitySelector<TestdataSolution> entitySelector =
                EntitySelectorFactory.<TestdataSolution> create(entitySelectorConfig)
                        .buildEntitySelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector)
                .isNotInstanceOf(ShufflingEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
    }

    @Test
    void justInTimeRandom() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.RANDOM);
        EntitySelector<TestdataSolution> entitySelector =
                EntitySelectorFactory.<TestdataSolution> create(entitySelectorConfig)
                        .buildEntitySelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(FromSolutionEntitySelector.class);
        // cacheType gets upgraded to STEP
        // assertEquals(SelectionCacheType.JUST_IN_TIME, entitySelector.getCacheType());
    }

    @Test
    void phaseShuffled() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        EntitySelector<TestdataSolution> entitySelector =
                EntitySelectorFactory.<TestdataSolution> create(entitySelectorConfig)
                        .buildEntitySelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(ShufflingEntitySelector.class);
        assertThat(((ShufflingEntitySelector<TestdataSolution>) entitySelector).getChildEntitySelector())
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.PHASE);
    }

    @Test
    void stepShuffled() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.STEP);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        EntitySelector<TestdataSolution> entitySelector =
                EntitySelectorFactory.<TestdataSolution> create(entitySelectorConfig)
                        .buildEntitySelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM);
        assertThat(entitySelector)
                .isInstanceOf(ShufflingEntitySelector.class);
        assertThat(((ShufflingEntitySelector<TestdataSolution>) entitySelector).getChildEntitySelector())
                .isInstanceOf(FromSolutionEntitySelector.class);
        assertThat(entitySelector.getCacheType()).isEqualTo(SelectionCacheType.STEP);
    }

    @Test
    void justInTimeShuffled() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setCacheType(SelectionCacheType.JUST_IN_TIME);
        entitySelectorConfig.setSelectionOrder(SelectionOrder.SHUFFLED);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> EntitySelectorFactory.<TestdataSolution> create(entitySelectorConfig)
                        .buildEntitySelector(buildHeuristicConfigPolicy(), SelectionCacheType.JUST_IN_TIME,
                                SelectionOrder.RANDOM));
    }

    @Test
    void applySorting_withSorterComparatorClass() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setSorterComparatorClass(DummyEntityComparator.class);
        applySorting(entitySelectorConfig);
    }

    @Test
    void applySorting_withSorterWeightFactoryClass() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setSorterWeightFactoryClass(DummySelectionSorterWeightFactory.class);
        applySorting(entitySelectorConfig);
    }

    private void applySorting(EntitySelectorConfig entitySelectorConfig) {
        EntitySelectorFactory<TestdataSolution> entitySelectorFactory = EntitySelectorFactory.create(entitySelectorConfig);
        entitySelectorFactory.validateSorting(SelectionOrder.SORTED);

        EntitySelector<TestdataSolution> baseEntitySelector = mock(EntitySelector.class);
        EntitySelector<TestdataSolution> resultingEntitySelector =
                entitySelectorFactory.applySorting(SelectionCacheType.PHASE, SelectionOrder.SORTED, baseEntitySelector);
        assertThat(resultingEntitySelector).isExactlyInstanceOf(SortingEntitySelector.class);
    }

    @Test
    void applyProbability_withProbabilityWeightFactoryClass() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setProbabilityWeightFactoryClass(DummySelectionProbabilityWeightFactory.class);

        EntitySelector<TestdataSolution> baseValueSelector =
                SelectorTestUtils.mockEntitySelector(TestdataEntity.class, new TestdataEntity("e1"));
        EntitySelectorFactory<TestdataSolution> entitySelectorFactory = EntitySelectorFactory.create(entitySelectorConfig);
        entitySelectorFactory.validateProbability(SelectionOrder.PROBABILISTIC);
        EntitySelector<TestdataSolution> resultingEntitySelector = entitySelectorFactory.applyProbability(
                SelectionCacheType.PHASE, SelectionOrder.PROBABILISTIC, baseValueSelector);
        assertThat(resultingEntitySelector).isExactlyInstanceOf(ProbabilityEntitySelector.class);
    }

    @Test
    void failFast_ifMimicRecordingIsUsedWithOtherProperty() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setSelectedCountLimit(10L);
        entitySelectorConfig.setMimicSelectorRef("someSelectorId");

        assertThatIllegalArgumentException().isThrownBy(
                () -> EntitySelectorFactory.create(entitySelectorConfig).buildMimicReplaying(mock(HeuristicConfigPolicy.class)))
                .withMessageContaining("has another property");
    }

    public static class DummySelectionProbabilityWeightFactory
            implements SelectionProbabilityWeightFactory<TestdataSolution, TestdataEntity> {

        @Override
        public double createProbabilityWeight(ScoreDirector<TestdataSolution> scoreDirector, TestdataEntity selection) {
            return 0.0;
        }
    }

    public static class DummySelectionSorterWeightFactory
            implements SelectionSorterWeightFactory<TestdataSolution, TestdataEntity> {
        @Override
        public Comparable createSorterWeight(TestdataSolution testdataSolution, TestdataEntity selection) {
            return 0;
        }
    }

    public static class DummyEntityComparator implements Comparator<TestdataEntity> {
        @Override
        public int compare(TestdataEntity testdataEntity, TestdataEntity testdataEntity2) {
            return 0;
        }
    }
}
