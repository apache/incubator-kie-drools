package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.DefineFunction;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getDefineFunction;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDefineFunction;

public class KiePMMLDefineFunctionInstanceFactoryTest {

    @Test
    void getKiePMMLDefineFunction() {
        final String functionName = "functionName";
        final DefineFunction toConvert = getDefineFunction(functionName);
        KiePMMLDefineFunction retrieved = KiePMMLDefineFunctionInstanceFactory.getKiePMMLDefineFunction(toConvert);
        commonVerifyKiePMMLDefineFunction(retrieved, toConvert);
    }
}