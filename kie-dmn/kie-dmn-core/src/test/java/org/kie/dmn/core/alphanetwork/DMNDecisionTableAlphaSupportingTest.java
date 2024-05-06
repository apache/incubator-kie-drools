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
package org.kie.dmn.core.alphanetwork;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsAlphaNetworkTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNDecisionTableAlphaSupportingTest extends BaseInterpretedVsAlphaNetworkTest {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(DMNDecisionTableAlphaSupportingTest.class);

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecision(boolean useAlphaNetwork) {
        alphaNetwork = useAlphaNetwork;
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("alphasupport.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_c0cf6e20-0b43-43ce-9def-c759a5f86df2", "DMN Specification Chapter 11 Example Reduced");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = runtime.newContext();
        context.set("Existing Customer", "s");
        context.set("Application Risk Score", new BigDecimal("123"));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.getContext().get("Pre-bureau risk category table")).isEqualTo("LOW");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleTableMultipleTests(boolean useAlphaNetwork) {
        alphaNetwork = useAlphaNetwork;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("an-simpletable-multipletests.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "an-simpletable-multipletests");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Age", 21);
        context.set("RiskCategory", "Low");
        context.set("isAffordable", true);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Approval Status")).isEqualTo("Approved");
    }
}
