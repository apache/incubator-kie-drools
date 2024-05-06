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
package org.kie.dmn.core.v1_4;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.model.Person;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.model.SupportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK;
import static org.kie.dmn.core.util.DMNRuntimeUtil.formatMessages;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

/**
 * at the time of first creation of these tests are to be considered provisional in support of the next publication
 */
public class DMN14specificTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMN14specificTest.class);

    public static Object[] params() {
        return new Object[]{KIE_API_TYPECHECK, BUILDER_DEFAULT_NOCL_TYPECHECK}; // only variants needed until DMNv1.4 is actually published
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void dMNv14Put(final BaseVariantTest.VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("exampleContextPut.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("ns1", "examplePut");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).describedAs(formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Support Request", new SupportRequest("John Doe", "47", "info@redhat.com", "+1", "somewhere", "tech", "app crashed", false));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).describedAs(formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Determine Priority").getResult()).isEqualTo("Medium");
        assertThat(dmnResult.getDecisionResultByName("Processed Request").getResult()).hasFieldOrPropertyWithValue("priority", "Medium");
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void dMNv14PutAll(final BaseVariantTest.VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("exampleContextMerge.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_864E9A62-12E5-41DC-A7A6-7F028822A067", "examplePutAll");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).describedAs(formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Partial Person", new Person("John", "Wick"));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).describedAs(formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Complete Person").getResult()).hasFieldOrPropertyWithValue("last name", "Doe")
                                                                                    .hasFieldOrPropertyWithValue("age", new BigDecimal(47));
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void simple(final BaseVariantTest.VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("simple.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_d9232146-7aaa-49a9-8668-261a01844ace", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("name", "John");

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("greet")).isEqualTo("Hello, John");
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void dMNv14Ch11(final BaseVariantTest.VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Chapter 11 Example.dmn", this.getClass(), "Financial.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Applicant data", mapOf(entry("Age", new BigDecimal(51)),
                                            entry("MaritalStatus", "M"),
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
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void dMNv14Ch11Example2(final BaseVariantTest.VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Recommended Loan Products.dmn", this.getClass(), "Loan info.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_736fa164-03d8-429f-8318-4913a548c3a6", "Recommended Loan Products");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Credit Score", new BigDecimal(735));
        context.set("Property", mapOf(entry("Address", mapOf(entry("Street", "272 10th St."),
                                                             entry("Unit", null),
                                                             entry("City", "Marina"),
                                                             entry("State", "CA"),
                                                             entry("ZIP", "93933"))),
                                      entry("Purchase Price", new BigDecimal(340_000)),
                                      entry("Monthly Tax Payment", new BigDecimal(350)),
                                      entry("Monthly Insurance Payment", new BigDecimal(100)),
                                      entry("Monthly HOA Condo Fee", new BigDecimal(0))));
        context.set("Down Payment", new BigDecimal(70_000));
        context.set("Borrower", mapOf(entry("Full Name", "Ken Customer"),
                                      entry("Tax ID", "111223333"),
                                      entry("Employment Income", new BigDecimal(10_000)),
                                      entry("Other Income", new BigDecimal(0)),
                                      entry("Assets", Arrays.asList(mapOf(entry("Type", "Checking Savings Brokerage account"),
                                                                          entry("Institution Account or Description", "Chase"),
                                                                          entry("Value", new BigDecimal(35_000))),
                                                                    mapOf(entry("Type", "Checking Savings Brokerage account"),
                                                                          entry("Institution Account or Description", "Vanguard"),
                                                                          entry("Value", new BigDecimal(45_000))),
                                                                    mapOf(entry("Type", "Other Non-Liquid"),
                                                                          entry("Institution Account or Description", null),
                                                                          entry("Value", new BigDecimal(17_000))))),
                                      entry("Liabilities", Arrays.asList(mapOf(entry("Type", "Credit card"),
                                                                               entry("Payee", "Chase"),
                                                                               entry("Monthly payment", new BigDecimal(300)),
                                                                               entry("Balance", new BigDecimal(0)),
                                                                               entry("To be paid off", false)),
                                                                         mapOf(entry("Type", "Lease"),
                                                                               entry("Payee", "BMW Finance"),
                                                                               entry("Monthly payment", new BigDecimal(450)),
                                                                               entry("Balance", new BigDecimal(0)),
                                                                               entry("To be paid off", false)),
                                                                         mapOf(entry("Type", "Alimony child support"),
                                                                               entry("Payee", null),
                                                                               entry("Monthly payment", new BigDecimal(1_000)),
                                                                               entry("Balance", new BigDecimal(0)),
                                                                               entry("To be paid off", false)),
                                                                         mapOf(entry("Type", "Lien"),
                                                                               entry("Payee", "LA County"),
                                                                               entry("Monthly payment", new BigDecimal(100)),
                                                                               entry("Balance", new BigDecimal(850)),
                                                                               entry("To be paid off", true))
                                      ))));
        context.set("Lender Ratings", Arrays.asList(mapOf(entry("Lender Name", "Lender A"),
                                                          entry("Customer Rating", new BigDecimal(4.2))),
                                                    mapOf(entry("Lender Name", "Lender B"),
                                                          entry("Customer Rating", new BigDecimal(3.6))),
                                                    mapOf(entry("Lender Name", "Lender C"),
                                                          entry("Customer Rating", new BigDecimal(4.9))),
                                                    mapOf(entry("Lender Name", "Lender D"),
                                                          entry("Customer Rating", new BigDecimal(4.05)))
        ));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Recommended Loan Products")).asList().containsExactly(mapOf(entry("Product", "Lender B - ARM5/1-Standard"),
                                                                                   entry("Note Amount", "$273,775.90"),
                                                                                   entry("Interest Rate Pct", " 3.75"),
                                                                                   entry("Monthly Payment", "$1,267.90"),
                                                                                   entry("Cash to Close", "$75,475.52"),
                                                                                   entry("Required Credit Score", new BigDecimal(720)),
                                                                                   entry("Recommendation", "Good")),
                                                                             mapOf(entry("Product", "Lender C - Fixed30-Standard"),
                                                                                   entry("Note Amount", "$274,599.40"),
                                                                                   entry("Interest Rate Pct", " 3.88"),
                                                                                   entry("Monthly Payment", "$1,291.27"),
                                                                                   entry("Cash to Close", "$75,491.99"),
                                                                                   entry("Required Credit Score", new BigDecimal(680)),
                                                                                   entry("Recommendation", "Best")),
                                                                             mapOf(entry("Product", "Lender B - ARM5/1-NoPoints"),
                                                                                   entry("Note Amount", "$271,776.00"),
                                                                                   entry("Interest Rate Pct", " 4.00"),
                                                                                   entry("Monthly Payment", "$1,297.50"),
                                                                                   entry("Cash to Close", "$75,435.52"),
                                                                                   entry("Required Credit Score", new BigDecimal(720)),
                                                                                   entry("Recommendation", "Good")),
                                                                             mapOf(entry("Product", "Lender A - Fixed30-NoPoints"),
                                                                                   entry("Note Amount", "$271,925.00"),
                                                                                   entry("Interest Rate Pct", " 4.08"),
                                                                                   entry("Monthly Payment", "$1,310.00"),
                                                                                   entry("Cash to Close", "$75,438.50"),
                                                                                   entry("Required Credit Score", new BigDecimal(680)),
                                                                                   entry("Recommendation", "Best")),
                                                                             mapOf(entry("Product", "Lender C - Fixed15-Standard"),
                                                                                   entry("Note Amount", "$274,045.90"),
                                                                                   entry("Interest Rate Pct", " 3.38"),
                                                                                   entry("Monthly Payment", "$1,942.33"),
                                                                                   entry("Cash to Close", "$75,480.92"),
                                                                                   entry("Required Credit Score", new BigDecimal(720)),
                                                                                   entry("Recommendation", "Best")),
                                                                             mapOf(entry("Product", "Lender A - Fixed15-NoPoints"),
                                                                                   entry("Note Amount", "$270,816.00"),
                                                                                   entry("Interest Rate Pct", " 3.75"),
                                                                                   entry("Monthly Payment", "$1,969.43"),
                                                                                   entry("Cash to Close", "$75,416.32"),
                                                                                   entry("Required Credit Score", new BigDecimal(720)),
                                                                                   entry("Recommendation", "Best")));
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void sample_for(final BaseVariantTest.VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("sampleFor.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_5d7731f1-525d-4e75-a24a-39066f52ccdf", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("sampleFor")).asList().containsExactly(new BigDecimal(2), new BigDecimal(3), new BigDecimal(4));
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void sample_quantified(final BaseVariantTest.VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("sampleQuantified.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_480411d5-e8b4-422f-9a76-1e8929930ead", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("sampleEvery")).isEqualTo(Boolean.TRUE);
        assertThat(result.get("sampleSome")).isEqualTo(Boolean.TRUE);
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void sample_conditional(final BaseVariantTest.VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("sampleConditional.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_da2ac43a-133b-483d-9c08-958d10024584", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("sampleConditional")).isEqualTo("Hello World");
    }

    @MethodSource("params")
    @ParameterizedTest(name = "{0}")
    public void sample_filter(final BaseVariantTest.VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = createRuntime("sampleFilter.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_e1291f4e-e828-4a47-86e8-474899d50185", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("sampleFilter")).asList().containsExactly(new BigDecimal(3), new BigDecimal(4));
    }
}
