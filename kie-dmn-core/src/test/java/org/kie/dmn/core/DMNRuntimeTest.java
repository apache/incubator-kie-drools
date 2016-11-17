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

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.core.api.*;
import org.kie.dmn.core.ast.InputDataNode;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DMNRuntimeTest {

    protected DMNRuntime createRuntime( String resourceName ) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId( "org.kie", "dmn-test", "1.0" ),
                ks.getResources().newClassPathResource( resourceName, DMNRuntimeTest.class ) );

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime( DMNRuntime.class );
        assertNotNull( runtime );
        return runtime;
    }

    @Test
    public void testSimpleEvaluateAll() {
        DMNRuntime runtime = createRuntime( "0001-input-data-string.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Full Name", "John Doe" );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getResult(), is( "Hello John Doe" ) );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );
    }

    @Test
    public void testSimpleEvaluateDecisionByName() {
        DMNRuntime runtime = createRuntime( "0001-input-data-string.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Full Name", "John Doe" );

        DMNResult dmnResult = runtime.evaluateDecisionByName( dmnModel, "Greeting Message", context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getResult(), is( "Hello John Doe" ) );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );
    }

    @Test
    public void testSimpleEvaluateDecisionById() {
        DMNRuntime runtime = createRuntime( "0001-input-data-string.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Full Name", "John Doe" );

        DMNResult dmnResult = runtime.evaluateDecisionById( dmnModel, "d_GreetingMessage", context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultById( "d_GreetingMessage" ).getResult(), is( "Hello John Doe" ) );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );
    }

    @Test
    public void testGetRequiredInputsByName() {
        DMNRuntime runtime = createRuntime( "0001-input-data-string.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        Set<InputDataNode> inputs = dmnModel.getRequiredInputsForDecisionName( "Greeting Message" );

        assertThat( inputs.size(), is(1) );
        assertThat( inputs.iterator().next().getName(), is("Full Name") );
    }

    @Test
    public void testGetRequiredInputsById() {
        DMNRuntime runtime = createRuntime( "0001-input-data-string.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        Set<InputDataNode> inputs = dmnModel.getRequiredInputsForDecisionId( "d_GreetingMessage" );

        assertThat( inputs.size(), is(1) );
        assertThat( inputs.iterator().next().getName(), is("Full Name") );
    }

    @Test
    public void testSimpleInputDataNumber() {
        DMNRuntime runtime = createRuntime( "0002-input-data-number.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0002-input-data-number" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Monthly Salary", new BigDecimal( 1000 ) );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Yearly Salary" ), is( new BigDecimal( 12000 ) ) );
    }

    @Test
    public void testSimpleAllowedValuesString() {
        DMNRuntime runtime = createRuntime( "0003-input-data-string-allowed-values.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0003-input-data-string-allowed-values" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Employment Status", "SELF-EMPLOYED" );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Employment Status Statement" ), is( "You are SELF-EMPLOYED" ) );
    }

    @Test
    public void testCompositeItemDefinition() {
        DMNRuntime runtime = createRuntime( "0008-LX-arithmetic.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0008-LX-arithmetic" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        Map loan = new HashMap(  );
        loan.put( "principal", 600000 );
        loan.put( "rate", 0.0375 );
        loan.put( "termMonths", 360 );
        context.set( "loan", loan );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "payment" ), is( new BigDecimal( "2778.693549432766720839844710324306" ) ) );
    }

    @Test
    public void testSimpleDTUnique() {
        DMNRuntime runtime = createRuntime( "0004-simpletable-U.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0004-simpletable-U" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Age", new BigDecimal( 18 ) );
        context.set( "RiskCategory", "Medium" );
        context.set( "isAffordable", true );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Approval Status" ), is( "Approved" ) );
    }
    
    @Ignore("Should have at least one message for failing the (allowed) input values - ASD is not a valid input value for RiskCategory")
    @Test
    public void testSimpleDTUniqueSatisfies() {
        DMNRuntime runtime = createRuntime( "0004-simpletable-U.dmn" );
        DMNModel dmnModel = runtime.getModel( "https://github.com/droolsjbpm/kie-dmn", "0004-simpletable-U" );
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
    public void testTrisotechNamespace() {
        DMNRuntime runtime = createRuntime( "trisotech_namespace.dmn" );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_b8feec86-dadf-4051-9feb-8e6093bbb530", "Solution 3" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "IsDoubleHulled", true );
        context.set("Residual Cargo Size", new BigDecimal( 0.1 ) );
        context.set("Ship Size", new BigDecimal( 50 ) );
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNContext result = dmnResult.getContext();
        assertThat( result.get("Ship can enter a Dutch port"), is( true ) );
    }

    @Test
    public void testEmptyDecision1() {
        DMNRuntime runtime = createRuntime( "empty_decision.dmn" );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_ba9fc4b1-5ced-4d00-9b61-290de4bf3213", "Solution 3" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        Map shipInfo = new HashMap(  );
        shipInfo.put( "Size", BigDecimal.valueOf( 70 ) );
        shipInfo.put( "Is Double Hulled", Boolean.FALSE );
        shipInfo.put( "Residual Cargo Size", BigDecimal.valueOf( 0.1 ) );
        context.set( "Ship Info", shipInfo );

        // Test that even if one decision is empty or missing input data,
        // the other decisions in the model are still evaluated
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        DMNContext result = dmnResult.getContext();
        assertThat( dmnResult.hasErrors(), is( true ) );
        assertThat( result.get("Ship Can Enter v2"), is( true ) );
    }

    @Test
    public void testEmptyDecision2() {
        DMNRuntime runtime = createRuntime( "empty_decision.dmn" );
        DMNModel dmnModel = runtime.getModel( "http://www.trisotech.com/dmn/definitions/_ba9fc4b1-5ced-4d00-9b61-290de4bf3213", "Solution 3" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        Map shipInfo = new HashMap(  );
        shipInfo.put( "Size", BigDecimal.valueOf( 70 ) );
        shipInfo.put( "Is Double Hulled", Boolean.FALSE );
        shipInfo.put( "Residual Cargo Size", BigDecimal.valueOf( 0.1 ) );
        context.set( "Ship Info", shipInfo );
        context.set( "Ship Size", BigDecimal.valueOf( 70 ) );
        context.set( "IsDoubleHulled", Boolean.FALSE );
        context.set( "Residual Cargo Size", BigDecimal.valueOf( 0.1 ) );

        // check that if all the input data is available, but the
        // decision expression is empty, the model returns a warning
        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        List<DMNMessage> messages = dmnResult.getMessages( DMNMessage.Severity.WARN );
        assertThat( messages.size(), is( 1 ) );
        assertThat( messages.get( 0 ).getSeverity(), is( DMNMessage.Severity.WARN ) );
        assertThat( messages.get( 0 ).getSourceId(), is( "_42806504-8ed5-488f-b274-de98c1bc67b9" ) );

        DMNContext result = dmnResult.getContext();
        assertThat( result.get("Ship Can Enter v2"), is( true ) );
    }
}
