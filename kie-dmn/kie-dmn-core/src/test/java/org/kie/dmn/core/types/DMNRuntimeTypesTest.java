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

package org.kie.dmn.core.types;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantNonTypeSafeTest;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
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
}

