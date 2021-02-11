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

package org.kie.dmn.legacy.tests.core.v1_1;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.types.AliasFEELType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.ClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DMNCompilerTest extends BaseDMN1_1VariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNCompilerTest.class);

    public DMNCompilerTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Test
    public void testItemDefAllowedValuesString() {
        final DMNRuntime runtime = createRuntime("0003-input-data-string-allowed-values.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0003-input-data-string-allowed-values" );
        assertThat( dmnModel, notNullValue() );

        final ItemDefNode itemDef = dmnModel.getItemDefinitionByName("tEmploymentStatus" );

        assertThat( itemDef.getName(), is( "tEmploymentStatus" ) );
        assertThat( itemDef.getId(), is( nullValue() ) );

        final DMNType type = itemDef.getType();

        assertThat( type, is( notNullValue() ) );
        assertThat( type.getName(), is( "tEmploymentStatus" ) );
        assertThat( type.getId(), is( nullValue() ) );
        assertThat( type, is( instanceOf( SimpleTypeImpl.class ) ) );

        final SimpleTypeImpl feelType = (SimpleTypeImpl) type;

        final EvaluationContext ctx = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), null);
        assertThat( feelType.getFeelType(), is(instanceOf(AliasFEELType.class)));
        assertThat( feelType.getFeelType().getName(), is("tEmploymentStatus"));
        assertThat( feelType.getAllowedValuesFEEL().size(), is( 4 ) );
        assertThat( feelType.getAllowedValuesFEEL().get( 0 ).apply( ctx, "UNEMPLOYED" ), is( true ) );
        assertThat( feelType.getAllowedValuesFEEL().get( 1 ).apply( ctx, "EMPLOYED" ), is( true )   );
        assertThat( feelType.getAllowedValuesFEEL().get( 2 ).apply( ctx, "SELF-EMPLOYED" ), is( true )  );
        assertThat( feelType.getAllowedValuesFEEL().get( 3 ).apply( ctx, "STUDENT" ), is( true )  );
    }

    @Test
    public void testCompositeItemDefinition() {
        final DMNRuntime runtime = createRuntime("0008-LX-arithmetic.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0008-LX-arithmetic" );
        assertThat( dmnModel, notNullValue() );

        final ItemDefNode itemDef = dmnModel.getItemDefinitionByName("tLoan" );

        assertThat( itemDef.getName(), is( "tLoan" ) );
        assertThat( itemDef.getId(), is( "tLoan" ) );

        final DMNType type = itemDef.getType();

        assertThat( type, is( notNullValue() ) );
        assertThat( type.getName(), is( "tLoan" ) );
        assertThat( type.getId(), is( "tLoan" ) );
        assertThat( type, is( instanceOf( CompositeTypeImpl.class ) ) );

        final CompositeTypeImpl compType = (CompositeTypeImpl) type;

        assertThat( compType.getFields().size(), is( 3 ) );
        final DMNType principal = compType.getFields().get("principal" );
        assertThat( principal, is( notNullValue() ) );
        assertThat( principal.getName(), is( "number" ) );
        assertThat( ((SimpleTypeImpl)principal).getFeelType(), is( BuiltInType.NUMBER ) );

        final DMNType rate = compType.getFields().get("rate" );
        assertThat( rate, is( notNullValue() ) );
        assertThat( rate.getName(), is( "number" ) );
        assertThat( ((SimpleTypeImpl)rate).getFeelType(), is( BuiltInType.NUMBER ) );

        final DMNType termMonths = compType.getFields().get("termMonths" );
        assertThat( termMonths, is( notNullValue() ) );
        assertThat( termMonths.getName(), is( "number" ) );
        assertThat( ((SimpleTypeImpl)termMonths).getFeelType(), is( BuiltInType.NUMBER ) );
    }

    @Test
    public void testCompilationThrowsNPE() {
        try {
            createRuntime("compilationThrowsNPE.dmn", this.getClass());
            fail("shouldn't have reached here.");
        } catch (final Exception ex) {
            assertThat(ex.getMessage(), Matchers.containsString("Unable to compile DMN model for the resource"));
        }
    }

    @Test
    public void testRecursiveFunctions() {
        // DROOLS-2161
        final DMNRuntime runtime = createRuntime("Recursive.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "Recursive" );
        assertThat( dmnModel, notNullValue() );
        assertFalse( evaluateModel(runtime, dmnModel, DMNFactory.newContext() ).hasErrors() );
    }

    @Test
    public void testImport() {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Importing_Model.dmn",
                                                                                       this.getClass(),
                                                                                       "Imported_Model.dmn");

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df36",
                                                        "Imported Model");
        assertThat(importedModel, notNullValue());
        for (final DMNMessage message : importedModel.getMessages()) {
            LOG.debug("{}", message);
        }

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edab52",
                                                   "Importing Model");
        assertThat(dmnModel, notNullValue());
        for (final DMNMessage message : dmnModel.getMessages()) {
            LOG.debug("{}", message);
        }

        final DMNContext context = runtime.newContext();
        context.set("A Person", mapOf(entry("name", "John"), entry("age", 47)));

        final DMNResult evaluateAll = evaluateModel(runtime, dmnModel, context);
        for (final DMNMessage message : evaluateAll.getMessages()) {
            LOG.debug("{}", message);
        }
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("Greeting").getResult(), is("Hello John!"));
    }

}
