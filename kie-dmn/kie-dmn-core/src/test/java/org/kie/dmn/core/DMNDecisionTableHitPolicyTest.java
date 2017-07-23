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

package org.kie.dmn.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;

public class DMNDecisionTableHitPolicyTest {

    @Test
    public void testSimpleDecisionTableHitPolicyUnique() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        assertThat(result.get("Approval Status"), is("Approved"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyUniqueSatisfies() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "ASD", false);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Approval Status"), nullValue());
        assertTrue(dmnResult.getMessages().size() > 0);
    }

    @Test
    public void testSimpleDecisionTableHitPolicyUniqueNullWarn() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U-noinputvalues.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U-noinputvalues");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "ASD", false);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Approval Status"), nullValue());
        assertTrue(dmnResult.getMessages().size() > 0);
        assertTrue(dmnResult.getMessages().stream().anyMatch(dm -> dm.getSeverity().equals(DMNMessage.Severity.WARN) && dm.getFeelEvent() instanceof HitPolicyViolationEvent && dm.getFeelEvent().getSeverity().equals(FEELEvent.Severity.WARN)));
    }

    @Test
    public void testDecisionTableHitPolicyUnique() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("BranchDistribution.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_cdf29af2-959b-4004-8271-82a9f5a62147", "Dessin 1");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = DMNFactory.newContext();
        context.set("Branches dispersion", "Province");
        context.set("Number of Branches", BigDecimal.valueOf(10));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Branches distribution"), is("Medium"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyFirst() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-F.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-F");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final Map<String, Object> decisionResult = (Map<String, Object>) result.get("Decision Result");
        assertThat(decisionResult.values(), hasSize(2));
        assertThat(decisionResult, hasEntry(is("Approval Status"), is("Approved")));
        assertThat(decisionResult, hasEntry(is("Decision Review"), is("Decision final")));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyAnyEqualRules() {
        testSimpleDecisionTableHitPolicyAny("0004-simpletable-A.dmn", "0004-simpletable-A", true);
    }

    @Test
    public void testSimpleDecisionTableHitPolicyAnyNonEqualRules() {
        testSimpleDecisionTableHitPolicyAny("0004-simpletable-A-non-equal.dmn", "0004-simpletable-A-non-equal", false);
    }

    private void testSimpleDecisionTableHitPolicyAny(final String resurceName, final String modelName, final boolean equalRules) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(resurceName, this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", modelName);
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        if (equalRules) {
            assertThat(result.get("Approval Status"), is("Approved"));
        } else {
            assertThat(dmnResult.hasErrors(), is(true));
            assertThat((String) result.get("Approval Status"), isEmptyOrNullString());
        }
    }

    @Test
    public void testSimpleDecisionTableHitPolicyPriority() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-P.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-P");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        assertThat(result.get("Approval Status"), is("Declined"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyPriorityMultipleOutputs() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-P-multiple-outputs.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-P-multiple-outputs");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final Map<String, Object> decisionResult = (Map<String, Object>) result.get("Decision Result");
        assertThat(decisionResult.values(), hasSize(2));
        assertThat(decisionResult, hasEntry(is("Approval Status"), is("Declined")));
        assertThat(decisionResult, hasEntry(is("Decision Review"), is("Needs verification")));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyOutputOrder() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-O.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-O");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final List<String> decisionResults = (List<String>) result.get("Approval Status");
        assertThat(decisionResults, hasSize(3));
        assertThat(decisionResults, contains("Declined", "Declined", "Approved"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyOutputOrderMultipleOutputs() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-O-multiple-outputs.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-O-multiple-outputs");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final List<Map<String, String>> decisionResult = (List<Map<String, String>>) result.get("Decision Result");
        assertThat(decisionResult, hasSize(4));
        // Must be ordered, so we can read from the list by index
        checkMultipleOutputResult(decisionResult.get(0), "Declined", "Needs verification");
        checkMultipleOutputResult(decisionResult.get(1), "Declined", "Decision final");
        checkMultipleOutputResult(decisionResult.get(2), "Approved", "Needs verification");
        checkMultipleOutputResult(decisionResult.get(3), "Approved", "Decision final");
    }

    private void checkMultipleOutputResult(final Map<String, String> outputResult,
            final String expectedApprovalStatus, final String expectedDecisionReview) {
        assertThat(outputResult, hasEntry(is("Approval Status"), is(expectedApprovalStatus)));
        assertThat(outputResult, hasEntry(is("Decision Review"), is(expectedDecisionReview)));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyRuleOrder() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-R.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-R");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final List<String> decisionResults = (List<String>) result.get("Approval Status");
        assertThat(decisionResults, hasSize(3));
        assertThat(decisionResults, contains("Approved", "Needs review", "Declined"));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollect() {
        List<BigDecimal> decisionResults = executeTestDecisionTableHitPolicyCollect(getSimpleTableContext( BigDecimal.valueOf( 70 ), "Medium", true));
        assertThat(decisionResults, hasSize(3));
        assertThat(decisionResults, contains(BigDecimal.valueOf(10), BigDecimal.valueOf(25), BigDecimal.valueOf(13)));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectNoHits() {
        List<BigDecimal> decisionResults = executeTestDecisionTableHitPolicyCollect(getSimpleTableContext( BigDecimal.valueOf( 5 ), "Medium", true));
        assertThat(decisionResults, hasSize(0));
    }

    private List<BigDecimal> executeTestDecisionTableHitPolicyCollect(DMNContext context) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0004-simpletable-C.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0004-simpletable-C");
        assertThat(dmnModel, notNullValue());

        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);

        final List<BigDecimal> decisionResults = (List<BigDecimal>) result.get( "Status number");
        return decisionResults;
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectSum() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-sum.dmn", "0004-simpletable-C-sum", BigDecimal.valueOf(48),
                getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectSumMultipleOutputs() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-C-sum-multiple-outputs.dmn", this.getClass());
        final DMNModel dmnModel =
                runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-C-sum-multiple-outputs");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        final DMNContext result = dmnResult.getContext();
        final Map<String, Object> decisionResult = (Map<String, Object>) result.get("Decision Result");
        assertThat(decisionResult.values(), hasSize(2));
        assertThat(decisionResult, hasEntry( "Value1", BigDecimal.valueOf( 25 ) ));
        assertThat(decisionResult, hasEntry( "Value2", BigDecimal.valueOf( 32 ) ));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectMin() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-min.dmn", "0004-simpletable-C-min", BigDecimal.valueOf(10),
                getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectMax() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-max.dmn", "0004-simpletable-C-max", BigDecimal.valueOf(25),
                getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectCount() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-count.dmn", "0004-simpletable-C-count", BigDecimal.valueOf(3),
                getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @Test
    public void testSimpleDecisionTableHitPolicyCollectCountNoHits() {
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-count.dmn", "0004-simpletable-C-count", BigDecimal.valueOf(0),
                getSimpleTableContext(BigDecimal.valueOf(5), "Medium", true));
    }

    private void testSimpleDecisionTableHitPolicyCollectAggregateFunction(
            final String resourceName, final String modelName, final BigDecimal expectedResult, final DMNContext context) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(resourceName, this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", modelName);
        assertThat(dmnModel, notNullValue());

        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        assertThat(result.get("Status number"), is(expectedResult));
    }

    private DMNContext evaluateSimpleTableWithContext(final DMNModel model, final DMNRuntime runtime, final DMNContext context) {
        final DMNResult dmnResult = runtime.evaluateAll(model, context);
        return dmnResult.getContext();
    }

    private DMNContext getSimpleTableContext(final BigDecimal age, final String riskCategory, final boolean isAffordable) {
        final DMNContext context = DMNFactory.newContext();
        context.set("Age", age);
        context.set("RiskCategory", riskCategory);
        context.set("isAffordable", isAffordable);
        return context;
    }

    @Test
    public void testDecisionTableHitPolicyCollect() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Collect_Hit_Policy.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_da1a4dcb-01bf-4dee-9be8-f498bc68178c", "Collect Hit Policy");
        assertThat(dmnModel, notNullValue());

        final DMNContext context = DMNFactory.newContext();
        context.set("Input", 20);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Collect"), is(BigDecimal.valueOf(50)));
    }

}
