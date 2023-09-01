package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.Constant;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;

import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLConstant;

public class KiePMMLConstantInstanceFactoryTest {

    @Test
    void getKiePMMLConstant() {
        Object value = 2342.21;
        Constant toConvert = new Constant();
        toConvert.setValue(value);
        KiePMMLConstant retrieved = KiePMMLConstantInstanceFactory.getKiePMMLConstant(toConvert);
        commonVerifyKiePMMLConstant(retrieved, toConvert);
    }
}