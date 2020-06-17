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

package org.optaplanner.core.config.score.director;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.score.definition.ScoreDefinitionType;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirector;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public class ScoreDirectorFactoryConfigTest {

    @Test
    public void buildSimpleScoreDefinition() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        config.setScoreDefinitionType(ScoreDefinitionType.SIMPLE);
        ScoreDefinition scoreDefinition = config.buildDeprecatedScoreDefinition();
        assertThat(scoreDefinition)
                .isInstanceOf(SimpleScoreDefinition.class);
    }

    @Test
    public void buildBendableScoreDefinition() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        config.setScoreDefinitionType(ScoreDefinitionType.BENDABLE);
        config.setBendableHardLevelsSize(2);
        config.setBendableSoftLevelsSize(3);
        BendableScoreDefinition scoreDefinition = (BendableScoreDefinition) config.buildDeprecatedScoreDefinition();
        assertThat(scoreDefinition.getHardLevelsSize()).isEqualTo(2);
        assertThat(scoreDefinition.getSoftLevelsSize()).isEqualTo(3);
    }

    @Test
    public void easyScoreCalculatorWithCustomProperties() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        config.setEasyScoreCalculatorClass(TestCustomPropertiesEasyScoreCalculator.class);
        HashMap<String, String> customProperties = new HashMap<>();
        customProperties.put("stringProperty", "string 1");
        customProperties.put("intProperty", "7");
        config.setEasyScoreCalculatorCustomProperties(customProperties);
        EasyScoreDirector<TestdataSolution> scoreDirector = (EasyScoreDirector<TestdataSolution>) config
                .buildScoreDirectorFactory(new SolverConfigContext(), getClass().getClassLoader(),
                        EnvironmentMode.REPRODUCIBLE, TestdataSolution.buildSolutionDescriptor())
                .buildScoreDirector();
        TestCustomPropertiesEasyScoreCalculator scoreCalculator = (TestCustomPropertiesEasyScoreCalculator) scoreDirector
                .getEasyScoreCalculator();
        assertThat(scoreCalculator.getStringProperty()).isEqualTo("string 1");
        assertThat(scoreCalculator.getIntProperty()).isEqualTo(7);
    }

    public static class TestCustomPropertiesEasyScoreCalculator implements EasyScoreCalculator<TestdataSolution> {

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

    @Test
    public void incrementalScoreCalculatorWithCustomProperties() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        config.setIncrementalScoreCalculatorClass(TestCustomPropertiesIncrementalScoreCalculator.class);
        HashMap<String, String> customProperties = new HashMap<>();
        customProperties.put("stringProperty", "string 1");
        customProperties.put("intProperty", "7");
        config.setIncrementalScoreCalculatorCustomProperties(customProperties);
        IncrementalScoreDirector<TestdataSolution> scoreDirector = (IncrementalScoreDirector<TestdataSolution>) config
                .buildScoreDirectorFactory(new SolverConfigContext(), getClass().getClassLoader(),
                        EnvironmentMode.REPRODUCIBLE, TestdataSolution.buildSolutionDescriptor())
                .buildScoreDirector();
        TestCustomPropertiesIncrementalScoreCalculator scoreCalculator =
                (TestCustomPropertiesIncrementalScoreCalculator) scoreDirector.getIncrementalScoreCalculator();
        assertThat(scoreCalculator.getStringProperty()).isEqualTo("string 1");
        assertThat(scoreCalculator.getIntProperty()).isEqualTo(7);
    }

    public static class TestCustomPropertiesIncrementalScoreCalculator implements IncrementalScoreCalculator<TestdataSolution> {

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

    @Test
    public void testGenerateDroolsTestOption() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        assertThat(config.isGenerateDroolsTestOnError()).isNull();
        config.setGenerateDroolsTestOnError(true);
        assertThat(config.isGenerateDroolsTestOnError()).isTrue();
        config.setGenerateDroolsTestOnError(Boolean.FALSE);
        assertThat(config.isGenerateDroolsTestOnError()).isFalse();
        config.setGenerateDroolsTestOnError(null);
        assertThat(config.isGenerateDroolsTestOnError()).isNull();
    }

}
