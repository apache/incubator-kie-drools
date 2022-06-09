package org.optaplanner.core.impl.testdata.domain.constraintconfiguration.extended;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfiguration;

@ConstraintConfiguration
public class TestdataExtendedConstraintConfiguration extends TestdataConstraintConfiguration {

    private SimpleScore thirdWeight = SimpleScore.of(300);

    public TestdataExtendedConstraintConfiguration() {
        super();
    }

    public TestdataExtendedConstraintConfiguration(String code) {
        super(code);
    }

    @ConstraintWeight("Third weight")
    public SimpleScore getThirdWeight() {
        return thirdWeight;
    }

    public void setThirdWeight(SimpleScore thirdWeight) {
        this.thirdWeight = thirdWeight;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
