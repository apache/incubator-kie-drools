package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.Row;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLRow;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomRow;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomRowWithCells;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLRow;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLRowWithCells;

public class KiePMMLRowInstanceFactoryTest {

    @Test
    void getKiePMMLRow() {
        Row toConvert = getRandomRow();
        KiePMMLRow retrieved = KiePMMLRowInstanceFactory.getKiePMMLRow(toConvert);
        commonVerifyKiePMMLRow(retrieved, toConvert);
        toConvert = getRandomRowWithCells();
        retrieved = KiePMMLRowInstanceFactory.getKiePMMLRow(toConvert);
        commonVerifyKiePMMLRowWithCells(retrieved, toConvert);
    }
}