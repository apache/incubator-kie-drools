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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.DMNRuntimeTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.v1_2.DMNDecisionServicesTest;
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
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;
import static org.kie.dmn.feel.util.EvalHelper.coerceNumber;

public class DMNRuntimeTypesTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeTypesTest.class);

    public DMNRuntimeTypesTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Test
    public void testOneOfEachType() throws Exception {
        final DMNRuntime runtime = createRuntime("OneOfEachType.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_4f5608e9-4d74-4c22-a47e-ab657257fc9c", "OneOfEachType");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNContext context = DMNFactory.newContext();
        if (!isTypeSafe()) {
            context.set("InputString", "John Doe");
            context.set("InputNumber", BigDecimal.ONE);
            context.set("InputBoolean", true);
            context.set("InputDTDuration", Duration.parse("P1D"));
            context.set("InputYMDuration", Period.parse("P1M"));
            context.set("InputDateAndTime", LocalDateTime.of(2020, 4, 2, 9, 0));
            context.set("InputDate", LocalDate.of(2020, 4, 2));
            context.set("InputTime", LocalTime.of(9, 0));
        } else {
            JsonMapper mapper = JsonMapper.builder()
                                          .addModule(new JavaTimeModule())
                                          .build();
            final String JSON = "{\n" +
                                "    \"InputBoolean\": true,\n" +
                                "    \"InputDTDuration\": \"P1D\",\n" +
                                "    \"InputDate\": \"2020-04-02\",\n" +
                                "    \"InputDateAndTime\": \"2020-04-02T09:00:00\",\n" +
                                "    \"InputNumber\": 1,\n" +
                                "    \"InputString\": \"John Doe\",\n" +
                                "    \"InputTime\": \"09:00\",\n" +
                                "    \"InputYMDuration\": \"P1M\"\n" +
                                "}";
            Class<?> inputSetClass = getStronglyClassByName(dmnModel, "InputSet");
            FEELPropertyAccessible inputSet = (FEELPropertyAccessible) mapper.readValue(JSON, inputSetClass);
            context = new DMNContextFPAImpl(inputSet);
        }

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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
        final DMNRuntime runtime = createRuntime("DecisionServiceABC_DMN12.dmn", DMNDecisionServicesTest.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_2443d3f5-f178-47c6-a0c9-b1fd1c933f60", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        // DecisionService only
        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult1 = evaluateDecisionService(runtime, dmnModel, context, "Decision Service ABC");
        LOG.debug("{}", dmnResult1);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult1.getMessages()), dmnResult1.hasErrors(), is(false));

        final DMNContext result = dmnResult1.getContext();
        // assertThat(result.getAll(), not(hasEntry(is("Invoking Decision"), anything()))); // we invoked only the Decision Service, not this other Decision in the model.
        assertThat(result.get("Invoking Decision"), nullValue());
        assertThat(result.get("ABC"), is("abc"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult1.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Invoking Decision"), nullValue());
            Object abc = allProperties.get("ABC");
            assertThat(abc, instanceOf(String.class));
            assertThat(abc, is("abc"));
        }

        // evaluateAll
        final DMNContext context2 = DMNFactory.newContext();

        final DMNResult dmnResult2 = evaluateModel(runtime, dmnModel, context2);
        LOG.debug("{}", dmnResult2);
        dmnResult2.getDecisionResults().forEach(x -> LOG.debug("{}", x));
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult2.getMessages()), dmnResult2.hasErrors(), is(false));

        final DMNContext result2 = dmnResult2.getContext();
        assertThat(result2.get("ABC"), is("abc"));
        assertThat(result2.get("Invoking Decision"), is("abc"));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult2.getContext()).getFpa();
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
        final DMNRuntime runtime = createRuntime("0009-invocation-arithmetic.dmn", DMNRuntimeTest.class);
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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

    @Test
    public void testCapitalLetterConflict() {
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible myPersonOut = (FEELPropertyAccessible) allProperties.get("myPerson");
            assertThat(myPersonOut.getClass().getSimpleName(), is("TPerson"));
            assertThat(myPersonOut.getFEELProperty("name").toOptional().get(), is("John"));
            assertThat(EvalHelper.coerceNumber(myPersonOut.getFEELProperty("age").toOptional().get()), is(EvalHelper.coerceNumber(28)));
            FEELPropertyAccessible myPersonCapitalOut = (FEELPropertyAccessible) allProperties.get("MyPerson");
            assertThat(myPersonCapitalOut.getClass().getSimpleName(), is("TPerson"));
            assertThat(myPersonCapitalOut.getFEELProperty("name").toOptional().get(), is("Paul"));
            assertThat(EvalHelper.coerceNumber(myPersonCapitalOut.getFEELProperty("age").toOptional().get()), is(EvalHelper.coerceNumber(26)));
            Object myDecision = (String) allProperties.get("myDecision");
            assertThat(myDecision, is("myDecision is John"));
            Object myDecisionCapital = (String) allProperties.get("MyDecision");
            assertThat(myDecisionCapital, is("MyDecision is Paul"));
        }
    }

    @Test
    public void testCapitalLetterConflictWithInputAndDecision() {
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
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible myPersonOut = (FEELPropertyAccessible) allProperties.get("myNode");
            assertThat(myPersonOut.getClass().getSimpleName(), is("TPerson"));
            assertThat(myPersonOut.getFEELProperty("name").toOptional().get(), is("John"));
            assertThat(EvalHelper.coerceNumber(myPersonOut.getFEELProperty("age").toOptional().get()), is(EvalHelper.coerceNumber(28)));
            Object myDecision = (String) allProperties.get("MyNode");
            assertThat(myDecision, is("MyNode is John"));
        }
    }

    @Test
    public void testCapitalLetterConflictItemDef() {
        final DMNRuntime runtime = createRuntime("capitalLetterConflictItemDef.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_DA986720-823F-4334-8AB5-5CBA76FD1B9E", "capitalLetterConflictItemDef");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final Map<String, Object> person = new HashMap<>();
        person.put("name", "john");
        person.put("Name", "John");

        final DMNContext context = DMNFactory.newContext();
        context.set("InputData-1", person);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible myPersonOut = (FEELPropertyAccessible) allProperties.get("Decision-1");
            assertThat(myPersonOut.getClass().getSimpleName(), is("TPerson"));
            assertThat(myPersonOut.getFEELProperty("name").toOptional().get(), is("paul"));
            assertThat(myPersonOut.getFEELProperty("Name").toOptional().get(), is("Paul"));
        } else {
            Map<String, Object> outPerson = (Map<String, Object>)dmnResult.getContext().get("Decision-1");
            assertThat(outPerson.get("name"), is("paul"));
            assertThat(outPerson.get("Name"), is("Paul"));
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

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible myPersonOut = (FEELPropertyAccessible) allProperties.get("outputPerson");
            assertThat(myPersonOut.getClass().getSimpleName(), is("TPerson"));
            assertThat(myPersonOut.getFEELProperty("name").toOptional().get(), is("Paul"));
            assertThat(myPersonOut.getFEELProperty("age").toOptional().get(), is(new BigDecimal(20)));
            assertThat(myPersonOut.getFEELProperty("employmentPeriod").toOptional().get(), is(ComparablePeriod.of(1, 3, 1)));
        } else {
            Map<String, Object> outputPerson = (Map<String, Object>) dmnResult.getContext().get("outputPerson");
            assertThat(outputPerson.get("name"), is("Paul"));
            assertThat(outputPerson.get("age"), is(new BigDecimal(20)));
            assertThat(outputPerson.get("employmentPeriod"), is(ComparablePeriod.of(1, 3, 1)));
        }
    }

    @Test
    public void testTopLevelTypeCollection() {
        final DMNRuntime runtime = createRuntime("PersonListHelloBKM2.dmn", DMNRuntimeTest.class);
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

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            List<FEELPropertyAccessible> personList = (List<FEELPropertyAccessible>) allProperties.get("My Decision");
            FEELPropertyAccessible person1 = personList.get(0);
            FEELPropertyAccessible person2 = personList.get(1);
            assertThat(person1.getFEELProperty("Full Name").toOptional().get(), anyOf(is("Prof. John Doe"),is("Prof. 47")));
            assertThat(person1.getFEELProperty("Age").toOptional().get(), anyOf(is(EvalHelper.coerceNumber(33)), is(EvalHelper.coerceNumber(47))));
            assertThat(person2.getFEELProperty("Full Name").toOptional().get(), anyOf(is("Prof. John Doe"),is("Prof. 47")));
            assertThat(person2.getFEELProperty("Age").toOptional().get(), anyOf(is(EvalHelper.coerceNumber(33)), is(EvalHelper.coerceNumber(47))));
        } else {
            assertThat((List<?>) dmnResult.getContext().get("My Decision"),
            contains(prototype(entry("Full Name", "Prof. John Doe"), entry("Age", EvalHelper.coerceNumber(33))),
                     prototype(entry("Full Name", "Prof. 47"), entry("Age", EvalHelper.coerceNumber(47)))));
        }
    }

    @Test
    public void testTopLevelCompositeCollection() {
        final DMNRuntime runtime = createRuntime("topLevelCompositeCollection.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "https://kiegroup.org/dmn/_3ED2F714-24F0-4764-88FA-04217901C05A", "topLevelCompositeCollection");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();

        List<?> pairs = Arrays.asList(mapOf(entry("letter", "A"), entry("num", new BigDecimal(1))),
                                      mapOf(entry("letter", "B"), entry("num", new BigDecimal(2))),
                                      mapOf(entry("letter", "C"), entry("num", new BigDecimal(3))));

        context.set("InputData-1", pairs);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            List<FEELPropertyAccessible> pairList = (List<FEELPropertyAccessible>) allProperties.get("Decision-1");
            FEELPropertyAccessible pair1 = pairList.get(0);
            FEELPropertyAccessible pair2 = pairList.get(1);
            assertThat(pair1.getFEELProperty("letter").toOptional().get(), anyOf(is("ABC"),is("DEF")));
            assertThat(pair1.getFEELProperty("num").toOptional().get(), anyOf(is(EvalHelper.coerceNumber(123)), is(EvalHelper.coerceNumber(456))));
            assertThat(pair2.getFEELProperty("letter").toOptional().get(), anyOf(is("ABC"),is("DEF")));
            assertThat(pair2.getFEELProperty("num").toOptional().get(), anyOf(is(EvalHelper.coerceNumber(123)), is(EvalHelper.coerceNumber(456))));
        } else {
            assertThat((List<?>) dmnResult.getContext().get("Decision-1"),
            contains(mapOf(entry("letter", "ABC"), entry("num", EvalHelper.coerceNumber(123))),
                     mapOf(entry("letter", "DEF"), entry("num", EvalHelper.coerceNumber(456)))));
        }
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

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
        } else {
            Map<String, Object> outputPerson = (Map<String, Object>)dmnResult.getContext().get("outputPerson");

            assertThat(outputPerson.get("name"), is("Paul"));
            Map<String, Object> outputAddr1 = (Map<String, Object>)((List)outputPerson.get("addressList")).get(0);
            Map<String, Object> outputAddr2 = (Map<String, Object>)((List)outputPerson.get("addressList")).get(1);

            assertThat(outputAddr1.get("city"), anyOf(is("cityA"), is("cityB")));
            assertThat(outputAddr1.get("street"), anyOf(is("streetA"), is("streetB")));
            assertThat(outputAddr2.get("city"), anyOf(is("cityA"), is("cityB")));
            assertThat(outputAddr2.get("street"), anyOf(is("streetA"), is("streetB")));
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

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
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
            Map<String, Object> outputPerson = (Map<String, Object>) dmnResult.getContext().get("outputPerson");

            assertThat(outputPerson.get("name"), is("Paul"));

            Map<String, Object> outputAddr1 = (Map<String, Object>) ((List) outputPerson.get("addressList")).get(0);
            Map<String, Object> outputAddr2 = (Map<String, Object>) ((List) outputPerson.get("addressList")).get(1);

            assertThat(outputAddr1.get("city"), anyOf(is("city1"), is("city2")));
            assertThat(outputAddr1.get("street"), anyOf(is("street1"), is("street2")));
            assertThat(outputAddr2.get("city"), anyOf(is("city1"), is("city2")));
            assertThat(outputAddr2.get("street"), anyOf(is("street1"), is("street2")));
        }
    }

    @Test
    public void testEvaluateByIdAndName() {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("2decisions.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_6453A539-85B5-4A4E-800E-6721C50B6B55", "2decisions");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("InputData-1", mapOf(entry("name", "John"), entry("age", 30)));

        final DMNResult dmnResult1 = evaluateById(runtime, dmnModel, context, "_0BD595AB-B8C6-4FBF-B2DD-BEB49420EDFE");

        assertThat(DMNRuntimeUtil.formatMessages(dmnResult1.getMessages()), dmnResult1.hasErrors(), is(false));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult1.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible person = (FEELPropertyAccessible)allProperties.get("Decision-1");
            assertThat(person.getClass().getSimpleName(), is("TPerson"));
            assertThat(person.getFEELProperty("name").toOptional().get(), is("Paul"));
            assertThat(person.getFEELProperty("age").toOptional().get(), is(EvalHelper.coerceNumber(28)));

            assertThat(allProperties.get("Decision-2"), nullValue());
        } else {
            Map<String, Object> person = (Map<String, Object>)dmnResult1.getContext().get("Decision-1");
            assertThat(person.get("name"), is("Paul"));
            assertThat(person.get("age"), is(EvalHelper.coerceNumber(28)));

            assertThat(dmnResult1.getContext().get("Decision-2"), nullValue());
        }

        final DMNResult dmnResult2 = evaluateByName(runtime, dmnModel, context, "Decision-2");

        assertThat(DMNRuntimeUtil.formatMessages(dmnResult2.getMessages()), dmnResult2.hasErrors(), is(false));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult2.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            FEELPropertyAccessible person = (FEELPropertyAccessible)allProperties.get("Decision-2");
            assertThat(person.getClass().getSimpleName(), is("TPerson"));
            assertThat(person.getFEELProperty("name").toOptional().get(), is("George"));
            assertThat(person.getFEELProperty("age").toOptional().get(), is(EvalHelper.coerceNumber(27)));

            assertThat(allProperties.get("Decision-1"), nullValue());
        } else {
            Map<String, Object> person = (Map<String, Object>)dmnResult2.getContext().get("Decision-2");
            assertThat(person.get("name"), is("George"));
            assertThat(person.get("age"), is(EvalHelper.coerceNumber(27)));

            assertThat(dmnResult2.getContext().get("Decision-1"), nullValue());
        }
    }

    public void testCollectionOfCollection() {
        final DMNRuntime runtime = createRuntime("topLevelColOfCol.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_74636626-ACB0-4A1F-9AD3-D4E0AFA1A24A", "topLevelColOfCol");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        // create ColB -> ColA -> Person data
        List<Map<String, Object>> personList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            personList.add(prototype(entry("name", "John" + i), entry("age", 20 + i)));
        }
        final List<Map<String, Object>> colA1 = personList.subList(0, 2);
        final List<Map<String, Object>> colA2 = personList.subList(2, 4);

        final List<List<Map<String, Object>>> colB = Arrays.asList(colA1, colA2);

        context.set("InputData-1", colB);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            List<List<FEELPropertyAccessible>> colBOut = (List<List<FEELPropertyAccessible>>) allProperties.get("Decision-1");
            List<FEELPropertyAccessible> colAOut1 = colBOut.get(0);
            assertPersonInCol(colAOut1.get(0));
            assertPersonInCol(colAOut1.get(1));
            List<FEELPropertyAccessible> colAOut2 = colBOut.get(1);
            assertPersonInCol(colAOut2.get(0));
            assertPersonInCol(colAOut2.get(1));
        } else {
            List<List<Map<String, Object>>> colBOut = (List<List<Map<String, Object>>>)dmnResult.getContext().get("Decision-1");
            List<Map<String, Object>> colAOut1 = colBOut.get(0);
            assertPersonMapInCol(colAOut1.get(0));
            assertPersonMapInCol(colAOut1.get(1));
            List<Map<String, Object>> colAOut2 = colBOut.get(1);
            assertPersonMapInCol(colAOut2.get(0));
            assertPersonMapInCol(colAOut2.get(1));
        }
    }

    private void assertPersonInCol(FEELPropertyAccessible person) {
        assertThat(person.getFEELProperty("name").toOptional().get(), anyOf(is("John0X"), is("John1X"), is("John2X"), is("John3X")));
        assertThat(person.getFEELProperty("age").toOptional().get(), anyOf(is(coerceNumber(21)), is(coerceNumber(22)), is(coerceNumber(23)), is(coerceNumber(24))));
    }

    private void assertPersonMapInCol(Map<String, Object> personMap) {
        assertThat(personMap.get("name"), anyOf(is("John0X"), is("John1X"), is("John2X"), is("John3X")));
        assertThat(personMap.get("age"), anyOf(is(coerceNumber(21)), is(coerceNumber(22)), is(coerceNumber(23)), is(coerceNumber(24))));
    }

    @Test
    public void testCollectionOfCollectionOfCollection() {
        final DMNRuntime runtime = createRuntime("topLevelColOfColOfCol.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_74636626-ACB0-4A1F-9AD3-D4E0AFA1A24A", "topLevelColOfColOfCol");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();

        // create ColC -> ColB -> ColA -> Person data
        List<Map<String, Object>> personList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            personList.add(prototype(entry("name", "John" + i), entry("age", 20 + i)));
        }
        final List<Map<String, Object>> colA1 = personList.subList(0, 2);
        final List<Map<String, Object>> colA2 = personList.subList(2, 3);
        final List<Map<String, Object>> colA3 = personList.subList(4, 6);
        final List<Map<String, Object>> colA4 = personList.subList(6, 8);

        final List<List<Map<String, Object>>> colB1 = Arrays.asList(colA1, colA2);
        final List<List<Map<String, Object>>> colB2 = Arrays.asList(colA3, colA4);

        final List<List<List<Map<String, Object>>>> colC = Arrays.asList(colB1, colB2);

        context.set("InputData-1", colC);

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(dmnResult.hasErrors(), is(false));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            List<List<List<FEELPropertyAccessible>>> colCOut = (List<List<List<FEELPropertyAccessible>>>) allProperties.get("Decision-1");
            List<FEELPropertyAccessible>  personOutList = colCOut.stream().flatMap(colB -> colB.stream()).flatMap(colA -> colA.stream()).collect(Collectors.toList());
            personOutList.stream().forEach(person -> assertPersonInDeepCol(person));
        } else {
            List<List<List<Map<String, Object>>>> colCOut = (List<List<List<Map<String, Object>>>>)dmnResult.getContext().get("Decision-1");
            List<Map<String, Object>>  personOutList = colCOut.stream().flatMap(colB -> colB.stream()).flatMap(colA -> colA.stream()).collect(Collectors.toList());
            personOutList.stream().forEach(person -> assertPersonMapInDeepCol(person));
        }
    }

    private void assertPersonInDeepCol(FEELPropertyAccessible person) {
        assertThat(person.getFEELProperty("name").toOptional().get(),
                   anyOf(is("John0X"), is("John1X"), is("John2X"), is("John3X"),
                         is("John4X"), is("John5X"), is("John6X"), is("John7X")));
        assertThat(person.getFEELProperty("age").toOptional().get(),
                   anyOf(is(coerceNumber(21)), is(coerceNumber(22)), is(coerceNumber(23)), is(coerceNumber(24)),
                         is(coerceNumber(25)), is(coerceNumber(26)), is(coerceNumber(27)), is(coerceNumber(28))));
    }

    private void assertPersonMapInDeepCol(Map<String, Object> personMap) {
        assertThat(personMap.get("name"),
                   anyOf(is("John0X"), is("John1X"), is("John2X"), is("John3X"),
                         is("John4X"), is("John5X"), is("John6X"), is("John7X")));
        assertThat(personMap.get("age"),
                   anyOf(is(coerceNumber(21)), is(coerceNumber(22)), is(coerceNumber(23)), is(coerceNumber(24)),
                         is(coerceNumber(25)), is(coerceNumber(26)), is(coerceNumber(27)), is(coerceNumber(28))));
    }
}
