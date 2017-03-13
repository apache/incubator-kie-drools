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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class DMNCompilerTest {

    @Test
    public void testItemDefAllowedValuesString() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0003-input-data-string-allowed-values.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0003-input-data-string-allowed-values" );
        assertThat( dmnModel, notNullValue() );

        ItemDefNode itemDef = dmnModel.getItemDefinitionByName( "tEmploymentStatus" );

        assertThat( itemDef.getName(), is( "tEmploymentStatus" ) );
        assertThat( itemDef.getId(), is( nullValue() ) );

        DMNType type = itemDef.getType();

        assertThat( type, is( notNullValue() ) );
        assertThat( type.getName(), is( "tEmploymentStatus" ) );
        assertThat( type.getId(), is( nullValue() ) );
        assertThat( type, is( instanceOf( SimpleTypeImpl.class ) ) );

        SimpleTypeImpl feelType = (SimpleTypeImpl) type;

        EvaluationContext ctx = new EvaluationContextImpl( null );
        assertThat( feelType.getFeelType(), is( BuiltInType.STRING ) );
        assertThat( feelType.getAllowedValues().size(), is( 4 ) );
        assertThat( feelType.getAllowedValues().get( 0 ).apply( ctx, "UNEMPLOYED" ), is( true ) );
        assertThat( feelType.getAllowedValues().get( 1 ).apply( ctx, "EMPLOYED" ), is( true )   );
        assertThat( feelType.getAllowedValues().get( 2 ).apply( ctx, "SELF-EMPLOYED" ), is( true )  );
        assertThat( feelType.getAllowedValues().get( 3 ).apply( ctx, "STUDENT" ), is( true )  );
    }

    @Test
    public void testCompositeItemDefinition() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0008-LX-arithmetic.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0008-LX-arithmetic" );
        assertThat( dmnModel, notNullValue() );

        ItemDefNode itemDef = dmnModel.getItemDefinitionByName( "tLoan" );

        assertThat( itemDef.getName(), is( "tLoan" ) );
        assertThat( itemDef.getId(), is( "tLoan" ) );

        DMNType type = itemDef.getType();

        assertThat( type, is( notNullValue() ) );
        assertThat( type.getName(), is( "tLoan" ) );
        assertThat( type.getId(), is( "tLoan" ) );
        assertThat( type, is( instanceOf( CompositeTypeImpl.class ) ) );

        CompositeTypeImpl compType = (CompositeTypeImpl) type;

        assertThat( compType.getFields().size(), is( 3 ) );
        DMNType principal = compType.getFields().get( "principal" );
        assertThat( principal, is( notNullValue() ) );
        assertThat( principal.getName(), is( "number" ) );
        assertThat( ((SimpleTypeImpl)principal).getFeelType(), is( BuiltInType.NUMBER ) );

        DMNType rate = compType.getFields().get( "rate" );
        assertThat( rate, is( notNullValue() ) );
        assertThat( rate.getName(), is( "number" ) );
        assertThat( ((SimpleTypeImpl)rate).getFeelType(), is( BuiltInType.NUMBER ) );

        DMNType termMonths = compType.getFields().get( "termMonths" );
        assertThat( termMonths, is( notNullValue() ) );
        assertThat( termMonths.getName(), is( "number" ) );
        assertThat( ((SimpleTypeImpl)termMonths).getFeelType(), is( BuiltInType.NUMBER ) );
    }


}
