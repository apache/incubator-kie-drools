/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.impl.DMNContextFPAImpl;
import org.kie.dmn.core.model.Person;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK_TYPESAFE;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK_TYPESAFE;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;

/* These are duplicated test that are run against the Typesafe DMN, see https://issues.redhat.com/browse/DROOLS-5061 */
public class DMNStronglyTypedSupportTest extends BaseVariantTest {

    public DMNStronglyTypedSupportTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{KIE_API_TYPECHECK, BUILDER_DEFAULT_NOCL_TYPECHECK, BUILDER_DEFAULT_NOCL_TYPECHECK_TYPESAFE, KIE_API_TYPECHECK_TYPESAFE};
    }

    @Test
    public void testDMNInputDataNodeTypeTest() {
        // DROOLS-1569
        final DMNRuntime runtime = createRuntime("DMNInputDataNodeTypeTest.dmn", this.getClass());
        final String MODEL_NAMESPACE = "http://www.trisotech.com/definitions/_17396034-163a-48aa-9a7f-c6eb17f9cc6c";
        final String FEEL_NAMESPACE = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_FEEL;
        final DMNModel dmnModel = runtime.getModel(MODEL_NAMESPACE, "DMNInputDataNodeTypeTest");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final InputDataNode idnMembership = dmnModel.getInputs().stream().filter(idn -> idn.getName().equals("Membership Level")).findFirst().get();
        assertThat(idnMembership.getType().getBaseType().getNamespace(), is(FEEL_NAMESPACE));
        assertThat(idnMembership.getType().getBaseType().getName(), is("string"));
        assertThat(idnMembership.getType().isCollection(), is(false));
        assertThat(idnMembership.getType().isComposite(), is(false));
        assertThat(idnMembership.getType().getAllowedValues().size(), is(3));
        assertThat(idnMembership.getType().getAllowedValues().get(0).toString(), is("\"Gold\""));
        assertThat(idnMembership.getType().getAllowedValues().get(1).toString(), is("\"Silver\""));
        assertThat(idnMembership.getType().getAllowedValues().get(2).toString(), is("\"None\""));

        final InputDataNode idnMembershipLevels = dmnModel.getInputs().stream().filter(idn -> idn.getName().equals("Membership Levels")).findFirst().get();
        assertThat(idnMembershipLevels.getType().getBaseType().getNamespace(), is(MODEL_NAMESPACE));
        assertThat(idnMembershipLevels.getType().getBaseType().getName(), is("tMembershipLevel"));
        assertThat(idnMembershipLevels.getType().isCollection(), is(true));
        assertThat(idnMembershipLevels.getType().isComposite(), is(false));
        assertThat(idnMembershipLevels.getType().getAllowedValues().isEmpty(), is(true));

        final InputDataNode idnPercent = dmnModel.getInputs().stream().filter(idn -> idn.getName().equals("Percent")).findFirst().get();
        assertThat(idnPercent.getType().getBaseType().getNamespace(), is(FEEL_NAMESPACE));
        assertThat(idnPercent.getType().getBaseType().getName(), is("number"));
        assertThat(idnPercent.getType().isCollection(), is(false));
        assertThat(idnPercent.getType().isComposite(), is(false));
        assertThat(idnPercent.getType().getAllowedValues().size(), is(1));
        assertThat(idnPercent.getType().getAllowedValues().get(0).toString(), is("[0..100]"));

        final InputDataNode idnCarDamageResponsibility = dmnModel.getInputs().stream().filter(idn -> idn.getName().equals("Car Damage Responsibility")).findFirst().get();
        assertThat(idnCarDamageResponsibility.getType().getBaseType(), is(nullValue()));
        assertThat(idnCarDamageResponsibility.getType().isCollection(), is(false));
        assertThat(idnCarDamageResponsibility.getType().isComposite(), is(true));
    }

    @Test
    public void testDateAndTime() {
        final DMNRuntime runtime = createRuntime("0007-date-time.dmn", getClass());
        runtime.addListener(DMNRuntimeUtil.createListener());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_69430b3e-17b8-430d-b760-c505bf6469f9", "dateTime Table 58");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("dateString", "2015-12-24");
        context.set("timeString", "00:00:01-01:00");
        context.set("dateTimeString", "2016-12-24T23:59:00-05:00");
        context.set("Hours", 12);
        context.set("Minutes", 59);
        context.set("Seconds", new BigDecimal("1.3"));

        context.set("Timezone", "PT-1H");
        context.set("Year", 1999);
        context.set("Month", 11);
        context.set("Day", 22);
        context.set("oneHour", Duration.parse("PT1H")); // <variable name="oneHour" typeRef="feel:days and time duration"/>
        context.set("durationString", "P13DT2H14S");      // <variable name="durationString" typeRef="feel:string"/>
        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        final DMNContext ctx = dmnResult.getContext();

        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("Date-Time"), is(ZonedDateTime.of(2016, 12, 24, 23, 59, 0, 0, ZoneOffset.ofHours(-5))));
            FEELPropertyAccessible resultDate = (FEELPropertyAccessible)allProperties.get("Date");
            assertThat(resultDate.getClass().getSimpleName(), is("TDateVariants"));
            assertThat(resultDate.getFEELProperty("fromString").toOptional().get(), is(LocalDate.of(2015, 12, 24)));
            assertThat(resultDate.getFEELProperty("fromDateTime").toOptional().get(), is(LocalDate.of(2016, 12, 24)));
            assertThat(resultDate.getFEELProperty("fromYearMonthDay").toOptional().get(), is(LocalDate.of(1999, 11, 22)));
            assertThat(allProperties.get("Time"), is(OffsetTime.of(0, 0, 1, 0, ZoneOffset.ofHours(-1))));
            assertThat(allProperties.get("Date-Time2"), is(ZonedDateTime.of(2015, 12, 24, 0, 0, 1, 0, ZoneOffset.ofHours(-1))));
            assertThat(allProperties.get("Time2"), is(OffsetTime.of(0, 0, 1, 0, ZoneOffset.ofHours(-1))));
            assertThat(allProperties.get("Time3"), is(OffsetTime.of(12, 59, 1, 300000000, ZoneOffset.ofHours(-1))));
            assertThat(allProperties.get("dtDuration1"), is(Duration.parse("P13DT2H14S")));
            assertThat(allProperties.get("dtDuration2"), is(Duration.parse("P367DT3H58M59S")));
            assertThat(allProperties.get("hoursInDuration"), is(new BigDecimal("3")));
            assertThat(allProperties.get("sumDurations"), is(Duration.parse("PT9125H59M13S")));
            assertThat(allProperties.get("ymDuration2"), is(ComparablePeriod.parse("P1Y")));
            assertThat(allProperties.get("cDay"), is(BigDecimal.valueOf(24)));
            assertThat(allProperties.get("cYear"), is(BigDecimal.valueOf(2015)));
            assertThat(allProperties.get("cMonth"), is(BigDecimal.valueOf(12)));
            assertThat(allProperties.get("cHour"), is(BigDecimal.valueOf(0)));
            assertThat(allProperties.get("cMinute"), is(BigDecimal.valueOf(0)));
            assertThat(allProperties.get("cSecond"), is(BigDecimal.valueOf(1)));
            assertThat(allProperties.get("cTimezone"), is("GMT-01:00"));
            assertThat(allProperties.get("years"), is(BigDecimal.valueOf(1)));
            assertThat(allProperties.get("d1seconds"), is(BigDecimal.valueOf(14)));
        } else {
            assertThat(ctx.get("Date-Time"), is(ZonedDateTime.of(2016, 12, 24, 23, 59, 0, 0, ZoneOffset.ofHours(-5))));
            assertThat(ctx.get("Date"), is(new HashMap<String, Object>() {{
                put("fromString", LocalDate.of(2015, 12, 24));
                put("fromDateTime", LocalDate.of(2016, 12, 24));
                put("fromYearMonthDay", LocalDate.of(1999, 11, 22));
            }}));
            assertThat(ctx.get("Time"), is(OffsetTime.of(0, 0, 1, 0, ZoneOffset.ofHours(-1))));
            assertThat(ctx.get("Date-Time2"), is(ZonedDateTime.of(2015, 12, 24, 0, 0, 1, 0, ZoneOffset.ofHours(-1))));
            assertThat(ctx.get("Time2"), is(OffsetTime.of(0, 0, 1, 0, ZoneOffset.ofHours(-1))));
            assertThat(ctx.get("Time3"), is(OffsetTime.of(12, 59, 1, 300000000, ZoneOffset.ofHours(-1))));
            assertThat(ctx.get("dtDuration1"), is(Duration.parse("P13DT2H14S")));
            assertThat(ctx.get("dtDuration2"), is(Duration.parse("P367DT3H58M59S")));
            assertThat(ctx.get("hoursInDuration"), is(new BigDecimal("3")));
            assertThat(ctx.get("sumDurations"), is(Duration.parse("PT9125H59M13S")));
            assertThat(ctx.get("ymDuration2"), is(ComparablePeriod.parse("P1Y")));
            assertThat(ctx.get("cDay"), is(BigDecimal.valueOf(24)));
            assertThat(ctx.get("cYear"), is(BigDecimal.valueOf(2015)));
            assertThat(ctx.get("cMonth"), is(BigDecimal.valueOf(12)));
            assertThat(ctx.get("cHour"), is(BigDecimal.valueOf(0)));
            assertThat(ctx.get("cMinute"), is(BigDecimal.valueOf(0)));
            assertThat(ctx.get("cSecond"), is(BigDecimal.valueOf(1)));
            assertThat(ctx.get("cTimezone"), is("GMT-01:00"));
            assertThat(ctx.get("years"), is(BigDecimal.valueOf(1)));
            assertThat(ctx.get("d1seconds"), is(BigDecimal.valueOf(14)));
        }
    }

    @Test
    public void testTimeFunction() {
        final DMNRuntime runtime = createRuntime("TimeFromDate.dmn", getClass());
        runtime.addListener(DMNRuntimeUtil.createListener());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ecf4ea54-2abc-4e2f-a101-4fe14e356a46", "Dessin 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("datetimestring", "2016-07-29T05:48:23");
        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.getContext().get("time"), is(LocalTime.of(5, 48, 23)));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("time"), is(LocalTime.of(5, 48, 23)));
        }
    }

    @Test
    public void testSO58507157() {
        // DROOLS-4679 DMN FEEL list contains() invocation from DMN layer fixes
        final DMNRuntime runtime = createRuntime("so58507157.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://sample.dmn", "DecisionNumberInList");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("inputNumber", 1);
        context.set("inputNumberList", Arrays.asList(0, 1));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat((Map<?, ?>) result.get("DecisionNumberInList"), hasEntry(is("Result_1_OK"), is(true)));
        assertThat((Map<?, ?>) result.get("DecisionNumberInList"), hasEntry(is("Result_2_OK"), is(true)));
        assertThat((Map<?, ?>) result.get("DecisionNumberInList"), hasEntry(is("Result_3"), is(true)));
        assertThat((Map<?, ?>) result.get("DecisionNumberInList"), hasEntry(is("Result_4"), is(true)));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            Map<?, ?> resultMap = (Map<?, ?>) allProperties.get("DecisionNumberInList");
            assertThat(resultMap, hasEntry(is("Result_1_OK"), is(true)));
            assertThat(resultMap, hasEntry(is("Result_2_OK"), is(true)));
            assertThat(resultMap, hasEntry(is("Result_3"), is(true)));
            assertThat(resultMap, hasEntry(is("Result_4"), is(true)));
        }
    }

    @Test
    public void testDTinputExprCollectionWithAllowedValuesA() {
        final DMNRuntime runtime = createRuntime("DROOLS-4379.dmn", this.getClass());
        // DROOLS-4379 DMN decision table input expr collection with allowedValues
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_95436b7a-7268-4713-bf84-58bff10407b4", "Dessin 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("test", Arrays.asList("r2", "r1"));
        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("D4"), is("Contains r1"));
        assertThat((List<?>) result.get("D5"), contains(is("r1"), is("r2")));

        if (isTypeSafe()) {
            FEELPropertyAccessible outputSet = ((DMNContextFPAImpl)dmnResult.getContext()).getFpa();
            Map<String, Object> allProperties = outputSet.allFEELProperties();
            assertThat(allProperties.get("D4"), is("Contains r1"));
            assertThat((List<?>) allProperties.get("D5"), contains(is("r1"), is("r2")));
        }
    }

    @Test(timeout = 30_000L)
    @Ignore("This shouldn't be supported by Typesafe DMN")
    public void testAccessorCache() {
        final DMNRuntime runtime = createRuntime("20180731-pr1997.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_7a39d775-bce9-45e3-aa3b-147d6f0028c7", "20180731-pr1997");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        for (int i = 0; i < 10_000; i++) {
            final DMNContext context = DMNFactory.newContext();
            context.set("a Person", new Person("John", "Doe", i));

            final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
            assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

            final DMNContext result = dmnResult.getContext();
            assertThat(result.get("Say hello and age"), is("Hello John Doe, your age is: " + i));
        }
    }

    @Test
    @Ignore("This test has a different assertion as the output is typesafe")
    public void testNotWithPredicates20180601b() {
        // DROOLS-2605
        final DMNRuntime runtime = createRuntime("BruceTask20180601.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_3802fcb2-5b93-4502-aff4-0f5c61244eab", "Bruce Task");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("TheBook", Arrays.asList(prototype(entry("Title", "55"), entry("Price", new BigDecimal(5)), entry("Quantity", new BigDecimal(5))),
                                             prototype(entry("Title", "510"), entry("Price", new BigDecimal(5)), entry("Quantity", new BigDecimal(10))),
                                             prototype(entry("Title", "810"), entry("Price", new BigDecimal(8)), entry("Quantity", new BigDecimal(10))),
                                             prototype(entry("Title", "85"), entry("Price", new BigDecimal(8)), entry("Quantity", new BigDecimal(5))),
                                             prototype(entry("Title", "66"), entry("Price", new BigDecimal(6)), entry("Quantity", new BigDecimal(6)))));

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Bruce"), is(instanceOf(Map.class)));
        final Map<String, Object> bruce = (Map<String, Object>) result.get("Bruce");

        assertEquals(2, ((List) bruce.get("one")).size());
        assertTrue(((List) bruce.get("one")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("55")));
        assertTrue(((List) bruce.get("one")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("510")));

        assertEquals(3, ((List) bruce.get("two")).size());
        assertTrue(((List) bruce.get("two")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("810")));
        assertTrue(((List) bruce.get("two")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("85")));
        assertTrue(((List) bruce.get("two")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("66")));

        assertEquals(1, ((List) bruce.get("three")).size());
        assertTrue(((List) bruce.get("three")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("510")));

        assertEquals(2, ((List) bruce.get("Four")).size());
        assertTrue(((List) bruce.get("Four")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("85")));
        assertTrue(((List) bruce.get("Four")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("66")));

        assertEquals(2, ((List) bruce.get("Five")).size());
        assertTrue(((List) bruce.get("Five")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("85")));
        assertTrue(((List) bruce.get("Five")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("66")));

        assertEquals(2, ((List) bruce.get("six")).size());
        assertTrue(((List) bruce.get("six")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("85")));
        assertTrue(((List) bruce.get("six")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("66")));
    }
}
