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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.util.IoUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jitexecutor.dmn.api.JITDMNResourceTest;
import org.kie.kogito.jitexecutor.dmn.responses.DMNResultWithExplanation;
import org.kie.kogito.jitexecutor.dmn.responses.JITDMNResult;

public class JITDMNServiceImplTest {

    private static String model;
    private static JITDMNService jitdmnService;

    @BeforeAll
    public static void setup() throws IOException {
        model = new String(IoUtils.readBytesFromInputStream(JITDMNResourceTest.class.getResourceAsStream("/test.dmn")));
        jitdmnService = new JITDMNServiceImpl(300, 1);
    }

    @Test
    public void testModelEvaluation() {
        Map<String, Object> context = new HashMap<>();
        context.put("FICO Score", 800);
        context.put("DTI Ratio", .1);
        context.put("PITI Ratio", .1);
        JITDMNResult dmnResult = jitdmnService.evaluateModel(model, context);

        Assertions.assertEquals("xls2dmn", dmnResult.getModelName());
        Assertions.assertEquals("xls2dmn_741b355c-685c-4827-b13a-833da8321da4", dmnResult.getNamespace());
        Assertions.assertTrue(dmnResult.getMessages().isEmpty());
        Assertions.assertEquals("Approved", dmnResult.getDecisionResultByName("Loan Approval").getResult());
    }

    @Test
    public void testExplainability() throws IOException {
        String allTypesModel = new String(IoUtils.readBytesFromInputStream(JITDMNResourceTest.class.getResourceAsStream("/allTypes.dmn")));

        Map<String, Object> context = new HashMap<>();
        context.put("stringInput", "test");
        context.put("listOfStringInput", Collections.singletonList("test"));
        context.put("numberInput", 1);
        context.put("listOfNumbersInput", Collections.singletonList(1));
        context.put("booleanInput", true);
        context.put("listOfBooleansInput", Collections.singletonList(true));

        context.put("timeInput", "h09:00");
        context.put("dateInput", "2020-04-02");
        context.put("dateAndTimeInput", "2020-04-02T09:00:00");
        context.put("daysAndTimeDurationInput", "P1DT1H");
        context.put("yearsAndMonthDurationInput", "P1Y1M");

        Map<String, Object> complexInput = new HashMap<>();
        complexInput.put("aNestedListOfNumbers", Collections.singletonList(1));
        complexInput.put("aNestedString", "test");
        complexInput.put("aNestedComplexInput", Collections.singletonMap("doubleNestedNumber", 1));

        context.put("complexInput", complexInput);
        context.put("listOfComplexInput", Collections.singletonList(complexInput));

        DMNResultWithExplanation response = jitdmnService.evaluateModelAndExplain(allTypesModel, context);

        Assertions.assertNotNull(response.dmnResult);
        Assertions.assertEquals(1, response.dmnResult.getDecisionResults().size());

        Assertions.assertNotNull(response.salienciesResponse);
        Assertions.assertEquals(1, response.salienciesResponse.getSaliencies().size());
        Assertions.assertEquals(17, response.salienciesResponse.getSaliencies().get(0).getFeatureImportance().size());
    }
}
