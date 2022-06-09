package org.optaplanner.core.impl.domain.constraintweight.descriptor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfiguration;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfigurationSolution;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.extended.TestdataExtendedConstraintConfiguration;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.extended.TestdataExtendedConstraintConfigurationSolution;

class ConstraintWeightDescriptorTest {

    @Test
    void extractionFunction() {
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
    void extractionFunctionExtended() {
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
