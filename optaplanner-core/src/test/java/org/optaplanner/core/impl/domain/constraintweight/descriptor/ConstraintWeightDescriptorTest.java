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

package org.optaplanner.core.impl.domain.constraintweight.descriptor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfiguration;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfigurationSolution;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.extended.TestdataExtendedConstraintConfiguration;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.extended.TestdataExtendedConstraintConfigurationSolution;

public class ConstraintWeightDescriptorTest {

    @Test
    public void extractionFunction() {
        SolutionDescriptor<TestdataConstraintConfigurationSolution> solutionDescriptor = TestdataConstraintConfigurationSolution
                .buildSolutionDescriptor();
        ConstraintConfigurationDescriptor<TestdataConstraintConfigurationSolution> constraintConfigurationDescriptor =
                solutionDescriptor.getConstraintConfigurationDescriptor();

        ConstraintWeightDescriptor<TestdataConstraintConfigurationSolution> firstWeightDescriptor =
                constraintConfigurationDescriptor.getConstraintWeightDescriptor("firstWeight");
        assertThat(firstWeightDescriptor.getConstraintPackage())
                .isEqualTo(TestdataConstraintConfigurationSolution.class.getPackage().getName());
        assertThat(firstWeightDescriptor.getConstraintName()).isEqualTo("First weight");

        ConstraintWeightDescriptor<TestdataConstraintConfigurationSolution> secondWeightDescriptor =
                constraintConfigurationDescriptor.getConstraintWeightDescriptor("secondWeight");
        assertThat(secondWeightDescriptor.getConstraintPackage()).isEqualTo("packageOverwrittenOnField");
        assertThat(secondWeightDescriptor.getConstraintName()).isEqualTo("Second weight");

        TestdataConstraintConfigurationSolution solution = new TestdataConstraintConfigurationSolution("solution");
        TestdataConstraintConfiguration constraintConfiguration = new TestdataConstraintConfiguration(
                "constraintConfiguration");
        constraintConfiguration.setFirstWeight(SimpleScore.ZERO);
        constraintConfiguration.setSecondWeight(SimpleScore.of(7));
        solution.setConstraintConfiguration(constraintConfiguration);

        assertThat(solutionDescriptor.getConstraintConfigurationMemberAccessor().executeGetter(solution))
                .isSameAs(constraintConfiguration);
        assertThat(firstWeightDescriptor.createExtractor().apply(solution)).isEqualTo(SimpleScore.ZERO);
        assertThat(secondWeightDescriptor.createExtractor().apply(solution)).isEqualTo(SimpleScore.of(7));
    }

    @Test
    public void extractionFunctionExtended() {
        SolutionDescriptor<TestdataExtendedConstraintConfigurationSolution> solutionDescriptor =
                TestdataExtendedConstraintConfigurationSolution.buildExtendedSolutionDescriptor();
        ConstraintConfigurationDescriptor<TestdataExtendedConstraintConfigurationSolution> constraintConfigurationDescriptor =
                solutionDescriptor.getConstraintConfigurationDescriptor();

        ConstraintWeightDescriptor<TestdataExtendedConstraintConfigurationSolution> firstWeightDescriptor =
                constraintConfigurationDescriptor.getConstraintWeightDescriptor("firstWeight");
        assertThat(firstWeightDescriptor.getConstraintPackage())
                .isEqualTo(TestdataConstraintConfigurationSolution.class.getPackage().getName());
        assertThat(firstWeightDescriptor.getConstraintName()).isEqualTo("First weight");

        ConstraintWeightDescriptor<TestdataExtendedConstraintConfigurationSolution> secondWeightDescriptor =
                constraintConfigurationDescriptor.getConstraintWeightDescriptor("secondWeight");
        assertThat(secondWeightDescriptor.getConstraintPackage()).isEqualTo("packageOverwrittenOnField");
        assertThat(secondWeightDescriptor.getConstraintName()).isEqualTo("Second weight");

        ConstraintWeightDescriptor<TestdataExtendedConstraintConfigurationSolution> thirdWeightDescriptor =
                constraintConfigurationDescriptor.getConstraintWeightDescriptor("thirdWeight");
        assertThat(thirdWeightDescriptor.getConstraintPackage())
                .isEqualTo(TestdataExtendedConstraintConfigurationSolution.class.getPackage().getName());
        assertThat(thirdWeightDescriptor.getConstraintName()).isEqualTo("Third weight");

        TestdataExtendedConstraintConfigurationSolution solution = new TestdataExtendedConstraintConfigurationSolution(
                "solution");
        TestdataExtendedConstraintConfiguration constraintConfiguration = new TestdataExtendedConstraintConfiguration(
                "constraintConfiguration");
        constraintConfiguration.setFirstWeight(SimpleScore.ZERO);
        constraintConfiguration.setSecondWeight(SimpleScore.of(7));
        constraintConfiguration.setThirdWeight(SimpleScore.of(9));
        solution.setConstraintConfiguration(constraintConfiguration);

        assertThat(solutionDescriptor.getConstraintConfigurationMemberAccessor().executeGetter(solution))
                .isSameAs(constraintConfiguration);
        assertThat(firstWeightDescriptor.createExtractor().apply(solution)).isEqualTo(SimpleScore.ZERO);
        assertThat(secondWeightDescriptor.createExtractor().apply(solution)).isEqualTo(SimpleScore.of(7));
        assertThat(thirdWeightDescriptor.createExtractor().apply(solution)).isEqualTo(SimpleScore.of(9));
    }

}
