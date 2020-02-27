/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.regression.tests;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.dmg.pmml.PMML;
import org.dmg.pmml.regression.RegressionModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.evaluator.core.PMMLContextImpl;

import java.nio.charset.Charset;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class LogisticRegressionWithNormalizationTest extends AbstractPMMLRegressionTest {

    private static final String MODEL_NAME = "LogisticRegressionWithNormalization";
    private static final String PMML_SOURCE = "/logisticRegressionWithNormalization.pmml";
    private static final String TARGET_FIELD = "result";
    private static final String PROBABILITY_SETOSA_FIELD = "Probability_setosa";
    private static final String PROBABILITY_VERSICOLOR_FIELD = "Probability_versicolor";
    private static final String PROBABILITY_VIRGINICA_FIELD = "Probability_virginica";

    private static final Percentage TOLERANCE_PERCENTAGE = Percentage.withPercentage(0.001);

    private static final String SOFTMAX_NORMALIZATION_METHOD = "softmax";
    private static final String LOGIT_NORMALIZATION_METHOD = "logit";
    private static final String PROBIT_NORMALIZATION_METHOD = "probit";
    private static final String CLOGLOG_NORMALIZATION_METHOD = "cloglog";
    private static final String LOGLOG_NORMALIZATION_METHOD = "loglog";
    private static final String CAUCHIT_NORMALIZATION_METHOD = "cauchit";

    private static final String NORMALIZATION_METHOD_PLACEHOLDER = "NORMALIZATION_METHOD_PLACEHOLDER";

    private double sepalLength;
    private double sepalWidth;
    private double petalLength;
    private double petalWidth;
    private String expectedResult;
    private String normalizationMethod;

    public LogisticRegressionWithNormalizationTest(double sepalLength, double sepalWidth, double petalLength,
                                                   double petalWidth, String expectedResult, String normalizationMethod) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.expectedResult = expectedResult;
        this.normalizationMethod = normalizationMethod;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {6.9, 3.1, 5.1, 2.3, "virginica", SOFTMAX_NORMALIZATION_METHOD},
                {5.8, 2.6, 4.0, 1.2, "versicolor", SOFTMAX_NORMALIZATION_METHOD},
                {5.4, 3.9, 1.3, 0.4, "setosa", SOFTMAX_NORMALIZATION_METHOD},

                {6.9, 3.1, 5.1, 2.3, "virginica", LOGIT_NORMALIZATION_METHOD},
                {5.8, 2.6, 4.0, 1.2, "versicolor", LOGIT_NORMALIZATION_METHOD},
                {5.4, 3.9, 1.3, 0.4, "setosa", LOGIT_NORMALIZATION_METHOD},

                {6.9, 3.1, 5.1, 2.3, "virginica", PROBIT_NORMALIZATION_METHOD},
                {5.8, 2.6, 4.0, 1.2, "versicolor", PROBIT_NORMALIZATION_METHOD},
                {5.4, 3.9, 1.3, 0.4, "setosa", PROBIT_NORMALIZATION_METHOD},

                {6.9, 3.1, 5.1, 2.3, "virginica", CLOGLOG_NORMALIZATION_METHOD},
                {5.8, 2.6, 4.0, 1.2, "versicolor", CLOGLOG_NORMALIZATION_METHOD},
                {5.4, 3.9, 1.3, 0.4, "setosa", CLOGLOG_NORMALIZATION_METHOD},

                {6.9, 3.1, 5.1, 2.3, "virginica", LOGLOG_NORMALIZATION_METHOD},
                {5.8, 2.6, 4.0, 1.2, "versicolor", LOGLOG_NORMALIZATION_METHOD},
                {5.4, 3.9, 1.3, 0.4, "setosa", LOGLOG_NORMALIZATION_METHOD},

                {6.9, 3.1, 5.1, 2.3, "virginica", CAUCHIT_NORMALIZATION_METHOD},
                {5.8, 2.6, 4.0, 1.2, "versicolor", CAUCHIT_NORMALIZATION_METHOD},
                {5.4, 3.9, 1.3, 0.4, "setosa", CAUCHIT_NORMALIZATION_METHOD}
        });
    }

    @Test
    public void testLogisticRegressionWithNormalization() throws Exception {
        String pmmlXML = IOUtils.resourceToString(PMML_SOURCE, Charset.defaultCharset());
        pmmlXML = pmmlXML.replace(NORMALIZATION_METHOD_PLACEHOLDER, normalizationMethod);
        final PMML pmml = TestUtils.loadFromSource(pmmlXML);

        Assertions.assertThat(pmml).isNotNull();
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof RegressionModel);

        final KiePMMLModel pmmlModel = PROVIDER.getKiePMMLModel(pmml.getDataDictionary(),
                (RegressionModel) pmml.getModels().get(0), RELEASE_ID);
        Assertions.assertThat(pmmlModel).isNotNull();

        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Sepal.Length", sepalLength);
        inputData.put("Sepal.Width", sepalWidth);
        inputData.put("Petal.Length", petalLength);
        inputData.put("Petal.Width", petalWidth);

        final PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, inputData);
        PMML4Result pmml4Result = EXECUTOR.evaluate(pmmlModel, new PMMLContextImpl(pmmlRequestData), RELEASE_ID);
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);

        List<Double> probabilities = calculateProbabilities();
        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_SETOSA_FIELD))
                .isCloseTo(probabilities.get(0), TOLERANCE_PERCENTAGE);
        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_VERSICOLOR_FIELD))
                .isCloseTo(probabilities.get(1), TOLERANCE_PERCENTAGE);
        Assertions.assertThat((double) pmml4Result.getResultVariables().get(PROBABILITY_VIRGINICA_FIELD))
                .isCloseTo(probabilities.get(2), TOLERANCE_PERCENTAGE);
    }

    private List<Double> calculateProbabilities() {
        final double rawSetosaProbability = 0.0660297693761902 * sepalLength + 0.242847872054487 * sepalWidth
                + -0.224657116235727 * petalLength + -0.0574727291860025 * petalWidth + 0.11822288946815;
        final double rawVersicolorProbability = -0.0201536848255179 * sepalLength + -0.44561625761404 * sepalWidth
                + 0.22066920522933 * petalLength + -0.494306595747785 * petalWidth + 1.57705897385745;
        final double rawVirginicaProbability = -0.0458760845506725 * sepalLength + 0.202768385559553 * sepalWidth
                + 0.00398791100639665 * petalLength + 0.551779324933787 * petalWidth - 0.695281863325603;

        final List<Double> rawProbabilities = new ArrayList<>();
        rawProbabilities.add(rawSetosaProbability);
        rawProbabilities.add(rawVersicolorProbability);
        rawProbabilities.add(rawVirginicaProbability);
        final List<Double> probabilities = normalization(rawProbabilities);

        return probabilities;
    }

    private List<Double> normalization(List<Double> rawProbabilities) {
        if (normalizationMethod.equals(SOFTMAX_NORMALIZATION_METHOD)) {
            return softmaxNormalization(rawProbabilities);
        } else if (normalizationMethod.equals(LOGIT_NORMALIZATION_METHOD)) {
            return logitNormalization(rawProbabilities);
        } else if (normalizationMethod.equals(CLOGLOG_NORMALIZATION_METHOD)) {
            return cloglogNormalization(rawProbabilities);
        } else if (normalizationMethod.equals(LOGLOG_NORMALIZATION_METHOD)) {
            return loglogNormalization(rawProbabilities);
        } else if (normalizationMethod.equals(CAUCHIT_NORMALIZATION_METHOD)) {
            return cauchitNormalization(rawProbabilities);
        } else {
            throw new RuntimeException("Unknown normalization method");
        }
    }

    private static List<Double> softmaxNormalization(List<Double> rawProbabilities) {
        double denominator = 0;
        for (double rawProbability : rawProbabilities) {
            denominator += rawProbability;
        }

        final List<Double> probabilities = new ArrayList<>();
        for (double rawProbability : rawProbabilities) {
            probabilities.add(Math.exp(rawProbability) / denominator);
        }

        return probabilities;
    }

    private static List<Double> logitNormalization(List<Double> rawProbabilities) {
        final List<Double> probabilities = new ArrayList<>();
        for (double rawProbability : rawProbabilities) {
            probabilities.add(1 / (1 + Math.exp(-rawProbability)));
        }

        return probabilities;
    }


    private static List<Double> cloglogNormalization(List<Double> rawProbabilities) {
        final List<Double> probabilities = new ArrayList<>();
        for (double rawProbability : rawProbabilities) {
            probabilities.add(1 - Math.exp(-Math.exp(rawProbability)));
        }

        return probabilities;
    }

    private static List<Double> loglogNormalization(List<Double> rawProbabilities) {
        final List<Double> probabilities = new ArrayList<>();
        for (double rawProbability : rawProbabilities) {
            probabilities.add(Math.exp(-Math.exp(-rawProbability)));
        }

        return probabilities;
    }

    private static List<Double> cauchitNormalization(List<Double> rawProbabilities) {
        final List<Double> probabilities = new ArrayList<>();
        for (double rawProbability : rawProbabilities) {
            probabilities.add(0.5 + (1/Math.PI) * Math.atan(rawProbability));
        }

        return probabilities;
    }
}
