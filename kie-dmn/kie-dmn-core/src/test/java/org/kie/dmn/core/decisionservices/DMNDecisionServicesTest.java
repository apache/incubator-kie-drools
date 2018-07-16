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
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
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

        DMNResult dmnResult = ((DMNRuntimeImpl) runtime).evaluateDecisionService(dmnModel, context, "A only as output knowing D and E");
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

        DMNResult dmnResult = ((DMNRuntimeImpl) runtime).evaluateDecisionService(dmnModel, context, "A Only Knowing B and C");
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

        DMNResult dmnResult = ((DMNRuntimeImpl) runtime).evaluateDecisionService(dmnModel, context, "A Only Knowing B and C");
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

        DMNResult dmnResult = ((DMNRuntimeImpl) runtime).evaluateDecisionService(dmnModel, context, "DS all");
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

        DMNResult dmnResult = ((DMNRuntimeImpl) runtime).evaluateDecisionService(dmnModel, context, "DS encapsulate");
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

        DMNResult dmnResult = ((DMNRuntimeImpl) runtime).evaluateDecisionService(dmnModel, context, "DS greet adult");
        LOG.debug("{}", dmnResult);
        dmnResult.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        DMNContext result = dmnResult.getContext();
        assertThat(result.getAll(), hasEntry(is("Greet the Person"), is("Hello, John")));
        assertThat(dmnResult.getDecisionResultByName("Person age"), nullValue());
        assertThat(result.getAll(), hasEntry(is("is Person an adult"), is(false)));
        assertThat(result.getAll(), not(hasEntry(is("hardcoded now"), anything())));
    }
}
