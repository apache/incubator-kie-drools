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
package org.kie.dmn.backend.marshalling.v1_1;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.XMLConstants;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.xstream.extensions.DecisionServicesExtensionRegister;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.InputData;
import org.kie.dmn.model.api.LiteralExpression;

import static org.assertj.core.api.Assertions.assertThat;

class DMNXMLLoaderTest {

    @Test
    void loadingDefinitions() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newDefaultMarshaller();

        final InputStream is = this.getClass().getResourceAsStream( "0001-input-data-string.dmn" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Definitions def = DMNMarshaller.unmarshal( isr );

        assertThat(def).isNotNull();
        assertThat(def.getName()).isEqualTo("0001-input-data-string");
        assertThat(def.getId()).isEqualTo("_0001-input-data-string");
        assertThat(def.getNamespace()).isEqualTo("https://github.com/agilepro/dmn-tck");

        assertThat(def.getDrgElement()).hasSize(2);
        assertThat(def.getDrgElement().get(0)).isInstanceOf(Decision.class) ;

        Decision dec = (Decision) def.getDrgElement().get( 0 );
        assertThat(dec.getName()).isEqualTo("Greeting Message");
        assertThat(dec.getId()).isEqualTo("d_GreetingMessage");        
        assertThat(dec.getVariable().getName()).isEqualTo("Greeting Message");
        assertThat(dec.getVariable().getTypeRef().getPrefix()).isEqualTo( "feel");
        assertThat(dec.getVariable().getTypeRef().getLocalPart()).isEqualTo( "string");
        assertThat(dec.getVariable().getTypeRef().getNamespaceURI()).isEqualTo(XMLConstants.NULL_NS_URI);

        assertThat(dec.getInformationRequirement()).hasSize(1);
        assertThat(dec.getInformationRequirement().get(0).getRequiredInput().getHref()).isEqualTo( "#i_FullName");
        
        assertThat(dec.getExpression()).isInstanceOf(LiteralExpression.class);

        LiteralExpression le = (LiteralExpression) dec.getExpression();
        assertThat(le.getText()).isEqualTo("\"Hello \" + Full Name");

        InputData idata = (InputData) def.getDrgElement().get( 1 );
        assertThat(idata.getId()).isEqualTo( "i_FullName");
        assertThat(idata.getName()).isEqualTo( "Full Name");
        assertThat(idata.getVariable().getName()).isEqualTo( "Full Name");
        assertThat(idata.getVariable().getTypeRef().getPrefix()).isEqualTo( "feel");
        assertThat(idata.getVariable().getTypeRef().getLocalPart()).isEqualTo( "string");
        assertThat(idata.getVariable().getTypeRef().getNamespaceURI()).isEqualTo( XMLConstants.NULL_NS_URI);
    }

    @Test
    void loadingDecisionServices() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newMarshallerWithExtensions(List.of(new DecisionServicesExtensionRegister()));

        final InputStream is = this.getClass().getResourceAsStream("0004-decision-services.dmn");
        final InputStreamReader isr = new InputStreamReader(is);
        final Definitions def = DMNMarshaller.unmarshal(isr);

        assertThat(def.getDecisionService()).hasSize(2);

        DecisionService decisionService1 = def.getDecisionService().get(0);
        assertThat(decisionService1.getId()).isEqualTo("_70386614-9838-420b-a2ae-ff901ada63fb");
        assertThat(decisionService1.getName()).isEqualTo("A Only Knowing B and C");
        assertThat(decisionService1.getDescription()).isEqualTo("Description of A (BC)");
        assertThat(decisionService1.getOutputDecision()).hasSize(1);
        assertThat(decisionService1.getEncapsulatedDecision()).hasSize(0);
        assertThat(decisionService1.getInputDecision()).hasSize(2);
        assertThat(decisionService1.getInputData()).hasSize(0);
        assertThat(decisionService1.getOutputDecision().get(0).getHref()).isEqualTo("#_c2b44706-d479-4ceb-bb74-73589d26dd04");

        DecisionService decisionService2 = def.getDecisionService().get(1);
        assertThat(decisionService2.getId()).isEqualTo("_4620ef13-248a-419e-bc68-6b601b725a03");
        assertThat(decisionService2.getName()).isEqualTo("A only as output knowing D and E");
        assertThat(decisionService2.getOutputDecision()).hasSize(1);
        assertThat(decisionService2.getEncapsulatedDecision()).hasSize(2);
        assertThat(decisionService2.getInputDecision()).hasSize(0);
        assertThat(decisionService2.getInputData()).hasSize(2);
        assertThat(decisionService2.getInputData().get(0).getHref()).isEqualTo("#_bcea16fb-6c19-4bde-b37d-73407002c064");
        assertThat(decisionService2.getInputData().get(1).getHref()).isEqualTo("#_207b9195-a441-47f2-9414-2fad64b463f9");

    }

    @Test
    void loadingWithNoDecisionServices() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newMarshallerWithExtensions(List.of(new DecisionServicesExtensionRegister()));

        final InputStream is = this.getClass().getResourceAsStream("0001-input-data-string.dmn");
        final InputStreamReader isr = new InputStreamReader(is);
        final Definitions def = DMNMarshaller.unmarshal(isr);

        assertThat(def.getDecisionService()).hasSize(0); // check if No DecisionServices in extended v1.1 does not NPE.
    }

    @Test
    void test0004_multiple_extensions() throws Exception {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(List.of(new DecisionServicesExtensionRegister()));

        final InputStream is = this.getClass().getResourceAsStream("0004-decision-services_multiple_extensions.dmn");
        final InputStreamReader isr = new InputStreamReader(is);
        final Definitions def = marshaller.unmarshal(isr);

        assertThat(def.getExtensionElements().getAny()).hasSize(1);
        // if arrived here, means it did not fail with exception while trying to unmarshall unknown rss extension element, hence it just skipped it.
    }

    @Test
    void loadingExample() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newDefaultMarshaller();

        final InputStream is = this.getClass().getResourceAsStream("ch11example.xml");
        final InputStreamReader isr = new InputStreamReader( is );
        final Definitions o = DMNMarshaller.unmarshal(isr);

        final Definitions root = o;

        assertThat(root).isNotNull();
    }

    @Test
    void loadingDishDecision() {
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newDefaultMarshaller();

        final InputStream is = this.getClass().getResourceAsStream("dish-decision.xml");
        final InputStreamReader isr = new InputStreamReader( is );
        final Definitions o = DMNMarshaller.unmarshal(isr);

        final Definitions root = o;

        assertThat(root).isNotNull();
    }

}
