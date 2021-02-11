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

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.InputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class DMNExtensionRegisterTest {
    private static final Logger LOG = LoggerFactory.getLogger(DMNExtensionRegisterTest.class);

    @Test
    public void testUsingSystemProperty() {
        try {
            System.setProperty("org.kie.dmn.profiles.FirstNameLastNameProfile", FirstNameLastNameProfile.class.getCanonicalName());
            assertEquals(FirstNameLastNameProfile.class.getCanonicalName(), System.getProperty("org.kie.dmn.profiles.FirstNameLastNameProfile"));
            
            final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string-with-extensions.dmn", this.getClass() );
            final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
            assertThat( dmnModel, notNullValue() );
            assertThat( formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );
            
            assertEquals(3, dmnModel.getDefinitions().getDrgElement().size());
            
            final InputData inputData1 = (InputData) dmnModel.getDefinitions().getDrgElement().get(1);
            assertEquals("First Name", inputData1.getName());
            final DMNElement.ExtensionElements id1elements = inputData1.getExtensionElements();
            assertNotNull(id1elements);
            assertEquals(1, id1elements.getAny().size());
            final FirstNameDescription firstNameDescription = (FirstNameDescription) id1elements.getAny().get(0);
            assertEquals("First name in latin characters", firstNameDescription.getContent());
            
            final InputData inputData2 = (InputData) dmnModel.getDefinitions().getDrgElement().get(2);
            assertEquals("Last Name", inputData2.getName());
            final DMNElement.ExtensionElements id2elements = inputData2.getExtensionElements();
            assertNotNull(id2elements);
            assertEquals(1, id2elements.getAny().size());
            final LastNameDescription lastNameDescription = (LastNameDescription) id2elements.getAny().get(0);
            assertEquals("Last name in latin characters", lastNameDescription.getContent());
        } catch (final Exception e) {
            LOG.error("{}", e.getLocalizedMessage(), e);
            throw e;
        } finally {
            System.clearProperty("org.kie.dmn.profiles.FirstNameLastNameProfile");
            assertNull(System.getProperty("org.kie.dmn.profiles.FirstNameLastNameProfile"));
        }
    }

    @Test
    public void testUsingKModuleProperty() {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        
        final KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty("org.kie.dmn.profiles.FirstNameLastNameProfile", FirstNameLastNameProfile.class.getCanonicalName());
        
        kfs.writeKModuleXML(kmm.toXML());
        
        kfs.write(ks.getResources().newClassPathResource("0001-input-data-string-with-extensions.dmn", this.getClass()));
        
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs ).buildAll();
        final Results results = kieBuilder.getResults();
        
        LOG.info("buildAll() completed.");
        results.getMessages(Level.WARNING).forEach( e -> LOG.warn("{}", e));
        assertEquals(0, results.getMessages(Level.WARNING).size());

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );
        assertThat( formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );
        
        assertEquals(3, dmnModel.getDefinitions().getDrgElement().size());
        
        final InputData inputData1 = (InputData) dmnModel.getDefinitions().getDrgElement().get(1);
        assertEquals("First Name", inputData1.getName());
        final DMNElement.ExtensionElements id1elements = inputData1.getExtensionElements();
        assertNotNull(id1elements);
        assertEquals(1, id1elements.getAny().size());
        final FirstNameDescription firstNameDescription = (FirstNameDescription) id1elements.getAny().get(0);
        assertEquals("First name in latin characters", firstNameDescription.getContent());
        
        final InputData inputData2 = (InputData) dmnModel.getDefinitions().getDrgElement().get(2);
        assertEquals("Last Name", inputData2.getName());
        final DMNElement.ExtensionElements id2elements = inputData2.getExtensionElements();
        assertNotNull(id2elements);
        assertEquals(1, id2elements.getAny().size());
        final LastNameDescription lastNameDescription = (LastNameDescription) id2elements.getAny().get(0);
        assertEquals("Last name in latin characters", lastNameDescription.getContent());
    }

    @Test
    public void testUsingKModuleProperty_WrongClasses() {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        
        final KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty("org.kie.dmn.profiles.FirstNameLastNameProfile", "foo");
        
        kfs.writeKModuleXML(kmm.toXML());
        
        kfs.write(ks.getResources().newClassPathResource("0001-input-data-string-with-extensions.dmn", this.getClass()));
        
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs ).buildAll();
        final Results results = kieBuilder.getResults();
        
        LOG.info("buildAll() completed.");
        results.getMessages(Level.WARNING).forEach( e -> LOG.warn("{}", e));
        assertTrue( results.getMessages(Level.WARNING).size() > 0 );

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat( dmnModel, notNullValue() );
        assertThat( formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );
        
        assertEquals(3, dmnModel.getDefinitions().getDrgElement().size());
        final InputData inputData1 = (InputData) dmnModel.getDefinitions().getDrgElement().get(1);
        assertEquals("First Name", inputData1.getName());
        final DMNElement.ExtensionElements id1elements = inputData1.getExtensionElements();
        assertNotNull(id1elements);
        assertEquals(0, id1elements.getAny().size());
        
        final InputData inputData2 = (InputData) dmnModel.getDefinitions().getDrgElement().get(2);
        assertEquals("Last Name", inputData2.getName());
        final DMNElement.ExtensionElements id2elements = inputData2.getExtensionElements();
        assertNotNull(id2elements);
        assertEquals(0, id2elements.getAny().size());
    }

    private String formatMessages(final List<DMNMessage> messages) {
        return messages.stream().map(Object::toString).collect(Collectors.joining("\n" ) );
    }
}
