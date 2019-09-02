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

import java.util.List;
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
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DMNRuntimeListenerTest extends BaseInterpretedVsCompiledTest {

    public DMNRuntimeListenerTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeListenerTest.class);

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

        final DMNContext context = DMNFactory.newContext();
        context.set("Name", "John Doe");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Greeting the Name"), is("Hello John Doe"));

        Object listenerInstance = kieContainer.getClassLoader().loadClass("com.acme.TestListener").newInstance();
        @SuppressWarnings("unchecked") // this was by necessity classloaded
        List<Object> results = (List<Object>) listenerInstance.getClass().getMethod("getResults").invoke(listenerInstance);
        assertThat(results, contains("Hello John Doe"));
    }

}

