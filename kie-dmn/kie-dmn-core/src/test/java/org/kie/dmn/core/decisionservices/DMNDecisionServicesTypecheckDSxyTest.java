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
package org.kie.dmn.core.decisionservices;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DMNDecisionServicesTypecheckDSxyTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServicesTypecheckDSxyTest.class);

    @FunctionalInterface
    public static interface TestEitherModelOrDS {

        DMNResult apply(DMNRuntime runtime, DMNModel dmnModel, DMNContext context);
    }

    public void initDMNDecisionServicesTypecheckDSxyTest(final TestEitherModelOrDS variant) {
        fn = variant;
    }

    public static Object[] params() {
        return new TestEitherModelOrDS[]{(runtime, dmnModel, context) -> runtime.evaluateAll(dmnModel, context),
                                         (runtime, dmnModel, context) -> runtime.evaluateDecisionService(dmnModel, context, "DecisionService-1")};
    }

    private TestEitherModelOrDS fn;
    private DMNRuntime runtime;
    private DMNModel dmnModel;


    @BeforeEach()
    void init() {
        runtime = DMNRuntimeUtil.createRuntime("DSxy.dmn", this.getClass());
        dmnModel = runtime.getModel("https://kiegroup.org/dmn/_127520A0-364A-4ADA-A012-3AB6A7E3585E", "DSxy");

        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
    }

    @MethodSource("params")
    @ParameterizedTest()
    public void dSParamsTConOK(final TestEitherModelOrDS variant) {
        initDMNDecisionServicesTypecheckDSxyTest(variant);
        final DMNContext context = DMNFactory.newContext();
        context.set("x", mapOf(entry("x", new BigDecimal(1)),
                               entry("y", new BigDecimal(2))));

        final DMNResult dmnResult = fn.apply(runtime, dmnModel, context);

        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SUCCEEDED);
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult()).isEqualTo(new BigDecimal(1));
    }

    @MethodSource("params")
    @ParameterizedTest()
    public void dSParamsTConFAIL(final TestEitherModelOrDS variant) {
        initDMNDecisionServicesTypecheckDSxyTest(variant);
        final DMNContext context = DMNFactory.newContext();
        context.set("x", mapOf(entry("x", new BigDecimal(1))));

        final DMNResult dmnResult = fn.apply(runtime, dmnModel, context);

        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.getMessages()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isNotEmpty();
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getEvaluationStatus()).isNotEqualTo(DecisionEvaluationStatus.SUCCEEDED);
    }

    @MethodSource("params")
    @ParameterizedTest()
    public void dSParamsTCoff(final TestEitherModelOrDS variant) {
        initDMNDecisionServicesTypecheckDSxyTest(variant);
        // IMPORTANT !!
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("x", mapOf(entry("x", new BigDecimal(1))));

        final DMNResult dmnResult = fn.apply(runtime, dmnModel, context);

        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SUCCEEDED);
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult()).isEqualTo(new BigDecimal(1));
    }
}
