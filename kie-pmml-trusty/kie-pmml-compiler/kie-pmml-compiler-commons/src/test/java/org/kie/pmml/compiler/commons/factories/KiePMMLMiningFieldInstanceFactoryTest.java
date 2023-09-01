package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.DataField;
import org.dmg.pmml.MiningField;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLMiningField;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomMiningField;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLMiningField;

public class KiePMMLMiningFieldInstanceFactoryTest {

    @Test
    void getKiePMMLMiningField() {
        DataField dataField = getRandomDataField();
        MiningField toConvert = getRandomMiningField(dataField);
        KiePMMLMiningField toVerify = KiePMMLMiningFieldInstanceFactory.getKiePMMLMiningField(toConvert, dataField);
        commonVerifyKiePMMLMiningField(toVerify, toConvert, dataField);
    }
}