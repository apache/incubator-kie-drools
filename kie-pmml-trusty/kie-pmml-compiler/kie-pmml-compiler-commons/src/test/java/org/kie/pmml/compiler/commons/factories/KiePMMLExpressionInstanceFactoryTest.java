package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.Expression;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomApply;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomConstant;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDiscretize;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomFieldRef;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomMapValues;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomNormContinuous;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomNormDiscrete;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTextIndex;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLExpression;

public class KiePMMLExpressionInstanceFactoryTest {

    @Test
    void getKiePMMLExpression() {
        Expression toConvert = getRandomApply();
        KiePMMLExpression retrieved =
                org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression(toConvert);
        commonVerifyKiePMMLExpression(retrieved, toConvert);
        toConvert = getRandomConstant();
        retrieved =
                org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression(toConvert);
        commonVerifyKiePMMLExpression(retrieved, toConvert);
        toConvert = getRandomDiscretize();
        retrieved =
                org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression(toConvert);
        commonVerifyKiePMMLExpression(retrieved, toConvert);
        toConvert = getRandomFieldRef();
        retrieved =
                org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression(toConvert);
        commonVerifyKiePMMLExpression(retrieved, toConvert);
        toConvert = getRandomMapValues();
        retrieved =
                org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression(toConvert);
        commonVerifyKiePMMLExpression(retrieved, toConvert);
        toConvert = getRandomNormContinuous();
        retrieved = org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression(toConvert);
        commonVerifyKiePMMLExpression(retrieved, toConvert);
        toConvert = getRandomNormDiscrete();
        retrieved = org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression(toConvert);
        commonVerifyKiePMMLExpression(retrieved, toConvert);
        toConvert = getRandomTextIndex();
        retrieved = KiePMMLExpressionInstanceFactory.getKiePMMLExpression(toConvert);
        commonVerifyKiePMMLExpression(retrieved, toConvert);
    }
}