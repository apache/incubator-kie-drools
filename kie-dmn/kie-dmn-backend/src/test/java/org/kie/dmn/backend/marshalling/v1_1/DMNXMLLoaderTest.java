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

package org.kie.dmn.backend.marshalling.v1_1;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.XMLConstants;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.model.v1_1.Decision;
import org.kie.dmn.model.v1_1.Definitions;
import org.kie.dmn.model.v1_1.InputData;
import org.kie.dmn.model.v1_1.LiteralExpression;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class DMNXMLLoaderTest {

    @Test
    public void testLoadingDefinitions() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newDefaultMarshaller();

        final InputStream is = this.getClass().getResourceAsStream( "0001-input-data-string.dmn" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Definitions def = DMNMarshaller.unmarshal( isr );

        assertThat( def, not( nullValue() ) );
        assertThat( def.getName(), is("0001-input-data-string") );
        assertThat( def.getId(), is("_0001-input-data-string") );
        assertThat( def.getNamespace(), is("https://github.com/agilepro/dmn-tck") );

        assertThat( def.getDrgElement().size(), is( 2 ) );
        assertThat( def.getDrgElement().get( 0 ), is( instanceOf( Decision.class ) ) );

        Decision dec = (Decision) def.getDrgElement().get( 0 );
        assertThat( dec.getName(), is("Greeting Message") );
        assertThat( dec.getId(), is("d_GreetingMessage") );
        assertThat( dec.getVariable().getName(), is("Greeting Message") );
        assertThat( dec.getVariable().getTypeRef().getPrefix(), is( "feel" ) );
        assertThat( dec.getVariable().getTypeRef().getLocalPart(), is( "string" ) );
        assertThat( dec.getVariable().getTypeRef().getNamespaceURI(), is( XMLConstants.NULL_NS_URI ) );

        assertThat( dec.getInformationRequirement().size(), is( 1 ) );
        assertThat( dec.getInformationRequirement().get( 0 ).getRequiredInput().getHref(), is( "#i_FullName" ) );
        assertThat( dec.getExpression(), is( instanceOf( LiteralExpression.class ) ) );

        LiteralExpression le = (LiteralExpression) dec.getExpression();
        assertThat( le.getText(), is("\"Hello \" + Full Name") );

        InputData idata = (InputData) def.getDrgElement().get( 1 );
        assertThat( idata.getId(), is( "i_FullName" ) );
        assertThat( idata.getName(), is( "Full Name" ) );
        assertThat( idata.getVariable().getName(), is( "Full Name" ) );
        assertThat( idata.getVariable().getTypeRef().getPrefix(), is( "feel" ) );
        assertThat( idata.getVariable().getTypeRef().getLocalPart(), is( "string" ) );
        assertThat( idata.getVariable().getTypeRef().getNamespaceURI(), is( XMLConstants.NULL_NS_URI ) );
    }

    @Test
    @Ignore("No unmarshaller implemented")
    public void testLoadingExample() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newDefaultMarshaller();

        final InputStream is = this.getClass().getResourceAsStream( "/src/test/resources/ch11example.xml" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Object o = DMNMarshaller.unmarshal( isr );

        final Definitions root = (Definitions) o;

        assertNotNull( root );
    }

    @Test
    @Ignore("No unmarshaller implemented")
    public void testLoadingDishDecision() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newDefaultMarshaller();

        final InputStream is = this.getClass().getResourceAsStream( "/src/test/resources/dish-decision.xml" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Object o = DMNMarshaller.unmarshal( isr );

        final Definitions root = (Definitions) o;

        assertNotNull( root );
    }

}
