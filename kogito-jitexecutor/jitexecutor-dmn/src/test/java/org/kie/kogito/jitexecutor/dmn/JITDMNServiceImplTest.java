/*
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
package org.kie.kogito.jitexecutor.dmn;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jitexecutor.dmn.responses.DMNResultWithExplanation;
import org.kie.kogito.jitexecutor.dmn.responses.JITDMNResult;

import static org.kie.kogito.jitexecutor.dmn.TestingUtils.getModelFromIoUtils;

public class JITDMNServiceImplTest {

    private static String model;
    private static JITDMNService jitdmnService;

    @BeforeAll
    public static void setup() throws IOException {
        model = getModelFromIoUtils("invalid_models/DMNv1_x/test.dmn");
        jitdmnService = new JITDMNServiceImpl(300, 1);
    }

    @Test
    void testModelEvaluation() {
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
    void testDecisionTableModelEvaluation() throws IOException {
        String decisionTableModel = getModelFromIoUtils("valid_models/DMNv1_x/LoanEligibility.dmn");
        Map<String, Object> client = new HashMap<>();
        client.put("age", 43);
        client.put("salary", 1950);
        client.put("existing payments", 100);

        Map<String, Object> loan = new HashMap<>();
        loan.put("duration", 15);
        loan.put("installment", 180);
        Map<String, Object> context = new HashMap<>();

        context.put("Client", client);
        context.put("Loan", loan);
        context.put("SupremeDirector", "No");
        context.put("Bribe", 10);
        JITDMNResult dmnResult = jitdmnService.evaluateModel(decisionTableModel, context);

        Assertions.assertEquals("LoanEligibility", dmnResult.getModelName());
        Assertions.assertEquals("https://github.com/kiegroup/kogito-examples/dmn-quarkus-listener-example",
                dmnResult.getNamespace());
        Assertions.assertTrue(dmnResult.getMessages().isEmpty());
        Assertions.assertEquals("Yes", dmnResult.getDecisionResultByName("Eligibility").getResult());
    }

    @Test
    void testEvaluationHitIds() throws IOException {
        final String thenElementId = "_6481FF12-61B5-451C-B775-4143D9B6CD6B";
        final String elseElementId = "_2CD02CB2-6B56-45C4-B461-405E89D45633";
        final String ruleId0 = "_1578BD9E-2BF9-4BFC-8956-1A736959C937";
        final String ruleId1 = "_31CD7AA3-A806-4E7E-B512-821F82043620";
        final String ruleId3 = "_2545E1A8-93D3-4C8A-A0ED-8AD8B10A58F9";
        final String ruleId4 = "_510A50DA-D5A4-4F06-B0BE-7F8F2AA83740";
        String decisionTableModel = getModelFromIoUtils("valid_models/DMNv1_5/RiskScore_Simple.dmn");
        Map<String, Object> context = new HashMap<>();
        context.put("Credit Score", "Poor");
        context.put("DTI", 33);
        JITDMNResult dmnResult = jitdmnService.evaluateModel(decisionTableModel, context);

        Assertions.assertEquals("DMN_A77074C1-21FE-4F7E-9753-F84661569AFC", dmnResult.getModelName());
        Assertions.assertTrue(dmnResult.getMessages().isEmpty());
        Assertions.assertEquals(BigDecimal.valueOf(50), dmnResult.getDecisionResultByName("Risk Score").getResult());
        Map<String, Integer> evaluationHitIds = dmnResult.getEvaluationHitIds();
        Assertions.assertNotNull(evaluationHitIds);
        Assertions.assertEquals(3, evaluationHitIds.size());
        Assertions.assertTrue(evaluationHitIds.containsKey(elseElementId));
        Assertions.assertTrue(evaluationHitIds.containsKey(ruleId0));
        Assertions.assertTrue(evaluationHitIds.containsKey(ruleId3));

        context = new HashMap<>();
        context.put("Credit Score", "Excellent");
        context.put("DTI", 10);
        dmnResult = jitdmnService.evaluateModel(decisionTableModel, context);

        Assertions.assertTrue(dmnResult.getMessages().isEmpty());
        Assertions.assertEquals(BigDecimal.valueOf(20), dmnResult.getDecisionResultByName("Risk Score").getResult());
        evaluationHitIds = dmnResult.getEvaluationHitIds();
        Assertions.assertNotNull(evaluationHitIds);
        Assertions.assertEquals(3, evaluationHitIds.size());
        Assertions.assertTrue(evaluationHitIds.containsKey(thenElementId));
        Assertions.assertTrue(evaluationHitIds.containsKey(ruleId1));
        Assertions.assertTrue(evaluationHitIds.containsKey(ruleId4));
    }

    @Test
    void testConditionalWithNestedDecisionTableFromRiskScoreEvaluation() throws IOException {
        final String thenElementId = "_6481FF12-61B5-451C-B775-4143D9B6CD6B";
        final String thenRuleId0 = "_D1753442-03F0-414B-94F8-6A86182DF6EB";
        final String thenRuleId4 = "_E787BA51-E31D-449B-A432-50BE7466A15E";
        final String elseElementId = "_2CD02CB2-6B56-45C4-B461-405E89D45633";
        final String elseRuleId2 = "_945A5471-9F91-4751-9D96-74978F6FB12B";
        final String elseRuleId5 = "_654BBFBC-9B84-4BD8-9D0B-13E8DD1B9F5D";
        String decisionTableModel = getModelFromIoUtils("valid_models/DMNv1_5/RiskScore_Conditional.dmn");

        Map<String, Object> context = new HashMap<>();
        context.put("Credit Score", "Poor");
        context.put("DTI", 33);
        context.put("World Region", "Asia");
        JITDMNResult dmnResult = jitdmnService.evaluateModel(decisionTableModel, context);

        Assertions.assertTrue(dmnResult.getMessages().isEmpty());
        Assertions.assertEquals(BigDecimal.valueOf(50), dmnResult.getDecisionResultByName("Risk Score").getResult());
        Map<String, Integer> evaluationHitIds = dmnResult.getEvaluationHitIds();
        Assertions.assertNotNull(evaluationHitIds);
        Assertions.assertEquals(3, evaluationHitIds.size());
        Assertions.assertTrue(evaluationHitIds.containsKey(thenElementId));
        Assertions.assertTrue(evaluationHitIds.containsKey(thenRuleId0));
        Assertions.assertTrue(evaluationHitIds.containsKey(thenRuleId4));

        context = new HashMap<>();
        context.put("Credit Score", "Excellent");
        context.put("DTI", 10);
        context.put("World Region", "Europe");
        dmnResult = jitdmnService.evaluateModel(decisionTableModel, context);

        Assertions.assertTrue(dmnResult.getMessages().isEmpty());
        Assertions.assertEquals(BigDecimal.valueOf(30), dmnResult.getDecisionResultByName("Risk Score").getResult());
        evaluationHitIds = dmnResult.getEvaluationHitIds();
        Assertions.assertNotNull(evaluationHitIds);
        Assertions.assertEquals(3, evaluationHitIds.size());
        Assertions.assertTrue(evaluationHitIds.containsKey(elseElementId));
        Assertions.assertTrue(evaluationHitIds.containsKey(elseRuleId2));
        Assertions.assertTrue(evaluationHitIds.containsKey(elseRuleId5));
    }

    @Test
    void testMultipleHitRulesEvaluation() throws IOException {
        final String rule0 = "_E5C380DA-AF7B-4401-9804-C58296EC09DD";
        final String rule1 = "_DFD65E8B-5648-4BFD-840F-8C76B8DDBD1A";
        final String rule2 = "_E80EE7F7-1C0C-4050-B560-F33611F16B05";
        String decisionTableModel = getModelFromIoUtils("valid_models/DMNv1_5/MultipleHitRules.dmn");

        final List<BigDecimal> numbers = new ArrayList<>();
        numbers.add(BigDecimal.valueOf(10));
        numbers.add(BigDecimal.valueOf(2));
        numbers.add(BigDecimal.valueOf(1));
        final Map<String, Object> context = new HashMap<>();
        context.put("Numbers", numbers);
        final JITDMNResult dmnResult = jitdmnService.evaluateModel(decisionTableModel, context);

        final List<BigDecimal> expectedStatistcs = new ArrayList<>();
        expectedStatistcs.add(BigDecimal.valueOf(6));
        expectedStatistcs.add(BigDecimal.valueOf(3));
        expectedStatistcs.add(BigDecimal.valueOf(1));
        Assertions.assertTrue(dmnResult.getMessages().isEmpty());
        Assertions.assertEquals(expectedStatistcs, dmnResult.getDecisionResultByName("Statistics").getResult());
        final Map<String, Integer> evaluationHitIds = dmnResult.getEvaluationHitIds();
        Assertions.assertNotNull(evaluationHitIds);
        Assertions.assertEquals(3, evaluationHitIds.size());
        Assertions.assertEquals(3, evaluationHitIds.get(rule0));
        Assertions.assertEquals(2, evaluationHitIds.get(rule1));
        Assertions.assertEquals(1, evaluationHitIds.get(rule2));
    }

    @Test
    void testExplainability() throws IOException {
        String allTypesModel = getModelFromIoUtils("valid_models/DMNv1_x/allTypes.dmn");

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
