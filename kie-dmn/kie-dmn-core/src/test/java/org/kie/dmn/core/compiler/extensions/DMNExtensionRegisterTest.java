/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.compiler.extensions;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.InputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

public class DMNExtensionRegisterTest {
    private static final Logger LOG = LoggerFactory.getLogger(DMNExtensionRegisterTest.class);

    @Test
    public void testUsingSystemProperty() {
        try {
            System.setProperty("org.kie.dmn.marshaller.extension.firstname", "org.kie.dmn.core.compiler.extensions.FirstNameDescriptionRegister");
            System.setProperty("org.kie.dmn.marshaller.extension.lastname", "org.kie.dmn.core.compiler.extensions.LastNameDescriptionRegister");   
            assertEquals("org.kie.dmn.core.compiler.extensions.FirstNameDescriptionRegister", System.getProperty("org.kie.dmn.marshaller.extension.firstname"));
            assertEquals("org.kie.dmn.core.compiler.extensions.LastNameDescriptionRegister", System.getProperty("org.kie.dmn.marshaller.extension.lastname"));
            
            DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0001-input-data-string-with-extensions.dmn", this.getClass() );
            DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
            assertThat( dmnModel, notNullValue() );
            assertThat( formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );
            
            assertEquals(3, dmnModel.getDefinitions().getDrgElement().size());
            
            InputData inputData1 = (InputData) dmnModel.getDefinitions().getDrgElement().get(1);
            assertEquals("First Name", inputData1.getName());
            DMNElement.ExtensionElements id1elements = inputData1.getExtensionElements();
            assertTrue(id1elements != null);
            assertEquals(1, id1elements.getAny().size());
            FirstNameDescription firstNameDescription = (FirstNameDescription) id1elements.getAny().get(0);
            assertTrue(firstNameDescription.getContent().equals("First name in latin characters"));
            
            InputData inputData2 = (InputData) dmnModel.getDefinitions().getDrgElement().get(2);
            assertEquals("Last Name", inputData2.getName());
            DMNElement.ExtensionElements id2elements = inputData2.getExtensionElements();
            assertTrue(id2elements != null);
            assertEquals(1, id2elements.getAny().size());
            LastNameDescription lastNameDescription = (LastNameDescription) id2elements.getAny().get(0);
            assertTrue(lastNameDescription.getContent().equals("Last name in latin characters"));
        } catch (Exception e) {
            LOG.error("{}", e.getLocalizedMessage(), e);
            throw e;
        } finally {
            System.clearProperty("org.kie.dmn.marshaller.extension.firstname");
            System.clearProperty("org.kie.dmn.marshaller.extension.lastname");
            assertNull(System.getProperty("org.kie.dmn.marshaller.extension.firstname"));
            assertNull(System.getProperty("org.kie.dmn.marshaller.extension.lastname"));
        }
    }
    
    @Test
    public void testUsingKModuleProperty() {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        
        KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty("org.kie.dmn.marshaller.extension.firstname", "org.kie.dmn.core.compiler.extensions.FirstNameDescriptionRegister");
        kmm.setConfigurationProperty("org.kie.dmn.marshaller.extension.lastname", "org.kie.dmn.core.compiler.extensions.LastNameDescriptionRegister");  
        
        kfs.writeKModuleXML(kmm.toXML());
        
        kfs.write(ks.getResources().newClassPathResource("0001-input-data-string-with-extensions.dmn", this.getClass()));
        
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        Results results = kieBuilder.getResults();
        
        LOG.info("buildAll() completed.");
        results.getMessages(Level.WARNING).forEach( e -> LOG.warn("{}", e));
        assertTrue( results.getMessages(Level.WARNING).size() == 0 );

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );
        assertThat( formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );
        
        assertEquals(3, dmnModel.getDefinitions().getDrgElement().size());
        
        InputData inputData1 = (InputData) dmnModel.getDefinitions().getDrgElement().get(1);
        assertEquals("First Name", inputData1.getName());
        DMNElement.ExtensionElements id1elements = inputData1.getExtensionElements();
        assertTrue(id1elements != null);
        assertEquals(1, id1elements.getAny().size());
        FirstNameDescription firstNameDescription = (FirstNameDescription) id1elements.getAny().get(0);
        assertTrue(firstNameDescription.getContent().equals("First name in latin characters"));
        
        InputData inputData2 = (InputData) dmnModel.getDefinitions().getDrgElement().get(2);
        assertEquals("Last Name", inputData2.getName());
        DMNElement.ExtensionElements id2elements = inputData2.getExtensionElements();
        assertTrue(id2elements != null);
        assertEquals(1, id2elements.getAny().size());
        LastNameDescription lastNameDescription = (LastNameDescription) id2elements.getAny().get(0);
        assertTrue(lastNameDescription.getContent().equals("Last name in latin characters"));
    }
    
    @Test
    public void testUsingKModuleProperty_WrongClasses() {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        
        KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty("org.kie.dmn.marshaller.extension.firstname", "foo");
        kmm.setConfigurationProperty("org.kie.dmn.marshaller.extension.lastname", "bar");  
        
        kfs.writeKModuleXML(kmm.toXML());
        
        kfs.write(ks.getResources().newClassPathResource("0001-input-data-string-with-extensions.dmn", this.getClass()));
        
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        Results results = kieBuilder.getResults();
        
        LOG.info("buildAll() completed.");
        results.getMessages(Level.WARNING).forEach( e -> LOG.warn("{}", e));
        assertTrue( results.getMessages(Level.WARNING).size() > 0 );

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        DMNModel dmnModel = runtime.getModel( "https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );
        assertThat( formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );
        
        assertEquals(3, dmnModel.getDefinitions().getDrgElement().size());
        InputData inputData1 = (InputData) dmnModel.getDefinitions().getDrgElement().get(1);
        assertEquals("First Name", inputData1.getName());
        DMNElement.ExtensionElements id1elements = inputData1.getExtensionElements();
        assertTrue(id1elements != null);
        assertEquals(0, id1elements.getAny().size());
        
        InputData inputData2 = (InputData) dmnModel.getDefinitions().getDrgElement().get(2);
        assertEquals("Last Name", inputData2.getName());
        DMNElement.ExtensionElements id2elements = inputData2.getExtensionElements();
        assertTrue(id2elements != null);
        assertEquals(0, id2elements.getAny().size());
    }

    private String formatMessages(List<DMNMessage> messages) {
        return messages.stream().map( m -> m.toString() ).collect( Collectors.joining( "\n" ) );
    }
}
