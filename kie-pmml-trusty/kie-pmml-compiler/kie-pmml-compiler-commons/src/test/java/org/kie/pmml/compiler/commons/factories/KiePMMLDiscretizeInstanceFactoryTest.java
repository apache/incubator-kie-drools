package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.Discretize;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretize;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDiscretize;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDiscretize;

public class KiePMMLDiscretizeInstanceFactoryTest {

    @Test
    void getKiePMMLDiscretize() {
        Discretize toConvert = getRandomDiscretize();
        KiePMMLDiscretize retrieved = KiePMMLDiscretizeInstanceFactory.getKiePMMLDiscretize(toConvert);
        commonVerifyKiePMMLDiscretize(retrieved, toConvert);
    }
}