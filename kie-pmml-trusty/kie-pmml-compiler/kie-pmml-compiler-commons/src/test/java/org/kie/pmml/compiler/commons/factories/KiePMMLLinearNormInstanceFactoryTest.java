package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.LinearNorm;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLLinearNorm;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomLinearNorm;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLLinearNorm;

public class KiePMMLLinearNormInstanceFactoryTest {

    @Test
    void getKiePMMLLinearNorms() {
        List<LinearNorm> toConvert =
                IntStream.range(0, 3).mapToObj(i -> getRandomLinearNorm()).collect(Collectors.toList());
        List<KiePMMLLinearNorm> retrieved = KiePMMLLinearNormInstanceFactory.getKiePMMLLinearNorms(toConvert);
        IntStream.range(0, 3).forEach(i -> commonVerifyKiePMMLLinearNorm(retrieved.get(i), toConvert.get(i)));
    }

    @Test
    void getKiePMMLLinearNorm() {
        final LinearNorm toConvert = getRandomLinearNorm();
        final KiePMMLLinearNorm retrieved = KiePMMLLinearNormInstanceFactory.getKiePMMLLinearNorm(toConvert);
        commonVerifyKiePMMLLinearNorm(retrieved, toConvert);
    }
}