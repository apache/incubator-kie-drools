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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.prototype;

public class DMNInputRuntimeTest extends BaseInterpretedVsCompiledTest {

    public DMNInputRuntimeTest(final boolean useExecModelCompiler ) {
        super( useExecModelCompiler );
    }

    @Test
    public void testInputStringEvaluateAll() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Full Name", "John Doe" );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getResult(), is( "Hello John Doe" ) );

        final DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );
    }

    @Test
    public void testInputStringEvaluateDecisionByName() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Full Name", "John Doe" );

        DMNResult dmnResult = runtime.evaluateByName( dmnModel, context, "Greeting Message");

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getResult(), is( "Hello John Doe" ) );

        final DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );

        dmnResult = runtime.evaluateByName( dmnModel, context, "nonExistantName");
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );

        dmnResult = runtime.evaluateByName( dmnModel, context, "" );
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );

        dmnResult = runtime.evaluateByName( dmnModel, context, (String) null);
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );
    }

    @Test
    public void testInputStringEvaluateDecisionById() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Full Name", "John Doe" );

        DMNResult dmnResult = runtime.evaluateById( dmnModel, context, "d_GreetingMessage" );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultById( "d_GreetingMessage" ).getResult(), is( "Hello John Doe" ) );

        final DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );

        dmnResult = runtime.evaluateById( dmnModel, context, "nonExistantId" );
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );

        dmnResult = runtime.evaluateById( dmnModel, context, "" );
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );

        dmnResult = runtime.evaluateById( dmnModel, context, (String) null);
        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getEvaluationStatus(), is( DMNDecisionResult.DecisionEvaluationStatus.NOT_EVALUATED ) );
    }

    @Test
    public void testInputStringAllowedValuesEvaluateAll() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0003-input-data-string-allowed-values.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0003-input-data-string-allowed-values" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Employment Status", "SELF-EMPLOYED" );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        final DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Employment Status Statement" ), is( "You are SELF-EMPLOYED" ) );
    }

    @Test
    public void testInputStringNotInTypeScopeEvaluateAll() {
        testInputStringNotAllowedValuesEvaluateAll("NOT-ALLOWED-VALUE");
    }

    @Test
    public void testInputStringWrongTypeEvaluateAll() {
        testInputStringNotAllowedValuesEvaluateAll(new Object());
    }

    private void testInputStringNotAllowedValuesEvaluateAll(final Object inputValue) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0003-input-data-string-allowed-values.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0003-input-data-string-allowed-values" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Employment Status", inputValue );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Employment Status Statement" ).getResult(), is((String) null) );
        assertThat( dmnResult.getMessages().size(), is(1) );
        assertThat( dmnResult.getMessages().get(0).getSeverity(), is(DMNMessage.Severity.ERROR) );
        assertThat( dmnResult.getDecisionResults().get(0).getMessages().size(), is(1) );
        assertThat( dmnResult.getDecisionResults().get(0).getMessages().get(0).getSeverity(), is(DMNMessage.Severity.ERROR) );
    }

    @Test
    public void testInputNumberEvaluateAll() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0002-input-data-number.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0002-input-data-number" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Monthly Salary", new BigDecimal( 1000 ) );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        final DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Yearly Salary" ), is( new BigDecimal( 12000 ) ) );
    }

    @Test
    public void testGetRequiredInputsByName() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        Set<InputDataNode> inputs = dmnModel.getRequiredInputsForDecisionName( "Greeting Message" );

        assertThat( inputs.size(), is(1) );
        assertThat( inputs.iterator().next().getName(), is("Full Name") );

        inputs = dmnModel.getRequiredInputsForDecisionName("nonExistantDecisionName");
        assertThat( inputs.size(), is(0) );
    }

    @Test
    public void testGetRequiredInputsById() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        Set<InputDataNode> inputs = dmnModel.getRequiredInputsForDecisionId( "d_GreetingMessage" );

        assertThat( inputs.size(), is(1) );
        assertThat( inputs.iterator().next().getName(), is("Full Name") );

        inputs = dmnModel.getRequiredInputsForDecisionId( "nonExistantId" );
        assertThat( inputs.size(), is(0) );
    }

    @Test
    public void testNonexistantInputNodeName() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/drools/kie-dmn", "_0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );

        final DMNContext context = DMNFactory.newContext();
        context.set( "Nonexistant Input", "John Doe" );

        final DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        assertThat( dmnResult.getDecisionResults().size(), is(1) );
        assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getResult(), is((String) null) );
        assertThat( dmnResult.getMessages().size(), is(1) );
        assertThat( dmnResult.getMessages().get(0).getSeverity(), is(DMNMessage.Severity.ERROR) );
        assertThat( dmnResult.getDecisionResults().get(0).getMessages().size(), is(1) );
        assertThat( dmnResult.getDecisionResults().get(0).getMessages().get(0).getSeverity(), is(DMNMessage.Severity.ERROR) );
    }

    @Test
    public void testAllowedValuesChecks() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "AllowedValuesChecks.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b-504f3e77b442",
                "AllowedValuesChecks" );
        assertThat( dmnModel, notNullValue() );
        assertThat( DMNRuntimeUtil.formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );

        final DMNContext ctx1 = runtime.newContext();
        ctx1.set("p1", prototype(entry("Name", "P1"), entry("Interests", Collections.singletonList("Golf"))));
        final DMNResult dmnResult1 = runtime.evaluateAll( dmnModel, ctx1 );
        assertThat( DMNRuntimeUtil.formatMessages( dmnResult1.getMessages() ), dmnResult1.hasErrors(), is( false ) );
        assertThat( dmnResult1.getContext().get( "MyDecision" ), is( "The Person P1 likes 1 thing(s)." ) );

        final DMNContext ctx2 = runtime.newContext();
        ctx2.set("p1", prototype(entry("Name", "P2"), entry("Interests", Collections.singletonList("x"))));
        final DMNResult dmnResult2 = runtime.evaluateAll( dmnModel, ctx2 );
        assertThat( DMNRuntimeUtil.formatMessages( dmnResult2.getMessages() ), dmnResult2.hasErrors(), is( true ) );
        assertThat( dmnResult2.getMessages().stream().anyMatch( m -> m.getMessageType().equals( DMNMessageType.ERROR_EVAL_NODE ) ), is( true ) );

        final DMNContext ctx3 = runtime.newContext();
        ctx3.set("p1", prototype(entry("Name", "P3"), entry("Interests", Arrays.asList("Golf", "Computer"))));
        final DMNResult dmnResult3 = runtime.evaluateAll( dmnModel, ctx3 );
        assertThat( DMNRuntimeUtil.formatMessages( dmnResult3.getMessages() ), dmnResult3.hasErrors(), is( false ) );
        assertThat( dmnResult3.getContext().get( "MyDecision" ), is( "The Person P3 likes 2 thing(s)." ) );

        final DMNContext ctx4 = runtime.newContext();
        ctx4.set("p1", prototype(entry("Name", "P4"), entry("Interests", Arrays.asList("Golf", "x"))));
        final DMNResult dmnResult4 = runtime.evaluateAll( dmnModel, ctx4 );
        assertThat( DMNRuntimeUtil.formatMessages( dmnResult4.getMessages() ), dmnResult4.hasErrors(), is( true ) );
        assertThat( dmnResult4.getMessages().stream().anyMatch( m -> m.getMessageType().equals( DMNMessageType.ERROR_EVAL_NODE ) ), is( true ) );
    }

    @Test
    public void testDMNInputDataNodeTypeTest() {
        // DROOLS-1569
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DMNInputDataNodeTypeTest.dmn", this.getClass());
        final String MODEL_NAMESPACE = "http://www.trisotech.com/definitions/_17396034-163a-48aa-9a7f-c6eb17f9cc6c";
        final String FEEL_NAMESPACE = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_FEEL;
        final DMNModel dmnModel = runtime.getModel(MODEL_NAMESPACE, "DMNInputDataNodeTypeTest");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final InputDataNode idnMembership = dmnModel.getInputs().stream().filter(idn -> idn.getName().equals("Membership Level")).findFirst().get();
        assertThat(idnMembership.getType().getBaseType().getNamespace(), is(FEEL_NAMESPACE));
        assertThat(idnMembership.getType().getBaseType().getName(), is("string"));
        assertThat(idnMembership.getType().isCollection(), is(false));
        assertThat(idnMembership.getType().isComposite(), is(false));
        assertThat(idnMembership.getType().getAllowedValues().size(), is(3));
        assertThat(idnMembership.getType().getAllowedValues().get(0).toString(), is("\"Gold\""));
        assertThat(idnMembership.getType().getAllowedValues().get(1).toString(), is("\"Silver\""));
        assertThat(idnMembership.getType().getAllowedValues().get(2).toString(), is("\"None\""));

        final InputDataNode idnMembershipLevels = dmnModel.getInputs().stream().filter(idn -> idn.getName().equals("Membership Levels")).findFirst().get();
        assertThat(idnMembershipLevels.getType().getBaseType().getNamespace(), is(MODEL_NAMESPACE));
        assertThat(idnMembershipLevels.getType().getBaseType().getName(), is("tMembershipLevel"));
        assertThat(idnMembershipLevels.getType().isCollection(), is(true));
        assertThat(idnMembershipLevels.getType().isComposite(), is(false));
        assertThat(idnMembershipLevels.getType().getAllowedValues().isEmpty(), is(true));

        final InputDataNode idnPercent = dmnModel.getInputs().stream().filter(idn -> idn.getName().equals("Percent")).findFirst().get();
        assertThat(idnPercent.getType().getBaseType().getNamespace(), is(FEEL_NAMESPACE));
        assertThat(idnPercent.getType().getBaseType().getName(), is("number"));
        assertThat(idnPercent.getType().isCollection(), is(false));
        assertThat(idnPercent.getType().isComposite(), is(false));
        assertThat(idnPercent.getType().getAllowedValues().size(), is(1));
        assertThat(idnPercent.getType().getAllowedValues().get(0).toString(), is("[0..100]"));

        final InputDataNode idnCarDamageResponsibility = dmnModel.getInputs().stream().filter(idn -> idn.getName().equals("Car Damage Responsibility")).findFirst().get();
        assertThat(idnCarDamageResponsibility.getType().getBaseType(), is(nullValue()));
        assertThat(idnCarDamageResponsibility.getType().isCollection(), is(false));
        assertThat(idnCarDamageResponsibility.getType().isComposite(), is(true));
    }

    @Test
    public void testInputClauseTypeRefWithAllowedValues() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("actualInputMatchInputValues-forTypeRef.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn/definitions", "definitions");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = runtime.newContext();
        context.set("MyInput", "a");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("MyDecision"), is("Decision taken"));
    }

    @Test
    public void testInputDataTypeRefWithAllowedValues() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("actualInputMatchInputValues-forTypeRef.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn/definitions", "definitions");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = runtime.newContext();
        context.set("MyInput", "zzz");              // <<< `zzz` is NOT in the list of allowed value as declared by the typeRef for this inputdata

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(true));
        assertThat(dmnResult.getMessages().size(), is(1));
        assertThat(dmnResult.getMessages().get(0).getSourceId(), is("_3d560678-a126-4654-a686-bc6d941fe40b"));
    }

    @Test
    public void testMissingInputData() {
        final List<DMNMessage> messages = DMNRuntimeUtil.createExpectingDMNMessages("missing_input_data.dmn", getClass());
        assertThat(messages.get(0).getMessageType(), is(DMNMessageType.ERR_COMPILING_FEEL));
    }
    
    
    @Test
    public void testOrdering() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Order.dmn", this.getClass());
        final DMNModel dmnModel = runtime	.getModel("http://www.trisotech.com/definitions/_6318588b-c32f-4070-848b-bd8017e6b94e", "Drawing 1");

        int index = 1;
        for (InputDataNode node : dmnModel.getInputs()) {
            assertTrue(node.getName().endsWith("" + index++));
        }

        index = 1;
        for (DecisionNode node : dmnModel.getDecisions()) {
            assertTrue(node.getName().endsWith("" + index++));
        }

        index = 1;
        for (BusinessKnowledgeModelNode node : dmnModel.getBusinessKnowledgeModels()) {
            assertTrue(node.getName().endsWith("" + index++));
        }

        index = 1;
        for (ItemDefNode node : dmnModel.getItemDefinitions()) {
            assertTrue(node.getName().endsWith("" + index++));
        }

        index = 1;
        for (DecisionServiceNode node : dmnModel.getDecisionServices()) {
            assertTrue(node.getName().endsWith("" + index++));
        }
    }
}
