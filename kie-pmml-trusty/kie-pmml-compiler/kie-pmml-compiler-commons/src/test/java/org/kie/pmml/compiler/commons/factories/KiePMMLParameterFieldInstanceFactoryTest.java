package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.ParameterField;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getParameterField;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLParameterField;

public class KiePMMLParameterFieldInstanceFactoryTest {

    @Test
    void getKiePMMLParameterField() {
        final String fieldName = "fieldName";
        final ParameterField toConvert = getParameterField(fieldName);
        KiePMMLParameterField retrieved = KiePMMLParameterFieldInstanceFactory.getKiePMMLParameterField(toConvert);
        commonVerifyKiePMMLParameterField(retrieved, toConvert);
    }
}