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
package org.kie.dmn.legacy.tests.core.v1_1;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNDecisionTableHitPolicyTest extends BaseDMN1_1VariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionTableHitPolicyTest.class);

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyUnique(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        assertThat(result.get("Approval Status")).isEqualTo("Approved");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyUniqueSatisfies(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "ASD", false);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Approval Status")).isNull();
        assertThat(dmnResult.getMessages()).hasSizeGreaterThan(0);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyUniqueNullWarn(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U-noinputvalues.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U-noinputvalues");
        assertThat(dmnModel).isNotNull();

        check_testSimpleDecisionTableHitPolicyUniqueNullWarn(runtime, dmnModel);
    }

    private void check_testSimpleDecisionTableHitPolicyUniqueNullWarn(DMNRuntime runtime, DMNModel dmnModel) {
        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "ASD", false);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Approval Status")).isNull();
        assertThat(dmnResult.getMessages()).hasSizeGreaterThan(0);
        assertThat(dmnResult.getMessages().stream().anyMatch(dm -> dm.getSeverity().equals(DMNMessage.Severity.WARN) && dm.getFeelEvent() instanceof HitPolicyViolationEvent && dm.getFeelEvent().getSeverity().equals(FEELEvent.Severity.WARN))).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyUniqueNullWarnCtxe(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U-noinputvalues-ctxe.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U-noinputvalues");
        assertThat(dmnModel).isNotNull();

        check_testSimpleDecisionTableHitPolicyUniqueNullWarn(runtime, dmnModel);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyUniqueNullWarnCtxr(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-U-noinputvalues-ctxr.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-U-noinputvalues");
        assertThat(dmnModel).isNotNull();

        check_testSimpleDecisionTableHitPolicyUniqueNullWarn(runtime, dmnModel);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableHitPolicyUnique(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("BranchDistribution.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_cdf29af2-959b-4004-8271-82a9f5a62147", "Dessin 1");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Branches dispersion", "Province");
        context.set("Number of Branches", BigDecimal.valueOf(10));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Branches distribution")).isEqualTo("Medium");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyFirst(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-F.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-F");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final Map<String, Object> decisionResult = (Map<String, Object>) result.get("Decision Result");
        assertThat(decisionResult.values()).hasSize(2);
        assertThat(decisionResult).containsEntry("Approval Status", "Approved");
        assertThat(decisionResult).containsEntry("Decision Review", "Decision final");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyAnyEqualRules(VariantTestConf conf) {
        testConfig = conf;
        testSimpleDecisionTableHitPolicyAny("0004-simpletable-A.dmn", "0004-simpletable-A", true);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyAnyNonEqualRules(VariantTestConf conf) {
        testConfig = conf;
        testSimpleDecisionTableHitPolicyAny("0004-simpletable-A-non-equal.dmn", "0004-simpletable-A-non-equal", false);
    }

    private void testSimpleDecisionTableHitPolicyAny(final String resurceName, final String modelName, final boolean equalRules) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(resurceName, this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", modelName);
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        if (equalRules) {
            assertThat(result.get("Approval Status")).isEqualTo("Approved");
        } else {
            assertThat(dmnResult.hasErrors()).isTrue();
            assertThat((String) result.get("Approval Status")).isNullOrEmpty();
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyPriority(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-P.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-P");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        assertThat(result.get("Approval Status")).isEqualTo("Declined");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyPriorityMultipleOutputs(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-P-multiple-outputs.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-P-multiple-outputs");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final Map<String, Object> decisionResult = (Map<String, Object>) result.get("Decision Result");
        assertThat(decisionResult.values()).hasSize(2);
        assertThat(decisionResult).containsEntry("Approval Status", "Declined");
        assertThat(decisionResult).containsEntry("Decision Review", "Needs verification");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyOutputOrder(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-O.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-O");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final List<String> decisionResults = (List<String>) result.get("Approval Status");
        assertThat(decisionResults).hasSize(3);
        assertThat(decisionResults).contains("Declined", "Declined", "Approved");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyOutputOrderMultipleOutputs(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-O-multiple-outputs.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-O-multiple-outputs");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(18), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final List<Map<String, String>> decisionResult = (List<Map<String, String>>) result.get("Decision Result");
        assertThat(decisionResult).hasSize(4);
        // Must be ordered, so we can read from the list by index
        checkMultipleOutputResult(decisionResult.get(0), "Declined", "Needs verification");
        checkMultipleOutputResult(decisionResult.get(1), "Declined", "Decision final");
        checkMultipleOutputResult(decisionResult.get(2), "Approved", "Needs verification");
        checkMultipleOutputResult(decisionResult.get(3), "Approved", "Decision final");
    }

    private void checkMultipleOutputResult(final Map<String, String> outputResult,
            final String expectedApprovalStatus, final String expectedDecisionReview) {
        assertThat(outputResult).containsEntry("Approval Status", expectedApprovalStatus);
        assertThat(outputResult).containsEntry("Decision Review", expectedDecisionReview);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyRuleOrder(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-R.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-R");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        final List<String> decisionResults = (List<String>) result.get("Approval Status");
        assertThat(decisionResults).hasSize(3);
        assertThat(decisionResults).contains("Approved", "Needs review", "Declined");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyCollect(VariantTestConf conf) {
        testConfig = conf;
        final List<BigDecimal> decisionResults = executeTestDecisionTableHitPolicyCollect(getSimpleTableContext(BigDecimal.valueOf(70 ), "Medium", true));
        assertThat(decisionResults).hasSize(3);
        assertThat(decisionResults).contains(BigDecimal.valueOf(10), BigDecimal.valueOf(25), BigDecimal.valueOf(13));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyCollectNoHits(VariantTestConf conf) {
        testConfig = conf;
        final List<BigDecimal> decisionResults = executeTestDecisionTableHitPolicyCollect(getSimpleTableContext(BigDecimal.valueOf(5 ), "Medium", true));
        assertThat(decisionResults).hasSize(0);
    }

    private List<BigDecimal> executeTestDecisionTableHitPolicyCollect(final DMNContext context) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0004-simpletable-C.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0004-simpletable-C");
        assertThat(dmnModel).isNotNull();

        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);

        return (List<BigDecimal>) result.get( "Status number");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyCollectSum(VariantTestConf conf) {
        testConfig = conf;
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-sum.dmn", "0004-simpletable-C-sum", BigDecimal.valueOf(48),
                getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyCollectSumMultipleOutputs(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-simpletable-C-sum-multiple-outputs.dmn", this.getClass());
        final DMNModel dmnModel =
                runtime.getModel("https://github.com/kiegroup/kie-dmn", "0004-simpletable-C-sum-multiple-outputs");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        final DMNContext result = dmnResult.getContext();
        final Map<String, Object> decisionResult = (Map<String, Object>) result.get("Decision Result");
        assertThat(decisionResult.values()).hasSize(2);
        assertThat(decisionResult).containsEntry("Value1", BigDecimal.valueOf(25));
        assertThat(decisionResult).containsEntry("Value2", BigDecimal.valueOf(32));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyCollectMin(VariantTestConf conf) {
        testConfig = conf;
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-min.dmn", "0004-simpletable-C-min", BigDecimal.valueOf(10),
                getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyCollectMax(VariantTestConf conf) {
        testConfig = conf;
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-max.dmn", "0004-simpletable-C-max", BigDecimal.valueOf(25),
                getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyCollectCount(VariantTestConf conf) {
        testConfig = conf;
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-count.dmn", "0004-simpletable-C-count", BigDecimal.valueOf(3),
                getSimpleTableContext(BigDecimal.valueOf(70), "Medium", true));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableHitPolicyCollectCountNoHits(VariantTestConf conf) {
        testConfig = conf;
        testSimpleDecisionTableHitPolicyCollectAggregateFunction(
                "0004-simpletable-C-count.dmn", "0004-simpletable-C-count", BigDecimal.valueOf(0),
                getSimpleTableContext(BigDecimal.valueOf(5), "Medium", true));
    }

    private void testSimpleDecisionTableHitPolicyCollectAggregateFunction(
            final String resourceName, final String modelName, final BigDecimal expectedResult, final DMNContext context) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(resourceName, this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", modelName);
        assertThat(dmnModel).isNotNull();

        final DMNContext result = evaluateSimpleTableWithContext(dmnModel, runtime, context);
        assertThat(result.get("Status number")).isEqualTo(expectedResult);
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableHitPolicyCollect(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Collect_Hit_Policy.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_da1a4dcb-01bf-4dee-9be8-f498bc68178c", "Collect Hit Policy");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Input", 20);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Collect")).isEqualTo(BigDecimal.valueOf(50));
    }

}
