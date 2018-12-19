/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.fluent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.RequestContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.core.util.KieHelper;
import org.kie.internal.builder.fluent.ExecutableBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DMNRuntimeFluentTest {

    @Test
    public void testFluentApi() {

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0");
        KieHelper.getKieContainer(releaseId,
                                  ks.getResources().newClassPathResource("org/kie/dmn/core/typecheck_in_DT.dmn", this.getClass()));

        ExecutableBuilder builder = ExecutableBuilder.create()
                .getKieContainer(releaseId)
                .newDMNRuntime()
                .setActiveModel("http://www.trisotech.com/definitions/_99ccd4df-41ac-43c3-a563-d58f43149829", "typecheck in DT")
                .setInput("a number", 0)
                .evaluateModel()
                .out("dmnResult")
                .getModel("http://www.trisotech.com/definitions/_99ccd4df-41ac-43c3-a563-d58f43149829", "typecheck in DT")
                .out("dmnModel")
                .getAllContext()
                .out("result")
                .getMessages()
                .out("messages")
                .end();

        RequestContext requestContext = ExecutableRunner.create().execute(builder.getExecutable());

        Map<String, Object> resultMap = requestContext.getOutput("result");
        DMNResult dmnResult = requestContext.getOutput("dmnResult");
        List<DMNMessage> messages = requestContext.getOutput("messages");
        DMNModel dmnModel = requestContext.getOutput("dmnModel");

        assertEquals(47, ((BigDecimal) resultMap.get("an odd decision")).intValue());
        assertNotNull(dmnResult);
        assertEquals(0, messages.size());
        assertNotNull(dmnModel);
    }
}