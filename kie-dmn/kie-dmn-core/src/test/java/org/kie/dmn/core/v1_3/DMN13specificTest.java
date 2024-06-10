/**
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
package org.kie.dmn.core.v1_3;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DMN13specificTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMN13specificTest.class);

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    void dMNv13Simple(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("simple.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("name", "John");

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("salutation")).isEqualTo("Hello, John");

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("salutation")).isEqualTo("Hello, John");
        }
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    void dMNv13Ch11(VariantTestConf conf) {
        testConfig = conf;
        testName = "testDMNv1_3_ch11";

        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Chapter 11 Example.dmn", this.getClass(), "Financial.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Applicant data", mapOf(entry("Age", new BigDecimal(51)),
                                            entry("MartitalStatus", "M"), // typo is present in DMNv1.3
                                            entry("EmploymentStatus", "EMPLOYED"),
                                            entry("ExistingCustomer", false),
                                            entry("Monthly", mapOf(entry("Income", new BigDecimal(100_000)),
                                                                   entry("Repayments", new BigDecimal(2_500)),
                                                                   entry("Expenses", new BigDecimal(10_000))))));
        context.set("Bureau data", mapOf(entry("Bankrupt", false),
                                         entry("CreditScore", new BigDecimal(600))));
        context.set("Requested product", mapOf(entry("ProductType", "STANDARD LOAN"),
                                               entry("Rate", new BigDecimal(0.08)),
                                               entry("Term", new BigDecimal(36)),
                                               entry("Amount", new BigDecimal(100_000))));
        context.set("Supporting documents", null);
        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Strategy")).isEqualTo("THROUGH");
        assertThat(result.get("Routing")).isEqualTo("ACCEPT");

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Strategy")).isEqualTo("THROUGH");
            assertThat(allProperties.get("Routing")).isEqualTo("ACCEPT");
        }
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    void dMNv13Ch11AsSpecInputDataValues(VariantTestConf conf) {
        testConfig = conf;
        testName = "testDMNv1_3_ch11_asSpecInputDataValues";
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Chapter 11 Example.dmn", this.getClass(), "Financial.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Applicant data", mapOf(entry("Age", new BigDecimal(51)),
                                            entry("MartitalStatus", "M"), // typo is present in DMNv1.3
                                            entry("EmploymentStatus", "EMPLOYED"),
                                            entry("ExistingCustomer", false),
                                            entry("Monthly", mapOf(entry("Income", new BigDecimal(10_000)),
                                                                   entry("Repayments", new BigDecimal(2_500)),
                                                                   entry("Expenses", new BigDecimal(3_000))))));
        context.set("Bureau data", mapOf(entry("Bankrupt", false),
                                         entry("CreditScore", new BigDecimal(600))));
        context.set("Requested product", mapOf(entry("ProductType", "STANDARD LOAN"),
                                               entry("Rate", new BigDecimal(0.08)),
                                               entry("Term", new BigDecimal(36)),
                                               entry("Amount", new BigDecimal(100_000))));
        context.set("Supporting documents", null);
        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Strategy")).isEqualTo("THROUGH");
        assertThat(result.get("Routing")).isEqualTo("ACCEPT");

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Strategy")).isEqualTo("THROUGH");
            assertThat(allProperties.get("Routing")).isEqualTo("ACCEPT");
        }
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    void bKMencapsulatedlogictyperef(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("bkmELTyperef.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_a49df6fc-c936-467a-9762-9aa3c9a93c06", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision1")).isEqualTo(new BigDecimal("3"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl) dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Decision1")).isEqualTo(new BigDecimal("3"));
        }
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    void bkmFnTypeVirtuous(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("bkmFnTypeVirtuous.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_563323ba-325e-4a3f-938f-369854010eaf", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("name", "John");

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("do greet the name")).isEqualTo("Hello, John");
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    void bkmFnTypeWrongEL(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("bkmFnTypeWrongEL.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_563323ba-325e-4a3f-938f-369854010eaf", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.getMessages(Severity.WARN)).as("Expected WARNs of mismatches type in "+DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isNotEmpty();

        final DMNContext context = DMNFactory.newContext();
        context.set("name", "John");

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("do greet the name")).isEqualTo("Hello, John");
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    void bkmWrongELExpr(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("bkmWrongELExprType.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_563323ba-325e-4a3f-938f-369854010eaf", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.getMessages(Severity.WARN)).as("Expected WARNs of mismatches type in "+DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isNotEmpty();

        final DMNContext context = DMNFactory.newContext();
        context.set("name", "John");

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("do greet the name")).isEqualTo("Hello, John");
    }
}
