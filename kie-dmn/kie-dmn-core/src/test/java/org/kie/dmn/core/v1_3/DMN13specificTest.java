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
import java.util.Arrays;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
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

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("salutation"), is("Hello, John"));
    }

    @Test
    public void testDMNv1_3_ch11() {
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
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Strategy"), is("THROUGH"));
        assertThat(result.get("Routing"), is("ACCEPT"));
    }

    @Test
    public void testDMNv1_3_ch11_asSpecInputDataValues() {
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
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Strategy"), is("THROUGH"));
        assertThat(result.get("Routing"), is("ACCEPT"));
    }

    @Test
    public void testDMNv1_3_ch11_Example2() {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Recommended Loan Products.dmn", this.getClass(), "Loan info.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_736fa164-03d8-429f-8318-4913a548c3a6", "Recommended Loan Products");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("Credit Score", new BigDecimal(735));
        context.set("Property", mapOf(entry("Address", mapOf(entry("Street", "272 10th St."),
                                                             entry("Unit", null),
                                                             entry("City", "Marina"),
                                                             entry("State", "CA"),
                                                             entry("ZIP", "93933"))),
                                      entry("Purchase Price", new BigDecimal(340_000)),
                                      entry("Monthly Tax Payment", new BigDecimal(350)),
                                      entry("Monthly Insurance Payment",new BigDecimal( 100)),
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

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Recommended Loan Products"), is(Arrays.asList(mapOf(entry("Product", "Lender B - ARM5/1-Standard"),
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
                                                                                   entry("Recommendation", "Best")))));
    }
       
}
