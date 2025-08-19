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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jitexecutor.dmn.responses.DMNResultWithExplanation;
import org.kie.kogito.jitexecutor.dmn.responses.JITDMNDecisionResult;
import org.kie.kogito.jitexecutor.dmn.responses.JITDMNResult;

import static org.assertj.core.api.Assertions.assertThat;
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
    void testSampleModel() throws IOException {
        final String ruleId0 = "_1FA12B9F-288C-42E8-B77F-BE2D3702B7B6";
        final String ruleId1 = "_C8FA33B1-AF6E-4A59-B7B9-6FDF1F495C44";

        String sampleModel = getModelFromIoUtils("valid_models/DMNv1_5/Sample.dmn");
        Map<String, Object> context = new HashMap<>();
        context.put("Credit Score", Map.of("FICO", 700));

        Map<String, Object> monthly = new HashMap<>();
        monthly.put("Income", 121233);
        monthly.put("Repayments", 33);
        monthly.put("Expenses", 123);
        monthly.put("Tax", 32);
        monthly.put("Insurance", 55);
        Map<String, Object> applicantData = new HashMap<>();
        applicantData.put("Age", 32);
        applicantData.put("Marital Status", "S");
        applicantData.put("Employment Status", "Employed");
        applicantData.put("Monthly", monthly);
        context.put("Applicant Data", applicantData);

        Map<String, Object> requestedProduct = new HashMap<>();
        requestedProduct.put("Type", "Special Loan");
        requestedProduct.put("Rate", 1);
        requestedProduct.put("Term", 2);
        requestedProduct.put("Amount", 333);
        context.put("Requested Product", requestedProduct);

        context.put("id", "_0A185BAC-7692-45FA-B722-7C86C626BD51");

        JITDMNResult dmnResult = jitdmnService.evaluateModel(sampleModel, context, true);
        assertThat(dmnResult.getModelName()).isEqualTo("loan_pre_qualification");
        assertThat(dmnResult.getNamespace()).isEqualTo("https://kie.apache.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB");
        assertThat(dmnResult.getMessages()).isEmpty();
        assertThat(dmnResult.getDecisionResultByName("Front End Ratio").getResult()).isEqualTo("Sufficient");
        assertThat(dmnResult.getDecisionResultByName("Back End Ratio").getResult()).isEqualTo("Sufficient");

        JITDMNDecisionResult retrievedDecisionResult = (JITDMNDecisionResult) dmnResult.getDecisionResultByName("Credit Score Rating");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo("Good");

        Map<String, Integer> evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsOnlyKeys(ruleId0);

        retrievedDecisionResult = (JITDMNDecisionResult) dmnResult.getDecisionResultByName("Loan Pre-Qualification");
        evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsOnlyKeys(ruleId1);
    }

    @Test
    void testModelEvaluation() {
        Map<String, Object> context = new HashMap<>();
        context.put("FICO Score", 800);
        context.put("DTI Ratio", .1);
        context.put("PITI Ratio", .1);
        JITDMNResult dmnResult = jitdmnService.evaluateModel(model, context, false);
        assertThat(dmnResult.getModelName()).isEqualTo("xls2dmn");
        assertThat(dmnResult.getNamespace()).isEqualTo("xls2dmn_741b355c-685c-4827-b13a-833da8321da4");
        assertThat(dmnResult.getMessages()).isEmpty();
        assertThat(dmnResult.getDecisionResultByName("Loan Approval").getResult()).isEqualTo("Approved");
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
        JITDMNResult dmnResult = jitdmnService.evaluateModel(decisionTableModel, context, false);

        assertThat(dmnResult.getModelName()).isEqualTo("LoanEligibility");
        assertThat(dmnResult.getNamespace()).isEqualTo("https://github.com/kiegroup/kogito-examples/dmn-quarkus-listener-example");
        assertThat(dmnResult.getMessages()).isEmpty();
        assertThat(dmnResult.getDecisionResultByName("Eligibility").getResult()).isEqualTo("Yes");
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
        JITDMNResult retrieved = jitdmnService.evaluateModel(decisionTableModel, context, false);
        assertThat(retrieved.getModelName()).isEqualTo("DMN_A77074C1-21FE-4F7E-9753-F84661569AFC");
        assertThat(retrieved.getMessages()).isEmpty();

        // Approved decision
        JITDMNDecisionResult retrievedDecisionResult = (JITDMNDecisionResult) retrieved.getDecisionResultByName("Risk Score");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo(BigDecimal.valueOf(50));

        Map<String, Integer> evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsOnlyKeys(ruleId0, ruleId3);
        // Not Qualified decision
        retrievedDecisionResult = (JITDMNDecisionResult) retrieved.getDecisionResultByName("Loan Pre-Qualification");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo("Not Qualified");
        evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsOnlyKeys(elseElementId);
        //---/
        context = new HashMap<>();
        context.put("Credit Score", "Excellent");
        context.put("DTI", 10);
        retrieved = jitdmnService.evaluateModel(decisionTableModel, context, false);
        assertThat(retrieved.getMessages()).isEmpty();
        // Approved decision
        retrievedDecisionResult = (JITDMNDecisionResult) retrieved.getDecisionResultByName("Risk Score");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo(BigDecimal.valueOf(20));
        evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsOnlyKeys(ruleId1, ruleId4);
        // Qualified decision
        retrievedDecisionResult = (JITDMNDecisionResult) retrieved.getDecisionResultByName("Loan Pre-Qualification");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo("Qualified");
        evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsOnlyKeys(thenElementId);
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
        JITDMNResult retrieved = jitdmnService.evaluateModel(decisionTableModel, context, false);
        assertThat(retrieved.getMessages()).isEmpty();
        // Approved decision
        JITDMNDecisionResult retrievedDecisionResult = (JITDMNDecisionResult) retrieved.getDecisionResultByName("Risk Score");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo(BigDecimal.valueOf(50));
        Map<String, Integer> evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsOnlyKeys(thenElementId, thenRuleId0, thenRuleId4);
        //---/
        context = new HashMap<>();
        context.put("Credit Score", "Excellent");
        context.put("DTI", 10);
        context.put("World Region", "Europe");
        retrieved = jitdmnService.evaluateModel(decisionTableModel, context, false);
        assertThat(retrieved.getMessages()).isEmpty();
        // Approved decision
        retrievedDecisionResult = (JITDMNDecisionResult) retrieved.getDecisionResultByName("Risk Score");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo(BigDecimal.valueOf(30));
        evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsOnlyKeys(elseElementId, elseRuleId2, elseRuleId5);
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
        final JITDMNResult retrieved = jitdmnService.evaluateModel(decisionTableModel, context, false);

        final List<BigDecimal> expectedStatistcs = new ArrayList<>();
        expectedStatistcs.add(BigDecimal.valueOf(6));
        expectedStatistcs.add(BigDecimal.valueOf(3));
        expectedStatistcs.add(BigDecimal.valueOf(1));
        assertThat(retrieved.getMessages()).isEmpty();
        JITDMNDecisionResult retrievedDecisionResult = (JITDMNDecisionResult) retrieved.getDecisionResultByName("Statistics");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo(expectedStatistcs);
        Map<String, Integer> evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsExactlyInAnyOrderEntriesOf(Map.of(rule0, 3, rule1, 2, rule2, 1));
    }

    @Test
    void nestedConditionalEvaluationHitIdsCheck() throws IOException {
        final String thenElementId = "_C69417CB-474E-4742-9D26-8D1ADB75CAEC";
        final String elseElementId = "_0C94AE89-A771-4CD8-A62F-B7BA7F8F2359";
        String nestedConditionalModel = getModelFromIoUtils("valid_models/DMNv1_5/NestedConditional.dmn");

        final Map<String, Object> context = new HashMap<>();
        context.put("A", 1);

        JITDMNResult retrieved = jitdmnService.evaluateModel(nestedConditionalModel, context, false);
        assertThat(retrieved.getMessages()).isEmpty();
        JITDMNDecisionResult retrievedDecisionResult = (JITDMNDecisionResult) retrieved.getDecisionResultByName("New Decision");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo(BigDecimal.valueOf(10));
        Map<String, Integer> evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsExactlyInAnyOrderEntriesOf(Map.of(thenElementId, 1));

        //
        context.clear();
        context.put("A", 0);
        retrieved = jitdmnService.evaluateModel(nestedConditionalModel, context, false);
        assertThat(retrieved.getMessages()).isEmpty();
        retrievedDecisionResult = (JITDMNDecisionResult) retrieved.getDecisionResultByName("New Decision");
        assertThat(retrievedDecisionResult.getResult()).isEqualTo(BigDecimal.valueOf(-10));
        evaluationHitIds = retrievedDecisionResult.getEvaluationHitIds();
        assertThat(evaluationHitIds).isNotNull()
                .containsExactlyInAnyOrderEntriesOf(Map.of(elseElementId, 1));
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

        DMNResultWithExplanation response = jitdmnService.evaluateModelAndExplain(allTypesModel, context, false);
        assertThat(response.dmnResult).isNotNull();
        assertThat(response.dmnResult.getDecisionResults()).hasSize(1);

        assertThat(response.salienciesResponse).isNotNull();
        assertThat(response.salienciesResponse.getSaliencies()).hasSize(1);
        assertThat(response.salienciesResponse.getSaliencies().get(0).getFeatureImportance()).hasSize(17);
    }
}
