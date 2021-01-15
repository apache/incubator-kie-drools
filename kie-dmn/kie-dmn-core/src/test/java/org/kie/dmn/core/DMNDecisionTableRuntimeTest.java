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

package org.kie.dmn.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class DMNDecisionTableRuntimeTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionTableRuntimeTest.class);

    public DMNDecisionTableRuntimeTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testDecisionTableWithCalculatedResult() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "calculation1.dmn", this.getClass() );
        checkDecisionTableWithCalculatedResult(runtime);
    }

    private void checkDecisionTableWithCalculatedResult(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_77ae284e-ce52-4579-a50f-f3cc584d7f4b", "Calculation1" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "MonthlyDeptPmt", BigDecimal.valueOf( 200 ) );
        context.set( "MonthlyPmt", BigDecimal.valueOf( 100 ) );
        context.set( "MonthlyIncome", BigDecimal.valueOf( 600 ) );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat( dmnResult.hasErrors(), is( false ) );

        final DMNContext result = dmnResult.getContext();
        assertThat( ((BigDecimal) result.get( "Logique de décision 1" )).setScale( 1, RoundingMode.CEILING ), is( BigDecimal.valueOf( 0.5 ) ) );
    }
    
    @Test(timeout = 30_000L)
    public void testDecisionTableWithCalculatedResult_parallel() throws Throwable {
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

    @Test
    public void testDecisionTableMultipleResults() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "car_damage_responsibility.dmn", this.getClass() );
        final DMNRuntimeEventListener listener = Mockito.mock( DMNRuntimeEventListener.class );
        runtime.addListener( listener );

        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_820611e9-c21c-47cd-8e52-5cba2be9f9cc", "Car Damage Responsibility" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Membership Level", "Silver" );
        context.set( "Damage Types", "Body" );
        context.set( "Responsible", "Driver" );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat( dmnResult.hasErrors(), is( false ) );

        final DMNContext result = dmnResult.getContext();
        assertThat( (Map<String, Object>) result.get( "Car Damage Responsibility" ), hasEntry( is( "EU Rent" ), is( BigDecimal.valueOf( 40 ) ) ) );
        assertThat( (Map<String, Object>) result.get( "Car Damage Responsibility" ), hasEntry( is( "Renter" ), is( BigDecimal.valueOf( 60 ) ) ) );
        assertThat( result.get( "Payment method" ), is( "Check" ) );

        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass( AfterEvaluateDecisionTableEvent.class );
        verify( listener, times( 2 ) ).afterEvaluateDecisionTable( captor.capture() );

        final AfterEvaluateDecisionTableEvent first = captor.getAllValues().get( 0 );
        assertThat( first.getMatches(), is( Collections.singletonList( 5 ) ) );
        assertThat( first.getSelected(), is( Collections.singletonList( 5 ) ) );

        final AfterEvaluateDecisionTableEvent second = captor.getAllValues().get( 1 );
        assertThat( second.getMatches(), is( Collections.singletonList( 3 ) ) );
        assertThat( second.getSelected(), is( Collections.singletonList( 3 ) ) );
    }

    @Test
    public void testSimpleDecisionTableMultipleOutputWrongOutputType() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0004-simpletable-P-multiple-outputs-wrong-output.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0004-simpletable-P-multiple-outputs-wrong-output" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Age", BigDecimal.valueOf( 18 ) );
        context.set( "RiskCategory", "Medium" );
        context.set( "isAffordable", true );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat( dmnResult.hasErrors(), is( true ) );
        assertThat( dmnResult.getMessages().stream().filter(
                message -> message.getFeelEvent().getSourceException() instanceof NullPointerException ).count(), is( 0L ) );
    }

    @Test
    public void testDecisionTableInvalidInputErrorMessage() {
        final DMNContext context = DMNFactory.newContext();
        context.set( "Branches dispersion", "Province" );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        testDecisionTableInvalidInput( context );
    }

    @Test
    public void testDecisionTableInvalidInputTypeErrorMessage() {
        final DMNContext context = DMNFactory.newContext();
        context.set( "Branches dispersion", 1 );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        testDecisionTableInvalidInput( context );
    }

    @Test
    public void testDecisionTableNonexistingInputErrorMessage() {
        final DMNContext context = DMNFactory.newContext();
        context.set( "Not exists", "Province" );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        testDecisionTableInvalidInput( context );
    }

    private void testDecisionTableInvalidInput(final DMNContext inputContext) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "InvalidInput.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_cdf29af2-959b-4004-8271-82a9f5a62147", "Dessin 1" );
        assertThat( dmnModel, notNullValue() );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, inputContext );
        assertThat( dmnResult.hasErrors(), is( true ) );

        final DMNContext result = dmnResult.getContext();
        assertThat( result.isDefined( "Branches distribution" ), is( false ) );
    }

    @Test
    public void testDecisionTableDefaultValue() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "decisiontable-default-value.dmn", this.getClass() );
        final DMNRuntimeEventListener listener = Mockito.mock( DMNRuntimeEventListener.class );
        runtime.addListener( listener );

        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "decisiontable-default-value" );
        assertThat( dmnModel, notNullValue() );
        assertThat( dmnModel.getMessages().toString(), dmnModel.hasErrors(), is( false ) );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 16 ) );
        context.set( "RiskCategory", "Medium" );
        context.set( "isAffordable", true );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat( dmnResult.getMessages().toString(), dmnResult.hasErrors(), is( false ) );

        final DMNContext result = dmnResult.getContext();
        assertThat( result.get( "Approval Status" ), is( "Declined" ) );

        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass( AfterEvaluateDecisionTableEvent.class );
        verify( listener ).afterEvaluateDecisionTable( captor.capture() );

        assertThat( captor.getValue().getMatches(), is( empty() ) );
        assertThat( captor.getValue().getSelected(), is( empty() ) );
    }

    @Test
    public void testTwoDecisionTables() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "two_decision_tables.dmn", this.getClass() );
        final DMNRuntimeEventListener listener = Mockito.mock( DMNRuntimeEventListener.class );
        runtime.addListener( listener );

        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_bbb692e7-3d95-407a-bf39-353085bf57f0", "Invocation with two decision table as parameters" );
        assertThat( dmnModel, notNullValue() );
        assertThat( dmnModel.getMessages().toString(), dmnModel.hasErrors(), is( false ) );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Number", 50 );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat( dmnResult.getMessages().toString(), dmnResult.hasErrors(), is( false ) );

        final DMNContext result = dmnResult.getContext();
        assertThat( (Map<String, Object>) result.get( "Decision Logic 2" ), hasEntry( "the 5 analysis", "A number greater than 5" ) );
        assertThat( (Map<String, Object>) result.get( "Decision Logic 2" ), hasEntry( "the 100 analysis", "A number smaller than 100" ) );

        final ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass( AfterEvaluateDecisionTableEvent.class );
        verify( listener, times( 2 ) ).afterEvaluateDecisionTable( captor.capture() );

        assertThat( captor.getAllValues().get( 0 ).getDecisionTableName(), is( "a" ) );
        assertThat( captor.getAllValues().get( 1 ).getDecisionTableName(), is( "b" ) );
    }

    @Test
    public void testDTInputExpressionLocalXmlnsInference() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("drools1502-InputExpression.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel(
                "https://www.drools.org/kie-dmn/definitions",
                "definitions" );
        assertThat( dmnModel, notNullValue() );
        assertThat( DMNRuntimeUtil.formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );

        final DMNContext context = runtime.newContext();
        context.set( "MyInput", "a" );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        assertThat( DMNRuntimeUtil.formatMessages( dmnResult.getMessages() ), dmnResult.hasErrors(), is( false ) );
        final DMNContext result = dmnResult.getContext();
        assertThat( result.get( "MyDecision" ), is( "Decision taken" ) );
    }

    @Test
    public void testDTInContext() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_in_context.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_4acdcb25-b298-435e-abd5-efd00ed686a5", "Drawing 1" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        assertThat( dmnResult.getDecisionResults().size(), is( 1 ) );
        assertThat( dmnResult.getDecisionResultByName( "D1" ).getResult(), is( instanceOf( Map.class ) ) );

        final DMNContext result = dmnResult.getContext();
        assertThat( ((Map) result.get( "D1" )).get( "Text color" ), is( "red" ) );
    }

    @Test
    public void testDTUsingEqualsUnaryTestWithVariable1() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_using_variables.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ed1ec15b-40aa-424d-b1d0-4936df80b135", "DT Using variables" );
        assertThat( dmnModel, notNullValue() );
        assertThat( DMNRuntimeUtil.formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );

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
        assertThat( result.get( "Compare Boolean" ), is( "Same boolean" ) );
        assertThat( result.get( "Compare Number" ), is( "Equals" ) );
        assertThat( result.get( "Compare String" ), is( "Same String" ) );
    }

    @Test
    public void testDTUsingEqualsUnaryTestWithVariable2() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DT_using_variables.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ed1ec15b-40aa-424d-b1d0-4936df80b135", "DT Using variables" );
        assertThat( dmnModel, notNullValue() );
        assertThat( dmnModel.hasErrors(), is( false ) );

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
        assertThat( result.get( "Compare Boolean" ), is( "Not same boolean" ) );
        assertThat( result.get( "Compare Number" ), is( "Bigger" ) );
        assertThat( result.get( "Compare String" ), is( "Different String" ) );
    }

    @Test
    public void testEmptyOutputCell() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "DT_empty_output_cell.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_77ae284e-ce52-4579-a50f-f3cc584d7f4b", "Calculation1" );
        assertThat( dmnModel, notNullValue() );
        final DMNContext context = DMNFactory.newContext();
        context.set( "MonthlyDeptPmt", BigDecimal.valueOf( 1 ) );
        context.set( "MonthlyPmt", BigDecimal.valueOf( 1 ) );
        context.set( "MonthlyIncome", BigDecimal.valueOf( 1 ) );
        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        LOG.debug("{}", dmnResult);
        assertThat( dmnResult.hasErrors(), is( false ) );
        assertNull( dmnResult.getContext().get("Logique de décision 1") );
    }

    @Test
    public void testNullRelation() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("nullrelation.dmn", getClass());
        final DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_946a2145-89ae-4197-88b4-40e6f88c8101", "Null in relations");
        assertThat(model, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(model.getMessages()), model.hasErrors(), is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("Value", "a3");
        final DMNResult result = runtime.evaluateByName(model, context, "Mapping");
        assertThat(DMNRuntimeUtil.formatMessages(result.getMessages()), result.hasErrors(), is(false));
    }

    @Test
    public void testDecisionTableOutputDMNTypeCollection() {
        // DROOLS-2359
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionTableOutputDMNTypeCollection.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a decision"), is(Arrays.asList("abc", "xyz")));
    }

    @Test
    public void testDecisionTableOutputDMNTypeCollection_NOtypecheck() {
        // DROOLS-2359
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("DecisionTableOutputDMNTypeCollection.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "Drawing 1");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a decision"), is(Arrays.asList("abc", "xyz")));
    }

    @Test
    public void testDecisionTableOutputDMNTypeCollectionWithLOV() {
        // DROOLS-2359
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionTableOutputDMNTypeCollectionWithLOV.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "List of Words in DT");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a decision"), is(Arrays.asList("abc", "a")));
    }

    @Test
    public void testDecisionTableOutputDMNTypeCollectionWithLOV_NOtypecheck() {
        // DROOLS-2359
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("DecisionTableOutputDMNTypeCollectionWithLOV.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_ae5d2033-c6d0-411f-a394-da33a70e5638", "List of Words in DT");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("selector", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("a decision"), is(Arrays.asList("abc", "a")));
    }

    @Test
    public void testDecisionTablesQuestionMarkVariableCorrectEvaluation() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmark.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkAsString() {
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

    @Test
    public void testDecisionTablesQuestionMarkVariableVsQuestionMarkString() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkVsQmarkString.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkOnly() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkonly.dmn",
                                               null,
                                               DMNDecisionResult.DecisionEvaluationStatus.FAILED);
    }

    @Test
    public void testDecisionTablesQuestionMarkMultivalue() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkMultivalue.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkMultivalueWithBrackets() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkMultivalueWithBrackets.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithNot() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithNot.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithContext() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithContext.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithQuantifiedExpression() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithQuantifiedExpr.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithForExpression() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithForExpr.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithFuncDef() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithFuncDef.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkInstanceOf() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkInstanceOf.dmn",
                                               "NOT OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkInWithUnaryTests() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkInWithUnaryTest.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithRange() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithRange.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkWithAnd() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkWithAnd.dmn",
                                               "OK",
                                               DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED);
    }

    @Test
    public void testDecisionTablesQuestionMarkInNonBooleanFunction() {
        testDecisionTablesQuestionMarkVariable("questionmarkunarytest/qmarkInNonBooleanFunction.dmn",
                                               null,
                                               DMNDecisionResult.DecisionEvaluationStatus.FAILED);
    }

    private void testDecisionTablesQuestionMarkVariable(final String dmnResourcePath, final String expectedResult,
                                                        final DMNDecisionResult.DecisionEvaluationStatus expectedStatus) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(dmnResourcePath, this.getClass() );
        final DMNModel model = runtime.getModel("http://www.trisotech.com/definitions/_88a36f38-4494-4fd8-aaea-f7a6b4c91825", "Enabling question marks" );
        assertThat( model, notNullValue() );
        assertThat( DMNRuntimeUtil.formatMessages( model.getMessages() ), model.hasErrors(), is( false ) );
        final DMNContext context = DMNFactory.newContext();
        final DMNDecisionResult result = runtime.evaluateByName(model, context, "Result").getDecisionResultByName("Result");

        assertEquals(expectedStatus, result.getEvaluationStatus());
        assertFalse(result.hasErrors());
        assertEquals(expectedResult, result.getResult());
    }

    @Test
    public void testDecisionTableOutputMessageRowIndex() {
        // DROOLS-5606 DMN wrong rule index in message when not conforming
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionTableOutputMessageRowIndex.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_D7A4B999-3178-4929-834F-8979E3C5000F", "DecisionTableOutputMessageRowIndex");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("InputData-1", "asd");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        LOG.debug("{}", dmnResult.getMessages());
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(true));
        DMNMessage msg0 = dmnResult.getMessages().get(0);
        Assertions.assertThat(msg0.getText()).containsIgnoringCase("Invalid result value on rule #1, output #1."); // there is only 1 row, 1 output column
    }

    @Test
    public void testDTand() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DTand.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_6697FFDC-B3D9-4B0B-BC07-AE5E5AC96CB4", "DTand");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("in1", null);
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("out1").getResult(), is("PASS"));
    }
}
