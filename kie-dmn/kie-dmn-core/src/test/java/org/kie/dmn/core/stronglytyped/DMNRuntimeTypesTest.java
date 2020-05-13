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
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

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
    }
}

