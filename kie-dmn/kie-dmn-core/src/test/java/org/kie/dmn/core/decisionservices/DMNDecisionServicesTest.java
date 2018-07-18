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
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.CoerceDecisionServiceSingletonOutputOption;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class DMNDecisionServicesTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServicesTest.class);

    @Test
    public void testBasic() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-decision-services.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        checkDSwithInputData(runtime, dmnModel);

        checkDSwithInputDecision(runtime, dmnModel);
        checkDSwithInputDecision2(runtime, dmnModel);
    }

    private void checkDSwithInputData(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "A only as output knowing D and E");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.get("A"), is("de"));
    }

    private void checkDSwithInputDecision(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "A Only Knowing B and C");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.get("A"), nullValue()); // because B and C are not defined in input.
    }

    private void checkDSwithInputDecision2(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");
        context.set("B", "inB");
        context.set("C", "inC");

        DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "A Only Knowing B and C");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.get("A"), is("inBinC"));
    }

    @Test
    public void testDSInLiteralExpression() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpression.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS"), is("xyde"));
    }

    @Test
    public void testDSInLiteralExpressionWithBKM() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionWithBKM.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS"), is("xydemn"));
    }

    @Test
    public void testDSInLiteralExpressionWithBKMUsingInvocation() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionWithBKMUsingInvocation.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS"), is("xydemn"));
    }

    @Test
    public void testDSInLiteralExpressionOnlyfromBKMUsingInvocation() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServicesInLiteralExpressionOnlyFromBKMUsingInvocation.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNContext context = DMNFactory.newContext();
        context.set("D", "d");
        context.set("E", "e");

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide based on A and DS"), is("demn"));
    }

    @Test
    public void testMixtypeDS() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("mixtype-DS.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_c9885563-aa54-4c7b-ae8a-738cfd29b544", "mixtype DS");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person year of birth", BigDecimal.valueOf(1980));

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
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

    private void testMixtypeDS_checkDSall(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person year of birth", BigDecimal.valueOf(2008));

        DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS all");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), hasEntry(is("Greet the Person"), is("Hello, John")));
        assertThat(result.getAll(), hasEntry(is("Person age"), is(BigDecimal.valueOf(10))));
        assertThat(result.getAll(), hasEntry(is("is Person an adult"), is(false)));
        assertThat(result.getAll(), not(hasEntry(is("hardcoded now"), anything())));
    }

    private void testMixtypeDS_checkDSencapsulate(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person year of birth", BigDecimal.valueOf(2008));

        DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS encapsulate");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), hasEntry(is("Greet the Person"), is("Hello, John")));
        assertThat(result.getAll(), not(hasEntry(is("Person age"), anything())));
        assertThat(result.getAll(), hasEntry(is("is Person an adult"), is(false)));
        assertThat(result.getAll(), not(hasEntry(is("hardcoded now"), anything())));
    }

    private void testMixtypeDS_checkDSgreetadult(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person age", BigDecimal.valueOf(10));

        DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS greet adult");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), hasEntry(is("Greet the Person"), is("Hello, John")));
        assertThat(dmnResult.getDecisionResultByName("Person age"), nullValue());
        assertThat(result.getAll(), hasEntry(is("is Person an adult"), is(false)));
        assertThat(result.getAll(), not(hasEntry(is("hardcoded now"), anything())));
    }

    @Test
    public void testDSForTypeCheck() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionService20180718.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_6eef3a7c-bb0d-40bb-858d-f9067789c18a", "Decision Service 20180718");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        testDSForTypeCheck_runNormal(runtime, dmnModel);
        testDSForTypeCheck_runAllDecisionsWithWrongTypes(runtime, dmnModel);
        testDSForTypeCheck_runDecisionService_Normal(runtime, dmnModel);
        testDSForTypeCheck_runDecisionService_WithWrongTypes(runtime, dmnModel);
    }

    private void testDSForTypeCheck_runNormal(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person age", BigDecimal.valueOf(21));

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.get("Greet the person"), is("Hello, John"));
        assertThat(result.get("is Person at age allowed"), is(true));
        assertThat(result.get("Final Decision"), is("Hello, John; you are allowed"));
    }

    private void testDSForTypeCheck_runAllDecisionsWithWrongTypes(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("Person name", BigDecimal.valueOf(21));
        context.set("Person age", "John");

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(true));
    }

    private void testDSForTypeCheck_runDecisionService_Normal(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("Person name", "John");
        context.set("Person age", BigDecimal.valueOf(21));

        DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS given inputdata");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), not(hasEntry(is("Greet the person"), anything()))); // Decision Service will encapsulate this decision
        assertThat(result.getAll(), not(hasEntry(is("is Person at age allowed"), anything()))); // Decision Service will encapsulate this decision
        assertThat(result.get("Final Decision"), is("Hello, John; you are allowed"));
    }

    private void testDSForTypeCheck_runDecisionService_WithWrongTypes(DMNRuntime runtime, DMNModel dmnModel) {
        DMNContext context = DMNFactory.newContext();
        context.set("Person name", BigDecimal.valueOf(21));
        context.set("Person age", "John");

        DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "DS given inputdata");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(true));
    }

    @Test
    public void testDSSingletonOrMultipleOutputDecisions() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decision-Services-singleton-or-multiple-output-decisions.dmn", this.getClass());
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b4ebfbf2-8608-4297-9662-be70bab01974", "Decision Services singleton or multiple output decisions");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNContext emptyContext = DMNFactory.newContext();

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.get("a Value"), is("a string Value"));
        assertThat(result.get("a String Value"), is("a String Value"));
        assertThat(result.get("a Number Value"), is(BigDecimal.valueOf(47)));
        assertThat(result.get("eval DS with singleton value"), is("a string Value"));
        assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions"), hasEntry(is("a String Value"), is("a String Value")));
        assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions"), hasEntry(is("a Number Value"), is(BigDecimal.valueOf(47))));

        DMNResult dmnResultDSSingleton = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with singleton value");
        LOG.debug("{}", dmnResultDSSingleton);
        dmnResultDSSingleton.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResultDSSingleton.getMessages()), dmnResultDSSingleton.hasErrors(), is(false));
        assertThat(dmnResultDSSingleton.getContext().get("a Value"), is("a string Value"));
        assertThat(dmnResultDSSingleton.getContext().getAll(), not(hasEntry(is("a String Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
        assertThat(dmnResultDSSingleton.getContext().getAll(), not(hasEntry(is("a Number Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.

        DMNResult dmnResultMultiple = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with multiple output decisions");
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
            DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decision-Services-singleton-or-multiple-output-decisions.dmn", this.getClass());
            DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b4ebfbf2-8608-4297-9662-be70bab01974", "Decision Services singleton or multiple output decisions");
            assertThat(dmnModel, notNullValue());
            assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

            DMNContext emptyContext = DMNFactory.newContext();

            DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
            LOG.debug("{}", dmnResult);
            dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
            assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

            DMNContext result = dmnResult.getContext();
            assertThat(result.get("a Value"), is("a string Value"));
            assertThat(result.get("a String Value"), is("a String Value"));
            assertThat(result.get("a Number Value"), is(BigDecimal.valueOf(47)));
            assertThat((Map<String, Object>) result.get("eval DS with singleton value"), hasEntry(is("a Value"), is("a string Value"))); // DIFFERENCE with base test
            assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions"), hasEntry(is("a String Value"), is("a String Value")));
            assertThat((Map<String, Object>) result.get("eval DS with multiple output decisions"), hasEntry(is("a Number Value"), is(BigDecimal.valueOf(47))));

            DMNResult dmnResultDSSingleton = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with singleton value");
            LOG.debug("{}", dmnResultDSSingleton);
            dmnResultDSSingleton.getDecisionResults().forEach(x -> LOG.debug("{}", x));
            assertThat(DMNRuntimeUtil.formatMessages(dmnResultDSSingleton.getMessages()), dmnResultDSSingleton.hasErrors(), is(false));
            assertThat(dmnResultDSSingleton.getContext().get("a Value"), is("a string Value"));
            assertThat(dmnResultDSSingleton.getContext().getAll(), not(hasEntry(is("a String Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
            assertThat(dmnResultDSSingleton.getContext().getAll(), not(hasEntry(is("a Number Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.

            DMNResult dmnResultMultiple = runtime.evaluateDecisionService(dmnModel, emptyContext, "DS with multiple output decisions");
            LOG.debug("{}", dmnResultMultiple);
            dmnResultMultiple.getDecisionResults().forEach(x -> LOG.debug("{}", x));
            assertThat(DMNRuntimeUtil.formatMessages(dmnResultMultiple.getMessages()), dmnResultMultiple.hasErrors(), is(false));
            assertThat(dmnResultMultiple.getContext().get("a String Value"), is("a String Value"));
            assertThat(dmnResultMultiple.getContext().get("a Number Value"), is(BigDecimal.valueOf(47)));
            assertThat(dmnResultMultiple.getContext().getAll(), not(hasEntry(is("a Value"), anything()))); // Decision Service will not expose (nor encapsulate hence not execute) this decision.
        } catch (Exception e) {
            LOG.error("{}", e.getLocalizedMessage(), e);
            throw e;
        } finally {
            System.clearProperty(CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME);
            assertNull(System.getProperty(CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME));
        }
    }
}
