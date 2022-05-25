/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class ScoreDirectorFactoryFactoryTest {

    @Test
    void constraintStreamsDroolsWithAlphaNetworkCompilationEnabledNoDrools_throws() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.BAVET)
                .withDroolsAlphaNetworkCompilationEnabled(true);
        ScoreDirectorFactoryFactory<TestdataSolution, SimpleScore> factoryFactory = new ScoreDirectorFactoryFactory<>(config);
        assertThatCode(() -> factoryFactory.buildScoreDirectorFactory(getClass().getClassLoader(),
                EnvironmentMode.FAST_ASSERT,
                TestdataSolution.buildSolutionDescriptor()))
                .hasMessage("If there is no scoreDrl (null), scoreDrlFile (null) or constraintProviderClass "
                        + "(class org.optaplanner.constraint.streams.bavet.ScoreDirectorFactoryFactoryTest$TestdataConstraintProvider)"
                        + " with DROOLS impl type (BAVET), there can be no droolsAlphaNetworkCompilationEnabled (true) either.");
    }

    @Test
    void constraintStreamsKieBaseSupplierNoDrools_throws() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.BAVET)
                .withGizmoKieBaseSupplier(() -> null);
        ScoreDirectorFactoryFactory<TestdataSolution, SimpleScore> factoryFactory = new ScoreDirectorFactoryFactory<>(config);
        assertThatCode(() -> factoryFactory.buildScoreDirectorFactory(getClass().getClassLoader(),
                EnvironmentMode.FAST_ASSERT,
                TestdataSolution.buildSolutionDescriptor()))
                .hasMessageContaining("If there is no constraintProviderClass "
                        + "(class org.optaplanner.constraint.streams.bavet.ScoreDirectorFactoryFactoryTest$TestdataConstraintProvider)"
                        + " with DROOLS impl type (BAVET), there can be no gizmoKieBaseSupplier ");
    }

    @Test
    void constraintStreamsBavet() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        AbstractConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory =
                (AbstractConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore>) new BavetConstraintStreamScoreDirectorFactoryService<TestdataSolution, SimpleScore>()
                        .buildScoreDirectorFactory(null, TestdataSolution.buildSolutionDescriptor(), config)
                        .get();
        assertThat(scoreDirectorFactory).isInstanceOf(BavetConstraintStreamScoreDirectorFactory.class);
    }

    public static class TestdataConstraintProvider implements ConstraintProvider {
        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
            return new Constraint[0];
        }
    }
}
