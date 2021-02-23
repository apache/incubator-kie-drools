/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.decisionservices;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

@RunWith(Parameterized.class)
public class DMNDecisionServicesTypecheckDSxyTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServicesTypecheckDSxyTest.class);

    @FunctionalInterface
    public static interface TestEitherModelOrDS {

        DMNResult apply(DMNRuntime runtime, DMNModel dmnModel, DMNContext context);
    }

    public DMNDecisionServicesTypecheckDSxyTest(final TestEitherModelOrDS variant) {
        fn = variant;
    }

    @Parameterized.Parameters()
    public static Object[] params() {
        return new TestEitherModelOrDS[]{(runtime, dmnModel, context) -> runtime.evaluateAll(dmnModel, context),
                                         (runtime, dmnModel, context) -> runtime.evaluateDecisionService(dmnModel, context, "DecisionService-1")};
    }

    private final TestEitherModelOrDS fn;
    private DMNRuntime runtime;
    private DMNModel dmnModel;


    @Before()
    public void init() {
        runtime = DMNRuntimeUtil.createRuntime("DSxy.dmn", this.getClass());
        dmnModel = runtime.getModel("https://kiegroup.org/dmn/_127520A0-364A-4ADA-A012-3AB6A7E3585E", "DSxy");

        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));
    }

    @Test
    public void testDSParamsTConOK() {
        final DMNContext context = DMNFactory.newContext();
        context.set("x", mapOf(entry("x", new BigDecimal(1)),
                               entry("y", new BigDecimal(2))));

        final DMNResult dmnResult = fn.apply(runtime, dmnModel, context);

        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getEvaluationStatus(), is(DecisionEvaluationStatus.SUCCEEDED));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult(), is(new BigDecimal(1)));
    }
    
    @Test
    public void testDSParamsTConFAIL() {
        final DMNContext context = DMNFactory.newContext();
        context.set("x", mapOf(entry("x", new BigDecimal(1))));

        final DMNResult dmnResult = fn.apply(runtime, dmnModel, context);

        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.getMessages().isEmpty(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getEvaluationStatus(), not(is(DecisionEvaluationStatus.SUCCEEDED)));
    }

    @Test
    public void testDSParamsTCoff() {
        // IMPORTANT !!
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("x", mapOf(entry("x", new BigDecimal(1))));

        final DMNResult dmnResult = fn.apply(runtime, dmnModel, context);

        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getEvaluationStatus(), is(DecisionEvaluationStatus.SUCCEEDED));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult(), is(new BigDecimal(1)));
    }
}
