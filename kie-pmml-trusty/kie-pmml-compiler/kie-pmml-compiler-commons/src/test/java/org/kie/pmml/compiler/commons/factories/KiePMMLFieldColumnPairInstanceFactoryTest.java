package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.FieldColumnPair;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldColumnPair;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomFieldColumnPair;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLFieldColumnPair;

public class KiePMMLFieldColumnPairInstanceFactoryTest {

    @Test
    void getKiePMMLFieldColumnPair() {
        final FieldColumnPair toConvert = getRandomFieldColumnPair();
        final KiePMMLFieldColumnPair retrieved = KiePMMLFieldColumnPairInstanceFactory.getKiePMMLFieldColumnPair(toConvert);
        commonVerifyKiePMMLFieldColumnPair(retrieved, toConvert);
    }
}