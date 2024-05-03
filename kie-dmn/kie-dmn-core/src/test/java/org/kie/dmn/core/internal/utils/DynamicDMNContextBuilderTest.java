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
package org.kie.dmn.core.internal.utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.decisionservices.DMNDecisionServicesTest;
import org.kie.dmn.core.stronglytyped.DMNRuntimeTypesTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DynamicDMNContextBuilderTest {

    public static final Logger LOG = LoggerFactory.getLogger(DynamicDMNContextBuilderTest.class);

    protected DMNRuntime createRuntime(String string, Class<?> class1) {
        return BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK.createRuntime(string, class1);
    }

    protected DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
        return BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK.createRuntimeWithAdditionalResources(string, class1, string2);
    }

    private ObjectMapper mapper = JsonMapper.builder().build();

    @SuppressWarnings("unchecked")
    protected Map<String, Object> readJSON(String content) throws Exception {
        return mapper.readValue(content, Map.class);
    }

    @Test
    void oneOfEachType() throws Exception {
        DMNRuntime runtime = createRuntime("OneOfEachType.dmn", DMNRuntimeTypesTest.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_4f5608e9-4d74-4c22-a47e-ab657257fc9c", "OneOfEachType");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();
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
        new DynamicDMNContextBuilder(context, dmnModel).populateContextWith(readJSON(JSON));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("DecisionString").getResult()).isEqualTo("Hello, John Doe");
        assertThat(dmnResult.getDecisionResultByName("DecisionNumber").getResult()).isEqualTo(new BigDecimal(2));
        assertThat(dmnResult.getDecisionResultByName("DecisionBoolean").getResult()).isEqualTo(Boolean.FALSE);
        assertThat(dmnResult.getDecisionResultByName("DecisionDTDuration").getResult()).isEqualTo(Duration.parse("P2D"));
        assertThat(dmnResult.getDecisionResultByName("DecisionYMDuration").getResult()).isEqualTo(ComparablePeriod.parse("P2M"));
        assertThat(dmnResult.getDecisionResultByName("DecisionDateAndTime").getResult()).isEqualTo(LocalDateTime.of(2020, 4, 2, 10, 0));
        assertThat(dmnResult.getDecisionResultByName("DecisionDate").getResult()).isEqualTo(LocalDate.of(2020, 4, 3));
        assertThat(dmnResult.getDecisionResultByName("DecisionTime").getResult()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    void nextDays() throws Exception {
        final DMNRuntime runtime = createRuntime("nextDays.dmn", DMNRuntimeTypesTest.class);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_8A1F9719-02AA-4517-97D4-5C4F5D22FE82", "nextDays");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();
        final String JSON = "{\n" +
                            "    \"few dates\": [ \"2019-12-31\", \"2020-02-21\" ]\n" +
                            "}";
        new DynamicDMNContextBuilder(context, dmnModel).populateContextWith(readJSON(JSON));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult()).asList().containsExactly(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 22));
    }

    @Test
    void trafficViolationAll() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Traffic Violation.dmn", DMNRuntimeTypesTest.class);
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF", "Traffic Violation");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();
        final String JSON = "{\n" +
                            "    \"Driver\": {\n" +
                            "        \"Name\": \"John Doe\",\n" +
                            "        \"Age\": 47,\n" +
                            "        \"State\": \"Italy\",\n" +
                            "        \"City\": \"Milan\",\n" +
                            "        \"Points\": 15,\n" +
                            "        \"additional\": \"NO5\"\n" + // intentional additional attribute
                            "    },\n" +
                            "    \"Violation\": {\n" +
                            "        \"Code\": \"s\",\n" +
                            "        \"Date\": \"2020-10-12\",\n" +
                            "        \"Type\": \"speed\",\n" +
                            "        \"Actual Speed\": 135,\n" +
                            "        \"Speed Limit\": 100\n" +
                            "    }\n" +
                            "}";
        new DynamicDMNContextBuilder(context, dmnModel).populateContextWith(readJSON(JSON));

        assertTrafficViolationSuspendedCase(runtime, dmnModel, context);
    }

    @Test
    void trafficViolationMin() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Traffic Violation.dmn", DMNRuntimeTypesTest.class);
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF", "Traffic Violation");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();
        final String JSON = "{\n" +
                            "    \"Driver\": {\n" +
                            "        \"Points\": 15,\n" +
                            "        \"additional\": \"NO5\"\n" + // intentional additional attribute
                            "    },\n" +
                            "    \"Violation\": {\n" +
                            "        \"Type\": \"speed\",\n" +
                            "        \"Actual Speed\": 135,\n" +
                            "        \"Speed Limit\": 100\n" +
                            "    }\n" +
                            "}";
        new DynamicDMNContextBuilder(context, dmnModel).populateContextWith(readJSON(JSON));

        assertTrafficViolationSuspendedCase(runtime, dmnModel, context);
    }

    @Test
    void trafficViolationArbitraryFine() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Traffic Violation.dmn", DMNRuntimeTypesTest.class);
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF", "Traffic Violation");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();
        final String JSON = "{\n" +
                            "    \"Driver\": {\n" +
                            "        \"Points\": 1\n" +
                            "    },\n" +
                            "    \"Violation\": {\n" +
                            "        \"Type\": \"speed\",\n" +
                            "        \"Actual Speed\": 100,\n" +
                            "        \"Speed Limit\": 100\n" +
                            "    },\n" +
                            "    \"Additional\": {\n" + // intentional additional object
                            "        \"Comment\": \"Totally arbitrarily object in context\"\n" +
                            "    },\n" +
                            "    \"Fine\": {\n" + // intentional overriding decision
                            "        \"Points\": 47,\n" +
                            "        \"Amount\": 9999\n" +
                            "    }\n" +
                            "}";
        new DynamicDMNContextBuilder(context, dmnModel).populateContextWith(readJSON(JSON));

        assertTrafficViolationSuspendedCase(runtime, dmnModel, context);
    }

    private void assertTrafficViolationSuspendedCase(final DMNRuntime runtime, final DMNModel dmnModel, DMNContext context) {
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Should the driver be suspended?").getResult()).isEqualTo("Yes");
    }

    @Test
    void dSBasicDS1() throws Exception {
        final DMNRuntime runtime = createRuntime("0004-decision-services.dmn", DMNDecisionServicesTest.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();
        final String JSON = "{ \"D\":\"d\", \"E\":\"e\"}";
        new DynamicDMNContextBuilder(context, dmnModel).populateContextForDecisionServiceWith("A only as output knowing D and E", readJSON(JSON));
        
        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "A only as output knowing D and E");
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("A").getResult()).isEqualTo("de");
    }

    @Test
    void dSBasicDS2() throws Exception {
        final DMNRuntime runtime = createRuntime("0004-decision-services.dmn", DMNDecisionServicesTest.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_686f58d4-4ec3-4c65-8c06-0e4fd8983def", "Decision Services");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();
        final String JSON = "{ \"additional\":123, \"D\":\"d\", \"E\":\"e\", \"B\":\"inB\", \"C\":\"inC\"}";
        new DynamicDMNContextBuilder(context, dmnModel).populateContextForDecisionServiceWith("A Only Knowing B and C", readJSON(JSON));

        final DMNResult dmnResult = runtime.evaluateDecisionService(dmnModel, context, "A Only Knowing B and C");
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("A").getResult()).isEqualTo("inBinC");
    }

    @Test
    void floatFromREST() throws Exception {
        final DMNRuntime runtime = createRuntime("numberRESTinLIST.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_91CAD4BC-859F-465A-9294-300EABF4EC8A", "numberRESTinLIST");
        Assertions.assertThat(dmnModel).isNotNull();
        Assertions.assertThat(dmnModel.hasErrors()).describedAs(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();
        final String JSON = "{ \"a number\": 3.0 }";
        new DynamicDMNContextBuilder(context, dmnModel).populateContextWith(readJSON(JSON));
        
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        Assertions.assertThat(dmnResult.hasErrors()).describedAs(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        Assertions.assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult()).isEqualTo("OK");
    }
}