package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.NormContinuous;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLNormContinuous;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomNormContinuous;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLNormContinuous;

public class KiePMMLNormContinuousInstanceFactoryTest {

    @Test
    void getKiePMMLNormContinuous() {
        final NormContinuous toConvert = getRandomNormContinuous();
        final KiePMMLNormContinuous retrieved =
                KiePMMLNormContinuousInstanceFactory.getKiePMMLNormContinuous(toConvert);
        commonVerifyKiePMMLNormContinuous(retrieved, toConvert);
    }
}