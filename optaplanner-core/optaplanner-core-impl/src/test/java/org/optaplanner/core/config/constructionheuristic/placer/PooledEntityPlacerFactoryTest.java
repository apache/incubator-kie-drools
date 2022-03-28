/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.constructionheuristic.placer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.constructionheuristic.placer.PooledEntityPlacerFactory;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class PooledEntityPlacerFactoryTest {

    @Test
    void unfoldNew() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();

        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig();
        moveSelectorConfig.setValueSelectorConfig(new ValueSelectorConfig("value"));

        HeuristicConfigPolicy<TestdataSolution> configPolicy = buildHeuristicConfigPolicy(solutionDescriptor);
        PooledEntityPlacerConfig placerConfig = PooledEntityPlacerFactory.unfoldNew(configPolicy, moveSelectorConfig);

        assertThat(placerConfig.getMoveSelectorConfig()).isExactlyInstanceOf(ChangeMoveSelectorConfig.class);

        ChangeMoveSelectorConfig changeMoveSelectorConfig =
                (ChangeMoveSelectorConfig) placerConfig.getMoveSelectorConfig();
        assertThat(changeMoveSelectorConfig.getEntitySelectorConfig().getEntityClass()).isNull();
        assertThat(changeMoveSelectorConfig.getEntitySelectorConfig().getMimicSelectorRef())
                .isEqualTo(TestdataEntity.class.getName());
        assertThat(changeMoveSelectorConfig.getValueSelectorConfig().getVariableName()).isEqualTo("value");
    }

    public HeuristicConfigPolicy<TestdataSolution>
            buildHeuristicConfigPolicy(SolutionDescriptor<TestdataSolution> solutionDescriptor) {
        InnerScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory = mock(InnerScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        return new HeuristicConfigPolicy.Builder<>(EnvironmentMode.REPRODUCIBLE, null, null, null, scoreDirectorFactory)
                .build();
    }
}
