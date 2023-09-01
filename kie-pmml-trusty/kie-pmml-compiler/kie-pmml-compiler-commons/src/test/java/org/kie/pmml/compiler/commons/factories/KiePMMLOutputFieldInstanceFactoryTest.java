package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.OutputField;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLOutputField;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomOutputField;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLOutputField;

public class KiePMMLOutputFieldInstanceFactoryTest {

    @Test
    void getKiePMMLOutputField() {
        OutputField toConvert = getRandomOutputField();
        KiePMMLOutputField retrieved = KiePMMLOutputFieldInstanceFactory.getKiePMMLOutputField(toConvert);
        commonVerifyKiePMMLOutputField(retrieved, toConvert);
    }
}