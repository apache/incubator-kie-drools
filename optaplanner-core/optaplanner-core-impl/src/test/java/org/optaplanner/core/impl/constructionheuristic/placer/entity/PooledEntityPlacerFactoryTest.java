package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.constructionheuristic.placer.PooledEntityPlacerConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.constructionheuristic.placer.PooledEntityPlacerFactory;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class PooledEntityPlacerFactoryTest {

    @Test
    void unfoldNew() {
        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig();
        moveSelectorConfig.setValueSelectorConfig(new ValueSelectorConfig("value"));

        HeuristicConfigPolicy<TestdataSolution> configPolicy = buildHeuristicConfigPolicy();
        PooledEntityPlacerConfig placerConfig = PooledEntityPlacerFactory.unfoldNew(configPolicy, moveSelectorConfig);

        assertThat(placerConfig.getMoveSelectorConfig()).isExactlyInstanceOf(ChangeMoveSelectorConfig.class);

        ChangeMoveSelectorConfig changeMoveSelectorConfig =
                (ChangeMoveSelectorConfig) placerConfig.getMoveSelectorConfig();
        assertThat(changeMoveSelectorConfig.getEntitySelectorConfig().getEntityClass()).isNull();
        assertThat(changeMoveSelectorConfig.getEntitySelectorConfig().getMimicSelectorRef())
                .isEqualTo(TestdataEntity.class.getName());
        assertThat(changeMoveSelectorConfig.getValueSelectorConfig().getVariableName()).isEqualTo("value");
    }
}
