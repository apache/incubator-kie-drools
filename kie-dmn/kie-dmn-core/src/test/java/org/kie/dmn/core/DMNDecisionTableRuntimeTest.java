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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Map;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class DMNDecisionTableRuntimeTest {

    @Test
    public void testSimpleDecisionTableUniqueHitPolicy() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0004-simpletable-U.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0004-simpletable-U" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 18 ) );
        context.set( "RiskCategory", "Medium" );
        context.set( "isAffordable", true );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Approval Status" ), is( "Approved" ) );
    }

    @Test
    public void testSimpleDecisionTableUniqueHitPolicySatisfies() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0004-simpletable-U.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0004-simpletable-U" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 18 ) );
        context.set( "RiskCategory", "ASD" );
        context.set( "isAffordable", false );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Approval Status" ), nullValue() );
        assertTrue( dmnResult.getMessages().size() > 0 );
    }
    
    @Test
    public void testSimpleDecisionTableUniqueHitPolicyNullWarn() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0004-simpletable-U-noinputvalues.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0004-simpletable-U-noinputvalues" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 18 ) );
        context.set( "RiskCategory", "ASD" );
        context.set( "isAffordable", false );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Approval Status" ), nullValue() );
        assertTrue( dmnResult.getMessages().size() > 0 );
        assertTrue( dmnResult.getMessages().stream().anyMatch( dm -> dm.getSeverity().equals(DMNMessage.Severity.WARN)
                                                                     && dm.getFeelEvent() instanceof HitPolicyViolationEvent
                                                                     && dm.getFeelEvent().getSeverity().equals(FEELEvent.Severity.WARN) ) );
    }

    @Test
    public void testDecisionTableWithCalculatedResult() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "calculation1.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_77ae284e-ce52-4579-a50f-f3cc584d7f4b", "Calculation1" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "MonthlyDeptPmt", BigDecimal.valueOf( 200 ) );
        context.set( "MonthlyPmt", BigDecimal.valueOf( 100 ) );
        context.set( "MonthlyIncome", BigDecimal.valueOf( 600 ) );

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat( dmnResult.hasErrors(), is( false ) );

        DMNContext result = dmnResult.getContext();
        assertThat( ((BigDecimal)result.get("Logique de décision 1")).setScale( 1, RoundingMode.CEILING), is( BigDecimal.valueOf( 0.5 ) ) );
    }

    @Test
    public void testDecisionTableMultipleResults() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "car_damage_responsibility.dmn", this.getClass() );
        DMNRuntimeEventListener listener = Mockito.mock( DMNRuntimeEventListener.class );
        runtime.addListener( listener );

        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_820611e9-c21c-47cd-8e52-5cba2be9f9cc", "Car Damage Responsibility" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Membership Level", "Silver" );
        context.set( "Damage Types", "Body" );
        context.set( "Responsible", "Driver" );

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat( dmnResult.hasErrors(), is( false ) );

        DMNContext result = dmnResult.getContext();
        assertThat( (Map<String,Object>)result.get("Car Damage Responsibility"), hasEntry( is( "EU Rent" ), is( BigDecimal.valueOf( 40 )) ));
        assertThat( (Map<String,Object>)result.get("Car Damage Responsibility"), hasEntry( is( "Renter" ), is( BigDecimal.valueOf( 60 )) ));
        assertThat( result.get("Payment method"), is( "Check" ) );

        ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass(AfterEvaluateDecisionTableEvent.class);
        verify( listener, times(2) ).afterEvaluateDecisionTable( captor.capture() );

        AfterEvaluateDecisionTableEvent first = captor.getAllValues().get( 0 );
        assertThat( first.getMatches(), is( Arrays.asList( 5 ) ) );
        assertThat( first.getSelected(), is( Arrays.asList( 5 ) ) );

        AfterEvaluateDecisionTableEvent second = captor.getAllValues().get( 1 );
        assertThat( second.getMatches(), is( Arrays.asList( 3 ) ) );
        assertThat( second.getSelected(), is( Arrays.asList( 3 ) ) );
    }

    @Test
    public void testDecisionTableUniqueHitPolicy() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "BranchDistribution.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_cdf29af2-959b-4004-8271-82a9f5a62147", "Dessin 1" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Branches dispersion", "Province" );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat( dmnResult.hasErrors(), is( false ) );

        DMNContext result = dmnResult.getContext();
        System.out.println(result);
        assertThat( result.get("Branches distribution"), is( "Medium" ) );
    }

    @Test
    public void testDecisionTableCollectHitPolicy() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "Collect_Hit_Policy.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_da1a4dcb-01bf-4dee-9be8-f498bc68178c", "Collect Hit Policy" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Input", 20 );

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat( dmnResult.hasErrors(), is( false ) );

        DMNContext result = dmnResult.getContext();
        assertThat( result.get("Collect"), is( BigDecimal.valueOf( 50 ) ) );
    }

    @Test
    public void testDecisionTableInvalidInputErrorMessage() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "InvalidInput.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_cdf29af2-959b-4004-8271-82a9f5a62147", "Dessin 1" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Branches dispersion", "Province" );
        context.set( "Number of Branches", BigDecimal.valueOf( 10 ) );

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat( dmnResult.hasErrors(), is( true ) );

        DMNContext result = dmnResult.getContext();
        assertThat( result.isDefined("Branches distribution"), is( false ) );
    }

    @Test
    public void testDecisionTableDefaultValue() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "decisiontable-default-value.dmn", this.getClass() );
        DMNRuntimeEventListener listener = Mockito.mock( DMNRuntimeEventListener.class );
        runtime.addListener( listener );

        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "decisiontable-default-value" );
        assertThat( dmnModel, notNullValue() );
        assertThat( dmnModel.getMessages().toString(), dmnModel.hasErrors(), is(false) );

        DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 16 ) );
        context.set( "RiskCategory", "Medium" );
        context.set( "isAffordable", true );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat( dmnResult.getMessages().toString(), dmnResult.hasErrors(), is(false) );

        DMNContext result = dmnResult.getContext();
        assertThat( result.get( "Approval Status" ), is( "Declined" ) );

        ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass(AfterEvaluateDecisionTableEvent.class);
        verify( listener ).afterEvaluateDecisionTable( captor.capture() );

        assertThat( captor.getValue().getMatches(), is( empty() ) );
        assertThat( captor.getValue().getSelected(), is( empty() ) );
    }

    @Test
    public void testTwoDecisionTables() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "two_decision_tables.dmn", this.getClass() );
        DMNRuntimeEventListener listener = Mockito.mock( DMNRuntimeEventListener.class );
        runtime.addListener( listener );

        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/definitions/_bbb692e7-3d95-407a-bf39-353085bf57f0", "Invocation with two decision table as parameters" );
        assertThat( dmnModel, notNullValue() );
        assertThat( dmnModel.getMessages().toString(), dmnModel.hasErrors(), is(false) );

        DMNContext context = DMNFactory.newContext();
        context.set( "Number", 50 );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );
        assertThat( dmnResult.getMessages().toString(), dmnResult.hasErrors(), is(false) );

        DMNContext result = dmnResult.getContext();
        assertThat( (Map<String, Object>)result.get( "Decision Logic 2" ), hasEntry( "the 5 analysis", "A number greater than 5" ) );
        assertThat( (Map<String, Object>)result.get( "Decision Logic 2" ), hasEntry( "the 100 analysis", "A number smaller than 100" ) );

        ArgumentCaptor<AfterEvaluateDecisionTableEvent> captor = ArgumentCaptor.forClass(AfterEvaluateDecisionTableEvent.class);
        verify( listener, times(2) ).afterEvaluateDecisionTable( captor.capture() );

        assertThat( captor.getAllValues().get( 0 ).getDecisionTableName(), is( "a" ) );
        assertThat( captor.getAllValues().get( 1 ).getDecisionTableName(), is( "b" ) );
    }

}
