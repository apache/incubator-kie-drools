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

@RunWith(Parameterized.class)
public class DecisionTreeWithSurrogateTest {

    private static final String DECISION_TREES_FOLDER = "org/kie/pmml/pmml_4_2/";
    private static final String treeWithSurrogate = DECISION_TREES_FOLDER + "test_tree_with_surrogate.pmml";

    private Optional<Double> temperature;
    private Optional<Double> humidity;
    private String decision;
    private String correlationId;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 { Optional.of(34.0), Optional.empty(), "sunglasses", "1234" }, //bug - RHPAM-1492
                { Optional.of(34.0), Optional.of(15.0), "sunglasses", "5678" },
                { Optional.empty(), Optional.of(15.0), "sunglasses", "9012" },
                { Optional.empty(), Optional.of(85.0), "umbrella", "3456" }, //bug - RHPAM-1492
                { Optional.of(22.0), Optional.of(85.0), "umbrella", "7890" },
                { Optional.of(22.0), Optional.of(35.0), "nothing", "4321" },
                { Optional.empty(), Optional.of(35.0), "nothing", "0987" }, //bug - RHPAM-1492
                { Optional.of(22.0), Optional.empty(), "nothing", "6543" },
        });
    }

    public DecisionTreeWithSurrogateTest(Optional<Double> temperature, Optional<Double> humidity, String decision, String correlationId) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.decision = decision;
        this.correlationId = correlationId;
    }

    @Test
    public void testTreeWithSurrogatePredicate() {
        Resource res = ResourceFactory.newClassPathResource(treeWithSurrogate);
        PMML4ExecutionHelper helper = PMML4ExecutionHelper.PMML4ExecutionHelperFactory
                .getExecutionHelper("SampleMine", res, null, false);
        helper.initModel();

        PMMLRequestDataBuilder rdb = new PMMLRequestDataBuilder(correlationId, "SampleMine");
        temperature.ifPresent( t -> rdb.addParameter("temperature", t, Double.class));
        humidity.ifPresent( h -> rdb.addParameter("humidity", h, Double.class));
        PMMLRequestData request = rdb.build();
        helper.submitRequest(request);
        helper.getResultData().iterator().forEachRemaining(rd -> {
            assertEquals("OK",rd.getResultCode());
            assertEquals(correlationId,rd.getCorrelationId());
            String value = rd.getResultValue("Decision", "value", String.class).orElse(null);
            assertEquals(decision, value);
        });
    }
}
