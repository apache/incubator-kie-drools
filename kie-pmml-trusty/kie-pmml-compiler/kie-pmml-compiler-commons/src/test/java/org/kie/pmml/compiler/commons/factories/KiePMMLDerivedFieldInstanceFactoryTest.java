package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;

import org.dmg.pmml.DerivedField;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getDerivedField;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDerivedField;

public class KiePMMLDerivedFieldInstanceFactoryTest {

    @Test
    void getKiePMMLDerivedField() {
        final String fieldName = "fieldName";
        final DerivedField toConvert = getDerivedField(fieldName);
        KiePMMLDerivedField retrieved = KiePMMLDerivedFieldInstanceFactory.getKiePMMLDerivedField(toConvert,
                                                                                                  Collections.emptyList());
        commonVerifyKiePMMLDerivedField(retrieved, toConvert);
    }
}