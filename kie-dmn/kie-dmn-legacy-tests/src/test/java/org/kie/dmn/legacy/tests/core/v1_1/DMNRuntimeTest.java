/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.legacy.tests.core.v1_1;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoPeriod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.api.core.EvaluatorResult;
import org.kie.dmn.core.ast.DMNContextEvaluator;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.core.model.Person;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.marshaller.FEELStringMarshaller;
import org.kie.dmn.feel.util.BuiltInTypeUtils;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.v1_1.TDecision;
import org.kie.dmn.model.v1_1.TDefinitions;
import org.kie.dmn.model.v1_1.TInformationItem;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.kie.dmn.core.util.DMNTestUtil.getAndAssertModelNoErrors;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class DMNRuntimeTest extends BaseDMN1_1VariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeTest.class);

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleItemDefinition(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("simple-item-def.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn/itemdef", "simple-item-def");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Monthly Salary", 1000);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Yearly Salary")).isEqualTo(new BigDecimal("12000"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void compositeItemDefinition(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0008-LX-arithmetic.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0008-LX-arithmetic");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        final Map<String, Object> loan = new HashMap<>();
        loan.put("principal", 600000);
        loan.put("rate", 0.0375);
        loan.put("termMonths", 360);
        context.set("loan", loan);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("payment")).isEqualTo(new BigDecimal("2778.693549432766768088520383236299"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void trisotechNamespace(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("trisotech_namespace.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b8feec86-dadf-4051-9feb-8e6093bbb530", "Solution 3");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("IsDoubleHulled", true);
        context.set("Residual Cargo Size", BigDecimal.valueOf(0.1));
        context.set("Ship Size", new BigDecimal(50));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Ship can enter a Dutch port")).isEqualTo(Boolean.TRUE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void emptyDecision1(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("empty_decision.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ba9fc4b1-5ced-4d00-9b61-290de4bf3213", "Solution 3");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        final Map<String, Object> shipInfo = new HashMap<>();
        shipInfo.put("Size", BigDecimal.valueOf(70));
        shipInfo.put("Is Double Hulled", Boolean.FALSE);
        shipInfo.put("Residual Cargo Size", BigDecimal.valueOf(0.1));
        context.set("Ship Info", shipInfo);

        // Test that even if one decision is empty or missing input data,
        // the other decisions in the model are still evaluated
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        assertThat(dmnResult.hasErrors()).isTrue();
        assertThat(result.get("Ship Can Enter v2")).isEqualTo(Boolean.TRUE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void emptyDecision2(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("empty_decision.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ba9fc4b1-5ced-4d00-9b61-290de4bf3213", "Solution 3");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        final Map<String, Object> shipInfo = new HashMap<>();
        shipInfo.put("Size", BigDecimal.valueOf(70));
        shipInfo.put("Is Double Hulled", Boolean.FALSE);
        shipInfo.put("Residual Cargo Size", BigDecimal.valueOf(0.1));
        context.set("Ship Info", shipInfo);
        context.set("Ship Size", BigDecimal.valueOf(70));
        context.set("IsDoubleHulled", Boolean.FALSE);
        context.set("Residual Cargo Size", BigDecimal.valueOf(0.1));

        // check that if all the input data is available, but the
        // decision expression is empty, the model returns a warning
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        final List<DMNMessage> messages = dmnResult.getMessages(DMNMessage.Severity.WARN);
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getSeverity()).isEqualTo(DMNMessage.Severity.WARN);
        assertThat(messages.get(0).getSourceId()).isEqualTo("_42806504-8ed5-488f-b274-de98c1bc67b9");

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Ship Can Enter v2")).isEqualTo(Boolean.TRUE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void eventListeners(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("car_damage_responsibility.dmn", this.getClass());

        final DMNRuntimeEventListener listener = mock(DMNRuntimeEventListener.class);
        runtime.addListener(listener);
        runtime.addListener(DMNRuntimeUtil.createListener());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_820611e9-c21c-47cd-8e52-5cba2be9f9cc", "Car Damage Responsibility");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Membership Level", "Silver");
        context.set("Damage Types", "Body");
        context.set("Responsible", "Driver");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> argument = ArgumentCaptor.forClass(AfterEvaluateDecisionTableEvent.class);
        verify(listener, times(2))
                .beforeEvaluateDecision(any(BeforeEvaluateDecisionEvent.class));
        verify(listener, times(2))
                .afterEvaluateDecision(any(AfterEvaluateDecisionEvent.class));
        verify(listener, times(2))
                .beforeEvaluateDecisionTable(any(BeforeEvaluateDecisionTableEvent.class));
        verify(listener, times(2))
                .afterEvaluateDecisionTable(argument.capture());

        AfterEvaluateDecisionTableEvent dte = argument.getAllValues().get(0);
        assertThat(dte.getDecisionTableName()).isEqualTo("Car Damage Responsibility");
        assertThat(dte.getMatches()).containsExactly(5); // rows are 1-based

        dte = argument.getAllValues().get(1);
        assertThat(dte.getDecisionTableName()).isEqualTo("Payment method");
        assertThat(dte.getMatches()).containsExactly(3); // rows are 1-based

        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat((Map<String, Object>) result.get("Car Damage Responsibility")).containsEntry("EU Rent", BigDecimal.valueOf(40));
        assertThat((Map<String, Object>) result.get("Car Damage Responsibility")).containsEntry("Renter", BigDecimal.valueOf(60));
        assertThat(result.get("Payment method")).isEqualTo("Check");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void contextEventListeners(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("context_listener.dmn", this.getClass());

        final DMNRuntimeEventListener listener = mock(DMNRuntimeEventListener.class);
        runtime.addListener(listener);
        runtime.addListener(DMNRuntimeUtil.createListener());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_73481d02-76fb-4927-ac11-5d936882e16c", "context listener");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        final ArgumentCaptor<AfterEvaluateContextEntryEvent> argument = ArgumentCaptor.forClass(AfterEvaluateContextEntryEvent.class);
        verify(listener, times(1))
                .beforeEvaluateDecision(any(BeforeEvaluateDecisionEvent.class));
        verify(listener, times(1))
                .afterEvaluateDecision(any(AfterEvaluateDecisionEvent.class));
        verify(listener, times(5))
                .beforeEvaluateContextEntry(any(BeforeEvaluateContextEntryEvent.class));
        verify(listener, times(5))
                .afterEvaluateContextEntry(argument.capture());

        AfterEvaluateContextEntryEvent aece = argument.getAllValues().get(0);
        assertThat(aece.getNodeName()).isEqualTo("d1");
        assertThat(aece.getVariableName()).isEqualTo("a1");
        assertThat(aece.getVariableId()).isEqualTo("_b199c7b1-cb87-4a92-b045-a2954ccc9d01");
        assertThat(aece.getExpressionId()).isEqualTo("_898c24f8-93da-4fe2-827c-924c30956833");
        assertThat(aece.getExpressionResult()).isEqualTo(BigDecimal.valueOf(10));

        aece = argument.getAllValues().get(1);
        assertThat(aece.getNodeName()).isEqualTo("d1");
        assertThat(aece.getVariableName()).isEqualTo("c1");
        assertThat(aece.getVariableId()).isEqualTo("_38a88aef-8b3c-424d-b60c-a139ffb610e1");
        assertThat(aece.getExpressionId()).isEqualTo("_879c4ac6-8b25-4cd1-9b8e-c18d0b0b281c");
        assertThat(aece.getExpressionResult()).isEqualTo("a");

        aece = argument.getAllValues().get(2);
        assertThat(aece.getNodeName()).isEqualTo("d1");
        assertThat(aece.getVariableName()).isEqualTo("c2");
        assertThat(aece.getVariableId()).isEqualTo("_3aad82f0-74b9-4921-8b2f-d6c277c840db");
        assertThat(aece.getExpressionId()).isEqualTo("_9acf4baf-6c49-4d47-88ab-2e511e598e04");
        assertThat(aece.getExpressionResult()).isEqualTo("b");

        aece = argument.getAllValues().get(3);
        assertThat(aece.getNodeName()).isEqualTo("d1");
        assertThat(aece.getVariableName()).isEqualTo("b1");
        assertThat(aece.getVariableId()).isEqualTo("_f4a6c2ba-e6e9-4dbd-b776-edef2c1a1343");
        assertThat(aece.getExpressionId()).isEqualTo("_c450d947-1874-41fe-9c0a-da5f1cca7fde");
        assertThat((Map<String, Object>) aece.getExpressionResult()).containsEntry("c1", "a");
        assertThat((Map<String, Object>) aece.getExpressionResult()).containsEntry("c2", "b");

        aece = argument.getAllValues().get(4);
        assertThat(aece.getNodeName()).isEqualTo("d1");
        assertThat(aece.getVariableName()).isEqualTo(DMNContextEvaluator.RESULT_ENTRY);
        assertThat(aece.getVariableId()).isNull();
        assertThat(aece.getExpressionId()).isEqualTo("_4264b25c-d676-4516-ab8a-a4ff34e7a95c");
        assertThat(aece.getExpressionResult()).isEqualTo("a");

        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("d1")).isEqualTo("a");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void errorMessages(VariantTestConf conf) {
        testConfig = conf;
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("car_damage_responsibility2.dmn", this.getClass());
        assertThat(messages).isNotEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void outputReuse(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Input_reuse_in_output.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_098bb607-eff7-4772-83ac-6ded8b371fa7", "Input reuse in output");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Age", 40);
        context.set("Requested Product", "Fixed30");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("My Decision")).isEqualTo("Fixed30");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleNot(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Simple_Not.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_98436ebb-7c42-48c0-8d11-d693e2a817c9", "Simple Not");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Occupation", "Student");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a")).isEqualTo("Is Student");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleNot2(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Simple_Not.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_98436ebb-7c42-48c0-8d11-d693e2a817c9", "Simple Not");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Occupation", "Engineer");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a")).isEqualTo("Is not a Student");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dinner(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Dinner.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_0c45df24-0d57-4acc-b296-b4cba8b71a36", "Dinner");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Guests with children", true);
        context.set("Season", "Fall");
        context.set("Number of guests", 4);
        context.set("Temp", 25);
        context.set("Rain Probability", 30);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getContext().get("Where to eat")).isEqualTo("Outside");
        assertThat(dmnResult.getContext().get("Dish")).isEqualTo("Spareribs");
        assertThat(dmnResult.getContext().get("Drinks")).asList().containsExactly("Apero", "Ale", "Juice Boxes");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void notificationsApproved2(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("NotificationsTest2.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "building-structure-rules");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("existingActivityApplicability", true);
        context.set("Distance", new BigDecimal(9999));
        context.set("willIncreaseTraffic", true);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Notification Status")).isEqualTo("Notification to Province Approved");
        assertThat(result.get("Permit Status")).isEqualTo("Building Activity Province Permit Required");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void boxedContext(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("BoxedContext.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_0de36357-fec0-4b4e-b7f1-382d381e06e9", "Dessin 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a", 10);
        context.set("b", 5);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat((Map<String, Object>) dmnResult.getContext().get("Math")).containsEntry("Sum", BigDecimal.valueOf(15));
        assertThat((Map<String, Object>) dmnResult.getContext().get("Math")).containsEntry("Product", BigDecimal.valueOf(50));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void functionDefAndInvocation(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("FunctionDefinition.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_0de36357-fec0-4b4e-b7f1-382d381e06e9", "Dessin 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a", 10);
        context.set("b", 5);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat((Map<String, Object>) dmnResult.getContext().get("Math")).containsEntry("Sum", BigDecimal.valueOf(15));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void builtInFunctionInvocation(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("BuiltInFunctionInvocation.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_b77219ee-ec28-48e3-b240-8e0dbbabefeb", "built in function invocation");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a", 10);
        context.set("b", 5);
        context.set("x", "Hello, World!");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getContext().get("calc min")).isEqualTo(BigDecimal.valueOf(5));
        assertThat(dmnResult.getContext().get("fixed params")).isEqualTo("World!");
        assertThat(dmnResult.getContext().get("out of order")).isEqualTo(BigDecimal.valueOf(5));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void bkmNode(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0009-invocation-arithmetic.dmn", getClass());
//        runtime.addListener( DMNRuntimeUtil.createListener() );

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_cb28c255-91cd-4c01-ac7b-1a9cb1ecdb11", "literal invocation1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final Map<String, Object> loan = new HashMap<>();
        loan.put("amount", BigDecimal.valueOf(600000));
        loan.put("rate", new BigDecimal("0.0375"));
        loan.put("term", BigDecimal.valueOf(360));
        final DMNContext context = DMNFactory.newContext();
        context.set("fee", 100);
        context.set("Loan", loan);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(
                ((BigDecimal) dmnResult.getContext().get("MonthlyPayment")).setScale(8, BigDecimal.ROUND_DOWN)).
                isEqualTo(new BigDecimal("2878.69354943277").setScale(8, BigDecimal.ROUND_DOWN));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void itemDefCollection(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-filter.dmn", getClass());
//        runtime.addListener( DMNRuntimeUtil.createListener() );

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_f52ca843-504b-4c3b-a6bc-4d377bffef7a", "filter01");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final Object[][] data = new Object[][]{
                {1, "Finances", "John"},
                {2, "Engineering", "Mary"},
                {3, "Sales", "Kevin"}
        };
        final List<Map<String, Object>> employees = new ArrayList<>();
        for (Object[] aData : data) {
            final Map<String, Object> e = new HashMap<>();
            e.put("id", aData[0]);
            e.put("dept", aData[1]);
            e.put("name", aData[2]);
            employees.add(e);
        }
        final DMNContext context = DMNFactory.newContext();
        context.set("Employee", employees);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getContext().get("filter01")).asList().containsExactly("Mary");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void list(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("list-expression.dmn", getClass());
//        runtime.addListener( DMNRuntimeUtil.createListener() );

        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "list-expression");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getContext().get("Name list")).asList().containsExactly("John", "Mary");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void relation(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("relation-expression.dmn", getClass());
//        runtime.addListener( DMNRuntimeUtil.createListener() );

        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "relation-expression");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getContext().get("Employee Relation")).isInstanceOf(List.class);

        final List<Map<String, Object>> employees = (List<Map<String, Object>>) dmnResult.getContext().get("Employee Relation");
        Map<String, Object> e = employees.get(0);
        assertThat(e.get("Name")).isEqualTo("John");
        assertThat(e.get("Dept")).isEqualTo("Sales");
        assertThat(e.get("Salary")).isEqualTo(BigDecimal.valueOf(100000));

        e = employees.get(1);
        assertThat(e.get("Name")).isEqualTo("Mary");
        assertThat(e.get("Dept")).isEqualTo("Finances");
        assertThat(e.get("Salary")).isEqualTo(BigDecimal.valueOf(120000));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void lendingExample(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0004-lending.dmn", getClass());
//        runtime.addListener( DMNRuntimeUtil.createListener() );

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_4e0f0b70-d31c-471c-bd52-5ca709ed362b", "Lending1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        final Map<String, Object> applicant = new HashMap<>();
        final Map<String, Object> monthly = new HashMap<>();
        monthly.put("Income", 6000);
        monthly.put("Expenses", 2000);
        monthly.put("Repayments", 0);
        applicant.put("Monthly", monthly);
        applicant.put("Age", 35);
        applicant.put("ExistingCustomer", true);
        applicant.put("MaritalStatus", "M");
        applicant.put("EmploymentStatus", "EMPLOYED");
        final Map<String, Object> product = new HashMap<>();
        product.put("ProductType", "STANDARD LOAN");
        product.put("Amount", 350000);
        product.put("Rate", new BigDecimal("0.0395"));
        product.put("Term", 360);
        final Map<String, Object> bureau = new HashMap<>();
        bureau.put("CreditScore", 649);
        bureau.put("Bankrupt", false);

        context.set("ApplicantData", applicant);
        context.set("RequestedProduct", product);
        context.set("BureauData", bureau);
        context.set("SupportingDocuments", "yes");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        final DMNContext ctx = dmnResult.getContext();

        assertThat(ctx.get("ApplicationRiskScore")).isEqualTo(BigDecimal.valueOf(130));
        assertThat(ctx.get("Pre-bureauRiskCategory")).isEqualTo("LOW");
        assertThat(ctx.get("BureauCallType")).isEqualTo("MINI");
        assertThat(ctx.get("Post-bureauRiskCategory")).isEqualTo("LOW");
        assertThat(((BigDecimal) ctx.get("RequiredMonthlyInstallment")).setScale(5, BigDecimal.ROUND_DOWN)).
                isEqualTo(new BigDecimal("1680.880325608555").setScale(5, BigDecimal.ROUND_DOWN));
        assertThat(ctx.get("Pre-bureauAffordability")).isEqualTo(Boolean.TRUE);
        assertThat(ctx.get("Eligibility")).isEqualTo("ELIGIBLE");
        assertThat(ctx.get("Strategy")).isEqualTo("BUREAU");
        assertThat(ctx.get("Post-bureauAffordability")).isEqualTo(Boolean.TRUE);
        assertThat(ctx.get("Routing")).isEqualTo("ACCEPT");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dateAndTime(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0007-date-time.dmn", getClass());
        runtime.addListener(DMNRuntimeUtil.createListener());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_69430b3e-17b8-430d-b760-c505bf6469f9", "dateTime Table 58");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

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
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext ctx = dmnResult.getContext();

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(ctx.get("Date-Time")).isEqualTo(ZonedDateTime.of(2016, 12, 24, 23, 59, 0, 0, ZoneOffset.ofHours(-5)));
        assertThat(ctx.get("Date")).isEqualTo(new HashMap<String, Object>() {{
            put("fromString", LocalDate.of(2015, 12, 24));
            put("fromDateTime", LocalDate.of(2016, 12, 24));
            put("fromYearMonthDay", LocalDate.of(1999, 11, 22));
        }});
        assertThat(ctx.get("Time")).isEqualTo(OffsetTime.of(0, 0, 1, 0, ZoneOffset.ofHours(-1)));
        assertThat(ctx.get("Date-Time2")).isEqualTo(ZonedDateTime.of(2015, 12, 24, 0, 0, 1, 0, ZoneOffset.ofHours(-1)));
        assertThat(ctx.get("Time2")).isEqualTo(OffsetTime.of(0, 0, 1, 0, ZoneOffset.ofHours(-1)));
        assertThat(ctx.get("Time3")).isEqualTo(OffsetTime.of(12, 59, 1, 300000000, ZoneOffset.ofHours(-1)));
        assertThat(ctx.get("dtDuration1")).isEqualTo(Duration.parse("P13DT2H14S"));
        assertThat(ctx.get("dtDuration2")).isEqualTo(Duration.parse("P367DT3H58M59S"));
        assertThat(ctx.get("hoursInDuration")).isEqualTo(new BigDecimal("3"));
        assertThat(ctx.get("sumDurations")).isEqualTo(Duration.parse("PT9125H59M13S"));
        assertThat(ctx.get("ymDuration2")).isEqualTo(ComparablePeriod.parse("P1Y"));
        assertThat(ctx.get("cDay")).isEqualTo(BigDecimal.valueOf(24));
        assertThat(ctx.get("cYear")).isEqualTo(BigDecimal.valueOf(2015));
        assertThat(ctx.get("cMonth")).isEqualTo(BigDecimal.valueOf(12));
        assertThat(ctx.get("cHour")).isEqualTo(BigDecimal.valueOf(0));
        assertThat(ctx.get("cMinute")).isEqualTo(BigDecimal.valueOf(0));
        assertThat(ctx.get("cSecond")).isEqualTo(BigDecimal.valueOf(1));
        assertThat(ctx.get("cTimezone")).isEqualTo("GMT-01:00");
        assertThat(ctx.get("years")).isEqualTo(BigDecimal.valueOf(1));
        assertThat(ctx.get("d1seconds")).isEqualTo(BigDecimal.valueOf(14));

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void filtering(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Person_filtering_by_age.dmn", getClass());
        runtime.addListener(DMNRuntimeUtil.createListener());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_e215ed7a-701b-4c53-b8df-4b4d23d5fe32", "Person filtering by age");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Min Age", 50);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(((List) dmnResult.getContext().get("Filtering"))).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).hasSize(2);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void nowFunction(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("today_function_test.dmn", getClass());
        runtime.addListener(DMNRuntimeUtil.createListener());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_4ad80959-5fd8-46b7-8c9a-ab2fa58cb5b4", "When is it");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("The date", LocalDate.of(2017, 1, 12));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.getContext().get("When is it")).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isEqualTo("It is in the past");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void timeFunction(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("TimeFromDate.dmn", getClass());
        runtime.addListener(DMNRuntimeUtil.createListener());

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ecf4ea54-2abc-4e2f-a101-4fe14e356a46", "Dessin 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("datetimestring", "2016-07-29T05:48:23");
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.getContext().get("time")).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isEqualTo(LocalTime.of(5, 48, 23));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void alternativeNSDecl(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("alternative_feel_ns_declaration.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0001-input-data-string");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Full Name", "John Doe");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(dmnResult.getDecisionResults()).hasSize(1);
        assertThat(dmnResult.getDecisionResultByName("Greeting Message").getResult()).isEqualTo("Hello John Doe");

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Greeting Message")).isEqualTo("Hello John Doe");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void loanComparison(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("loanComparison.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_3a1fd8f4-ea04-4453-aa30-ff14140e3441", "loanComparison");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("RequestedAmt", 500000);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void getViableLoanProducts(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Get_Viable_Loan_Products.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_3e1a628d-36bc-45f1-8464-b201735e5ce0", "Get Viable Loan Products");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final Map<String, Object> requested = new HashMap<>();
        requested.put("PropertyZIP", "91001");
        requested.put("LoanAmt", 300000);
        requested.put("Objective", "Payment");
        requested.put("DownPct", new BigDecimal("0.4"));
        requested.put("MortgageType", "Fixed 20");
        final DMNContext context = DMNFactory.newContext();
        context.set("Requested", requested);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("isConforming")).isEqualTo(Boolean.TRUE);
        assertThat((Collection<Object>) result.get("LoanTypes")).hasSize(3);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void yearsAndMonthsDuration(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("yearMonthDuration.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_6eda1490-21ca-441e-8a26-ab3ca800e43c", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final BuiltInType feelType = (BuiltInType) BuiltInTypeUtils.determineTypeFromName("yearMonthDuration");
        final ChronoPeriod period = (ChronoPeriod) feelType.fromString("P2Y1M");

        final DMNContext context = runtime.newContext();
        context.set("iDuration", period);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("How long")).isEqualTo("Longer than a year");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void invalidVariableNames(VariantTestConf conf) {
        testConfig = conf;
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("invalid-variable-names.dmn", this.getClass());
        assertThat(messages).isNotEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void testNull(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("null_values.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "Null values model");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();
        context.set("Null Input", null);

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        DMNContext result = dmnResult.getContext();
        assertThat(result.get("Null value")).isEqualTo("Input is null");

        context = runtime.newContext();
        context.set("Null Input", "foo");

        dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        result = dmnResult.getContext();
        assertThat(result.get("Null value")).isEqualTo("Input is not null");

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void invalidModel(VariantTestConf conf) {
        testConfig = conf;
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("Loan_Prequalification_Condensed_Invalid.dmn", this.getClass());
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getSourceId()).isEqualTo("_8b5cac9e-c8ca-4817-b05a-c70fa79a8d48");
        assertThat(messages.get(1).getSourceId()).isEqualTo("_ef09d90e-e1a4-4ec9-885b-482d1f4a1cee");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void nullOnNumber(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Number_and_null_entry.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_a293b9f9-c912-41ee-8147-eae59ba86ac5", "Number and null entry");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        DMNContext context = runtime.newContext();

        context.set("num", null);

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision Logic 1")).isEqualTo("Null");

        context = runtime.newContext();
        context.set("num", 4);

        dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        result = dmnResult.getContext();
        assertThat(result.get("Decision Logic 1")).isEqualTo("Positive number");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void loanRecommendation2(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Loan_Recommendation2.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_35c7339b-b868-43da-8f06-eb481708c73c", "Loan Recommendation2");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final Map<String, Object> loan = new HashMap<>();
        loan.put("Amount", 100000);
        loan.put("Rate", 2.39);
        loan.put("Term", 60);

        final Map<String, Object> borrower = new HashMap<>();
        borrower.put("Age", 39);
        borrower.put("EmploymentStatus", "Employed");
        borrower.put("YearsAtCurrentEmployer", 10);
        borrower.put("TotalAnnualIncome", 150000);
        borrower.put("NonSalaryIncome", 0);
        borrower.put("MonthlyDebtPmtAmt", 2000);
        borrower.put("LiquidAssetsAmt", 50000);

        final DMNContext context = runtime.newContext();
        context.set("Credit Score", null);
        context.set("Appraised Value", 200000);
        context.set("Loan", loan);
        context.set("Borrower", borrower);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Loan Recommendation")).isEqualTo("Decline");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void priorityTable(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("priority_table.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_ff54a44d-b8f5-48fc-b2b7-43db767e8a1c",
                "not quite all or nothing P");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        context.set("isAffordable", false);
        context.set("RiskCategory", "Medium");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Approval Status")).isEqualTo("Declined");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void priorityTableContextRecursion(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("priority_table_context_recursion.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_ff54a44d-b8f5-48fc-b2b7-43db767e8a1c",
                "not quite all or nothing P");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        context.set("isAffordable", false);
        context.set("RiskCategory", "Medium");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Approval Status")).isEqualTo("Declined");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void priorityTableMissingOutputValues(VariantTestConf conf) {
        testConfig = conf;
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("DTABLE_PRIORITY_MISSING_OUTVALS.dmn", this.getClass());
        assertThat(messages).hasSize(1);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void non_priority_table_missing_output_values(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DTABLE_NON_PRIORITY_MISSING_OUTVALS.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "https://github.com/kiegroup/kie-dmn",
                "DTABLE_NON_PRIORITY_MISSING_OUTVALS");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void priorityTableOneOutputValue(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DTABLE_PRIORITY_ONE_OUTVAL.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "https://github.com/kiegroup/kie-dmn",
                "DTABLE_PRIORITY_ONE_OUTVAL");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void noPrefix(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("drools1502-noprefix.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "https://www.drools.org/kie-dmn/definitions",
                "definitions");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        context.set("MyInput", "a");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("MyDecision")).isEqualTo("Decision taken");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void wrongConstraintsInItemDefinition(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1503
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("WrongConstraintsInItemDefinition.dmn", this.getClass());
        assertThat(messages).as(DMNRuntimeUtil.formatMessages(messages)).hasSize(3);
        assertThat(messages.get(0).getSourceReference()).isInstanceOf(ItemDefinition.class);
        assertThat(messages.get(0).getMessageType()).isEqualTo(DMNMessageType.ERR_COMPILING_FEEL);
        assertThat(messages.get(1).getSourceId()).isEqualTo("_e794c655-4fdf-45d1-b7b7-d990df513f92");
        assertThat(messages.get(1).getMessageType()).isEqualTo(DMNMessageType.ERR_COMPILING_FEEL);

        // The DecisionTable does not define typeRef for the single OutputClause, but neither the enclosing Decision define typeRef for its variable
        assertThat(messages.get(2).getSourceId()).isEqualTo("_31911de7-e184-411c-99d1-f33977971270");
        assertThat(messages.get(2).getMessageType()).isEqualTo(DMNMessageType.MISSING_TYPE_REF);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void resolutionOfVariableWithLeadingOrTrailingSpaces(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1504
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("variableLeadingTrailingSpaces.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "https://www.drools.org/kie-dmn/definitions",
                "definitions");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        final Map<String, String> person = new HashMap<>();
        person.put("Name", "John");
        person.put("Surname", "Doe");
        context.set("Input Person", person);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Further Decision")).isEqualTo("The person was greeted with: 'Ciao John Doe'");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void outOfOrderItemsNPE(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("out-of-order-items.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "https://github.com/kiegroup/kie-dmn",
                "out-of-order");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.getMessages().stream().anyMatch(m -> m.getMessageType().equals(DMNMessageType.FAILED_VALIDATOR)))
                .as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void itemDefDependencies(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1505
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("itemDef-dependency.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_2374ee6d-75ed-4e9d-95d3-a88c135e1c43",
                "Drawing 1a");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        final Map<String, String> person = new HashMap<>();
        person.put("Full Name", "John Doe");
        person.put("Address", "100 East Davie Street");
        context.set("Input Person", person);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("My Decision")).isEqualTo("The person John Doe is located at 100 East Davie Street");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionResultTypeCheck(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1513
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("LoanRecommendationWrongOutputType.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/dmn/definitions/_591d49d0-26e1-4a1c-9f72-b65bec09964a",
                "Loan Recommendation Multi-step");
        assertThat(dmnModel).isNotNull();
        System.out.println(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()));
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        final Map<String, Number> loan = new HashMap<>();
        loan.put("Amount", 100);
        loan.put("Rate", 12);
        loan.put("Term", 1);
        context.set("Loan", loan);

        final DMNResult dmnResult = runtime.evaluateByName(dmnModel, context, "Loan Payment");
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();
        assertThat(dmnResult.getMessages()).hasSize(1);
        assertThat(dmnResult.getMessages().get(0).getSourceId()).isEqualTo("_93062144-ebc7-4ef7-a156-c342aeffac49");
        assertThat(dmnResult.getMessages().get(0).getMessageType()).isEqualTo(DMNMessageType.ERROR_EVAL_NODE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void npe(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1512
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("NPE.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/dmn/definitions/_95b7ee22-1964-4be5-b7db-7db66692c707",
                "Dessin 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();
        assertThat(dmnResult.getMessages().stream().anyMatch(m -> m.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isEqualTo(Boolean.TRUE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void unionofLetters(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Union_of_letters.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_76362694-41e8-400c-8dea-e5f033d4f405",
                "Union of letters");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext ctx1 = runtime.newContext();
        ctx1.set("A1", Arrays.asList("a", "b"));
        ctx1.set("A2", Arrays.asList("b", "c"));
        final DMNResult dmnResult1 = runtime.evaluateAll(dmnModel, ctx1);
        assertThat(dmnResult1.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult1.getMessages())).isFalse();
        assertThat((List<?>) dmnResult1.getContext().get("D1")).asList().contains("a", "b", "c");

        final DMNContext ctx2 = runtime.newContext();
        ctx2.set("A1", Arrays.asList("a", "b"));
        ctx2.set("A2", Arrays.asList("b", "x"));
        final DMNResult dmnResult2 = runtime.evaluateAll(dmnModel, ctx2);
        assertThat(dmnResult2.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult2.getMessages())).isTrue();
        assertThat(dmnResult2.getMessages().stream().anyMatch(m -> m.getMessageType().equals(DMNMessageType.ERROR_EVAL_NODE))).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void unknownVariable1(VariantTestConf conf) {
        testConfig = conf;
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("unknown_variable1.dmn", this.getClass());
        assertThat(messages.stream().filter(m -> m.getMessageType().equals(DMNMessageType.ERR_COMPILING_FEEL))
                .filter(m -> m.getMessage().contains("Unknown variable 'NonSalaryPct'"))
                .count()).isEqualTo(1);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void unknownVariable2(VariantTestConf conf) {
        testConfig = conf;
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("unknown_variable2.dmn", this.getClass());
        assertThat(messages.get(0).getMessageType()).isEqualTo(DMNMessageType.ERR_COMPILING_FEEL);
        assertThat(messages.get(0).getMessage()).containsSequence("Unknown variable 'Borrower.liquidAssetsAmt'");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void singleDecisionWithContext(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("SingleDecisionWithContext.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_71af58db-e1df-4b0f-aee2-48e0e8d89672",
                "SingleDecisionWithContext");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = runtime.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("MyDecision")).isEqualTo("Hello John Doe");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void ex61(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Ex_6_1.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_5f1269c8-1e6f-4748-9eca-26aa1b1278ef",
                "Ex 6-1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext ctx = runtime.newContext();
        final Map<String, Object> t1 = new HashMap<>();
        t1.put("city", "Los Angeles");
        t1.put("name", "Los Angeles");
        t1.put("wins", 0);
        t1.put("losses", 1);
        t1.put("bonus points", 40);
        final Map<String, Object> t2 = new HashMap<>();
        t2.put("city", "San Francisco");
        t2.put("name", "San Francisco");
        t2.put("wins", 1);
        t2.put("losses", 0);
        t2.put("bonus points", 7);
        ctx.set("NBA Pacific", Arrays.asList(t1, t2));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Number of distinct cities")).isEqualTo(new BigDecimal(2));
        assertThat(result.get("Second place losses")).isEqualTo(new BigDecimal(0));
        assertThat(result.get("Max wins")).isEqualTo(new BigDecimal(1));
        assertThat(result.get("Mean wins")).isEqualTo(new BigDecimal(0.5));
        assertThat((List<?>) result.get("Positions of Los Angeles teams")).asList().contains(new BigDecimal(1));
        assertThat(result.get("Number of teams")).isEqualTo(new BigDecimal(2));
        assertThat(result.get("Sum of bonus points")).isEqualTo(new BigDecimal(47));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void singletonlistFunctionCall(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("singletonlist_fuction_call.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_0768879b-5ee1-410f-92f0-7732573b069d",
                "expression function subst [a] with a");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext ctx = runtime.newContext();
        ctx.set("InputLineItem", prototype(entry("Line", "0015"), entry("Description", "additional Battery")));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("The Battery")).isEqualTo(prototype(entry("Line", "0010"), entry("Description", "Battery")));
        assertThat((List<?>) result.get("Remove Battery")).asList().contains(prototype(entry("Line", "0020"), entry("Description", "Case")),
                prototype(entry("Line", "0030"), entry("Description", "Power Supply"))
        );
        assertThat((List<?>) result.get("Remove Battery")).asList().doesNotContain(prototype(entry("Line", "0010"), entry("Description", "Battery")));

        assertThat((List<?>) result.get("Insert before Line 0020")).asList().contains(prototype(entry("Line", "0010"), entry("Description", "Battery")),
                prototype(entry("Line", "0015"), entry("Description", "additional Battery")),
                prototype(entry("Line", "0020"), entry("Description", "Case")),
                prototype(entry("Line", "0030"), entry("Description", "Power Supply"))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void javaFunctionContext(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("java_function_context.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/dmn/definitions/_b42317c4-4f0c-474e-a0bf-2895b0b3c314",
                "Dessin 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext ctx = runtime.newContext();
        ctx.set("Input", 3.14);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat(((BigDecimal) result.get("D1")).setScale(4, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal("-1.0000"));
        assertThat(((BigDecimal) result.get("D2")).setScale(4, BigDecimal.ROUND_HALF_UP)).isEqualTo(new BigDecimal("-1.0000"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void javaFunctionContextWithErrors(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1568
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("java_function_context_with_errors.dmn", this.getClass());
        assertThat(messages).hasSize(1);

        final List<String> sourceIDs = messages.stream().map(DMNMessage::getSourceId).collect(Collectors.toList());
        // FEEL FuncDefNode not checked at compile time: assertTrue( sourceIDs.contains( "_a72a7aff-48c3-4806-83ca-fc1f1fe34320") );
        assertThat(sourceIDs).contains("_a72a7aff-48c3-4806-83ca-fc1f1fe34321");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void count_csatrade_ratings(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1563
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("countCSATradeRatings.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_1a7d184c-2e38-4462-ae28-15591ef6d534",
                "countCSATradeRatings");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext ctx = runtime.newContext();
        final List<Map<?, ?>> ratings = new ArrayList<>();
        ratings.add(prototype(entry("Agency", "FITCH"), entry("Value", "val1")));
        ratings.add(prototype(entry("Agency", "MOODY"), entry("Value", "val2")));
        ctx.set("CSA Trade Ratings", ratings);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Trade Ratings")).isEqualTo(new BigDecimal(2));


        final DMNContext ctx2 = runtime.newContext();
        ctx2.set("CSA Trade Ratings", null);
        final DMNResult dmnResult2 = runtime.evaluateAll(dmnModel, ctx2);
        assertThat(dmnResult2.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult2.getMessages())).isTrue();
        assertThat(dmnResult2.getMessages().stream().anyMatch(m -> m.getMessageType().equals(DMNMessageType.FEEL_EVALUATION_ERROR))).isTrue();
        assertThat(dmnResult2.getDecisionResultByName("Trade Ratings").getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.FAILED);


        final DMNResult dmnResult3 = runtime.evaluateAll(dmnModel, runtime.newContext());
        assertThat(dmnResult3.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult3.getMessages())).isTrue();
        assertThat(dmnResult3.getMessages().stream().anyMatch(m -> m.getMessageType().equals(DMNMessageType.REQ_NOT_FOUND))).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void forLoopTypeCheck(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1580
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("PersonListHelloBKM.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_ec5a78c7-a317-4c39-8310-db59be60f1c8",
                "PersonListHelloBKM");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();

        final Map<String, Object> p1 = prototype(entry("Full Name", "John Doe"), entry("Age", 33));
        final Map<String, Object> p2 = prototype(entry("Full Name", "47"), entry("Age", 47));

        context.set("My Input Data", Arrays.asList(p1, p2));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat((List<?>) result.get("My Decision")).asList().contains("The person named John Doe is 33 years old.",
                "The person named 47 is 47 years old.");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void typeInferenceForNestedContextAnonymousEntry(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1585
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("PersonListHelloBKM2.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_7e41a76e-2df6-4899-bf81-ae098757a3b6",
                "PersonListHelloBKM2");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();

        final Map<String, Object> p1 = prototype(entry("Full Name", "John Doe"), entry("Age", 33));
        final Map<String, Object> p2 = prototype(entry("Full Name", "47"), entry("Age", 47));

        context.set("My Input Data", Arrays.asList(p1, p2));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat((List<?>) result.get("My Decision")).asList().contains(prototype(entry("Full Name", "Prof. John Doe"), entry("Age", NumberEvalHelper.coerceNumber(33))),
                prototype(entry("Full Name", "Prof. 47"), entry("Age", NumberEvalHelper.coerceNumber(47)))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void sameEveryTypeCheck(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-1587
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("same_every_type_check.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_09a13244-114d-43fb-9e00-cda89a2000dd",
                "same every type check");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = runtime.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        final DMNContext result = dmnResult.getContext();

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(result.get("Some are even")).isEqualTo(Boolean.TRUE);
        assertThat(result.get("Every are even")).isEqualTo(Boolean.FALSE);
        assertThat(result.get("Some are positive")).isEqualTo(Boolean.TRUE);
        assertThat(result.get("Every are positive")).isEqualTo(Boolean.TRUE);
        assertThat(result.get("Some are negative")).isEqualTo(Boolean.FALSE);
        assertThat(result.get("Every are negative")).isEqualTo(Boolean.FALSE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dateAllowedValues(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("date_allowed_values.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_fbf002a3-615b-4f02-98e4-c28d4676225a",
                "Error with constraints verification");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext ctx = runtime.newContext();
        final Object duration = BuiltInType.DURATION.fromString("P20Y");
        ctx.set("yearsMonth", duration);
        final Object dateTime = BuiltInType.DATE_TIME.fromString("2017-05-16T17:58:00.000");
        ctx.set("dateTime", dateTime);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);
        final DMNContext result = dmnResult.getContext();

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat((Map<String, Object>) result.get("Decision Logic 1")).containsEntry("years and months", duration);
        assertThat((Map<String, Object>) result.get("Decision Logic 1")).containsEntry("Date Time", dateTime);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void artificialAttributes(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string-artificial-attributes.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools", "0001-input-data-string");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Full Name", "John Doe");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(dmnResult.getDecisionResults()).hasSize(1);
        assertThat(dmnResult.getDecisionResultByName("Greeting Message").getResult()).isEqualTo("Hello John Doe");

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Greeting Message")).isEqualTo("Hello John Doe");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void invokeFunctionSuccess(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Caller.dmn", this.getClass(), "Calling.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_b0a696d6-3d57-4e97-b5d4-b44a63909d67", "Caller");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("My Name", "John Doe");
        context.set("My Number", 3);
        context.set("Call ns", "http://www.trisotech.com/definitions/_88156d21-3acc-43b6-8b81-385caf0bb6ca");
        context.set("Call name", "Calling");
        context.set("Invoke decision", "Final Result");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Final decision")).isEqualTo("The final decision is: Hello, John Doe your number once double is equal to: 6");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void invokeFunctionWrongNamespace(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Caller.dmn", this.getClass(), "Calling.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_b0a696d6-3d57-4e97-b5d4-b44a63909d67", "Caller");
        assertThat(dmnModel).isNotNull();

        final DMNContext wrongContext = DMNFactory.newContext();
        wrongContext.set("My Name", "John Doe");
        wrongContext.set("My Number", 3);
        wrongContext.set("Call ns", "http://www.acme.com/a-wrong-namespace");
        wrongContext.set("Call name", "Calling");
        wrongContext.set("Invoke decision", "Final Result");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, wrongContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();
        // total of: 2. x1 error in calling external decision, and x1 error in making final decision as it depends on the former.
        assertThat(dmnResult.getMessages()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).hasSize(3);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void invokeFunctionWrongDecisionName(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Caller.dmn", this.getClass(), "Calling.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_b0a696d6-3d57-4e97-b5d4-b44a63909d67", "Caller");
        assertThat(dmnModel).isNotNull();

        final DMNContext wrongContext = DMNFactory.newContext();
        wrongContext.set("My Name", "John Doe");
        wrongContext.set("My Number", 3);
        wrongContext.set("Call ns", "http://www.trisotech.com/definitions/_88156d21-3acc-43b6-8b81-385caf0bb6ca");
        wrongContext.set("Call name", "Calling");
        wrongContext.set("Invoke decision", "<unexistent decision>");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, wrongContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();
        // total of: 2. x1 error in calling external decision, and x1 error in making final decision as it depends on the former.
        assertThat(dmnResult.getMessages()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).hasSize(3);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void invokeFunctionCallerError(VariantTestConf conf) {
        testConfig = conf;

        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Caller.dmn", this.getClass(), "Calling.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_b0a696d6-3d57-4e97-b5d4-b44a63909d67", "Caller");
        assertThat(dmnModel).isNotNull();

        final DMNContext wrongContext = DMNFactory.newContext();
        wrongContext.set("My Name", "John Doe");
        wrongContext.set("My Number", "<not a number>");
        wrongContext.set("Call ns", "http://www.trisotech.com/definitions/_88156d21-3acc-43b6-8b81-385caf0bb6ca");
        wrongContext.set("Call name", "Calling");
        wrongContext.set("Invoke decision", "Final Result");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, wrongContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();
        // total of: 2. x1 error in calling external decision, and x1 error in making final decision as it depends on the former.
        // please notice it will print 4 lines in the log, 2x are the "external invocation" and then 2x are the one by the caller, checked herebelow:
        assertThat(dmnResult.getMessages()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).hasSize(3);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void invalidFunction(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("InvalidFunction.dmn", this.getClass());
        final DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_84453b71-5d23-479f-9481-5196d92bacae", "0003-iteration-augmented");
        assertThat(model).isNotNull();
        final DMNContext context = DMNFactory.newContext();
        context.set("Loans", new HashMap<>());
        final DMNResult result = runtime.evaluateAll(model, context);
        final List<DMNDecisionResult> decisionResults = result.getDecisionResults();
        FEELStringMarshaller.INSTANCE.marshall(Arrays.asList(decisionResults.get(0).getResult(), decisionResults.get(1).getResult()));
    }

    private Definitions buildSimplifiedDefinitions(final String namespace, final String... decisions) {
        final Definitions def = new TDefinitions();
        def.setNamespace(namespace);
        for (final String d : decisions) {
            final Decision dec = new TDecision();
            dec.setName(d);
            InformationItem variable = new TInformationItem();
            variable.setName("variable-" + d);
            dec.setVariable(variable);
            def.getDrgElement().add(dec);
            def.addChildren(dec);
            dec.setParent(def);
        }
        return def;
    }

    private DecisionNodeImpl buildSimplifiedDecisionNode(final Definitions def, final String name) {
        DecisionNodeImpl toReturn = new DecisionNodeImpl(def.getDrgElement().stream().filter(drg -> drg.getName().equals(name)).filter(Decision.class::isInstance)
                .map(Decision.class::cast)
                .findFirst().get());
        toReturn.setEvaluator((eventManager, result) -> new EvaluatorResultImpl(name, EvaluatorResult.ResultType.SUCCESS));
        toReturn.setResultType(SimpleTypeImpl.UNKNOWN_DMNTYPE(""));
        return toReturn;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void cycleDetection(VariantTestConf conf) {
        testConfig = conf;
        final Definitions defs = buildSimplifiedDefinitions("ns", "a", "b");
        final DecisionNodeImpl a = buildSimplifiedDecisionNode(defs, "a");
        final DecisionNodeImpl b = buildSimplifiedDecisionNode(defs, "b");
        a.addDependency("b", b);
        b.addDependency("a", b);
        final DMNModelImpl model = new DMNModelImpl(defs);
        model.setDefinitions(defs);
        model.addDecision(a);
        model.addDecision(b);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(this.getClass());
        final DMNResult result = runtime.evaluateAll(model, DMNFactory.newContext());
        assertThat(result.hasErrors()).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void cycleDetectionSelfReference(VariantTestConf conf) {
        testConfig = conf;
        final Definitions defs = buildSimplifiedDefinitions("ns", "self");
        final DecisionNodeImpl decision = buildSimplifiedDecisionNode(defs, "self");
        decision.addDependency("self", decision);
        final DMNModelImpl model = new DMNModelImpl(defs);
        model.setDefinitions(defs);
        model.addDecision(decision);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(this.getClass());
        final DMNResult result = runtime.evaluateAll(model, DMNFactory.newContext());
        assertThat(result.hasErrors()).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void sharedDependency(VariantTestConf conf) {
        testConfig = conf;
        final Definitions defs = buildSimplifiedDefinitions("ns", "a", "b", "c");
        final DecisionNodeImpl a = buildSimplifiedDecisionNode(defs, "a");
        final DecisionNodeImpl b = buildSimplifiedDecisionNode(defs, "b");
        final DecisionNodeImpl c = buildSimplifiedDecisionNode(defs, "c");
        a.addDependency("c", c);
        b.addDependency("c", c);
        final DMNModelImpl model = new DMNModelImpl(defs);
        model.setDefinitions(defs);
        model.addDecision(a);
        model.addDecision(b);
        model.addDecision(c);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(this.getClass());
        final DMNResult result = runtime.evaluateAll(model, DMNFactory.newContext());
        assertThat(result.hasErrors()).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void cycleDetectionDeadlyDiamond(VariantTestConf conf) {
        testConfig = conf;
        final Definitions defs = buildSimplifiedDefinitions("ns", "a", "b", "c", "d");
        final DecisionNodeImpl a = buildSimplifiedDecisionNode(defs, "a");
        final DecisionNodeImpl b = buildSimplifiedDecisionNode(defs, "b");
        final DecisionNodeImpl c = buildSimplifiedDecisionNode(defs, "c");
        final DecisionNodeImpl d = buildSimplifiedDecisionNode(defs, "d");
        a.addDependency("b", b);
        a.addDependency("c", c);
        b.addDependency("d", d);
        c.addDependency("d", d);
        final DMNModelImpl model = new DMNModelImpl(defs);
        model.setDefinitions(defs);
        model.addDecision(a);
        model.addDecision(b);
        model.addDecision(c);
        model.addDecision(d);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(this.getClass());
        final DMNResult result = runtime.evaluateAll(model, DMNFactory.newContext());
        assertThat(result.hasErrors()).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void ex43simplified(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Ex_4_3simplified.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_5c5a9c72-627e-4666-ae85-31356fed3658", "Ex_4_3simplified");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("number", 123.123456d);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        System.out.println(dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Formatted Monthly Payment")).isEqualTo("€123.12");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void ex43simplifiedASD(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2117 improve Msg.ERROR_EVAL_NODE_DEP_WRONG_TYPE
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Ex_4_3simplified.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_5c5a9c72-627e-4666-ae85-31356fed3658", "Ex_4_3simplified");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("number", "ciao");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        System.out.println(dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();

        // we want the error message to include not only which value was incompatible, but the type which was expected.
        // in this case the value is `ciao` for a String
        // but should have been a FEEL:number.
        assertThat(dmnResult.getMessages().stream().filter(m -> m.getMessageType() == DMNMessageType.ERROR_EVAL_NODE).anyMatch(m -> m.getMessage().endsWith("is not allowed by the declared type (DMNType{ http://www.omg.org/spec/FEEL/20140401 : number })"))).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void drools2125(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2125
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("drools2125.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_9f976b29-4cdd-42e9-8737-0ccbc2ad9498", "drools2125");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("person", "Bob");
        context.set("list of persons", Arrays.asList("Bob", "John"));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("person is Bob")).isEqualTo("yes");
        assertThat(result.get("persons complies with UT list")).isEqualTo("yes");
        assertThat(result.get("person on the list of persons")).isEqualTo("yes");
        assertThat(result.get("persons complies with hardcoded list")).isEqualTo("yes");
        assertThat(result.get("person is person")).isEqualTo("yes");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void drools2147(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2147
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DROOLS-2147.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_cbdacb7b-f72d-457d-b4f4-54020a06db24", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext resultContext = dmnResult.getContext();
        final List people = (List) resultContext.get("People");
        final List peopleGroups = (List) resultContext.get("People groups");

        assertThat(people).hasSize(6);

        assertThat(peopleGroups).hasSize(3);
        assertThat(((List) peopleGroups.get(0))).hasSize(2);
        assertThat(((List) peopleGroups.get(1))).hasSize(2);
        assertThat(((List) peopleGroups.get(2))).hasSize(2);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void drools2147Message(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2147 truncate message length
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Ex_4_3simplified.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_5c5a9c72-627e-4666-ae85-31356fed3658", "Ex_4_3simplified");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        final StringBuilder sb = new StringBuilder("abcdefghijklmnopqrstuvwxyz");
        for (int i = 0; i < 100; i++) {
            sb.append("abcdefghijklmnopqrstuvwxyz");
        }
        context.set("number", sb.toString());

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        System.out.println(dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();

        assertThat(dmnResult.getMessages().stream().filter(m -> m.getMessageType() == DMNMessageType.ERROR_EVAL_NODE).anyMatch(m -> m.getMessage().contains("... [string clipped after 50 chars, total length is"))).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void drools2192(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2192
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("hardcoded_function_definition.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_99854980-65c8-4e9b-b365-bd30ded69f40", "hardcoded_function_definition");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext resultContext = dmnResult.getContext();
        assertThat(((BigDecimal) resultContext.get("hardcoded decision")).intValue()).isEqualTo(47);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void drools2200(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2200
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("will_be_null_if_negative.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_c5889555-7ae5-4a67-a872-3a9492caf6e7", "will be null if negative");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a number", -1);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext resultContext = dmnResult.getContext();
        assertThat(((Map) resultContext.get("will be null if negative")).get("s1")).isNull();
        assertThat(((Map) resultContext.get("will be null if negative")).get("s2")).isEqualTo("negative");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void drools2201(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2201
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                ks.getResources().newClassPathResource("typecheck_in_context_result.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_42bf043d-df86-48bd-9045-dfc08aa8ba0d", "typecheck in context result");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext resultContext = dmnResult.getContext();
        assertThat(((Map<String, Object>) resultContext.get("an hardcoded person"))).containsKeys("name", "age");
        assertThat(((Map<String, Object>) resultContext.get("an hardcoded person with no name"))).containsKeys("age");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void drools2201b(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2201
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                ks.getResources().newClassPathResource("typecheck_in_DT.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_99ccd4df-41ac-43c3-a563-d58f43149829", "typecheck in DT");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a number", 0);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext resultContext = dmnResult.getContext();
        assertThat(((BigDecimal) resultContext.get("an odd decision")).intValue()).isEqualTo(47);
    }

    public static class DROOLS2286 {
        private String name;
        private String surname;

        DROOLS2286(final String name, final String surname) {
            this.name = name;
            this.surname = surname;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void drools2286(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2286
        final DROOLS2286 johnCena = new DROOLS2286("John", "Cena");
        final DROOLS2286 leslieBrown = new DROOLS2286("Leslie", "Brown");
        final DROOLS2286 johnWick = new DROOLS2286("John", "Wick");

        final List<DROOLS2286> personList = new ArrayList<>();
        personList.add(johnCena);
        personList.add(leslieBrown);
        personList.add(johnWick);
        final DMNContext context = DMNFactory.newContext();
        context.set("PersonList", personList);

        assertDROOLS2286(context);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void drools2286bis(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2286 (map case)
        final Map<String, Object> johnCena = prototype(entry("name", "John"), entry("surname", "Cena"));
        final Map<String, Object> leslieBrown = prototype(entry("name", "Leslie"), entry("surname", "Brown"));
        final Map<String, Object> johnWick = prototype(entry("name", "John"), entry("surname", "Wick"));

        final List<Map<String, Object>> personList = new ArrayList<>();
        personList.add(johnCena);
        personList.add(leslieBrown);
        personList.add(johnWick);
        final DMNContext context = DMNFactory.newContext();
        context.set("PersonList", personList);

        assertDROOLS2286(context);
    }

    private void assertDROOLS2286(final DMNContext context) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("FilterJohns.dmn", this.getClass());
        final DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_feb9886e-22ce-469a-bbb6-096f13b71c7d", "FilterJohns");

        final DMNResult result = runtime.evaluateAll(model, context);
        final List<?> resultObject = (ArrayList<?>) result.getDecisionResultByName("PickAllJohns").getResult();

        assertThat(resultObject).hasSize(2);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void verifyExtendedKieFEELFunctionNow(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2322
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("just_now.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_56fd6445-ff6a-4c28-8206-71fce7f80436", "just now");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        assertThat(dmnResult.getDecisionResultByName("a decision just now").getResult()).isNotNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void verifyExtendedKieFEELFunctionToday(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2322
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("just_today.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_56fd6445-ff6a-4c28-8206-71fce7f80436", "just today");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        assertThat(dmnResult.getDecisionResultByName("a decision just today").getResult()).isNotNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void enhancedForLoop(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2307
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("drools2307.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_03d9481e-dcfc-4a59-9bdd-4f021cb2f0d8", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("an hardcoded forloop")).asList().containsExactly(new BigDecimal(2), new BigDecimal(3), new BigDecimal(4));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void listOfVowels(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2357
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("List_of_Vowels.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_c5f007ce-4d45-4aac-8729-991d4abc7826", "List of Vowels");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();
        assertThat(dmnResult.getMessages().stream()
                .filter(m -> m.getMessageType() == DMNMessageType.ERROR_EVAL_NODE)
                .anyMatch(m -> m.getSourceId().equals("_b2205027-d06c-41b5-8419-e14b501e14a6"))).isTrue();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide Vowel a")).isEqualTo(Collections.singletonList("a"));
        assertThat(result.get("Decide BAD")).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void enhancedForLoop2(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("MACD-enhanced_iteration.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6cfe7d88-6741-45d1-968c-b61a597d0964", "MACD-enhanced iteration");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        final Map<String, Object> d1 = prototype(entry("aDate", LocalDate.of(2018, 3, 5)), entry("Close", 1010));
        final Map<String, Object> d2 = prototype(entry("aDate", LocalDate.of(2018, 3, 6)), entry("Close", 1020));
        final Map<String, Object> d3 = prototype(entry("aDate", LocalDate.of(2018, 3, 7)), entry("Close", 1030));
        context.set("DailyTable", Arrays.asList(d1, d2, d3));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        LOG.debug("{}", dmnResult);

        final DMNContext resultContext = dmnResult.getContext();
        assertThat(((Map<String, Object>) ((List) resultContext.get("MACDTable")).get(0)).get("aDate")).isEqualTo(LocalDate.of(2018, 3, 5));
        assertThat(((Map<String, Object>) ((List) resultContext.get("MACDTable")).get(1)).get("aDate")).isEqualTo(LocalDate.of(2018, 3, 6));
        assertThat(((Map<String, Object>) ((List) resultContext.get("MACDTable")).get(2)).get("aDate")).isEqualTo(LocalDate.of(2018, 3, 7));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void notListInDT(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2416
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("anot.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_292c1c7b-6b38-415d-938f-e9ca51d30b2b", "anot");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a letter", "a");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("what letter decision")).isEqualTo("vowel");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void listContainmentDT(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2416
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("list_containment_DT.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6ab2bd6d-adaa-45c4-a141-a84382a201eb", "list containment DT");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Passenger", prototype(entry("name", "Osama bin Laden")));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Boarding Status")).isEqualTo("Denied");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void relationwithemptycell(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2439
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("relation_with_empty_cell.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_99a00903-2943-47df-bab1-a32f276617ea", "Relation with empty cell");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        System.out.println(dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision A")).asList().containsExactly(prototype(entry("name", null), entry("age", null)),
                prototype(entry("name", "John"), entry("age", new BigDecimal(1))),
                prototype(entry("name", null), entry("age", null)),
                prototype(entry("name", "Matteo"), entry("age", null)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void testFor(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2317
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Dynamic composition.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_c2d86765-c3c7-4e1d-b1fa-b830fa5bc529", "Dynamic composition");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void usingReusableKeywordAsPartOfBKMName(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2317 FEEL Syntax error on function(bkm) containing `for` keyword
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("say_for_hello.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_b6f2a9ca-a246-4f27-896a-e8ef04ea439c", "say for hello");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("just say")).asList().containsExactly("Hello", "Hello", "Hello");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void productFunction(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("product.dmn", this.getClass());
        final DMNModel model = runtime.getModel("http://www.trisotech.com/dmn/definitions/_40fdbc2c-a631-4ba4-8435-17571b5d1942", "Drawing 1");
        assertThat(model).isNotNull();
        assertThat(model.hasErrors()).as(DMNRuntimeUtil.formatMessages(model.getMessages())).isFalse();
        final DMNContext context = DMNFactory.newContext();
        context.set("product", new HashMap<String, Object>() {{
            put("name", "Product1");
            put("type", 1);
        }});
        final DMNDecisionResult result = runtime.evaluateAll(model, context).getDecisionResultByName("TestDecision");
        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getResult()).isEqualTo("This is product 1");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void notWithPredicates20180601(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2605
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("test20180601.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_9b8f2642-2597-4a99-9fcd-f9302692d3dc", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context3 = DMNFactory.newContext();
        context3.set("my num", new BigDecimal(3));

        final DMNResult dmnResult3 = runtime.evaluateAll(dmnModel, context3);
        assertThat(dmnResult3.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult3.getMessages())).isFalse();
        assertThat(dmnResult3.getDecisionResultByName("my decision").getResult()).isEqualTo(Boolean.FALSE);

        final DMNContext context10 = DMNFactory.newContext();
        context10.set("my num", new BigDecimal(10));

        final DMNResult dmnResult10 = runtime.evaluateAll(dmnModel, context10);
        assertThat(dmnResult10.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult10.getMessages())).isFalse();
        assertThat(dmnResult10.getDecisionResultByName("my decision").getResult()).isEqualTo(Boolean.TRUE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void notWithPredicates20180601b(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2605
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("BruceTask20180601.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_3802fcb2-5b93-4502-aff4-0f5c61244eab", "Bruce Task");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("TheBook", Arrays.asList(prototype(entry("Title", "55"), entry("Price", new BigDecimal(5)), entry("Quantity", new BigDecimal(5))),
                prototype(entry("Title", "510"), entry("Price", new BigDecimal(5)), entry("Quantity", new BigDecimal(10))),
                prototype(entry("Title", "810"), entry("Price", new BigDecimal(8)), entry("Quantity", new BigDecimal(10))),
                prototype(entry("Title", "85"), entry("Price", new BigDecimal(8)), entry("Quantity", new BigDecimal(5))),
                prototype(entry("Title", "66"), entry("Price", new BigDecimal(6)), entry("Quantity", new BigDecimal(6)))));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Bruce")).isInstanceOf(Map.class);
        final Map<String, Object> bruce = (Map<String, Object>) result.get("Bruce");

        assertThat(((List) bruce.get("one"))).hasSize(2);
        assertThat(((List) bruce.get("one")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("55"))).isTrue();
        assertThat(((List) bruce.get("one")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("510"))).isTrue();

        assertThat(((List) bruce.get("two"))).hasSize(3);
        assertThat(((List) bruce.get("two")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("810"))).isTrue();
        assertThat(((List) bruce.get("two")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("85"))).isTrue();
        assertThat(((List) bruce.get("two")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("66"))).isTrue();

        assertThat(((List) bruce.get("three"))).hasSize(1);
        assertThat(((List) bruce.get("three")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("510"))).isTrue();

        assertThat(((List) bruce.get("Four"))).hasSize(2);
        assertThat(((List) bruce.get("Four")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("85"))).isTrue();
        assertThat(((List) bruce.get("Four")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("66"))).isTrue();

        assertThat(((List) bruce.get("Five"))).hasSize(2);
        assertThat(((List) bruce.get("Five")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("85"))).isTrue();
        assertThat(((List) bruce.get("Five")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("66"))).isTrue();

        assertThat(((List) bruce.get("six"))).hasSize(2);
        assertThat(((List) bruce.get("six")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("85"))).isTrue();
        assertThat(((List) bruce.get("six")).stream().anyMatch(e -> ((Map<String, Object>) e).get("Title").equals("66"))).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void modelById(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("simple-item-def.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModelById("https://github.com/kiegroup/kie-dmn/itemdef", "_simple-item-def");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void weekdayOnDateDMN12(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2648 DMN v1.2 weekday on 'date', 'date and time'
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("weekday-on-date.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_55a2dafd-ab4d-4154-bace-826d426da251", "weekday on date");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        for (int i = 0; i < 7; i++) {
            final DMNContext context = DMNFactory.newContext();
            context.set("Run Date", LocalDate.of(2018, 6, 25).plusDays(i));

            final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
            LOG.debug("{}", dmnResult);
            assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

            final DMNContext result = dmnResult.getContext();
            assertThat(result.get("Weekday")).isEqualTo(new BigDecimal(i + 1));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dmn_VsFEELInstanceofInteraction(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2665
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Instance_of.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_b5c4d644-5a15-4528-8028-86537cb1c836", "Instance of");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("input year month duration", Duration.parse("P12D"));
        context.set("input day time duration", Duration.parse("P1DT2H"));
        context.set("input date time", LocalDateTime.of(2018, 6, 28, 12, 34));
        context.set("input myType", LocalDate.of(2018, 6, 28));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decision Logic 1")).isEqualTo(Boolean.TRUE);
        assertThat(result.get("Decision Logic 2")).isEqualTo(Boolean.TRUE);
        assertThat(result.get("Decision Logic 3")).isEqualTo(Boolean.TRUE);
        assertThat(result.get("Decision Logic 4")).isEqualTo(Boolean.TRUE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void invokingAFunctionOnALiteralContext(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2732 FEEL invoking a function on a literal context
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("invokingAFunctionOnALiteralContext.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_781968dd-64dc-4231-9cd0-2ce590881f2c", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("invoking a function on a literal context")).isEqualTo(new BigDecimal(3));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void boxedInvocationMissingExpression(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2813 DMN boxed invocation missing expression NPE and Validator issue
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("DROOLS-2813-NPE-BoxedInvocationMissingExpression.dmn", this.getClass());

        assertThat(messages.stream().anyMatch(p -> p.getMessageType().equals(DMNMessageType.MISSING_EXPRESSION) && p.getSourceId().equals("_a111c4df-c5b5-4d84-81e7-3ec735b50d06"))).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void notHeuristicForFunctionInvocation(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2822 FEEL augment not() heuristic for function invocation
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Not-heuristic-for-function-invocation-drools-2822.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_82f7e67e-0a8c-492d-aa78-94851c10eee6", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Not working")).isEqualTo(Boolean.FALSE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    @Timeout(value = 30_000L, unit = TimeUnit.MILLISECONDS)
    void accessorCache(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("20180731-pr1997.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_7a39d775-bce9-45e3-aa3b-147d6f0028c7", "20180731-pr1997");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        for (int i = 0; i < 10_000; i++) {
            final DMNContext context = DMNFactory.newContext();
            context.set("a Person", new Person("John", "Doe", i));

            final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
            LOG.debug("{}", dmnResult);
            assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

            final DMNContext result = dmnResult.getContext();
            assertThat(result.get("Say hello and age")).isEqualTo("Hello John Doe, your age is: " + i);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void wrongTypeRefForDRGElement(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2917 DMN resolveTypeRef returning null in BKM causes NPE during KieContainer compilation
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("WrongTypeRefForDRGElement.dmn", this.getClass());
        DMNRuntimeUtil.formatMessages(messages);
        assertThat(messages).isNotEmpty();
        assertThat(messages.stream().anyMatch(m -> m.getMessageType().equals(DMNMessageType.TYPE_DEF_NOT_FOUND))).isTrue();
        assertThat(messages.stream().anyMatch(m -> m.getSourceId().equals("_561d31ba-a34b-4cf3-b9a4-537e21ce1013"))).isTrue();
        assertThat(messages.stream().anyMatch(m -> m.getSourceId().equals("_45fa8674-f4f0-4c06-b2fd-52bbd17d8550"))).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableInputClauseImportingItemDefinition(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2927 DMN DecisionTable inputClause importing ItemDefinition throws NPE at compilation
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("imports/Imported_Model.dmn",
                this.getClass(),
                "imports/Importing_Person_DT_with_Person.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_3d586cb1-3ed0-4bc4-a1a7-070b70ece398", "Importing Person DT with Person");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("A Person here", mapOf(entry("age", new BigDecimal(17)),
                entry("name", "John")));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("A Decision Table")).isEqualTo("NOT Allowed");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void assignNullToAllowedValues(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-3132 DMN assign null to ItemDefinition with allowedValues
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("assignNullToAllowedValues.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_700a46e0-01ed-4361-9034-4afdb2537ea4", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("an input letter", null);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        DMNRuntimeUtil.formatMessages(dmnResult.getMessages());
        assertThat(dmnResult.hasErrors()).isTrue();
        assertThat(dmnResult.getMessages().stream().anyMatch(m -> m.getSourceId().equals("_24e8b31b-9505-4f52-93af-6dd9ef39c72a"))).isTrue();
        assertThat(dmnResult.getMessages().stream().anyMatch(m -> m.getSourceId().equals("_09945fda-2b89-4148-8758-0bcb91a66e4a"))).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void assignNullToAllowedValuesExplicitingNull(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-3132 DMN assign null to ItemDefinition with allowedValues
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("assignNullToAllowedValuesExplicitingNull.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_700a46e0-01ed-4361-9034-4afdb2537ea4", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("an input letter", null);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("hardcoded letter").getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SUCCEEDED);
        assertThat(dmnResult.getDecisionResultByName("hardcoded letter").getResult()).isNull();
        assertThat(dmnResult.getDecisionResultByName("decision over the input letter").getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SUCCEEDED);
        assertThat(dmnResult.getDecisionResultByName("decision over the input letter").getResult()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void getEntries(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-3308 DMN implement missing functions only described in chapter "10.3.2.6 Context"
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("getentriesgetvalue.dmn", this.getClass());
        final DMNModel dmnModel = getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_0fad1a80-0642-4278-ac3d-47668c4f689a", "Drawing 1");

        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("using get entries").getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SUCCEEDED);
        assertThat(dmnResult.getDecisionResultByName("using get entries").getResult()).asList().containsExactly("value2");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void getValue(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-3308 DMN implement missing functions only described in chapter "10.3.2.6 Context"
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("getentriesgetvalue.dmn", this.getClass());
        final DMNModel dmnModel = getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_0fad1a80-0642-4278-ac3d-47668c4f689a", "Drawing 1");

        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("using get value").getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SUCCEEDED);
        assertThat(dmnResult.getDecisionResultByName("using get value").getResult()).isEqualTo("value2");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dTinputExprCollectionWithAllowedValuesA(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DROOLS-4379.dmn", this.getClass());
        testDTinputExprCollectionWithAllowedValues(runtime);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dTinputExprCollectionWithAllowedValuesB(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DROOLS-4379b.dmn", this.getClass());
        testDTinputExprCollectionWithAllowedValues(runtime);
    }

    private void testDTinputExprCollectionWithAllowedValues(final DMNRuntime runtime) {
        // DROOLS-4379 DMN decision table input expr collection with allowedValues
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_95436b7a-7268-4713-bf84-58bff10407b4", "Dessin 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("test", Arrays.asList("r2", "r1"));
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("D4")).isEqualTo("Contains r1");
        assertThat((List<?>) result.get("D5")).asList().contains("r1", "r2");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void evaluateByNameWithEmptyParam(VariantTestConf conf) {
        testConfig = conf;
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("simple-item-def.dmn", this.getClass());
            final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn/itemdef", "simple-item" +
                    "-def");
            assertThat(dmnModel).isNotNull();
            assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

            final DMNContext context = DMNFactory.newContext();
            context.set("Monthly Salary", 1000);

            String[] decisionNames = new String[]{};
            runtime.evaluateByName(dmnModel, context, decisionNames);
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void evaluateByIdWithEmptyParam(VariantTestConf conf) {
        testConfig = conf;
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("simple-item-def.dmn", this.getClass());
            final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn/itemdef", "simple-item" +
                    "-def");
            assertThat(dmnModel).isNotNull();
            assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

            final DMNContext context = DMNFactory.newContext();
            context.set("Monthly Salary", 1000);

            String[] decisionIds = new String[]{};
            runtime.evaluateById(dmnModel, context, decisionIds);
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void uniqueMissingMatchDefaultEmpty(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("uniqueNoMatch.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://activiti.org/schema/1.0/dmn", "decisionmulti");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        checkUniqueMissingMatchDefaultEmpty(runtime, dmnModel, 11, true);
        checkUniqueMissingMatchDefaultEmpty(runtime, dmnModel, 12, null);
    }

    private void checkUniqueMissingMatchDefaultEmpty(final DMNRuntime runtime, final DMNModel dmnModel, int input, Boolean output) {
        final DMNContext context = DMNFactory.newContext();
        context.set("inputInteger", input);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Decision_decisionboolean").getResult()).isEqualTo(output);
    }
}

