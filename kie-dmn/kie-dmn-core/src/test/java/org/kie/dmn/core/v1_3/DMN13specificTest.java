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

package org.kie.dmn.core.v1_3;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DMN13specificTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMN13specificTest.class);

    public DMN13specificTest(final BaseVariantTest.VariantTestConf conf) {
        super(conf);
    }

    @Test
    public void testDMNv1_3_simple() {
        final DMNRuntime runtime = createRuntime("simple.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("name", "John");

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("salutation"), is("Hello, John"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("salutation"), is("Hello, John"));
        }
    }

    @Test
    public void testDMNv1_3_ch11() {
        testName = "testDMNv1_3_ch11";

        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Chapter 11 Example.dmn", this.getClass(), "Financial.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

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
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Strategy"), is("THROUGH"));
        assertThat(result.get("Routing"), is("ACCEPT"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Strategy"), is("THROUGH"));
            assertThat(allProperties.get("Routing"), is("ACCEPT"));
        }
    }

    @Test
    public void testDMNv1_3_ch11_asSpecInputDataValues() {
        testName = "testDMNv1_3_ch11_asSpecInputDataValues";
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Chapter 11 Example.dmn", this.getClass(), "Financial.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

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
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Strategy"), is("THROUGH"));
        assertThat(result.get("Routing"), is("ACCEPT"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Strategy"), is("THROUGH"));
            assertThat(allProperties.get("Routing"), is("ACCEPT"));
        }
    }

    @Test
    public void testBKMencapsulatedlogictyperef() {
        final DMNRuntime runtime = createRuntime("bkmELTyperef.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_a49df6fc-c936-467a-9762-9aa3c9a93c06", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision1"), is(new BigDecimal("3")));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl) dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Decision1"), is(new BigDecimal("3")));
        }
    }
}
