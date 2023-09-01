package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.TargetValue;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLTargetValue;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTargetValue;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLTargetValue;

public class KiePMMLTargetValueInstanceFactoryTest {

    @Test
    void getKiePMMLTargetValue() {
        final TargetValue toConvert = getRandomTargetValue();
        KiePMMLTargetValue retrieved = KiePMMLTargetValueInstanceFactory.getKiePMMLTargetValue(toConvert);
        commonVerifyKiePMMLTargetValue(retrieved, toConvert);
    }
}