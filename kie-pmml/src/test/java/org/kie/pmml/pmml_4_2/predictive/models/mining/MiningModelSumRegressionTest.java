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

package org.kie.pmml.pmml_4_2.predictive.models.mining;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class MiningModelSumRegressionTest {

    private static final String PMML_SOURCE = "org/kie/pmml/pmml_4_2/test_mining_model_summed_regression.pmml";
    private static final String MINING_MODEL = "SampleMiningModel";

    private static final String INPUT1_FIELD_NAME = "input1";
    private static final String INPUT2_FIELD_NAME = "input2";
    private static final String INPUT3_FIELD_NAME = "input3";
    private static final String SUM_OUTPUT_FIELD_NAME = "Sum_Result";
    private static final String BASE_OUTPUT_FIELD_NAME = "Result";

    private static final double COMPARISON_DELTA = 0.001;

    private Optional<Double> input1;
    private Optional<Double> input2;
    private Optional<Double> input3;
    private double result;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Optional.of(10.0), Optional.of(10.0), Optional.of(10.0), 2070 },
                { Optional.of(200.0), Optional.of(-1.0), Optional.of(2.0), -299 },
                { Optional.of(90.0), Optional.of(2.0), Optional.of(4.0), 17040 },
        });
    }

    public MiningModelSumRegressionTest(Optional<Double> input1, Optional<Double> input2,
                                                Optional<Double> input3, double result) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.result = result;
    }

    @Test
    public void testMiningModelSumRegression() {
        final Resource res = ResourceFactory.newClassPathResource(PMML_SOURCE);
        final PMML4ExecutionHelper helper = PMML4ExecutionHelper.PMML4ExecutionHelperFactory
                .getExecutionHelper(MINING_MODEL, res, null, true);
        final PMMLRequestDataBuilder rdb = new PMMLRequestDataBuilder("1234", MINING_MODEL);
        input1.ifPresent(x -> rdb.addParameter(INPUT1_FIELD_NAME, x, Double.class));
        input2.ifPresent(x -> rdb.addParameter(INPUT2_FIELD_NAME, x, Double.class));
        input3.ifPresent(x -> rdb.addParameter(INPUT3_FIELD_NAME, x, Double.class));
        PMMLRequestData request = rdb.build();
        helper.submitRequest(request);
        helper.getResultData().iterator().forEachRemaining(rd -> {
            assertEquals(request.getCorrelationId(), rd.getCorrelationId());
            if (rd.getSegmentationId() == null) {
                assertEquals("OK",rd.getResultCode());
                double value = rd.getResultValue(SUM_OUTPUT_FIELD_NAME, "value", Double.class).orElse(null);
                assertNotNull(value);
                assertEquals(result, value, COMPARISON_DELTA);
                value = rd.getResultValue(BASE_OUTPUT_FIELD_NAME, "value", Double.class).orElse(null);
                assertNotNull(value);
                assertEquals(result, value, COMPARISON_DELTA);
            }
        });
    }
}
