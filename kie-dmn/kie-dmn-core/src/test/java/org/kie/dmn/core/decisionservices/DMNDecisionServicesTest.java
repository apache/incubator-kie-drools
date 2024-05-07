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
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.CoerceDecisionServiceSingletonOutputOption;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DMNDecisionServicesTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServicesTest.class);

    @ParameterizedTest
    @MethodSource("params")
    void basic(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-decision-services.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        checkDSwithInputData(runtime, dmnModel);

        checkDSwithInputDecision(runtime, dmnModel);
        checkDSwithInputDecision2(runtime, dmnModel);
    }

    private void checkDSwithInputData(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "A only as output knowing D and E");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("A")).isEqualTo("de");
    }

    private void checkDSwithInputDecision(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "A Only Knowing B and C");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("A")).isNull(); // because B and C are not defined in input.
    }

    private void checkDSwithInputDecision2(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");
        context.set("B", "inB");
        context.set("C", "inC");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "A Only Knowing B and C");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("A")).isEqualTo("inBinC");
    }

    @ParameterizedTest
    @MethodSource("params")
    void dSInLiteralExpression(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpression.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS")).isEqualTo("xyde");
    }

    @ParameterizedTest
    @MethodSource("params")
    void dSInLiteralExpressionWithBKM(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionWithBKM.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS")).isEqualTo("xydemn");
    }

    @ParameterizedTest
    @MethodSource("params")
    void dSInLiteralExpressionWithBKMUsingInvocation(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionWithBKMUsingInvocation.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS")).isEqualTo("xydemn");
    }

    @ParameterizedTest
    @MethodSource("params")
    void dSInLiteralExpressionOnlyfromBKMUsingInvocation(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionOnlyFromBKMUsingInvocation.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS")).isEqualTo("demn");
    }

    @ParameterizedTest
    @MethodSource("params")
    void mixtypeDS(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("mixtype-DS.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_c9885563-aa54-4c7b-ae8a-738cfd29b544", "mixtype DS");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person year of birth", BigDecimal.valueOf(1980));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Greet the Person")).isEqualTo("Hello, John");
        assertThat(result.get("Person age")).isEqualTo(BigDecimal.valueOf(38));
        assertThat(result.get("is Person an adult")).isEqualTo(Boolean.TRUE);

        assertThat((Map<String, Object>) result.get("eval DS all")).containsEntry("Greet the Person", "Hello, ds all");
        assertThat((Map<String, Object>) result.get("eval DS all")).containsEntry("Person age", BigDecimal.valueOf(18));
        assertThat((Map<String, Object>) result.get("eval DS all")).containsEntry("is Person an adult", true);
        assertThat((Map<String, Object>) result.get("eval DS all")).doesNotContainKey("hardcoded now");

        assertThat((Map<String, Object>) result.get("eval DS encapsulate")).containsEntry("Greet the Person", "Hello, DS encapsulate");
        assertThat((Map<String, Object>) result.get("eval DS encapsulate")).doesNotContainKey("Person age");
        assertThat((Map<String, Object>) result.get("eval DS encapsulate")).containsEntry("is Person an adult", true);
        assertThat((Map<String, Object>) result.get("eval DS encapsulate")).doesNotContainKey("hardcoded now");

        assertThat((Map<String, Object>) result.get("eval DS greet adult")).containsEntry("Greet the Person", "Hello, DS greet adult");
        assertThat((Map<String, Object>) result.get("eval DS greet adult")).doesNotContainKey("Person age");
        assertThat((Map<String, Object>) result.get("eval DS greet adult")).containsEntry("is Person an adult", true);
        assertThat((Map<String, Object>) result.get("eval DS greet adult")).doesNotContainKey("hardcoded now");

        // additionally check DS one-by-one
        testMixtypeDS_checkDSall(runtime, dmnModel);
        testMixtypeDS_checkDSencapsulate(runtime, dmnModel);
        testMixtypeDS_checkDSgreetadult(runtime, dmnModel);
    }

    private void testMixtypeDS_checkDSall(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person year of birth", BigDecimal.valueOf(2008));

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS all");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll()).containsEntry("Greet the Person", "Hello, John");
        assertThat(result.getAll()).containsEntry("Person age", BigDecimal.valueOf(10));
        assertThat(result.getAll()).containsEntry("is Person an adult", false);
        assertThat(result.getAll()).doesNotContainKey("hardcoded now");
    }

    private void testMixtypeDS_checkDSencapsulate(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person year of birth", BigDecimal.valueOf(2008));

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS encapsulate");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll()).containsEntry("Greet the Person", "Hello, John");
        assertThat(result.getAll()).doesNotContainKey("Person age");
        assertThat(result.getAll()).containsEntry("is Person an adult", false);
        assertThat(result.getAll()).doesNotContainKey("hardcoded now");
    }

    private void testMixtypeDS_checkDSgreetadult(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person age", BigDecimal.valueOf(10));

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS greet adult");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll()).containsEntry("Greet the Person", "Hello, John");
        assertThat(dmnResult.getDecisionResultByName("Person age")).isNull();
        assertThat(result.getAll()).containsEntry("is Person an adult", false);
        assertThat(result.getAll()).doesNotContainKey("hardcoded now");
    }

    @ParameterizedTest
    @MethodSource("params")
    void dSForTypeCheck(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionService20180718.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_6eef3a7c-bb0d-40bb-858d-f9067789c18a", "Decision Service 20180718");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        testDSForTypeCheck_runNormal(runtime, dmnModel);
        testDSForTypeCheck_runAllDecisionsWithWrongTypes(runtime, dmnModel);
        testDSForTypeCheck_runDecisionService_Normal(runtime, dmnModel);
        testDSForTypeCheck_runDecisionService_WithWrongTypes(runtime, dmnModel);
    }

    private void testDSForTypeCheck_runNormal(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person age", BigDecimal.valueOf(21));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Greet the person")).isEqualTo("Hello, John");
        assertThat(result.get("is Person at age allowed")).isEqualTo(Boolean.TRUE);
        assertThat(result.get("Final Decision")).isEqualTo("Hello, John; you are allowed");
    }

    private void testDSForTypeCheck_runAllDecisionsWithWrongTypes(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", BigDecimal.valueOf(21));
        context.set("Person age", "John");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();
    }

    private void testDSForTypeCheck_runDecisionService_Normal(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person age", BigDecimal.valueOf(21));

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS given inputdata");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll()).doesNotContainKey("Greet the person"); // Decision Service will encapsulate this decision
        assertThat(result.getAll()).doesNotContainKey("is Person at age allowed"); // Decision Service will encapsulate this decision
        assertThat(result.get("Final Decision")).isEqualTo("Hello, John; you are allowed");
    }

    private void testDSForTypeCheck_runDecisionService_WithWrongTypes(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", BigDecimal.valueOf(21));
        context.set("Person age", "John");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS given inputdata");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.getMessages().stream().anyMatch(m -> m.getSourceId().equals("_cf49add9-84a4-40ac-8306-1eea599ff43c") && m.getLevel() == Level.WARNING))
        .as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();
    }

    @ParameterizedTest
    @MethodSource("params")
    void dSSingletonOrMultipleOutputDecisions(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decision-Services-singleton-or-multiple-output-decisions.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b4ebfbf2-8608-4297-9662-be70bab01974", "Decision Services singleton or multiple output decisions");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a Value")).isEqualTo("a string Value");
        assertThat(result.get("a String Value")).isEqualTo("a String Value");
        assertThat(result.get("a Number Value")).isEqualTo(BigDecimal.valueOf(47));
        assertThat(result.get("eval DS with singleton value")).isEqualTo("a string Value");
        assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions")).containsEntry("a String Value", "a String Value");
        assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions")).containsEntry("a Number Value", BigDecimal.valueOf(47));

        final DMNResult dmnResultDSSingleton = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with singleton value");
        LOG.debug("{}", dmnResultDSSingleton);
        dmnResultDSSingleton.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResultDSSingleton.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResultDSSingleton.getMessages())).isFalse();
        assertThat(dmnResultDSSingleton.getContext().get("a Value")).isEqualTo("a string Value");
        assertThat(dmnResultDSSingleton.getContext().getAll()).doesNotContainKey("a String Value"); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
        assertThat(dmnResultDSSingleton.getContext().getAll()).doesNotContainKey("a Number Value"); // Decision Service will not expose (nor encapsulate hence not execute) this decision.

        final DMNResult dmnResultMultiple = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with multiple output decisions");
        LOG.debug("{}", dmnResultMultiple);
        dmnResultMultiple.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResultMultiple.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResultMultiple.getMessages())).isFalse();
        assertThat(dmnResultMultiple.getContext().get("a String Value")).isEqualTo("a String Value");
        assertThat(dmnResultMultiple.getContext().get("a Number Value")).isEqualTo(BigDecimal.valueOf(47));
        assertThat(dmnResultMultiple.getContext().getAll()).doesNotContainKey("a Value"); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
    }

    @ParameterizedTest
    @MethodSource("params")
    void dSSingletonOrMultipleOutputDecisionsOVERRIDE(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        try {
            System.setProperty(CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME, "false");
            final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decision-Services-singleton-or-multiple-output-decisions.dmn", this.getClass());
            final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b4ebfbf2-8608-4297-9662-be70bab01974", "Decision Services singleton or multiple output decisions");
            assertThat(dmnModel).isNotNull();
            assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

            final DMNContext emptyContext = DMNFactory.newContext();

            final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
            LOG.debug("{}", dmnResult);
            dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
            assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

            final DMNContext result = dmnResult.getContext();
            assertThat(result.get("a Value")).isEqualTo("a string Value");
            assertThat(result.get("a String Value")).isEqualTo("a String Value");
            assertThat(result.get("a Number Value")).isEqualTo(BigDecimal.valueOf(47));
            assertThat((Map<String, Object>) result.get("eval DS with singleton value")).containsEntry("a Value", "a string Value"); // DIFFERENCE with base test
            assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions")).containsEntry("a String Value", "a String Value");
            assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions")).containsEntry("a Number Value", BigDecimal.valueOf(47));

            final DMNResult dmnResultDSSingleton = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with singleton value");
            LOG.debug("{}", dmnResultDSSingleton);
            dmnResultDSSingleton.getDecisionResults().forEach(x -> LOG.debug("{}", x));
            assertThat(dmnResultDSSingleton.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResultDSSingleton.getMessages())).isFalse();
            assertThat(dmnResultDSSingleton.getContext().get("a Value")).isEqualTo("a string Value");
            assertThat(dmnResultDSSingleton.getContext().getAll()).doesNotContainKey("a String Value"); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
            assertThat(dmnResultDSSingleton.getContext().getAll()).doesNotContainKey("a Number Value"); // Decision Service will not expose (nor encapsulate hence not execute) this decision.

            final DMNResult dmnResultMultiple = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with multiple output decisions");
            LOG.debug("{}", dmnResultMultiple);
            dmnResultMultiple.getDecisionResults().forEach(x -> LOG.debug("{}", x));
            assertThat(dmnResultMultiple.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResultMultiple.getMessages())).isFalse();
            assertThat(dmnResultMultiple.getContext().get("a String Value")).isEqualTo("a String Value");
            assertThat(dmnResultMultiple.getContext().get("a Number Value")).isEqualTo(BigDecimal.valueOf(47));
            assertThat(dmnResultMultiple.getContext().getAll()).doesNotContainKey("a Value"); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
        } catch (final Exception e) {
            LOG.error("{}", e.getLocalizedMessage(), e);
            throw e;
        } finally {
            System.clearProperty(CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME);
            assertThat(System.getProperty(CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME)).isNull();
        }
    }

    @ParameterizedTest
    @MethodSource("params")
    void importDS(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-2768 DMN Decision Service encapsulate Decision which imports a Decision Service
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("DecisionService20180718.dmn", this.getClass(), "ImportDecisionService20180718.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_0ff3708a-c861-4a96-b85c-7b882f18b7a1", "Import Decision Service 20180718");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        testImportDS_testEvaluateAll(runtime, dmnModel);
        testImportDS_testEvaluateDS(runtime, dmnModel);
    }

    private void testImportDS_testEvaluateAll(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("L1 person name", "L1 Import John");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("invoke imported DS")).isEqualTo("Hello, L1 Import John; you are allowed");
        assertThat(result.get("Prefixing")).isEqualTo("Hello, L1 Import John");
        assertThat(result.get("final Import L1 decision")).isEqualTo("Hello, L1 Import John the result of invoking the imported DS is: Hello, L1 Import John; you are allowed");
    }

    private void testImportDS_testEvaluateDS(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("L1 person name", "L1 Import Evaluate DS NAME");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "Import L1 DS");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll()).doesNotContainKey("invoke imported DS"); // Decision Service will encapsulate this decision
        assertThat(result.getAll()).doesNotContainKey("Prefixing"); // Decision Service will encapsulate this decision
        assertThat(result.get("final Import L1 decision")).isEqualTo("Hello, L1 Import Evaluate DS NAME the result of invoking the imported DS is: Hello, L1 Import Evaluate DS NAME; you are allowed");
    }

    @ParameterizedTest
    @MethodSource("params")
    void transitiveImportDS(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-2768 DMN Decision Service encapsulate Decision which imports a Decision Service   
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("DecisionService20180718.dmn", this.getClass(),
                                                                                       "ImportDecisionService20180718.dmn",
                                                                                       "ImportofImportDecisionService20180718.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_6698dc07-cc43-47ec-8187-8faa7d8c35ba", "Import of Import Decision Service 20180718");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        testTransitiveImportDS_testEvaluateAll(runtime, dmnModel);
        testTransitiveImportDS_testEvaluateDS(runtime, dmnModel);
    }

    private void testTransitiveImportDS_testEvaluateAll(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("L2 Person name", "L2 Bob");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("L2 Invoking the L1 import")).isEqualTo("Hello, L2 Bob the result of invoking the imported DS is: Hello, L2 Bob; you are allowed");
        assertThat(result.get("Final L2 Decision")).isEqualTo("The result of invoking the L1 DS was: Hello, L2 Bob the result of invoking the imported DS is: Hello, L2 Bob; you are allowed");
    }

    private void testTransitiveImportDS_testEvaluateDS(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("L2 Person name", "L2 Bob DS");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "L2 DS");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll()).doesNotContainKey("L2 Invoking the L1 import"); // Decision Service will encapsulate this decision
        assertThat(result.get("Final L2 Decision")).isEqualTo("The result of invoking the L1 DS was: Hello, L2 Bob DS the result of invoking the imported DS is: Hello, L2 Bob DS; you are allowed");
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionServiceCompiler20180830(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServiceABC.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_2443d3f5-f178-47c6-a0c9-b1fd1c933f60", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        testDecisionServiceCompiler20180830_testEvaluateDS(runtime, dmnModel);
        testDecisionServiceCompiler20180830_testEvaluateAll(runtime, dmnModel);
    }

    public static void testDecisionServiceCompiler20180830_testEvaluateAll(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("ABC")).isEqualTo("abc");
        assertThat(result.get("Invoking Decision")).isEqualTo("abc");
    }

    public static void testDecisionServiceCompiler20180830_testEvaluateDS(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "Decision Service ABC");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        // NOTE: Decision Service "Decision Service ABC" does NOT encapsulate any decision. 
        assertThat(result.getAll()).doesNotContainKey("Invoking Decision"); // we invoked only the Decision Service, not this other Decision in the model.
        assertThat(result.get("ABC")).isEqualTo("abc");
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionService20180920(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-3005 DMN DecisionService having an imported requiredInput
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("DSWithImportRequiredInput20180920.dmn",
                                                                                       this.getClass(),
                                                                                       "DSWithImportRequiredInput20180920-import-1.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_76165d7d-12f8-46d3-b8af-120f1ac8b3fc", "Model B");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        testDecisionService20180920_testEvaluateAll(runtime, dmnModel);
        testDecisionService20180920_testEvaluateDS(runtime, dmnModel);
    }

    private static void testDecisionService20180920_testEvaluateAll(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Model A", mapOf(entry("Input 1", "input 1 value")));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision B")).isEqualTo("input 1 value");
        assertThat(result.get("Invoke Decision B DS")).isEqualTo("A value");
    }

    private static void testDecisionService20180920_testEvaluateDS(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Model A", mapOf(entry("Input 1", "input 1 value")));

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "Decision B DS");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll()).doesNotContainKey("Invoke Decision B DS"); // we invoked only the Decision Service, not this other Decision in the model.
        assertThat(result.get("Decision B")).isEqualTo("input 1 value");
    }

    @ParameterizedTest
    @MethodSource("params")
    void test20190520(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DStypecheck.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6e76b9ca-ce06-426a-91c0-99b70665321a", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat((Map<String, Object>) dmnResult.getDecisionResultByName("my invoke DS1").getResult()).containsEntry("outDS1", true);
    }

    @ParameterizedTest
    @MethodSource("params")
    void importingBasic(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("0004-decision-services.dmn", this.getClass(), "importing0004.dmn");
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_346B2E00-71E5-4CEA-ADE1-7A0872481F38", "importing0004");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        assertSupplyingBandC(runtime, dmnModel);
    }

    private void assertSupplyingBandC(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("B", "inB");
        context.set("C", "inC");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision1")).isEqualTo("inBinC");
    }

    @ParameterizedTest
    @MethodSource("params")
    void importingBoxedInvocation(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("0004-decision-services.dmn", this.getClass(), "importing0004boxedInvocation.dmn");
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_346B2E00-71E5-4CEA-ADE1-7A0872481F38", "importing0004");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        assertSupplyingBandC(runtime, dmnModel);
    }

    @ParameterizedTest
    @MethodSource("params")
    void importingBkmBoxedInvocation(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("0004-decision-services.dmn", this.getClass(), "importing0004bkmBoxedInvocation.dmn");
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_0CA8CCDE-106B-4805-B0C1-8D8D740C80F7", "importing0004bkmBoxedInvocation");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        assertSupplyingBandC(runtime, dmnModel);
    }

    @ParameterizedTest
    @MethodSource("params")
    void importingWithSameDSName(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("myHelloDS.dmn", this.getClass(), "importingMyHelloDS.dmn");
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_3295867F-C02D-4312-849B-844F74C51ADE", "importingMyHelloDS");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        assertSupplyingPersonname(runtime, dmnModel);
    }

    private void assertSupplyingPersonname(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John Doe");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision1")).isEqualTo("Hello, John Doe");
    }

    @ParameterizedTest
    @MethodSource("params")
    void importingWithSameDSNameBoxedInvocation(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("myHelloDS.dmn", this.getClass(), "importingMyHelloDSboxedInvocation.dmn");
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_3295867F-C02D-4312-849B-844F74C51ADE", "importingMyHelloDS");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        assertSupplyingPersonname(runtime, dmnModel);
    }

    @ParameterizedTest
    @MethodSource("params")
    void importingWithSameDSNameBKMBoxedInvocation(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("myHelloDS.dmn", this.getClass(), "importingMyHelloDSbkmBoxedInvocation.dmn");
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_3295867F-C02D-4312-849B-844F74C51ADE", "importingMyHelloDS");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        assertSupplyingPersonname(runtime, dmnModel);
    }
}
