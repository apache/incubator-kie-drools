package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.NormDiscrete;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLNormDiscrete;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomNormDiscrete;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLNormDiscrete;

public class KiePMMLNormDiscreteInstanceFactoryTest {

    @Test
    void getKiePMMLNormDiscrete() {
        NormDiscrete toConvert = getRandomNormDiscrete();
        KiePMMLNormDiscrete retrieved = KiePMMLNormDiscreteInstanceFactory.getKiePMMLNormDiscrete(toConvert);
        commonVerifyKiePMMLNormDiscrete(retrieved, toConvert);
    }
}