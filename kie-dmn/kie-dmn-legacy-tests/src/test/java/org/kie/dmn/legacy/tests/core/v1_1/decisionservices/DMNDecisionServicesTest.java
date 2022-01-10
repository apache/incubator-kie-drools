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

package org.kie.dmn.legacy.tests.core.v1_1.decisionservices;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.CoerceDecisionServiceSingletonOutputOption;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.legacy.tests.core.v1_1.BaseDMN1_1VariantTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

public class DMNDecisionServicesTest extends BaseDMN1_1VariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServicesTest.class);

    public DMNDecisionServicesTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Test
    public void testBasic() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-decision-services.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

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
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("A"), is("de"));
    }

    private void checkDSwithInputDecision(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "A Only Knowing B and C");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("A"), nullValue()); // because B and C are not defined in input.
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
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("A"), is("inBinC"));
    }

    @Test
    public void testDSInLiteralExpression() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpression.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS"), is("xyde"));
    }

    @Test
    public void testDSInLiteralExpressionWithBKM() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionWithBKM.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS"), is("xydemn"));
    }

    @Test
    public void testDSInLiteralExpressionWithBKMUsingInvocation() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionWithBKMUsingInvocation.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS"), is("xydemn"));
    }

    @Test
    public void testDSInLiteralExpressionOnlyfromBKMUsingInvocation() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionOnlyFromBKMUsingInvocation.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS"), is("demn"));
    }

    @Test
    public void testMixtypeDS() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("mixtype-DS.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_c9885563-aa54-4c7b-ae8a-738cfd29b544", "mixtype DS");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person year of birth", BigDecimal.valueOf(1980));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Greet the Person"), is("Hello, John"));
        assertThat(result.get("Person age"), is(BigDecimal.valueOf(38)));
        assertThat(result.get("is Person an adult"), is(true));

        assertThat((Map<String, Object>) result.get("eval DS all"), hasEntry(is("Greet the Person"), is("Hello, ds all")));
        assertThat((Map<String, Object>) result.get("eval DS all"), hasEntry(is("Person age"), is(BigDecimal.valueOf(18))));
        assertThat((Map<String, Object>) result.get("eval DS all"), hasEntry(is("is Person an adult"), is(true)));
        assertThat((Map<String, Object>) result.get("eval DS all"), not(hasEntry(is("hardcoded now"), anything())));

        assertThat((Map<String, Object>) result.get("eval DS encapsulate"), hasEntry(is("Greet the Person"), is("Hello, DS encapsulate")));
        assertThat((Map<String, Object>) result.get("eval DS encapsulate"), not(hasEntry(is("Person age"), anything())));
        assertThat((Map<String, Object>) result.get("eval DS encapsulate"), hasEntry(is("is Person an adult"), is(true)));
        assertThat((Map<String, Object>) result.get("eval DS encapsulate"), not(hasEntry(is("hardcoded now"), anything())));

        assertThat((Map<String, Object>) result.get("eval DS greet adult"), hasEntry(is("Greet the Person"), is("Hello, DS greet adult")));
        assertThat((Map<String, Object>) result.get("eval DS greet adult"), not(hasEntry(is("Person age"), anything())));
        assertThat((Map<String, Object>) result.get("eval DS greet adult"), hasEntry(is("is Person an adult"), is(true)));
        assertThat((Map<String, Object>) result.get("eval DS greet adult"), not(hasEntry(is("hardcoded now"), anything())));

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
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), hasEntry(is("Greet the Person"), is("Hello, John")));
        assertThat(result.getAll(), hasEntry(is("Person age"), is(BigDecimal.valueOf(10))));
        assertThat(result.getAll(), hasEntry(is("is Person an adult"), is(false)));
        assertThat(result.getAll(), not(hasEntry(is("hardcoded now"), anything())));
    }

    private void testMixtypeDS_checkDSencapsulate(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person year of birth", BigDecimal.valueOf(2008));

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS encapsulate");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), hasEntry(is("Greet the Person"), is("Hello, John")));
        assertThat(result.getAll(), not(hasEntry(is("Person age"), anything())));
        assertThat(result.getAll(), hasEntry(is("is Person an adult"), is(false)));
        assertThat(result.getAll(), not(hasEntry(is("hardcoded now"), anything())));
    }

    private void testMixtypeDS_checkDSgreetadult(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person age", BigDecimal.valueOf(10));

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS greet adult");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), hasEntry(is("Greet the Person"), is("Hello, John")));
        assertThat(dmnResult.getDecisionResultByName("Person age"), nullValue());
        assertThat(result.getAll(), hasEntry(is("is Person an adult"), is(false)));
        assertThat(result.getAll(), not(hasEntry(is("hardcoded now"), anything())));
    }

    @Test
    public void testDSForTypeCheck() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionService20180718.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_6eef3a7c-bb0d-40bb-858d-f9067789c18a", "Decision Service 20180718");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

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
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Greet the person"), is("Hello, John"));
        assertThat(result.get("is Person at age allowed"), is(true));
        assertThat(result.get("Final Decision"), is("Hello, John; you are allowed"));
    }

    private void testDSForTypeCheck_runAllDecisionsWithWrongTypes(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", BigDecimal.valueOf(21));
        context.set("Person age", "John");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(true));
    }

    private void testDSForTypeCheck_runDecisionService_Normal(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person age", BigDecimal.valueOf(21));

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS given inputdata");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), not(hasEntry(is("Greet the person"), anything()))); // Decision Service will encapsulate this decision
        assertThat(result.getAll(), not(hasEntry(is("is Person at age allowed"), anything()))); // Decision Service will encapsulate this decision
        assertThat(result.get("Final Decision"), is("Hello, John; you are allowed"));
    }

    private void testDSForTypeCheck_runDecisionService_WithWrongTypes(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Person name", BigDecimal.valueOf(21));
        context.set("Person age", "John");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS given inputdata");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()),
                   dmnResult.getMessages().stream().anyMatch(m -> m.getSourceId().equals("_cf49add9-84a4-40ac-8306-1eea599ff43c") && m.getLevel() == Level.WARNING),
                   is(true));
    }

    @Test
    public void testDSSingletonOrMultipleOutputDecisions() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decision-Services-singleton-or-multiple-output-decisions.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b4ebfbf2-8608-4297-9662-be70bab01974", "Decision Services singleton or multiple output decisions");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a Value"), is("a string Value"));
        assertThat(result.get("a String Value"), is("a String Value"));
        assertThat(result.get("a Number Value"), is(BigDecimal.valueOf(47)));
        assertThat(result.get("eval DS with singleton value"), is("a string Value"));
        assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions"), hasEntry(is("a String Value"), is("a String Value")));
        assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions"), hasEntry(is("a Number Value"), is(BigDecimal.valueOf(47))));

        final DMNResult dmnResultDSSingleton = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with singleton value");
        LOG.debug("{}", dmnResultDSSingleton);
        dmnResultDSSingleton.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResultDSSingleton.getMessages()), dmnResultDSSingleton.hasErrors(), is(false));
        assertThat(dmnResultDSSingleton.getContext().get("a Value"), is("a string Value"));
        assertThat(dmnResultDSSingleton.getContext().getAll(), not(hasEntry(is("a String Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
        assertThat(dmnResultDSSingleton.getContext().getAll(), not(hasEntry(is("a Number Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.

        final DMNResult dmnResultMultiple = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with multiple output decisions");
        LOG.debug("{}", dmnResultMultiple);
        dmnResultMultiple.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResultMultiple.getMessages()), dmnResultMultiple.hasErrors(), is(false));
        assertThat(dmnResultMultiple.getContext().get("a String Value"), is("a String Value"));
        assertThat(dmnResultMultiple.getContext().get("a Number Value"), is(BigDecimal.valueOf(47)));
        assertThat(dmnResultMultiple.getContext().getAll(), not(hasEntry(is("a Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
    }

    @Test
    public void testDSSingletonOrMultipleOutputDecisions_OVERRIDE() {
        try {
            System.setProperty(CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME, "false");
            final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decision-Services-singleton-or-multiple-output-decisions.dmn", this.getClass());
            final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b4ebfbf2-8608-4297-9662-be70bab01974", "Decision Services singleton or multiple output decisions");
            assertThat(dmnModel, notNullValue());
            assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

            final DMNContext emptyContext = DMNFactory.newContext();

            final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
            LOG.debug("{}", dmnResult);
            dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
            assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

            final DMNContext result = dmnResult.getContext();
            assertThat(result.get("a Value"), is("a string Value"));
            assertThat(result.get("a String Value"), is("a String Value"));
            assertThat(result.get("a Number Value"), is(BigDecimal.valueOf(47)));
            assertThat((Map<String, Object>) result.get("eval DS with singleton value"), hasEntry(is("a Value"), is("a string Value"))); // DIFFERENCE with base test
            assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions"), hasEntry(is("a String Value"), is("a String Value")));
            assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions"), hasEntry(is("a Number Value"), is(BigDecimal.valueOf(47))));

            final DMNResult dmnResultDSSingleton = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with singleton value");
            LOG.debug("{}", dmnResultDSSingleton);
            dmnResultDSSingleton.getDecisionResults().forEach(x -> LOG.debug("{}", x));
            assertThat(DMNRuntimeUtil.formatMessages(dmnResultDSSingleton.getMessages()), dmnResultDSSingleton.hasErrors(), is(false));
            assertThat(dmnResultDSSingleton.getContext().get("a Value"), is("a string Value"));
            assertThat(dmnResultDSSingleton.getContext().getAll(), not(hasEntry(is("a String Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
            assertThat(dmnResultDSSingleton.getContext().getAll(), not(hasEntry(is("a Number Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.

            final DMNResult dmnResultMultiple = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with multiple output decisions");
            LOG.debug("{}", dmnResultMultiple);
            dmnResultMultiple.getDecisionResults().forEach(x -> LOG.debug("{}", x));
            assertThat(DMNRuntimeUtil.formatMessages(dmnResultMultiple.getMessages()), dmnResultMultiple.hasErrors(), is(false));
            assertThat(dmnResultMultiple.getContext().get("a String Value"), is("a String Value"));
            assertThat(dmnResultMultiple.getContext().get("a Number Value"), is(BigDecimal.valueOf(47)));
            assertThat(dmnResultMultiple.getContext().getAll(), not(hasEntry(is("a Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
        } catch (final Exception e) {
            LOG.error("{}", e.getLocalizedMessage(), e);
            throw e;
        } finally {
            System.clearProperty(CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME);
            assertNull(System.getProperty(CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME));
        }
    }

    @Test
    public void testImportDS() {
        // DROOLS-2768 DMN Decision Service encapsulate Decision which imports a Decision Service
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("DecisionService20180718.dmn", this.getClass(), "ImportDecisionService20180718.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_0ff3708a-c861-4a96-b85c-7b882f18b7a1", "Import Decision Service 20180718");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        testImportDS_testEvaluateAll(runtime, dmnModel);
        testImportDS_testEvaluateDS(runtime, dmnModel);
    }

    private void testImportDS_testEvaluateAll(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("L1 person name", "L1 Import John");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("invoke imported DS"), is("Hello, L1 Import John; you are allowed"));
        assertThat(result.get("Prefixing"), is("Hello, L1 Import John"));
        assertThat(result.get("final Import L1 decision"), is("Hello, L1 Import John the result of invoking the imported DS is: Hello, L1 Import John; you are allowed"));
    }

    private void testImportDS_testEvaluateDS(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("L1 person name", "L1 Import Evaluate DS NAME");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "Import L1 DS");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), not(hasEntry(is("invoke imported DS"), anything()))); // Decision Service will encapsulate this decision
        assertThat(result.getAll(), not(hasEntry(is("Prefixing"), anything()))); // Decision Service will encapsulate this decision
        assertThat(result.get("final Import L1 decision"), is("Hello, L1 Import Evaluate DS NAME the result of invoking the imported DS is: Hello, L1 Import Evaluate DS NAME; you are allowed"));
    }

    @Test
    public void testTransitiveImportDS() {
        // DROOLS-2768 DMN Decision Service encapsulate Decision which imports a Decision Service   
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("DecisionService20180718.dmn", this.getClass(),
                                                                                       "ImportDecisionService20180718.dmn",
                                                                                       "ImportofImportDecisionService20180718.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_6698dc07-cc43-47ec-8187-8faa7d8c35ba", "Import of Import Decision Service 20180718");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        testTransitiveImportDS_testEvaluateAll(runtime, dmnModel);
        testTransitiveImportDS_testEvaluateDS(runtime, dmnModel);
    }

    private void testTransitiveImportDS_testEvaluateAll(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("L2 Person name", "L2 Bob");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("L2 Invoking the L1 import"), is("Hello, L2 Bob the result of invoking the imported DS is: Hello, L2 Bob; you are allowed"));
        assertThat(result.get("Final L2 Decision"), is("The result of invoking the L1 DS was: Hello, L2 Bob the result of invoking the imported DS is: Hello, L2 Bob; you are allowed"));
    }

    private void testTransitiveImportDS_testEvaluateDS(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();
        context.set("L2 Person name", "L2 Bob DS");

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "L2 DS");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), not(hasEntry(is("L2 Invoking the L1 import"), anything()))); // Decision Service will encapsulate this decision
        assertThat(result.get("Final L2 Decision"), is("The result of invoking the L1 DS was: Hello, L2 Bob DS the result of invoking the imported DS is: Hello, L2 Bob DS; you are allowed"));
    }

    @Test
    public void testDecisionServiceCompiler20180830() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServiceABC.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_2443d3f5-f178-47c6-a0c9-b1fd1c933f60", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        testDecisionServiceCompiler20180830_testEvaluateDS(runtime, dmnModel);
        testDecisionServiceCompiler20180830_testEvaluateAll(runtime, dmnModel);
    }

    public static void testDecisionServiceCompiler20180830_testEvaluateAll(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("ABC"), is("abc"));
        assertThat(result.get("Invoking Decision"), is("abc"));
    }

    public static void testDecisionServiceCompiler20180830_testEvaluateDS(final DMNRuntime runtime, final DMNModel dmnModel) {
        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "Decision Service ABC");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        // NOTE: Decision Service "Decision Service ABC" does NOT encapsulate any decision. 
        assertThat(result.getAll(), not(hasEntry(is("Invoking Decision"), anything()))); // we invoked only the Decision Service, not this other Decision in the model.
        assertThat(result.get("ABC"), is("abc"));
    }
}
