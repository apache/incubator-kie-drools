/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.pmml_4_2.predictive.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper.PMML4ExecutionHelperFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class SimpleRegressionTest extends DroolsAbstractPMMLTest {

    private static final String source1 = "org/kie/pmml/pmml_4_2/test_regression.pmml";
    private static final String source2 = "org/kie/pmml/pmml_4_2/test_regression_clax.pmml";

    private static final double COMPARISON_DELTA = 0.000001;

    private double fld1;
    private double fld2;
    private String fld3;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {1.0, 1.0, "x"},
                {0.9, 0.3, "x"},
                {12.0, 25.0, "x"},
                {0.2, 0.1, "x"},
                {5, 8, "y"},
        });
    }

    public SimpleRegressionTest(double fld1, double fld2, String fld3) {
        this.fld1 = fld1;
        this.fld2 = fld2;
        this.fld3 = fld3;
    }

    @Test
    public void testRegression() throws Exception {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("LinReg",
                                                                                     ResourceFactory.newClassPathResource(source1),
                                                                                     null);
    	
        PMMLRequestData request = new PMMLRequestData("123","LinReg");
        request.addRequestParam("fld1",fld1);
        request.addRequestParam("fld2", fld2);
        request.addRequestParam("fld3", fld3);
        
        PMML4Result resultHolder = helper.submitRequest(request);
        assertEquals("OK",resultHolder.getResultCode());
        assertNotNull(resultHolder.getResultValue("Fld4", null));
        Double value = resultHolder.getResultValue("Fld4", "value", Double.class).orElse(null);
        assertNotNull(value);

        final double expectedValue = simpleRegressionResult(fld1, fld2, fld3);
		assertEquals(expectedValue, value, COMPARISON_DELTA);
    }

    private double simpleRegressionResult(double fld1, double fld2, String fld3) {
        double result = 0.5 + 5 * fld1 * fld1 + 2 * fld2 + fld3Coefficient(fld3) + 0.4 * fld1 * fld2;
        result = 1.0 / (1.0 + Math.exp(-result));

        return result;
    }

    @Test
    public void testClassification() throws Exception {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("LinReg",
                                                                                     ResourceFactory.newClassPathResource(source2),
                                                                                     null);

        PMMLRequestData request = new PMMLRequestData("123","LinReg");
        request.addRequestParam("fld1", fld1);
        request.addRequestParam("fld2", fld2);
        request.addRequestParam("fld3", fld3);

        PMML4Result resultHolder = helper.submitRequest(request);
        Map<String, Double> probabilities = categoryProbabilities(fld1, fld2, fld3);
        String maxCategory = null;
        double maxValue = Double.MIN_VALUE;
        for (String key : probabilities.keySet()) {
            double value = probabilities.get(key);
            if (value > maxValue) {
                maxCategory = key;
                maxValue = value;
            }
        }
        
        assertNotNull(resultHolder.getResultValue("RegOut", null));
        assertNotNull(resultHolder.getResultValue("RegProb", null));
        assertNotNull(resultHolder.getResultValue("RegProbA", null));
        
        String regOut = resultHolder.getResultValue("RegOut", "value", String.class).orElse(null);
        Double regProb = resultHolder.getResultValue("RegProb", "value", Double.class).orElse(null);
        Double regProbA = resultHolder.getResultValue("RegProbA", "value", Double.class).orElse(null);
        assertEquals("cat" + maxCategory, regOut);
        assertEquals(maxValue, regProb, COMPARISON_DELTA);
        assertEquals(probabilities.get("A"), regProbA, COMPARISON_DELTA);
    }

    private Map<String, Double> categoryProbabilities(double fld1, double fld2, String fld3) {
        final Map<String, RegressionInterface> regressionTables = new HashMap<>();
        regressionTables.put("A", (f1, f2, f3) -> 0.1 + fld1 + fld2 + fld3Coefficient(fld3));
        regressionTables.put("B", (f1, f2, f3) -> 0.2 + 2 * fld1 + 2 * fld2 + fld3Coefficient(fld3));
        regressionTables.put("C", (f1, f2, f3) -> 0.3 + 3 * fld1 + 3 * fld2 + fld3Coefficient(fld3));
        regressionTables.put("D", (f1, f2, f3) -> 5.0);

        final Map<String, Double> regressionTablesValues = new HashMap<>();
        double sum = 0;
        for (String item : regressionTables.keySet()) {
            double value = regressionTables.get(item).apply(fld1, fld2, fld3);
            value = Math.exp(value);
            regressionTablesValues.put(item, value);
            sum += value;
        }

        for (String item : regressionTables.keySet()) {
            regressionTablesValues.put(item, regressionTablesValues.get(item) / sum);
        }

        return regressionTablesValues;
    }

    private static double fld3Coefficient(String fld3) {
        final Map<String, Double> fld3ValueMap = new HashMap<>();
        fld3ValueMap.put("x", -3.0);
        fld3ValueMap.put("y", 3.0);

        if (!fld3ValueMap.containsKey(fld3)) {
            return 0;
        }
        return fld3ValueMap.get(fld3);
    }

    @FunctionalInterface
    private interface RegressionInterface {
        public Double apply(double fld1, double fld2, String fld3);
    }
}
