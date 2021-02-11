/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.classloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.drools.core.util.Drools;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.AfterEvaluateBKMEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.AfterInvokeBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateAllEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.BeforeInvokeBKMEvent;
import org.kie.dmn.api.core.event.DMNEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class DMNRuntimeListenerTest extends BaseInterpretedVsCompiledTest {

    public DMNRuntimeListenerTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeListenerTest.class);

    public static final Map<String, Object> TEST_METADATA = new HashMap<String, Object>() {{
        put("uuid", UUID.fromString("8ad1cbec-55f7-48aa-ae86-34f5e9bf33e8"));
        put("fieldName", "fieldValue");
    }};

    @Test
    public void testBasicListenerFromKModule() throws Exception {
        final String javaSource = "package com.acme;" +
                                  "" +
                                  "public class TestListener implements org.kie.dmn.api.core.event.DMNRuntimeEventListener {\n" +
                                  "\n" +
                                  "    private static final java.util.List<Object> decisionResults = new java.util.concurrent.CopyOnWriteArrayList<>();\n" +
                                  "\n" +
                                  "    @Override\n" +
                                  "    public void afterEvaluateDecision(org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent event) {\n" +
                                  "        org.kie.dmn.api.core.DMNDecisionResult decisionResult = event.getResult().getDecisionResultByName(event.getDecision().getName());\n" +
                                  "        if (decisionResult.getEvaluationStatus() == org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED) {\n" +
                                  "            System.out.println(decisionResult.getResult());\n" +
                                  "            decisionResults.add(decisionResult.getResult());\n" +
                                  "        }\n" +
                                  "    }\n" +
                                  "\n" +
                                  "    public java.util.List<Object> getResults() {\n" +
                                  "        return java.util.Collections.unmodifiableList(decisionResults);\n" +
                                  "    }\n" +
                                  "}";
        LOG.debug("javaSource:\n{}", javaSource);
        final KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0");

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/java/com/acme/TestListener.java", javaSource);
        kfs.write(ks.getResources().newClassPathResource("Greetings.dmn", this.getClass()));
        kfs.writeKModuleXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                            "  <configuration>\n" +
                            "    <property key=\"org.kie.dmn.runtime.listeners.X\" value=\"com.acme.TestListener\"/>\n" +
                            "  </configuration>\n" +
                            "</kmodule>");
        kfs.writePomXML(DMNClassloaderTest.getPom(releaseId,
                                                  ks.newReleaseId("org.kie", "kie-dmn-api", Drools.getFullVersion()),
                                                  ks.newReleaseId("org.kie", "kie-dmn-model", Drools.getFullVersion()),
                                                  ks.newReleaseId("org.kie.soup", "kie-soup-maven-support", Drools.getFullVersion()),
                                                  ks.newReleaseId("org.kie", "kie-api", Drools.getFullVersion()),
                                                  ks.newReleaseId("org.kie", "kie-internal", Drools.getFullVersion())
                                                  )
                        );
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertTrue(kieBuilder.getResults().getMessages().toString(), kieBuilder.getResults().getMessages().isEmpty());

        final KieContainer kieContainer = ks.newKieContainer(releaseId);

        final DMNRuntime runtime = DMNRuntimeUtil.typeSafeGetKieRuntime(kieContainer);
        Assert.assertNotNull(runtime);

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_2027051c-0030-40f1-8b96-1b1422f8b257", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = newEmptyContextWithTestMetadata();
        context.set("Name", "John Doe");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Greeting the Name"), is("Hello John Doe"));
        assertThat(result.getMetadata().asMap(), is(TEST_METADATA));

        Object listenerInstance = kieContainer.getClassLoader().loadClass("com.acme.TestListener").newInstance();
        @SuppressWarnings("unchecked") // this was by necessity classloaded
        List<Object> results = (List<Object>) listenerInstance.getClass().getMethod("getResults").invoke(listenerInstance);
        assertThat(results, contains("Hello John Doe"));
    }

    @Test
    public void testListenerWithBKM() throws Exception {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("org/kie/dmn/core/say_for_hello.dmn", this.getClass());
        TestEventListener listener = new TestEventListener();
        runtime.addListener(listener);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b6f2a9ca-a246-4f27-896a-e8ef04ea439c", "say for hello");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext emptyContext = newEmptyContextWithTestMetadata();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("just say"), is(Arrays.asList("Hello", "Hello", "Hello")));
        assertThat(result.getMetadata().asMap(), is(TEST_METADATA));

        List<DMNEvent> eventList = listener.getEventList();
        assertThat(eventList.get(0), instanceOf(BeforeEvaluateDecisionEvent.class));
        assertThat(eventList.get(0).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((BeforeEvaluateDecisionEvent) eventList.get(0)).getDecision().getName(), is("just say"));

        // Evaluate 2 BKMs
        assertThat(eventList.get(1), instanceOf(BeforeEvaluateBKMEvent.class));
        assertThat(eventList.get(1).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((BeforeEvaluateBKMEvent) eventList.get(1)).getBusinessKnowledgeModel().getName(), is("prefix say for hello"));
        assertThat(eventList.get(2), instanceOf(AfterEvaluateBKMEvent.class));
        assertThat(eventList.get(2).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((AfterEvaluateBKMEvent) eventList.get(2)).getBusinessKnowledgeModel().getName(), is("prefix say for hello"));
        assertThat(eventList.get(3), instanceOf(BeforeEvaluateBKMEvent.class));
        assertThat(eventList.get(3).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((BeforeEvaluateBKMEvent) eventList.get(3)).getBusinessKnowledgeModel().getName(), is("prefix aaa for hello"));
        assertThat(eventList.get(4), instanceOf(AfterEvaluateBKMEvent.class));
        assertThat(eventList.get(4).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((AfterEvaluateBKMEvent) eventList.get(4)).getBusinessKnowledgeModel().getName(), is("prefix aaa for hello"));

        // Invoke function 3 times
        assertThat(eventList.get(5), instanceOf(BeforeInvokeBKMEvent.class));
        assertThat(eventList.get(5).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((BeforeInvokeBKMEvent) eventList.get(5)).getBusinessKnowledgeModel().getName(), is("prefix say for hello"));
        assertThat(eventList.get(6), instanceOf(AfterInvokeBKMEvent.class));
        assertThat(eventList.get(6).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((AfterInvokeBKMEvent) eventList.get(6)).getBusinessKnowledgeModel().getName(), is("prefix say for hello"));
        assertThat(eventList.get(7), instanceOf(BeforeInvokeBKMEvent.class));
        assertThat(eventList.get(7).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((BeforeInvokeBKMEvent) eventList.get(7)).getBusinessKnowledgeModel().getName(), is("prefix say for hello"));
        assertThat(eventList.get(8), instanceOf(AfterInvokeBKMEvent.class));
        assertThat(eventList.get(8).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((AfterInvokeBKMEvent) eventList.get(8)).getBusinessKnowledgeModel().getName(), is("prefix say for hello"));
        assertThat(eventList.get(9), instanceOf(BeforeInvokeBKMEvent.class));
        assertThat(eventList.get(9).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((BeforeInvokeBKMEvent) eventList.get(9)).getBusinessKnowledgeModel().getName(), is("prefix say for hello"));
        assertThat(eventList.get(10), instanceOf(AfterInvokeBKMEvent.class));
        assertThat(eventList.get(10).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((AfterInvokeBKMEvent) eventList.get(10)).getBusinessKnowledgeModel().getName(), is("prefix say for hello"));

        assertThat(eventList.get(11), instanceOf(AfterEvaluateDecisionEvent.class));
        assertThat(eventList.get(11).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((AfterEvaluateDecisionEvent) eventList.get(11)).getDecision().getName(), is("just say"));
    }

    @Test
    public void testListenerWithDecisionService() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("org/kie/dmn/core/decisionservices/DecisionServiceABC.dmn", this.getClass());
        TestEventListener listener = new TestEventListener();
        runtime.addListener(listener);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_2443d3f5-f178-47c6-a0c9-b1fd1c933f60", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = newEmptyContextWithTestMetadata();

        final DMNResult dmnResult = runtime.evaluateByName(dmnModel, context, "Invoking Decision");

        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Invoking Decision"), is("abc"));

        List<DMNEvent> eventList = listener.getEventList();

        assertThat(eventList.get(0), instanceOf(BeforeEvaluateDecisionEvent.class));
        assertThat(eventList.get(0).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((BeforeEvaluateDecisionEvent) eventList.get(0)).getDecision().getName(), is("Invoking Decision"));

        // Evaluate DecisionService
        assertThat(eventList.get(1), instanceOf(BeforeEvaluateDecisionServiceEvent.class));
        assertThat(eventList.get(1).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((BeforeEvaluateDecisionServiceEvent) eventList.get(1)).getDecisionService().getName(), is("Decision Service ABC"));

        // Evaluate internal Decision
        assertThat(eventList.get(2), instanceOf(BeforeEvaluateDecisionEvent.class));
        assertThat(eventList.get(2).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((BeforeEvaluateDecisionEvent) eventList.get(2)).getDecision().getName(), is("ABC"));
        assertThat(eventList.get(3), instanceOf(AfterEvaluateDecisionEvent.class));
        assertThat(eventList.get(3).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((AfterEvaluateDecisionEvent) eventList.get(3)).getDecision().getName(), is("ABC"));

        assertThat(eventList.get(4), instanceOf(AfterEvaluateDecisionServiceEvent.class));
        assertThat(eventList.get(4).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((AfterEvaluateDecisionServiceEvent) eventList.get(4)).getDecisionService().getName(), is("Decision Service ABC"));

        assertThat(eventList.get(5), instanceOf(AfterEvaluateDecisionEvent.class));
        assertThat(eventList.get(5).getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
        assertThat(((AfterEvaluateDecisionEvent) eventList.get(5)).getDecision().getName(), is("Invoking Decision"));
    }

    @Test
    public void testBeforeAndAfterEvaluateAllEvents() {
        final String modelResource = "org/kie/dmn/core/say_for_hello.dmn";
        final String modelNamespace = "http://www.trisotech.com/dmn/definitions/_b6f2a9ca-a246-4f27-896a-e8ef04ea439c";
        final String modelName = "say for hello";

        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(modelResource, this.getClass());

        TestBeforeAndAfterEvaluateAllEventListener listener = new TestBeforeAndAfterEvaluateAllEventListener();
        runtime.addListener(listener);

        final DMNModel dmnModel = runtime.getModel(modelNamespace, modelName);
        final DMNContext emptyContext = newEmptyContextWithTestMetadata();
        runtime.evaluateAll(dmnModel, emptyContext);

        assertNotNull(listener.beforeEvent);
        assertEquals(listener.beforeEvent.getModelNamespace(), modelNamespace);
        assertEquals(listener.beforeEvent.getModelName(), modelName);
        assertNotNull(listener.beforeEvent.getResult());
        assertThat(listener.beforeEvent.getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));

        assertNotNull(listener.afterEvent);
        assertEquals(listener.afterEvent.getModelNamespace(), modelNamespace);
        assertEquals(listener.afterEvent.getModelName(), modelName);
        assertNotNull(listener.afterEvent.getResult());
        assertThat(listener.afterEvent.getResult().getContext().getMetadata().asMap(), is(TEST_METADATA));
    }

    private static DMNContext newEmptyContextWithTestMetadata() {
        DMNContext ctx = DMNFactory.newContext();
        TEST_METADATA.forEach(ctx.getMetadata()::set);
        return ctx;
    }

    static class TestEventListener implements DMNRuntimeEventListener {

        private List<DMNEvent> eventList = new ArrayList<>();

        public List<DMNEvent> getEventList() {
            return eventList;
        }

        @Override
        public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
            eventList.add(event);
        }

        @Override
        public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
            eventList.add(event);
        }

        @Override
        public void beforeEvaluateBKM(BeforeEvaluateBKMEvent event) {
            eventList.add(event);
        }

        @Override
        public void afterEvaluateBKM(AfterEvaluateBKMEvent event) {
            eventList.add(event);
        }

        @Override
        public void beforeEvaluateDecisionService(BeforeEvaluateDecisionServiceEvent event) {
            eventList.add(event);
        }

        @Override
        public void afterEvaluateDecisionService(AfterEvaluateDecisionServiceEvent event) {
            eventList.add(event);
        }

        @Override
        public void beforeInvokeBKM(BeforeInvokeBKMEvent event) {
            eventList.add(event);
        }

        @Override
        public void afterInvokeBKM(AfterInvokeBKMEvent event) {
            eventList.add(event);
        }
    }

    static class TestBeforeAndAfterEvaluateAllEventListener implements DMNRuntimeEventListener {
        BeforeEvaluateAllEvent beforeEvent = null;
        AfterEvaluateAllEvent afterEvent = null;

        @Override
        public void beforeEvaluateAll(BeforeEvaluateAllEvent event) {
            if (beforeEvent != null) {
                throw new IllegalStateException("BeforeEvaluateAllEvent already fired");
            }
            beforeEvent = event;
        }

        @Override
        public void afterEvaluateAll(AfterEvaluateAllEvent event) {
            if (afterEvent != null) {
                throw new IllegalStateException("AfterEvaluateAllEvent already fired");
            }
            afterEvent = event;
        }

    }
}
