package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.Target;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLTarget;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTarget;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLTarget;

public class KiePMMLTargetInstanceFactoryTest {

    @Test
    void getKiePMMLTarget() {
        final Target toConvert = getRandomTarget();
        KiePMMLTarget retrieved = KiePMMLTargetInstanceFactory.getKiePMMLTarget(toConvert);
        commonVerifyKiePMMLTarget(retrieved, toConvert);
    }
}