package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.FieldRef;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomFieldRef;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLFieldRef;

public class KiePMMLFieldRefInstanceFactoryTest {

    @Test
    void getKiePMMLFieldRef() {
        FieldRef toConvert = getRandomFieldRef();
        KiePMMLFieldRef retrieved = KiePMMLFieldRefInstanceFactory.getKiePMMLFieldRef(toConvert);
        commonVerifyKiePMMLFieldRef(retrieved, toConvert);
    }
}