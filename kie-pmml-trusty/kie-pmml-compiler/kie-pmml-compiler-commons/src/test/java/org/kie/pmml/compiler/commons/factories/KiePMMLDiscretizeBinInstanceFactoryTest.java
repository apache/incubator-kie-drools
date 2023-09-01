package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.DiscretizeBin;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretizeBin;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDiscretizeBin;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDiscretizeBin;

public class KiePMMLDiscretizeBinInstanceFactoryTest {

    @Test
    void getKiePMMLDiscretizeBin() {
        DiscretizeBin toConvert = getRandomDiscretizeBin();
        KiePMMLDiscretizeBin retrieved = KiePMMLDiscretizeBinInstanceFactory.getKiePMMLDiscretizeBin(toConvert);
        commonVerifyKiePMMLDiscretizeBin(retrieved, toConvert);
    }
}