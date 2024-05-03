/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.ruleset2dmn;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.ruleset2dmn.TestUtils.ctxFromJson;

class WifiTest {

    @Test
    void test() throws Exception {
        final String dmnXml = Converter.parse("wifi", this.getClass().getResourceAsStream("/wifi.pmml"));
        // Files.write(new File("src/test/resources/wifi.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new TestDMNRuntimeEventListener());
        final DMNModel modelUnderTest = dmnRuntime.getModels().get(0);

        // [X4 <= -61.0] ^ [X5 <= -63.0] -> 1
        assertThat(dmnRuntime.evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"X4\" : -61, \"X5\": -63}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("1"));

        // [X1 >= -54.0] -> 2
        assertThat(dmnRuntime.evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"X1\" : -54}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("2"));

        // [X5 <= -57.0] ^ [X1 <= -46.0] ^ [X3 >= -53.0] ^ [X1 >= -55.0] ^ [X7 <= -73.0] -> 3
        assertThat(dmnRuntime.evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"X5\" : -57, \"X1\": -46, \"X3\": -53, \"X7\": -73}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("3"));

        // DECISION_TABLE_MASKED_RULE: Rule 12 is masked by rule: 11 (if it was a Priority with lov := 1,2,3 (DROOLS-7022)
        // [X3 >= -51.0] ^ [X3 <= -51.0] ^ [X1 >= -43.0] ^ [X1 <= -43.0] ^ [X2 >= -43.0] ^ [X2 < -43.0] -> 3
        assertThat(dmnRuntime.evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"X1\" : -43, \"X3\": -51, \"X2\": -42}")) // DROOLS-7023 capture this cases [x..x] in the Converter?
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("2"));

        // default -> 0
        assertThat(dmnRuntime.evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"nothing\" : 999}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("4"));
    }
}
