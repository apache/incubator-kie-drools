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

package org.kie.pmml.runtime.regression.tests;

import org.assertj.core.api.Assertions;
import org.dmg.pmml.PMML;
import org.dmg.pmml.regression.RegressionModel;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.library.testutils.TestUtils;
import org.kie.pmml.models.regression.executor.RegressionModelImplementationProvider;
import org.kie.pmml.runtime.core.PMMLContextImpl;
import org.kie.pmml.runtime.core.executor.PMMLModelExecutor;
import org.kie.pmml.runtime.core.utils.PMMLRequestDataBuilder;
import org.kie.pmml.runtime.regression.executor.PMMLRegressionModelExecutor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NumericVariablesRegressionTest {

    private static final String MODEL_NAME = "lm_Model";
    private static final String PMML_SOURCE = "/numericRegression.pmml";
    private static final String TARGET_FIELD = "result";

    private final static RegressionModelImplementationProvider PROVIDER = new RegressionModelImplementationProvider();
    private final static PMMLModelExecutor EXECUTOR = new PMMLRegressionModelExecutor();
    private final static String RELEASE_ID = "org.drools:kie-pmml-models-testing:1.0";

    @Test
    public void testNumericVariableRegression() throws Exception {
        final PMML pmml;
        try (final InputStream inputStream = NumericVariablesRegressionTest.class.getResourceAsStream(PMML_SOURCE)) {
            pmml = TestUtils.loadFromInputStream(inputStream);
        }
        Assertions.assertThat(pmml).isNotNull();
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof RegressionModel);

        final KiePMMLModel pmmlModel = PROVIDER.getKiePMMLModel(pmml.getDataDictionary(),
                (RegressionModel) pmml.getModels().get(0), RELEASE_ID);
        Assertions.assertThat(pmmlModel).isNotNull();

        final PMMLModelExecutor pmmlModelExecutor = new PMMLRegressionModelExecutor();

        double x = 1;
        double y = 5;
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("x", 1);
        inputData.put("y", 5);

        final PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, inputData);
        PMML4Result pmml4Result = EXECUTOR.evaluate(pmmlModel, new PMMLContextImpl(pmmlRequestData), RELEASE_ID);

        Assertions.assertThat(pmml4Result).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables()).containsKey(TARGET_FIELD);
        Assertions.assertThat((Double) pmml4Result.getResultVariables().get(TARGET_FIELD))
                .isEqualTo(regressionFunction(x, y));
    }

    public static PMMLRequestData getPMMLRequestData(String modelName, Map<String, Object> parameters) {
        String correlationId = "CORRELATION_ID";
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(correlationId, modelName);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            Object pValue = entry.getValue();
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(entry.getKey(), pValue, class1);
        }
        return pmmlRequestDataBuilder.build();
    }

    private static double regressionFunction(double x, double y) {
        return 2*x + y + 5;
    }
}
