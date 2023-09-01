package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.Interval;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomInterval;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLInterval;

public class KiePMMLIntervalInstanceFactoryTest {

    @Test
    void getKiePMMLInterval() {
        Interval toConvert = getRandomInterval();
        KiePMMLInterval retrieved = KiePMMLIntervalInstanceFactory.getKiePMMLInterval(toConvert);
        commonVerifyKiePMMLInterval(retrieved, toConvert);
    }
}