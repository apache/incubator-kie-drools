/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.alphanetwork;

import java.math.BigDecimal;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsAlphaNetworkTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DMNDecisionTableAlphaSupportingTest extends BaseInterpretedVsAlphaNetworkTest {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(DMNDecisionTableAlphaSupportingTest.class);

    public DMNDecisionTableAlphaSupportingTest(final boolean useAlphaNetwork ) {
        super( useAlphaNetwork );
    }

    @Test
    public void testSimpleDecision() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("alphasupport.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_c0cf6e20-0b43-43ce-9def-c759a5f86df2", "DMN Specification Chapter 11 Example Reduced");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = runtime.newContext();
        context.set("Existing Customer", "s");
        context.set("Application Risk Score", new BigDecimal("123"));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.getContext().get("Pre-bureau risk category table"), is("LOW"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectMax() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("an-simpletable-multipletests.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "an-simpletable-multipletests");
        MatcherAssert.assertThat(dmnModel, notNullValue());

        final DMNContext context = DMNFactory.newContext();
        context.set("Age", 21);
        context.set("RiskCategory", "Low");
        context.set("isAffordable", true);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        MatcherAssert.assertThat(result.get("Approval Status"), is("Approved"));
    }
}
