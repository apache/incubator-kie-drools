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
package org.kie.dmn.core;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNResultImplFactory;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.kie.dmn.model.api.BuiltinAggregator;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class DMNDecisionTableRuntimeTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionTableRuntimeTest.class);

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableWithCalculatedResult(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "calculation1.dmn", this.getClass() );
        checkDecisionTableWithCalculatedResult(runtime);
    }

    private void checkDecisionTableWithCalculatedResult(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_77ae284e-ce52-4579-a50f-f3cc584d7f4b", "Calculation1" );
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set( "MonthlyDeptPmt", BigDecimal.valueOf( 200 ) );
        context.set( "MonthlyPmt", BigDecimal.valueOf( 100 ) );
        context.set( "MonthlyIncome", BigDecimal.valueOf( 600 ) );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat( ((BigDecimal) result.get( "Logique de décision 1" )).setScale( 1, RoundingMode.CEILING )).isEqualTo(BigDecimal.valueOf( 0.5 ) );
    }

    @ParameterizedTest
    @MethodSource("params")
    @Timeout(value = 30_000L, unit = TimeUnit.MILLISECONDS)
    void decisionTableWithCalculatedResultParallel(boolean useExecModelCompiler) throws Throwable {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "calculation1.dmn", this.getClass() );
        final Runnable task = () -> checkDecisionTableWithCalculatedResult(runtime);
        final List<Throwable> problems = Collections.synchronizedList(new ArrayList<>());
        final List<CompletableFuture<Void>> tasks = new ArrayList<>();
        
        for ( int i=0 ; i<10_000; i++) {
            final CompletableFuture<Void> newtask = CompletableFuture.runAsync(task).exceptionally(t -> {problems.add(t); return null;});
            tasks.add( newtask );
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture<?>[]{})).get();

        if (problems.size() > 0) {
            throw problems.get(0);
        }
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableMultipleResults(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "car_damage_responsibility.dmn", this.getClass() );
        final DMNRuntimeEventListener listener = Mockito.mock( DMNRuntimeEventListener.class );
        runtime.addListener( listener );

        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_820611e9-c21c-47cd-8e52-5cba2be9f9cc", "Car Damage Responsibility" );
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set( "Membership Level", "Silver" );
        context.set( "Damage Types", "Body" );
        context.set( "Responsible", "Driver" );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat( (Map<String, Object>) result.get( "Car Damage Responsibility" )).containsEntry("EU Rent", BigDecimal.valueOf(40));
        assertThat( (Map<String, Object>) result.get( "Car Damage Responsibility" )).containsEntry("Renter", BigDecimal.valueOf(60));
        assertThat( result.get( "Payment method" )).isEqualTo("Check" );

        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass( AfterEvaluateDecisionTableEvent.class );
        verify( listener, times( 2 ) ).afterEvaluateDecisionTable( captor.capture() );

        final AfterEvaluateDecisionTableEvent first = captor.getAllValues().get( 0 );
        assertThat( first.getMatches()).containsExactly(5);
        assertThat( first.getSelected()).containsExactly(5);

        final AfterEvaluateDecisionTableEvent second = captor.getAllValues().get( 1 );
        assertThat( second.getMatches()).containsExactly(3);
        assertThat( second.getSelected()).containsExactly(3);
    }

    @ParameterizedTest
    @MethodSource("params")
    void simpleDecisionTableMultipleOutputWrongOutputType(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0004-simpletable-P-multiple-outputs-wrong-output.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0004-simpletable-P-multiple-outputs-wrong-output" );
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set( "Age", BigDecimal.valueOf( 18 ) );
        context.set( "RiskCategory", "Medium" );
        context.set( "isAffordable", true );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat( dmnResult.hasErrors()).isTrue();
        assertThat( dmnResult.getMessages().stream().filter(
                message -> message.getFeelEvent().getSourceException() instanceof NullPointerException ).count()).isEqualTo(0L );
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableInvalidInputErrorMessage(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNContext context = DMNFactory.newContext();
        context.set( "Branches dispersion", "Province" );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        testDecisionTableInvalidInput( context );
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableInvalidInputTypeErrorMessage(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNContext context = DMNFactory.newContext();
        context.set( "Branches dispersion", 1 );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        testDecisionTableInvalidInput( context );
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableNonexistingInputErrorMessage(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNContext context = DMNFactory.newContext();
        context.set( "Not exists", "Province" );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        testDecisionTableInvalidInput( context );
    }

    private void testDecisionTableInvalidInput(final DMNContext inputContext) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "InvalidInput.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_cdf29af2-959b-4004-8271-82a9f5a62147", "Dessin 1" );
        assertThat(dmnModel).isNotNull();

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, inputContext );
        assertThat( dmnResult.hasErrors()).isTrue();

        final DMNContext result = dmnResult.getContext();
        assertThat( result.isDefined( "Branches distribution" )).isEqualTo(Boolean.FALSE);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableDefaultValue(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "decisiontable-default-value.dmn", this.getClass() );
        final DMNRuntimeEventListener listener = Mockito.mock( DMNRuntimeEventListener.class );
        runtime.addListener( listener );

        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "decisiontable-default-value" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 16 ) );
        context.set( "RiskCategory", "Medium" );
        context.set( "isAffordable", true );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat( result.get( "Approval Status" )).isEqualTo("Declined" );

        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass( AfterEvaluateDecisionTableEvent.class );
        verify( listener ).afterEvaluateDecisionTable( captor.capture() );

        assertThat( captor.getValue().getMatches()).isEmpty();
        assertThat( captor.getValue().getSelected()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("params")
    void twoDecisionTables(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "two_decision_tables.dmn", this.getClass() );
        final DMNRuntimeEventListener listener = Mockito.mock( DMNRuntimeEventListener.class );
        runtime.addListener( listener );

        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_bbb692e7-3d95-407a-bf39-353085bf57f0", "Invocation with two decision table as parameters" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set( "Number", 50 );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat( (Map<String, Object>) result.get( "Decision Logic 2" )).containsEntry("the 5 analysis", "A number greater than 5");
        assertThat( (Map<String, Object>) result.get( "Decision Logic 2" )).containsEntry("the 100 analysis", "A number smaller than 100");

        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass( AfterEvaluateDecisionTableEvent.class );
        verify( listener, times( 2 ) ).afterEvaluateDecisionTable( captor.capture() );

        assertThat( captor.getAllValues().get( 0 ).getDecisionTableName()).isEqualTo("a" );
        assertThat( captor.getAllValues().get( 1 ).getDecisionTableName()).isEqualTo("b" );
    }

    @ParameterizedTest
    @MethodSource("params")
    void dTInputExpressionLocalXmlnsInference(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("drools1502-InputExpression.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel(
                "https://www.drools.org/kie-dmn/definitions",
                "definitions" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        context.set( "MyInput", "a" );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        final DMNContext result = dmnResult.getContext();
        assertThat( result.get( "MyDecision" )).isEqualTo("Decision taken" );
    }

    @ParameterizedTest
    @MethodSource("params")
    void dTInContext(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_in_context.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_4acdcb25-b298-435e-abd5-efd00ed686a5", "Drawing 1" );
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        assertThat( dmnResult.getDecisionResults()).hasSize(1);
        assertThat( dmnResult.getDecisionResultByName( "D1" ).getResult()).isInstanceOf(Map.class);

        final DMNContext result = dmnResult.getContext();
        assertThat( ((Map) result.get( "D1" )).get( "Text color" )).isEqualTo("red" );
    }

    @ParameterizedTest
    @MethodSource("params")
    void dTUsingEqualsUnaryTestWithVariable1(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_using_variables.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ed1ec15b-40aa-424d-b1d0-4936df80b135", "DT Using variables" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final Map<String, Object> complex = new HashMap<>();
        complex.put( "aBoolean", true );
        complex.put( "aNumber", 10 );
        complex.put( "aString", "bar" );
        final DMNContext context = DMNFactory.newContext();
        context.set( "Complex", complex );
        context.set( "Another boolean", true );
        context.set( "Another String", "bar" );
        context.set( "Another number", 10 );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        final DMNContext result = dmnResult.getContext();
        assertThat( result.get( "Compare Boolean" )).isEqualTo("Same boolean" );
        assertThat( result.get( "Compare Number" )).isEqualTo("Equals" );
        assertThat( result.get( "Compare String" )).isEqualTo("Same String" );
    }

    @ParameterizedTest
    @MethodSource("params")
    void dTUsingEqualsUnaryTestWithVariable2(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_using_variables.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ed1ec15b-40aa-424d-b1d0-4936df80b135", "DT Using variables" );
        assertThat(dmnModel).isNotNull();
        assertThat( dmnModel.hasErrors()).isFalse();

        final Map<String, Object> complex = new HashMap<>();
        complex.put( "aBoolean", true );
        complex.put( "aNumber", 10 );
        complex.put( "aString", "bar" );
        final DMNContext context = DMNFactory.newContext();
        context.set( "Complex", complex );
        context.set( "Another boolean", false );
        context.set( "Another String", "foo" );
        context.set( "Another number", 20 );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        final DMNContext result = dmnResult.getContext();
        assertThat( result.get( "Compare Boolean" )).isEqualTo("Not same boolean" );
        assertThat( result.get( "Compare Number" )).isEqualTo("Bigger" );
        assertThat( result.get( "Compare String" )).isEqualTo("Different String" );
    }

    @ParameterizedTest
    @MethodSource("params")
    void emptyOutputCell(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "DT_empty_output_cell.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_77ae284e-ce52-4579-a50f-f3cc584d7f4b", "Calculation1" );
        assertThat(dmnModel).isNotNull();
        final DMNContext context = DMNFactory.newContext();
        context.set( "MonthlyDeptPmt", BigDecimal.valueOf( 1 ) );
        context.set( "MonthlyPmt", BigDecimal.valueOf( 1 ) );
        context.set( "MonthlyIncome", BigDecimal.valueOf( 1 ) );
        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isFalse();
        assertThat(dmnResult.getContext().get("Logique de décision 1")).isNull();
    }

    @ParameterizedTest
    @MethodSource("params")
    void nullRelation(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("nullrelation.dmn", getClass());
        final DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_946a2145-89ae-4197-88b4-40e6f88c8101", "Null in relations");
        assertThat(model).isNotNull();
        assertThat(model.hasErrors()).as(DMNRuntimeUtil.formatMessages(model.getMessages())).isFalse();
        final DMNContext context = DMNFactory.newContext();
        context.set("Value", "a3");
        final DMNResult result = runtime.evaluateByName(model, context, "Mapping");
        assertThat(result.hasErrors()).as(DMNRuntimeUtil.formatMessages(result.getMessages())).isFalse();
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableOutputDMNTypeCollection(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-2359
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionTableOutputDMNTypeCollection.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a decision")).asList().containsExactly("abc", "xyz");
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableOutputDMNTypeCollectionNOtypecheck(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-2359
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("DecisionTableOutputDMNTypeCollection.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a decision")).asList().containsExactly("abc", "xyz");
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableOutputDMNTypeCollectionWithLOV(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-2359
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionTableOutputDMNTypeCollectionWithLOV.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "List of Words in DT");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a decision")).asList().containsExactly("abc", "a");
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableOutputDMNTypeCollectionWithLOVNOtypecheck(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-2359
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("DecisionTableOutputDMNTypeCollectionWithLOV.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "List of Words in DT");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a decision")).asList().containsExactly("abc", "a");
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkVariableCorrectEvaluation(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmark.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkAsString(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkAsString.dmn",
                                               "NOT OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkAsString2.dmn",
                                               "NOT OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkAsString3.dmn",
                                               "NOT OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkVariableVsQuestionMarkString(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkVsQmarkString.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkOnly(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkonly.dmn",
                                               null,
                                               DMNDecisionResult.DecisionEvaluationStatus.FAILED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkMultivalue(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkMultivalue.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkMultivalueWithBrackets(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkMultivalueWithBrackets.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkWithNot(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithNot.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkWithContext(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithContext.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkWithQuantifiedExpression(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithQuantifiedExpr.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkWithForExpression(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithForExpr.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkWithFuncDef(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithFuncDef.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkInstanceOf(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkInstanceOf.dmn",
                                               "NOT OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkInWithUnaryTests(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkInWithUnaryTest.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkWithRange(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithRange.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkWithAnd(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithAnd.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTablesQuestionMarkInNonBooleanFunction(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkInNonBooleanFunction.dmn",
                                               null,
                                               DMNDecisionResult.DecisionEvaluationStatus.FAILED);
    }

    private void testDecisionTablesQuestionMarkVariable(final String dmnResourcePath, final String expectedResult,
                                                        final DMNDecisionResult.DecisionEvaluationStatus expectedStatus) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(dmnResourcePath, this.getClass() );
        final DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_88a36f38-4494-4fd8-aaea-f7a6b4c91825", "Enabling question marks" );
        assertThat(model).isNotNull();
        assertThat(model.hasErrors()).as(DMNRuntimeUtil.formatMessages(model.getMessages())).isFalse();
        final DMNContext context = DMNFactory.newContext();
        final DMNDecisionResult result = runtime.evaluateByName(model, context, "Result").getDecisionResultByName("Result");

        assertThat(result.getEvaluationStatus()).isEqualTo(expectedStatus);
        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getResult()).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("params")
    void decisionTableOutputMessageRowIndex(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // DROOLS-5606 DMN wrong rule index in message when not conforming
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionTableOutputMessageRowIndex.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_D7A4B999-3178-4929-834F-8979E3C5000F", "DecisionTableOutputMessageRowIndex");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("InputData-1", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        LOG.debug("{}", dmnResult.getMessages());
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();
        DMNMessage msg0 = dmnResult.getMessages().get(0);
        assertThat(msg0.getText()).containsIgnoringCase("Invalid result value on rule #1, output #1."); // there is only 1 row, 1 output column
    }

    @ParameterizedTest
    @MethodSource("params")
    void dTand(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DTand.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_6697FFDC-B3D9-4B0B-BC07-AE5E5AC96CB4", "DTand");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("in1", null);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("out1").getResult()).isEqualTo("PASS");
    }

    @ParameterizedTest
    @MethodSource("params")
    void qMarkAndNullShouldNotThrowNPEs(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("questionmarkunarytest/qmarkMatches.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_D1CF8332-8443-41C8-B214-D282B82C7632", "qmarkMatches");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        List<NullPointerException> feelNPEs = new ArrayList<>();
        DMNRuntimeImpl runtimeImpl = (DMNRuntimeImpl) runtime;
        runtimeImpl.setDMNResultImplFactory(new DMNResultImplFactory() {
            @Override
            public DMNResultImpl newDMNResultImpl(DMNModel model) {
                return new DMNResultImpl(model) {
                    @Override
                    public DMNMessage addMessage(Severity severity, String message, DMNMessageType messageType, DMNModelInstrumentedBase source, FEELEvent feelEvent) {
                        if (feelEvent.getSourceException() instanceof NullPointerException) {
                            feelNPEs.add((NullPointerException) feelEvent.getSourceException());
                        }
                        return super.addMessage(severity, message, messageType, source, feelEvent);
                    }
                };
            }
        });

        final DMNContext context = DMNFactory.newContext();
        context.set("MyInput", null);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();

        assertThat(feelNPEs).as("while it's okay to have error-ed on evaluation of FEEL, there should not be any sort of NPEs when reporting the human friendly message").isEmpty();
    }

    @ParameterizedTest
    @MethodSource("params")
    void dTCollectOperatorsMultipleOutputs(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        checkDTCollectOperatorsMultipleOutputs(BuiltinAggregator.SUM, 100, 111, 18);
        checkDTCollectOperatorsMultipleOutputs(BuiltinAggregator.COUNT, 10, 2, 2);
        checkDTCollectOperatorsMultipleOutputs(BuiltinAggregator.MIN, 100, 1, 3);
        checkDTCollectOperatorsMultipleOutputs(BuiltinAggregator.MAX, 100, 100, 9);
    }
    
    private void checkDTCollectOperatorsMultipleOutputs(BuiltinAggregator aggregator, int level, int a, int b) {
        // DROOLS-6590 DMN composite output on DT Collect with operators - this is beyond the spec.
        final KieServices ks = KieServices.Factory.get();
        DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();
        final Definitions definitions = marshaller
                .unmarshal(new InputStreamReader(this.getClass().getResourceAsStream("multipleOutputsCollectDT.dmn")));
        
        DRGElement drgElement1 = definitions.getDrgElement().get(1);
        assertThat(drgElement1).describedAs("xml of the test changed, unable to locate Decision Node").isInstanceOf(Decision.class);
        ((DecisionTable)((Decision)drgElement1).getExpression()).setAggregation(aggregator);
        
        final String dmnXml = marshaller.marshal(definitions);
        
        final ReleaseId kjarReleaseId = ks.newReleaseId("org.kie.dmn.core", "DMNDecisionTableRuntimeTest", UUID.randomUUID().toString());
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/multipleOutputsCollectDT.dmn", dmnXml);
        kfs.generateAndWritePomXML(kjarReleaseId);
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertThat(kieBuilder.getResults().getMessages()).as(kieBuilder.getResults().getMessages().toString()).isEmpty();

        final KieContainer container = ks.newKieContainer(kjarReleaseId);
        final DMNRuntime runtime = KieRuntimeFactory.of(container.getKieBase()).get(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_943A3581-5FD1-4BCF-9A52-AC7242CC451C", "multipleOutputsCollectDT");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("level", level);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        Object decision1 = dmnResult.getDecisionResultByName("Decision-1").getResult();
        assertThat(decision1).hasFieldOrPropertyWithValue("a", new BigDecimal(a));
        assertThat(decision1).hasFieldOrPropertyWithValue("b", new BigDecimal(b));
    }
}
