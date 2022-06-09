package org.optaplanner.core.impl.testdata.domain.constraintconfiguration.extended;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

@PlanningSolution
public class TestdataExtendedConstraintConfigurationSolution extends TestdataSolution {

    public static SolutionDescriptor<TestdataExtendedConstraintConfigurationSolution> buildExtendedSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TestdataExtendedConstraintConfigurationSolution.class,
                TestdataEntity.class);
    }

    private TestdataExtendedConstraintConfiguration constraintConfiguration;

    public TestdataExtendedConstraintConfigurationSolution() {
    }

    public TestdataExtendedConstraintConfigurationSolution(String code) {
        super(code);
    }

    @ConstraintConfigurationProvider
    public TestdataExtendedConstraintConfiguration getConstraintConfiguration() {
        return constraintConfiguration;
    }

    public void setConstraintConfiguration(TestdataExtendedConstraintConfiguration constraintConfiguration) {
        this.constraintConfiguration = constraintConfiguration;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
