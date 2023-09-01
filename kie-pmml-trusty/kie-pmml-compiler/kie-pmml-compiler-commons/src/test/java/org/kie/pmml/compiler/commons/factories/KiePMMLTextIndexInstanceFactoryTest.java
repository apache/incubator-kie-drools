package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.TextIndex;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLTextIndex;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTextIndex;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLTextIndex;

public class KiePMMLTextIndexInstanceFactoryTest {

    @Test
    void getKiePMMLTextIndex() {
        final TextIndex toConvert = getRandomTextIndex();
        final KiePMMLTextIndex retrieved = KiePMMLTextIndexInstanceFactory.getKiePMMLTextIndex(toConvert);
        commonVerifyKiePMMLTextIndex(retrieved, toConvert);
    }
}