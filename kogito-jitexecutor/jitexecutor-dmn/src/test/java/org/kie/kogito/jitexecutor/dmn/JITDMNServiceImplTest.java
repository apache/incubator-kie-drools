/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jitexecutor.dmn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.IoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.dmn.rest.DMNResult;
import org.kie.kogito.jitexecutor.dmn.api.JITDMNResourceTest;

public class JITDMNServiceImplTest {

    private static String model;
    private static JITDMNService jitdmnService;

    @BeforeAll
    public static void setup() throws IOException {
        model = new String(IoUtils.readBytesFromInputStream(JITDMNResourceTest.class.getResourceAsStream("/test.dmn")));
        jitdmnService = new JITDMNServiceImpl();
    }

    @Test
    public void testModelEvaluation() {
        Map<String, Object> context = new HashMap<>();
        context.put("FICO Score", 800);
        context.put("DTI Ratio", .1);
        context.put("PITI Ratio", .1);
        DMNResult dmnResult = jitdmnService.evaluateModel(model, context);

        Assertions.assertEquals("xls2dmn", dmnResult.getModelName());
        Assertions.assertEquals("xls2dmn_741b355c-685c-4827-b13a-833da8321da4", dmnResult.getNamespace());
        Assertions.assertTrue(dmnResult.getMessages().isEmpty());
        Assertions.assertEquals("Approved", dmnResult.getDecisionResultByName("Loan Approval").getResult());
    }
}
