/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.stronglytyped;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.EvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;

public class DMNRuntimeTypesTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeTypesTest.class);

    public DMNRuntimeTypesTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Test
    public void testOneOfEachType() {
        final DMNRuntime runtime = createRuntime("OneOfEachType.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_4f5608e9-4d74-4c22-a47e-ab657257fc9c", "OneOfEachType");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("InputString", "John Doe");
        context.set("InputNumber", BigDecimal.ONE);
        context.set("InputBoolean", true);
        context.set("InputDTDuration", Duration.parse("P1D"));
        context.set("InputYMDuration", Period.parse("P1M"));
        context.set("InputDateAndTime", LocalDateTime.of(2020, 4, 2, 9, 0));
        context.set("InputDate", LocalDate.of(2020, 4, 2));
        context.set("InputTime", LocalTime.of(9, 0));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("DecisionString").getResult(), is("Hello, John Doe"));
        assertThat(dmnResult.getDecisionResultByName("DecisionNumber").getResult(), is(new BigDecimal(2)));
        assertThat(dmnResult.getDecisionResultByName("DecisionBoolean").getResult(), is(false));
        assertThat(dmnResult.getDecisionResultByName("DecisionDTDuration").getResult(), is(Duration.parse("P2D")));
        assertThat(dmnResult.getDecisionResultByName("DecisionYMDuration").getResult(), is(ComparablePeriod.parse("P2M")));
        assertThat(dmnResult.getDecisionResultByName("DecisionDateAndTime").getResult(), is(LocalDateTime.of(2020, 4, 2, 10, 0)));
        assertThat(dmnResult.getDecisionResultByName("DecisionDate").getResult(), is(LocalDate.of(2020, 4, 3)));
        assertThat(dmnResult.getDecisionResultByName("DecisionTime").getResult(), is(LocalTime.of(10, 0)));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("DecisionString"), is("Hello, John Doe"));
            assertThat(allProperties.get("DecisionNumber"), is(new BigDecimal(2)));
            assertThat(allProperties.get("DecisionBoolean"), is(false));
            assertThat(allProperties.get("DecisionDTDuration"), is(Duration.parse("P2D")));
            assertThat(allProperties.get("DecisionYMDuration"), is(ComparablePeriod.parse("P2M")));
            assertThat(allProperties.get("DecisionDateAndTime"), is(LocalDateTime.of(2020, 4, 2, 10, 0)));
            assertThat(allProperties.get("DecisionDate"), is(LocalDate.of(2020, 4, 3)));
            assertThat(allProperties.get("DecisionTime"), is(LocalTime.of(10, 0)));
        }
    }

    @Test
    public void testJavaKeywords() {
        final DMNRuntime runtime = createRuntime("javaKeywords.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_C41C1676-0DA9-47EA-90AD-F9BAA257129F", "A1B1A8AD-B0DC-453D-86A7-C9475450C982");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        Map<String, Object> aThing = mapOf(entry("name", "name"),
                                           entry("const", "const"),
                                           entry("class", "class"));
        context.set("a thing", aThing);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult(), is("nameconstclass"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Decision-1"), is("nameconstclass"));
        }
    }

    @Test
    public void testInnerComposite() {
        final DMNRuntime runtime = createRuntime("innerComposite.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_641BCEBF-8D10-4E08-B47F-A9181C737A82", "new-file");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        Map<String, Object> yearly = mapOf(entry("Q1", 1),
                                           entry("Q2", new BigDecimal(2)),
                                           entry("Q3", 3),
                                           entry("Q4", new BigDecimal(4)));
        context.set("Yearly", yearly);

        Map<String, Object> employee = mapOf(entry("Name", "John Doe"),
                                             entry("Yearly", mapOf(entry("H1", 1),
                                                                   entry("H2", new BigDecimal(2)))));
        context.set("Employee", employee);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Decision Yearly").getResult(), is("Total Yearly 10"));
        assertThat(dmnResult.getDecisionResultByName("Decision Employee").getResult(), is("For John Doe total: 3"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Decision Yearly"), is("Total Yearly 10"));
            assertThat(allProperties.get("Decision Employee"), is("For John Doe total: 3"));
        }
    }

    @Test
    public void testFixInnerComposite() {
        final DMNRuntime runtime = createRuntime("fixInnerComposite.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_E82058C1-27D3-44F3-B1B3-4C02D17B7A05", "new-file");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        Map<String, Object> employee = mapOf(entry("Name", "John Doe"),
                                             entry("Marital Status", "S"));
        context.set("InputData-1", employee);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult(), is("John Doe is S"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Decision-1"), is("John Doe is S"));
        }
    }

    @Test
    public void testInnerCompositeCollection() {
        final DMNRuntime runtime = createRuntime("innerCompositeCollection.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_D8AE5AF4-1F9E-4423-873A-B8F3C3BE5FE5", "new-file");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        List<?> pairs = Arrays.asList(mapOf(entry("letter", "A"), entry("num", new BigDecimal(1))),
                                      mapOf(entry("letter", "B"), entry("num", new BigDecimal(2))),
                                      mapOf(entry("letter", "C"), entry("num", new BigDecimal(3))));
        Map<String, Object> person = mapOf(entry("full name", "John Doe"),
                                           entry("pairs", pairs));
        context.set("person", person);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult(), is("John Doe has 3 pairs."));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Decision-1"), is("John Doe has 3 pairs."));
        }
    }

    @Test
    public void testInputAny() {
        final DMNRuntime runtime = createRuntime("inputAny.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_7D9140EF-DC52-4DC1-8983-9C2EC5B89BAE", "new-file");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("Input Any", "John Doe");

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult(), is("Decision: John Doe"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Decision-1"), is("Decision: John Doe"));
        }
    }

    @Test
    public void testRecursiveEmployee() {
        final DMNRuntime runtime = createRuntime("recursiveEmployee.dmn", this.getClass());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_d1e3d83e-230d-42fb-bc58-313463f7f40b", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        Map<String, Object> report1 = mapOf(entry("full name", "Bob"),
                                            entry("age", new BigDecimal(48)),
                                            entry("manager", null), // in FEEL there cannot be recursion in values, only in type definitions; these nulls are expected.
                                            entry("direct reports", null));
        Map<String, Object> report2 = mapOf(entry("full name", "Carl"),
                                            entry("age", new BigDecimal(49)),
                                            entry("manager", null),
                                            entry("direct reports", null));
        Map<String, Object> mgr = mapOf(entry("full name", "John's Manager"),
                                        entry("age", new BigDecimal(46)),
                                        entry("manager", null),
                                        entry("direct reports", null));
        Map<String, Object> john = mapOf(entry("full name", "John Doe"),
                                         entry("age", new BigDecimal(47)),
                                         entry("manager", mgr),
                                         entry("direct reports", Arrays.asList(report1, report2)));

        final DMNContext context = DMNFactory.newContext();
        context.set("an Employee", john);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("highlights").getResult(), is("John Doe: reports to John's Manager and is manager of 2 : [ Bob, Carl ]"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("highlights"), is("John Doe: reports to John's Manager and is manager of 2 : [ Bob, Carl ]"));
        }
    }

    @Test
    public void testListBasic() {
        final DMNRuntime runtime = createRuntime("listBasic.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_B84B17F3-3E84-4DED-996E-AA630A6BF9C4", "new-file");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("listNumber", Arrays.asList(1, 2, 3));
        context.set("vowel", "e");
        context.set("listVowel", Arrays.asList("a", "e"));
        context.set("justA", "a");
        context.set("listOfA", Arrays.asList("a"));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("DecisionListNumber").getResult(), is(new BigDecimal(3)));
        assertThat(dmnResult.getDecisionResultByName("DecisionVowel").getResult(), is("the e"));
        assertThat(dmnResult.getDecisionResultByName("DecisionListVowel").getResult(), is(new BigDecimal(2)));
        assertThat(dmnResult.getDecisionResultByName("DecisionJustA").getResult(), is("the a"));
        assertThat(dmnResult.getDecisionResultByName("DecisionListOfA").getResult(), is(new BigDecimal(1)));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("DecisionListNumber"), is(new BigDecimal(3)));
            assertThat(allProperties.get("DecisionVowel"), is("the e"));
            assertThat(allProperties.get("DecisionListVowel"), is(new BigDecimal(2)));
            assertThat(allProperties.get("DecisionJustA"), is("the a"));
            assertThat(allProperties.get("DecisionListOfA"), is(new BigDecimal(1)));
        }
    }

    @Test
    public void testListBasic_LOVerror() {
        final DMNRuntime runtime = createRuntime("listBasic.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_B84B17F3-3E84-4DED-996E-AA630A6BF9C4", "new-file");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("listNumber", Arrays.asList(1, 2, 3));
        context.set("vowel", "x"); // fails allowedValues
        context.set("listVowel", Arrays.asList("a", "x")); // fails allowedValues of the inner type
        context.set("justA", "e"); // fails allowedValues
        context.set("listOfA", Arrays.asList("e")); // fails allowedValues of the inner type

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(true));
        assertThat(dmnResult.getDecisionResultByName("DecisionListNumber").getResult(), is(new BigDecimal(3)));
        assertThat(dmnResult.getDecisionResultByName("DecisionVowel").getEvaluationStatus(), not(DecisionEvaluationStatus.SUCCEEDED));
        assertThat(dmnResult.getDecisionResultByName("DecisionListVowel").getEvaluationStatus(), not(DecisionEvaluationStatus.SUCCEEDED));
        assertThat(dmnResult.getDecisionResultByName("DecisionJustA").getEvaluationStatus(), not(DecisionEvaluationStatus.SUCCEEDED));
        assertThat(dmnResult.getDecisionResultByName("DecisionListOfA").getEvaluationStatus(), not(DecisionEvaluationStatus.SUCCEEDED));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("DecisionListNumber"), is(new BigDecimal(3)));
            assertThat(allProperties.get("DecisionVowel"), nullValue());
            assertThat(allProperties.get("DecisionListVowel"), nullValue());
            assertThat(allProperties.get("DecisionJustA"), nullValue());
            assertThat(allProperties.get("DecisionListOfA"), nullValue());
        }
    }

    @Test
    public void testSameTypeNameMultiple() {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("class_imported.dmn", this.getClass(), "class_importing.dmn");
        final DMNModel dmnModel0 = runtime.getModel("http://www.trisotech.com/definitions/_b3deed2b-245f-4cc4-a4bf-1e95cd240664", "imported");
        assertThat(dmnModel0, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel0.getMessages()), dmnModel0.hasErrors(), is(false));

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_17540606-3d41-40f4-85f6-ad9e8faa8a87", "importing");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        Map<String, Object> importedClass = mapOf(entry("L1name", "L1name"),
                                                  entry("class", mapOf(entry("L2name", "L2name"))));
        context.set("imported class", importedClass);
        Map<String, Object> class_ = mapOf(entry("name", "name"));
        context.set("class", class_);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("decision1").getResult(), is("L1nameL2namename"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("decision1"), is("L1nameL2namename"));
        }
    }

    @Test
    public void testFieldCapitalization() {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Traffic Violation.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF", "Traffic Violation");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("Driver",
                    mapOf(
                            entry("Name", "Luca"),
                            entry("Age", 35),
                            entry("State", "Italy"),
                            entry("City", "Milan"),
                            entry("Points", 2000)
                          )
        );
        context.set("Violation", mapOf(
                entry("Code", "s"),
                entry("Date", LocalDate.of(1984, 11, 6)),
                entry("Type", "speed"),
                entry("Actual Speed", 120),
                entry("Speed Limit", 100)));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Should the driver be suspended?").getResult(), is("Yes"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible driver = (FEELPropertyAccessible)allProperties.get("Driver");
            assertThat(driver.getClass().getSimpleName(), is("TDriver"));
            assertThat(driver.getFEELProperty("Name").toOptional().get(), is("Luca"));
            assertThat(driver.getFEELProperty("Age").toOptional().get(), is(35));
            assertThat(driver.getFEELProperty("State").toOptional().get(), is("Italy"));
            assertThat(driver.getFEELProperty("City").toOptional().get(), is("Milan"));
            assertThat(driver.getFEELProperty("Points").toOptional().get(), is(2000));

            FEELPropertyAccessible violation = (FEELPropertyAccessible)allProperties.get("Violation");
            assertThat(violation.getClass().getSimpleName(), is("TViolation"));
            assertThat(violation.getFEELProperty("Code").toOptional().get(), is("s"));
            assertThat(violation.getFEELProperty("Date").toOptional().get(), is(LocalDate.of(1984, 11, 6)));
            assertThat(violation.getFEELProperty("Type").toOptional().get(), is("speed"));
            assertThat(violation.getFEELProperty("Actual Speed").toOptional().get(), is(120));
            assertThat(violation.getFEELProperty("Speed Limit").toOptional().get(), is(100));

            FEELPropertyAccessible fine = (FEELPropertyAccessible)allProperties.get("Fine");
            assertThat(fine.getClass().getSimpleName(), is("TFine"));
            assertThat(fine.getFEELProperty("Amount").toOptional().get(), is(new BigDecimal("500")));
            assertThat(fine.getFEELProperty("Points").toOptional().get(), is(new BigDecimal("3")));

            Object suspended = allProperties.get("Should the driver be suspended?");
            assertThat(suspended, instanceOf(String.class));
            assertThat(suspended, is("Yes"));
        }
    }

    @Test
    public void testDecisionService() {
        final DMNRuntime runtime = createRuntime("DecisionServiceABC_DMN12.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_2443d3f5-f178-47c6-a0c9-b1fd1c933f60", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        // DecisionService only
        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult1 = evaluateDecisionService(runtime, dmnModel, context, "Decision Service ABC");
        LOG.debug("{}", dmnResult1);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult1.getMessages()), dmnResult1.hasErrors(), is(false));

        final DMNContext result = dmnResult1.getContext();
        assertThat(result.getAll(), not(hasEntry(is("Invoking Decision"), anything()))); // we invoked only the Decision Service, not this other Decision in the model.
        assertThat(result.get("ABC"), is("abc"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult1);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Invoking Decision"), nullValue());
            Object abc = allProperties.get("ABC");
            assertThat(abc, instanceOf(String.class));
            assertThat(abc, is("abc"));
        }

        // evaluateAll
        final DMNContext context2 = DMNFactory.newContext();

        final DMNResult dmnResult2 = runtime.evaluateAll(dmnModel, context2);
        LOG.debug("{}", dmnResult2);
        dmnResult2.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult2.getMessages()), dmnResult2.hasErrors(), is(false));

        final DMNContext result2 = dmnResult2.getContext();
        assertThat(result2.get("ABC"), is("abc"));
        assertThat(result2.get("Invoking Decision"), is("abc"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult2);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            Object decisionService = allProperties.get("Decision Service ABC");
            assertThat(decisionService, instanceOf(FEELFunction.class));
            assertThat(((FEELFunction)decisionService).getName(), is("Decision Service ABC"));
            Object invokingDecision = allProperties.get("Invoking Decision");
            assertThat(invokingDecision, instanceOf(String.class));
            assertThat(invokingDecision, is("abc"));
            Object abc = allProperties.get("ABC");
            assertThat(abc, instanceOf(String.class));
            assertThat(abc, is("abc"));
        }
    }

    @Test
    public void testBKM() {
        final DMNRuntime runtime = createRuntime("0009-invocation-arithmetic.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_cb28c255-91cd-4c01-ac7b-1a9cb1ecdb11", "literal invocation1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final Map<String, Object> loan = new HashMap<>();
        loan.put("amount", BigDecimal.valueOf(600000));
        loan.put("rate", new BigDecimal("0.0375"));
        loan.put("term", BigDecimal.valueOf(360));
        final DMNContext context = DMNFactory.newContext();
        context.set("fee", 100);
        context.set("Loan", loan);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));
        assertThat(
                   ((BigDecimal) dmnResult.getContext().get("MonthlyPayment")).setScale(8, BigDecimal.ROUND_DOWN),
                   is(new BigDecimal("2878.69354943277").setScale(8, BigDecimal.ROUND_DOWN)));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            Object fee = allProperties.get("fee");
            assertThat(fee, is(100));
            FEELPropertyAccessible loanOut = (FEELPropertyAccessible)allProperties.get("Loan");
            assertThat(loanOut.getClass().getSimpleName(), is("TLoan"));
            assertThat(loanOut.getFEELProperty("amount").toOptional().get(), is(BigDecimal.valueOf(600000)));
            assertThat(loanOut.getFEELProperty("rate").toOptional().get(), is(new BigDecimal("0.0375")));
            assertThat(loanOut.getFEELProperty("term").toOptional().get(), is(BigDecimal.valueOf(360)));
            Object bkm = allProperties.get("PMT");
            assertThat(bkm, instanceOf(FEELFunction.class));
            assertThat(((FEELFunction)bkm).getName(), is("PMT"));
            Object monthlyPayment = allProperties.get("MonthlyPayment");
            assertThat(((BigDecimal) monthlyPayment).setScale(8, BigDecimal.ROUND_DOWN),
                       is(new BigDecimal("2878.69354943277").setScale(8, BigDecimal.ROUND_DOWN)));
        }
    }

    @Ignore
    @Test
    public void testCapitalLetterConflict() {
        // To be fixed by DROOLS-5518
        final DMNRuntime runtime = createRuntime("capitalLetterConflict.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_B321C9B1-856E-45DE-B05D-5B4D4D301D37", "capitalLetterConflict");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final Map<String, Object> myPerson = new HashMap<>();
        myPerson.put("name", "John");
        myPerson.put("age", 28);
        final Map<String, Object> myPersonCapital = new HashMap<>();
        myPersonCapital.put("name", "Paul");
        myPersonCapital.put("age", 26);

        final DMNContext context = DMNFactory.newContext();
        context.set("myPerson", myPerson);
        context.set("MyPerson", myPersonCapital);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getContext().get("myDecision"), is("myDecision is John"));
        assertThat(dmnResult.getContext().get("MyDecision"), is("MyDecision is Paul"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible myPersonOut = (FEELPropertyAccessible) allProperties.get("myPerson");
            assertThat(myPersonOut.getClass().getSimpleName(), is("TPerson"));
            assertThat(myPersonOut.getFEELProperty("name").toOptional().get(), is("John"));
            assertThat(myPersonOut.getFEELProperty("age").toOptional().get(), is(28));
            FEELPropertyAccessible myPersonCapitalOut = (FEELPropertyAccessible) allProperties.get("MyPerson");
            assertThat(myPersonCapitalOut.getClass().getSimpleName(), is("TPerson"));
            assertThat(myPersonCapitalOut.getFEELProperty("name").toOptional().get(), is("Paul"));
            assertThat(myPersonCapitalOut.getFEELProperty("age").toOptional().get(), is(26));
            Object myDecision = (String) allProperties.get("myDecision");
            assertThat(myDecision, is("myDecision is John"));
            Object myDecisionCapital = (String) allProperties.get("MyDecision");
            assertThat(myDecisionCapital, is("MyDecision is Paul"));
        }
    }

    @Ignore
    @Test
    public void testCapitalLetterConflictWithInputAndDecision() {
        // To be fixed by DROOLS-5518
        final DMNRuntime runtime = createRuntime("capitalLetterConflictWithInputAndDecision.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_EE9DAFC0-D50D-4D23-8676-FF8A40E02919", "capitalLetterConflictWithInputAndDecision");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final Map<String, Object> person = new HashMap<>();
        person.put("name", "John");
        person.put("age", 28);

        final DMNContext context = DMNFactory.newContext();
        context.set("myNode", person);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getContext().get("MyNode"), is("MyNode is John"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible myPersonOut = (FEELPropertyAccessible) allProperties.get("myNode");
            assertThat(myPersonOut.getClass().getSimpleName(), is("TPerson"));
            assertThat(myPersonOut.getFEELProperty("name").toOptional().get(), is("John"));
            assertThat(myPersonOut.getFEELProperty("age").toOptional().get(), is(28));
            Object myDecision = (String) allProperties.get("MyNode");
            assertThat(myDecision, is("MyNode is John"));
        }
    }

    @Test
    public void testShareTypeForInputAndOutput() {
        final DMNRuntime runtime = createRuntime("shareTypeForInputAndOutput.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_DBEFBA7B-C568-4631-A89E-AA31F7C6564B", "shareTypeForInputAndOutput");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final Map<String, Object> person = new HashMap<>();
        person.put("name", "John");
        person.put("age", 28);
        person.put("employmentPeriod", Period.of(1, 2, 1));

        final DMNContext context = DMNFactory.newContext();
        context.set("inputPerson", person);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        Map<String, Object> outputPerson = (Map<String, Object>) dmnResult.getContext().get("outputPerson");
        assertThat(outputPerson.get("name"), is("Paul"));
        assertThat(outputPerson.get("age"), is(new BigDecimal(20)));
        assertThat(outputPerson.get("employmentPeriod"), is(ComparablePeriod.of(1, 3, 1)));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible myPersonOut = (FEELPropertyAccessible) allProperties.get("outputPerson");
            assertThat(myPersonOut.getClass().getSimpleName(), is("TPerson"));
            assertThat(myPersonOut.getFEELProperty("name").toOptional().get(), is("Paul"));
            assertThat(myPersonOut.getFEELProperty("age").toOptional().get(), is(new BigDecimal(20)));
            assertThat(myPersonOut.getFEELProperty("employmentPeriod").toOptional().get(), is(ComparablePeriod.of(1, 3, 1)));
        }
    }

    @Ignore
    @Test
    public void testCollectionType() {
        // To be fixed by DROOLS-5538
        final DMNRuntime runtime = createRuntime("PersonListHelloBKM2.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_7e41a76e-2df6-4899-bf81-ae098757a3b6", "PersonListHelloBKM2");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();

        final Map<String, Object> p1 = prototype(entry("Full Name", "John Doe"), entry("Age", 33));
        final Map<String, Object> p2 = prototype(entry("Full Name", "47"), entry("Age", 47));

        context.set("My Input Data", Arrays.asList(p1, p2));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        assertThat((List<?>) dmnResult.getContext().get("My Decision"),
                contains(prototype(entry("Full Name", "Prof. John Doe"), entry("Age", EvalHelper.coerceNumber(33))),
                         prototype(entry("Full Name", "Prof. 47"), entry("Age", EvalHelper.coerceNumber(47)))));

        // Add typeSafe assertion
    }

    @Test
    public void testComponentCollection() {
        final DMNRuntime runtime = createRuntime("collections.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_2A93F258-EF3B-4150-A202-1D02A893DF2B", "collections");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();

        final Map<String, Object> addr1 = prototype(entry("city", "city1"), entry("street", "street1"));
        final Map<String, Object> addr2 = prototype(entry("city", "city2"), entry("street", "street2"));

        final Map<String, Object> person = prototype(entry("name", "John"), entry("addressList", Arrays.asList(addr1, addr2)));

        context.set("inputPerson", person);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        Map<String, Object> outputPerson = (Map<String, Object>)dmnResult.getContext().get("outputPerson");

        assertThat(outputPerson.get("name"), is("Paul"));
        Map<String, Object> outputAddr1 = (Map<String, Object>)((List)outputPerson.get("addressList")).get(0);
        Map<String, Object> outputAddr2 = (Map<String, Object>)((List)outputPerson.get("addressList")).get(1);

        assertThat(outputAddr1.get("city"), anyOf(is("cityA"), is("cityB")));
        assertThat(outputAddr1.get("street"), anyOf(is("streetA"), is("streetB")));
        assertThat(outputAddr2.get("city"), anyOf(is("cityA"), is("cityB")));
        assertThat(outputAddr2.get("street"), anyOf(is("streetA"), is("streetB")));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible typedOutputPerson = (FEELPropertyAccessible) allProperties.get("outputPerson");
            assertThat(typedOutputPerson.getClass().getSimpleName(), is("TPerson"));
            assertThat(typedOutputPerson.getFEELProperty("name").toOptional().get(), is("Paul"));
            List<FEELPropertyAccessible> addressList = (List<FEELPropertyAccessible>)typedOutputPerson.getFEELProperty("addressList").toOptional().get();
            FEELPropertyAccessible typedOutputAddr1 = addressList.get(0);
            FEELPropertyAccessible typedOutputAddr2 = addressList.get(1);
            assertThat(typedOutputAddr1.getFEELProperty("city").toOptional().get(), anyOf(is("cityA"), is("cityB")));
            assertThat(typedOutputAddr1.getFEELProperty("street").toOptional().get(), anyOf(is("streetA"), is("streetB")));
            assertThat(typedOutputAddr2.getFEELProperty("city").toOptional().get(), anyOf(is("cityA"), is("cityB")));
            assertThat(typedOutputAddr2.getFEELProperty("street").toOptional().get(), anyOf(is("streetA"), is("streetB")));
        }
    }

    @Test
    public void testComponentCollectionPassTypedObject() {
        final DMNRuntime runtime = createRuntime("collectionsPassTypedObject.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_10C4DB2B-1DCA-4B4F-A994-FA046AE5C7B0", "collectionsPassTypedObject");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();

        final Map<String, Object> addr1 = prototype(entry("city", "city1"), entry("street", "street1"));
        final Map<String, Object> addr2 = prototype(entry("city", "city2"), entry("street", "street2"));

        final Map<String, Object> person = prototype(entry("name", "John"), entry("addressList", Arrays.asList(addr1, addr2)));

        context.set("inputPerson", person);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        Map<String, Object> outputPerson = (Map<String, Object>) dmnResult.getContext().get("outputPerson");

        assertThat(outputPerson.get("name"), is("Paul"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = convertToOutputSet(dmnModel, dmnResult);
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible typedOutputPerson = (FEELPropertyAccessible) allProperties.get("outputPerson");
            assertThat(typedOutputPerson.getClass().getSimpleName(), is("TPerson"));
            assertThat(typedOutputPerson.getFEELProperty("name").toOptional().get(), is("Paul"));
            List<FEELPropertyAccessible> addressList = (List<FEELPropertyAccessible>) typedOutputPerson.getFEELProperty("addressList").toOptional().get();
            FEELPropertyAccessible typedOutputAddr1 = addressList.get(0);
            FEELPropertyAccessible typedOutputAddr2 = addressList.get(1);
            assertThat(typedOutputAddr1.getFEELProperty("city").toOptional().get(), anyOf(is("city1"), is("city2")));
            assertThat(typedOutputAddr1.getFEELProperty("street").toOptional().get(), anyOf(is("street1"), is("street2")));
            assertThat(typedOutputAddr2.getFEELProperty("city").toOptional().get(), anyOf(is("city1"), is("city2")));
            assertThat(typedOutputAddr2.getFEELProperty("street").toOptional().get(), anyOf(is("street1"), is("street2")));
        } else {
            // if TypeSafe, ((List) outputPerson.get("addressList")).get(0) returns TAddress
            Map<String, Object> outputAddr1 = (Map<String, Object>) ((List) outputPerson.get("addressList")).get(0);
            Map<String, Object> outputAddr2 = (Map<String, Object>) ((List) outputPerson.get("addressList")).get(1);

            assertThat(outputAddr1.get("city"), anyOf(is("city1"), is("city2")));
            assertThat(outputAddr1.get("street"), anyOf(is("street1"), is("street2")));
            assertThat(outputAddr2.get("city"), anyOf(is("city1"), is("city2")));
            assertThat(outputAddr2.get("street"), anyOf(is("street1"), is("street2")));
        }
    }
}
