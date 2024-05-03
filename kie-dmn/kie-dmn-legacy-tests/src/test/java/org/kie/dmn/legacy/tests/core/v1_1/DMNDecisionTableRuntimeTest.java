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
package org.kie.dmn.legacy.tests.core.v1_1;

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class DMNDecisionTableRuntimeTest extends BaseDMN1_1VariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionTableRuntimeTest.class);

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")    
    void decisionTableWithCalculatedResult(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    @Timeout(value = 30_000L, unit = TimeUnit.MILLISECONDS)
    void decisionTableWithCalculatedResultParallel() throws Throwable {
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableMultipleResults(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void simpleDecisionTableMultipleOutputWrongOutputType(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableInvalidInputErrorMessage(VariantTestConf conf) {
        testConfig = conf;
        final DMNContext context = DMNFactory.newContext();
        context.set( "Branches dispersion", "Province" );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        testDecisionTableInvalidInput( context );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableInvalidInputTypeErrorMessage(VariantTestConf conf) {
        testConfig = conf;
        final DMNContext context = DMNFactory.newContext();
        context.set( "Branches dispersion", 1 );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        testDecisionTableInvalidInput( context );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableNonexistingInputErrorMessage(VariantTestConf conf) {
        testConfig = conf;
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
        assertThat(result.isDefined( "Branches distribution")).isEqualTo(Boolean.FALSE);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableDefaultValue(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void twoDecisionTables(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dTInputExpressionLocalXmlnsInference(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dTInContext(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dTUsingEqualsUnaryTestWithVariable1(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void dTUsingEqualsUnaryTestWithVariable2(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void emptyOutputCell(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void nullRelation(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("nullrelation.dmn", getClass());
        final DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_946a2145-89ae-4197-88b4-40e6f88c8101", "Null in relations");
        assertThat(model).isNotNull();
        assertThat(model.hasErrors()).as(DMNRuntimeUtil.formatMessages(model.getMessages())).isFalse();
        final DMNContext context = DMNFactory.newContext();
        context.set("Value", "a3");
        final DMNResult result = runtime.evaluateByName(model, context, "Mapping");
        assertThat(result.hasErrors()).as(DMNRuntimeUtil.formatMessages(result.getMessages())).isFalse();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableOutputDMNTypeCollection(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableOutputDMNTypeCollectionNOtypecheck(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableOutputDMNTypeCollectionWithLOV(VariantTestConf conf) {
        testConfig = conf;
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void decisionTableOutputDMNTypeCollectionWithLOVNOtypecheck(VariantTestConf conf) {
        testConfig = conf;
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

}
