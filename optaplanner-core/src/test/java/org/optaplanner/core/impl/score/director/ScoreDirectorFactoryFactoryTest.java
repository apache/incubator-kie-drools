/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.director;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirector;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreDirector;
import org.optaplanner.core.impl.score.director.stream.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.stream.BavetConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.stream.DroolsConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class ScoreDirectorFactoryFactoryTest {

    @Test
    void easyScoreCalculatorWithCustomProperties() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        config.setEasyScoreCalculatorClass(ScoreDirectorFactoryFactoryTest.TestCustomPropertiesEasyScoreCalculator.class);
        HashMap<String, String> customProperties = new HashMap<>();
        customProperties.put("stringProperty", "string 1");
        customProperties.put("intProperty", "7");
        config.setEasyScoreCalculatorCustomProperties(customProperties);

        EasyScoreDirector<TestdataSolution, ?> scoreDirector =
                (EasyScoreDirector<TestdataSolution, ?>) buildTestdataScoreDirectoryFactory(config).buildScoreDirector();
        ScoreDirectorFactoryFactoryTest.TestCustomPropertiesEasyScoreCalculator scoreCalculator =
                (ScoreDirectorFactoryFactoryTest.TestCustomPropertiesEasyScoreCalculator) scoreDirector
                        .getEasyScoreCalculator();
        assertThat(scoreCalculator.getStringProperty()).isEqualTo("string 1");
        assertThat(scoreCalculator.getIntProperty()).isEqualTo(7);
    }

    @Test
    void incrementalScoreCalculatorWithCustomProperties() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        config.setIncrementalScoreCalculatorClass(
                ScoreDirectorFactoryFactoryTest.TestCustomPropertiesIncrementalScoreCalculator.class);
        HashMap<String, String> customProperties = new HashMap<>();
        customProperties.put("stringProperty", "string 1");
        customProperties.put("intProperty", "7");
        config.setIncrementalScoreCalculatorCustomProperties(customProperties);

        ScoreDirectorFactory<TestdataSolution> scoreDirectorFactory = buildTestdataScoreDirectoryFactory(config);
        IncrementalScoreDirector<TestdataSolution, ?> scoreDirector =
                (IncrementalScoreDirector<TestdataSolution, ?>) scoreDirectorFactory.buildScoreDirector();
        ScoreDirectorFactoryFactoryTest.TestCustomPropertiesIncrementalScoreCalculator scoreCalculator =
                (ScoreDirectorFactoryFactoryTest.TestCustomPropertiesIncrementalScoreCalculator) scoreDirector
                        .getIncrementalScoreCalculator();
        assertThat(scoreCalculator.getStringProperty()).isEqualTo("string 1");
        assertThat(scoreCalculator.getIntProperty()).isEqualTo(7);
    }

    @Test
    void buildWithAssertionScoreDirectorFactory() {
        ScoreDirectorFactoryConfig assertionScoreDirectorConfig = new ScoreDirectorFactoryConfig()
                .withIncrementalScoreCalculatorClass(TestCustomPropertiesIncrementalScoreCalculator.class);
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withAssertionScoreDirectorFactory(assertionScoreDirectorConfig);

        AbstractScoreDirectorFactory<TestdataSolution, ?> scoreDirectorFactory =
                (AbstractScoreDirectorFactory<TestdataSolution, ?>) buildTestdataScoreDirectoryFactory(config,
                        EnvironmentMode.FAST_ASSERT);

        ScoreDirectorFactory<TestdataSolution> assertionScoreDirectorFactory =
                scoreDirectorFactory.getAssertionScoreDirectorFactory();
        IncrementalScoreDirector<TestdataSolution, ?> assertionScoreDirector =
                (IncrementalScoreDirector<TestdataSolution, ?>) assertionScoreDirectorFactory.buildScoreDirector();
        IncrementalScoreCalculator<TestdataSolution, ?> assertionScoreCalculator =
                assertionScoreDirector.getIncrementalScoreCalculator();

        assertThat(assertionScoreCalculator)
                .isNotNull()
                .isExactlyInstanceOf(TestCustomPropertiesIncrementalScoreCalculator.class);
    }

    @Test
    void multipleScoreCalculations_throwsException() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withEasyScoreCalculatorClass(TestCustomPropertiesEasyScoreCalculator.class)
                .withIncrementalScoreCalculatorClass(TestCustomPropertiesIncrementalScoreCalculator.class)
                .withScoreDrls(getClass().getPackage().getName().replace('.', '/') + "/dummySimpleScoreDroolsConstraints.drl");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> buildTestdataScoreDirectoryFactory(config))
                .withMessageContaining("scoreDirectorFactory")
                .withMessageContaining("together");
    }

    @Test
    void constraintStreamsDroolsWithAlphaNetworkCompilationEnabled() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withDroolsAlphaNetworkCompilationEnabled(true);
        ScoreDirectorFactoryFactory<TestdataSolution, SimpleScore> factoryFactory = new ScoreDirectorFactoryFactory<>(config);
        AbstractConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore> uncastScoreDirectorFactory =
                factoryFactory.buildConstraintStreamScoreDirectorFactory(TestdataSolution.buildSolutionDescriptor());
        assertThat(uncastScoreDirectorFactory).isInstanceOf(DroolsConstraintStreamScoreDirectorFactory.class);
        DroolsConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory =
                (DroolsConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore>) uncastScoreDirectorFactory;
        assertThat(scoreDirectorFactory.isDroolsAlphaNetworkCompilationEnabled()).isTrue();
    }

    @Test
    void constraintStreamsDroolsWithAlphaNetworkCompilationEnabledNoDrools_throws() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.BAVET)
                .withDroolsAlphaNetworkCompilationEnabled(true);
        ScoreDirectorFactoryFactory<TestdataSolution, SimpleScore> factoryFactory = new ScoreDirectorFactoryFactory<>(config);
        assertThatCode(() -> factoryFactory.buildScoreDirectorFactory(ScoreDirectorFactoryFactoryTest.class.getClassLoader(),
                EnvironmentMode.FAST_ASSERT,
                TestdataSolution.buildSolutionDescriptor()))
                        .hasMessage("If there is no scoreDrl (null), scoreDrlFile (null) or constraintProviderClass " +
                                "(class org.optaplanner.core.impl.score.director.ScoreDirectorFactoryFactoryTest$TestdataConstraintProvider)"
                                +
                                " with DROOLS impl type (BAVET), there can be no droolsAlphaNetworkCompilationEnabled (true) either.");
    }

    @Test
    void constraintStreamsKieBaseSupplierNoDrools_throws() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.BAVET)
                .withGizmoKieBaseSupplier(() -> null);
        ScoreDirectorFactoryFactory<TestdataSolution, SimpleScore> factoryFactory = new ScoreDirectorFactoryFactory<>(config);
        assertThatCode(() -> factoryFactory.buildScoreDirectorFactory(ScoreDirectorFactoryFactoryTest.class.getClassLoader(),
                EnvironmentMode.FAST_ASSERT,
                TestdataSolution.buildSolutionDescriptor()))
                        .hasMessageContaining("If there is no scoreDrl (null), scoreDrlFile (null) or constraintProviderClass "
                                +
                                "(class org.optaplanner.core.impl.score.director.ScoreDirectorFactoryFactoryTest$TestdataConstraintProvider)"
                                +
                                " with DROOLS impl type (BAVET), there can be no gizmoKieBaseSupplier ");
    }

    @Test
    void constraintStreamsKieBaseSupplierNotKieBaseDescriptor_throws() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withGizmoKieBaseSupplier(() -> null);
        ScoreDirectorFactoryFactory<TestdataSolution, SimpleScore> factoryFactory = new ScoreDirectorFactoryFactory<>(config);
        assertThatCode(() -> factoryFactory.buildScoreDirectorFactory(ScoreDirectorFactoryFactoryTest.class.getClassLoader(),
                EnvironmentMode.FAST_ASSERT,
                TestdataSolution.buildSolutionDescriptor()))
                        .hasMessageContainingAll("The kieBaseSupplier (",
                                ") is not a KieBaseDescriptor. Maybe remove calls to ScoreDirectorFactoryConfig.setKieBaseSupplier(Supplier)?");
    }

    @Test
    void constraintStreamsDroolsWithAlphaNetworkCompilationDisabled() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withDroolsAlphaNetworkCompilationEnabled(false);
        ScoreDirectorFactoryFactory<TestdataSolution, SimpleScore> factoryFactory = new ScoreDirectorFactoryFactory<>(config);
        AbstractConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore> uncastScoreDirectorFactory =
                factoryFactory.buildConstraintStreamScoreDirectorFactory(TestdataSolution.buildSolutionDescriptor());
        assertThat(uncastScoreDirectorFactory).isInstanceOf(DroolsConstraintStreamScoreDirectorFactory.class);
        DroolsConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory =
                (DroolsConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore>) uncastScoreDirectorFactory;
        assertThat(scoreDirectorFactory.isDroolsAlphaNetworkCompilationEnabled()).isFalse();
    }

    @Test
    void constraintStreamsBavet() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        ScoreDirectorFactoryFactory<TestdataSolution, SimpleScore> factoryFactory = new ScoreDirectorFactoryFactory<>(config);
        AbstractConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory =
                factoryFactory.buildConstraintStreamScoreDirectorFactory(TestdataSolution.buildSolutionDescriptor());
        assertThat(scoreDirectorFactory).isInstanceOf(BavetConstraintStreamScoreDirectorFactory.class);
    }

    @Test
    void invalidDrlResource_throwsException() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withScoreDrls("org/optaplanner/core/impl/score/director/invalidDroolsConstraints.drl");
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> buildTestdataScoreDirectoryFactory(config))
                .withMessageContaining("scoreDrl")
                .withRootCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void nonExistingDrlResource_throwsException() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withScoreDrls("nonExisting.drl");
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> buildTestdataScoreDirectoryFactory(config))
                .withMessageContaining("scoreDrl");
    }

    @Test
    void nonExistingDrlFile_throwsException() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withScoreDrlFiles(new File("nonExisting.drl"));
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> buildTestdataScoreDirectoryFactory(config))
                .withMessageContaining("scoreDrl");
    }

    private <Score_ extends Score<Score_>> ScoreDirectorFactory<TestdataSolution> buildTestdataScoreDirectoryFactory(
            ScoreDirectorFactoryConfig config, EnvironmentMode environmentMode) {
        return new ScoreDirectorFactoryFactory<TestdataSolution, Score_>(config)
                .buildScoreDirectorFactory(getClass().getClassLoader(), environmentMode,
                        TestdataSolution.buildSolutionDescriptor());
    }

    private ScoreDirectorFactory<TestdataSolution> buildTestdataScoreDirectoryFactory(ScoreDirectorFactoryConfig config) {
        return buildTestdataScoreDirectoryFactory(config, EnvironmentMode.REPRODUCIBLE);
    }

    public static class TestCustomPropertiesEasyScoreCalculator
            implements EasyScoreCalculator<TestdataSolution, SimpleScore> {

        private String stringProperty;
        private int intProperty;

        public String getStringProperty() {
            return stringProperty;
        }

        @SuppressWarnings("unused")
        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        @SuppressWarnings("unused")
        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        @Override
        public SimpleScore calculateScore(TestdataSolution testdataSolution) {
            return SimpleScore.ZERO;
        }
    }

    public static class TestCustomPropertiesIncrementalScoreCalculator
            implements IncrementalScoreCalculator<TestdataSolution, SimpleScore> {

        private String stringProperty;
        private int intProperty;

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        @Override
        public void resetWorkingSolution(TestdataSolution workingSolution) {
        }

        @Override
        public void beforeEntityAdded(Object entity) {
        }

        @Override
        public void afterEntityAdded(Object entity) {
        }

        @Override
        public void beforeVariableChanged(Object entity, String variableName) {
        }

        @Override
        public void afterVariableChanged(Object entity, String variableName) {
        }

        @Override
        public void beforeEntityRemoved(Object entity) {
        }

        @Override
        public void afterEntityRemoved(Object entity) {
        }

        @Override
        public SimpleScore calculateScore() {
            return SimpleScore.ZERO;
        }
    }

    public static class TestdataConstraintProvider implements ConstraintProvider {
        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
            return new Constraint[0];
        }
    }
}
