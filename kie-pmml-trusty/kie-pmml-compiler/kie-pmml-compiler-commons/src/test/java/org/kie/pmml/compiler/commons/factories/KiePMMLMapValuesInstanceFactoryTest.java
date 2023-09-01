package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.MapValues;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLMapValues;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomMapValues;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLMapValues;

public class KiePMMLMapValuesInstanceFactoryTest {

    @Test
    void getKiePMMLMapValues() {
        MapValues toConvert = getRandomMapValues();
        KiePMMLMapValues retrieved = KiePMMLMapValuesInstanceFactory.getKiePMMLMapValues(toConvert);
        commonVerifyKiePMMLMapValues(retrieved, toConvert);
    }
}