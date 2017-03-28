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
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.api.*;
import org.kie.dmn.core.util.DMNRuntimeUtil;

public class DMNInputRuntimeTest {

    @Test
    public void testInputStringEvaluateAll() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
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
    public void testInputStringEvaluateDecisionByName() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Full Name", "John Doe" );

        DMNResult dmnResult = runtime.evaluateDecisionByName( dmnModel, "Greeting Message", context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getResult(), is( "Hello John Doe" ) );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );

        dmnResult = runtime.evaluateDecisionByName( dmnModel, "nonExistantName", context );
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );

        dmnResult = runtime.evaluateDecisionByName( dmnModel, "", context );
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );

        dmnResult = runtime.evaluateDecisionByName( dmnModel, null, context );
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );
    }

    @Test
    public void testInputStringEvaluateDecisionById() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Full Name", "John Doe" );

        DMNResult dmnResult = runtime.evaluateDecisionById( dmnModel, "d_GreetingMessage", context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultById( "d_GreetingMessage" ).getResult(), is( "Hello John Doe" ) );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );

        dmnResult = runtime.evaluateDecisionById( dmnModel, "nonExistantId", context );
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );

        dmnResult = runtime.evaluateDecisionById( dmnModel, "", context );
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );

        dmnResult = runtime.evaluateDecisionById( dmnModel, null, context );
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );
    }

    @Test
    public void testInputStringAllowedValuesEvaluateAll() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0003-input-data-string-allowed-values.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0003-input-data-string-allowed-values" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Employment Status", "SELF-EMPLOYED" );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Employment Status Statement" ), is( "You are SELF-EMPLOYED" ) );
    }

    @Ignore("Needs fixing")
    @Test
    public void testInputStringNotAllowedValuesEvaluateAll() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0003-input-data-string-allowed-values.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0003-input-data-string-allowed-values" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Employment Status", "NOT-ALLOWED-VALUE" );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Employment Status Statement" ).getResult(), is((String) null) );
        assertThat( dmnResult.getMessages().size(), is(1) );
        assertThat( dmnResult.getMessages().get(0).getSeverity(), is(DMNMessage.Severity.ERROR) );
        assertThat( dmnResult.getDecisionResults().get(0).getMessages().size(), is(1) );
        assertThat( dmnResult.getDecisionResults().get(0).getMessages().get(0).getSeverity(), is(DMNMessage.Severity.ERROR) );
    }

    @Test
    public void testInputNumberEvaluateAll() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0002-input-data-number.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0002-input-data-number" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Monthly Salary", new BigDecimal( 1000 ) );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Yearly Salary" ), is( new BigDecimal( 12000 ) ) );
    }

    @Test
    public void testGetRequiredInputsByName() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        Set<InputDataNode> inputs = dmnModel.getRequiredInputsForDecisionName( "Greeting Message" );

        assertThat( inputs.size(), is(1) );
        assertThat( inputs.iterator().next().getName(), is("Full Name") );

        inputs = dmnModel.getRequiredInputsForDecisionName("nonExistantDecisionName");
        assertThat( inputs.size(), is(0) );
    }

    @Test
    public void testGetRequiredInputsById() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        Set<InputDataNode> inputs = dmnModel.getRequiredInputsForDecisionId( "d_GreetingMessage" );

        assertThat( inputs.size(), is(1) );
        assertThat( inputs.iterator().next().getName(), is("Full Name") );

        inputs = dmnModel.getRequiredInputsForDecisionId( "nonExistantId" );
        assertThat( inputs.size(), is(0) );
    }

    @Test
    public void testNonexistantInputNodeName() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();
        context.set( "Nonexistant Input", "John Doe" );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getResult(), is((String) null) );
        assertThat( dmnResult.getMessages().size(), is(1) );
        assertThat( dmnResult.getMessages().get(0).getSeverity(), is(DMNMessage.Severity.ERROR) );
        assertThat( dmnResult.getDecisionResults().get(0).getMessages().size(), is(1) );
        assertThat( dmnResult.getDecisionResults().get(0).getMessages().get(0).getSeverity(), is(DMNMessage.Severity.ERROR) );
    }
}
