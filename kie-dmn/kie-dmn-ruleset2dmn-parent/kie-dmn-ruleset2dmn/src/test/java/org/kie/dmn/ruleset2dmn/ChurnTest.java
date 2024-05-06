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

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.ruleset2dmn.TestUtils.ctxFromJson;

class ChurnTest {

    @Test
    void test() throws Exception {
        String dmnXml = Converter.parse("churn", this.getClass().getResourceAsStream("/churn.pmml"));
        // Files.write(new File("src/test/resources/churn.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new TestDMNRuntimeEventListener());
        final DMNModel modelUnderTest = dmnRuntime.getModels().get(0);

        // [Children <= 1.0] ^ [Gender == M] ^ [Age >= 39.213333] ^ [EstIncome >= 58328.8] ^ [EstIncome <= 95405.7] ^ [Age <= 63.933333] -> T
        assertThat(dmnRuntime
                .evaluateAll(modelUnderTest,
                        ctxFromJson(modelUnderTest, "{ \"Children\": 1, \"Gender\": \"M\", \"Age\": 40, \"EstIncome\": 75000, \"Usage\": 0, \"RatePlan\": 0}"))
                .getDecisionResults().get(0).getResult()).isEqualTo("T");

        // [Status == S] ^ [Gender == F] ^ [Usage >= 50.29] ^ [EstIncome <= 38000.0] -> T
        assertThat(dmnRuntime
                .evaluateAll(modelUnderTest,
                        ctxFromJson(modelUnderTest, "{ \"Children\": 1, \"Gender\": \"F\", \"Age\": 0, \"EstIncome\": 35000, \"Status\": \"S\", \"Usage\": 51, \"RatePlan\": 0}"))
                .getDecisionResults().get(0).getResult()).isEqualTo("T");
        
        // default -> F
        assertThat(dmnRuntime
                .evaluateAll(modelUnderTest,
                        ctxFromJson(modelUnderTest, "{ \"Children\": 0, \"Age\": 0, \"EstIncome\": 35000, \"Status\": \"S\", \"Usage\": 51, \"RatePlan\": 0}"))
                .getDecisionResults().get(0).getResult()).isEqualTo("F");
    }
}
