package org.optaplanner.core.impl.testdata.domain.constraintconfiguration;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@ConstraintConfiguration
public class TestdataConstraintConfiguration extends TestdataObject {

    private SimpleScore firstWeight = SimpleScore.of(1);
    private SimpleScore secondWeight = SimpleScore.of(20);

    public TestdataConstraintConfiguration() {
        super();
    }

    public TestdataConstraintConfiguration(String code) {
        super(code);
    }

    @ConstraintWeight("First weight")
    public SimpleScore getFirstWeight() {
        return firstWeight;
    }

    public void setFirstWeight(SimpleScore firstWeight) {
        this.firstWeight = firstWeight;
    }

    @ConstraintWeight(constraintPackage = "packageOverwrittenOnField", value = "Second weight")
    public SimpleScore getSecondWeight() {
        return secondWeight;
    }

    public void setSecondWeight(SimpleScore secondWeight) {
        this.secondWeight = secondWeight;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
