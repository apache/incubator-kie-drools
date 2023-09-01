package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.Apply;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomApply;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLApply;

public class KiePMMLApplyInstanceFactoryTest {

    @Test
    void getKiePMMLApply() {
        Apply toConvert = getRandomApply();
        KiePMMLApply retrieved = KiePMMLApplyInstanceFactory.getKiePMMLApply(toConvert);
        commonVerifyKiePMMLApply(retrieved, toConvert);
    }
}