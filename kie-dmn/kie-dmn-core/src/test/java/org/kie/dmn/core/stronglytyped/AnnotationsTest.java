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
package org.kie.dmn.core.stronglytyped;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Arrays;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationsTest extends BaseVariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(AnnotationsTest.class);
    private boolean strongly;

    @ParameterizedTest
    @MethodSource("params")
    void nswe(VariantTestConf conf) throws Exception {
        testConfig = conf;
        strongly = testConfig.isTypeSafe();
        final DMNRuntime runtime = createRuntime("NSEW.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_FBA17BF4-BC04-4C16-9305-40E8B4B2FECB", "NSEW");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).withFailMessage(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("direction", "East");

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnModel.hasErrors()).withFailMessage(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult()).isEqualTo("You decided to go East.");

        if (strongly) {
            Class<?> inputSetClass = getStronglyClassByName(dmnModel, "InputSet");
            Field directionAsField = inputSetClass.getDeclaredField("direction");
            org.eclipse.microprofile.openapi.annotations.media.Schema ann = directionAsField.getDeclaredAnnotation(org.eclipse.microprofile.openapi.annotations.media.Schema.class);
            assertThat(ann).isNotNull();
            assertThat(ann.enumeration()).isNotNull().contains("North", "South", "East", "West");

            Field definedKeySet = inputSetClass.getDeclaredField("definedKeySet");
            org.eclipse.microprofile.openapi.annotations.media.Schema ann2 = definedKeySet.getDeclaredAnnotation(org.eclipse.microprofile.openapi.annotations.media.Schema.class);
            assertThat(ann2).isNotNull();
            assertThat(ann2.hidden()).isTrue();
            io.swagger.v3.oas.annotations.media.Schema ann3 = definedKeySet.getDeclaredAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
            assertThat(ann3).isNotNull();
            assertThat(ann3.hidden()).isTrue();
        }
    }

    @ParameterizedTest
    @MethodSource("params")
    void oneOfEachType(VariantTestConf conf) throws Exception {
        testConfig = conf;
        strongly = testConfig.isTypeSafe();
        final DMNRuntime runtime = createRuntime("OneOfEachType.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_4f5608e9-4d74-4c22-a47e-ab657257fc9c", "OneOfEachType");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).withFailMessage(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        // this is already tested for execution/evaluation semantic in its proper test unit, here we check Annotations presence.

        if (strongly) {
            Class<?> inputSetClass = getStronglyClassByName(dmnModel, "InputSet");
            checkAnnOneOfEachType(inputSetClass, "inputDate", "InputDate", LocalDate.class);
            checkAnnOneOfEachType(inputSetClass, "inputTime", "InputTime", LocalTime.class);
            checkAnnOneOfEachType(inputSetClass, "inputDateAndTime", "InputDateAndTime", LocalDateTime.class);
            checkAnnOneOfEachType(inputSetClass, "inputYMDuration", "InputYMDuration", Period.class);
            checkAnnOneOfEachType(inputSetClass, "inputDTDuration", "InputDTDuration", Duration.class);
        }
    }

    private void checkAnnOneOfEachType(Class<?> inputSetClass, String fieldName, String name, Class<?> implementation) throws Exception {
        Field directionAsField = inputSetClass.getDeclaredField(fieldName);
        org.eclipse.microprofile.openapi.annotations.media.Schema annMP = directionAsField.getDeclaredAnnotation(org.eclipse.microprofile.openapi.annotations.media.Schema.class);
        assertThat(annMP).isNotNull();
        assertThat(annMP.name()).isEqualTo(name);
        assertThat(annMP.implementation()).isEqualTo(implementation);
        if (implementation == Period.class) {
            assertThat(annMP.example()).isEqualTo("P1Y2M");
        }
        io.swagger.v3.oas.annotations.media.Schema annIOSwagger = directionAsField.getDeclaredAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
        assertThat(annIOSwagger).isNotNull();
        assertThat(annIOSwagger.name()).isEqualTo(name);
        assertThat(annIOSwagger.implementation()).isEqualTo(implementation);
        if (implementation == Period.class) {
            assertThat(annIOSwagger.example()).isEqualTo("P1Y2M");
        }
    }

    @ParameterizedTest
    @MethodSource("params")
    void nextDays(VariantTestConf conf) throws Exception {
        testConfig = conf;
        strongly = testConfig.isTypeSafe();
        final DMNRuntime runtime = createRuntime("nextDays.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_8A1F9719-02AA-4517-97D4-5C4F5D22FE82", "nextDays");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).withFailMessage(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = DMNFactory.newContext();
        if (!isTypeSafe()) {
            context.set("few dates", Arrays.asList(LocalDate.of(2019, 12, 31), LocalDate.of(2020, 2, 21)));
        } else {
            JsonMapper mapper = JsonMapper.builder()
                                          .addModule(new JavaTimeModule())
                                          .build();
            final String JSON = "{\n" +
                                "    \"few dates\": [ \"2019-12-31\", \"2020-02-21\" ]\n" +
                                "}";
            Class<?> inputSetClass = getStronglyClassByName(dmnModel, "InputSet");
            FEELPropertyAccessible inputSet = (FEELPropertyAccessible) mapper.readValue(JSON, inputSetClass);
            context = new DMNContextFPAImpl(inputSet);
        }

        final DMNResult dmnResult = evaluateModel(runtime, dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnModel.hasErrors()).withFailMessage(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult()).asList().contains(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 22));

        if (strongly) {
            Class<?> inputSetClass = getStronglyClassByName(dmnModel, "InputSet");
            Field directionAsField = inputSetClass.getDeclaredField("few_32dates");
            org.eclipse.microprofile.openapi.annotations.media.Schema annMP = directionAsField.getDeclaredAnnotation(org.eclipse.microprofile.openapi.annotations.media.Schema.class);
            assertThat(annMP).isNotNull();
            assertThat(annMP.type()).isEqualTo(org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY);
            io.swagger.v3.oas.annotations.media.Schema annIOSwagger = directionAsField.getDeclaredAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
            assertThat(annIOSwagger).isNotNull();
            assertThat(annIOSwagger.type()).isEqualTo("array");
        }
    }
}

