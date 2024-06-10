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
package org.kie.dmn.core.compiler.extensions;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;

class DMNExtensionRegisterTest {
    private static final Logger LOG = LoggerFactory.getLogger(DMNExtensionRegisterTest.class);

    @Test
    void usingSystemProperty() {
        try {
            System.setProperty("org.kie.dmn.profiles.FirstNameLastNameProfile", FirstNameLastNameProfile.class.getCanonicalName());
            assertThat(System.getProperty("org.kie.dmn.profiles.FirstNameLastNameProfile")).isEqualTo(FirstNameLastNameProfile.class.getCanonicalName());
            
            final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0001-input-data-string-with-extensions.dmn", this.getClass() );
            final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
            assertThat(dmnModel).isNotNull();
            assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

            assertThat(dmnModel.getDefinitions().getDrgElement()).hasSize(3);
            
            final InputData inputData1 = (InputData) dmnModel.getDefinitions().getDrgElement().get(1);
            assertThat(inputData1.getName()).isEqualTo("First Name");
            final DMNElement.ExtensionElements id1elements = inputData1.getExtensionElements();
            assertThat(id1elements).isNotNull();
            assertThat(id1elements.getAny()).hasSize(1);
            final FirstNameDescription firstNameDescription = (FirstNameDescription) id1elements.getAny().get(0);
            assertThat(firstNameDescription.getContent()).isEqualTo("First name in latin characters");
            
            final InputData inputData2 = (InputData) dmnModel.getDefinitions().getDrgElement().get(2);
            assertThat(inputData2.getName()).isEqualTo("Last Name");
            final DMNElement.ExtensionElements id2elements = inputData2.getExtensionElements();
            assertThat(id2elements).isNotNull();
            assertThat(id2elements.getAny()).hasSize(1);
            final LastNameDescription lastNameDescription = (LastNameDescription) id2elements.getAny().get(0);
            assertThat(lastNameDescription.getContent()).isEqualTo("Last name in latin characters");
        } catch (final Exception e) {
            LOG.error("{}", e.getLocalizedMessage(), e);
            throw e;
        } finally {
            System.clearProperty("org.kie.dmn.profiles.FirstNameLastNameProfile");
            assertThat(System.getProperty("org.kie.dmn.profiles.FirstNameLastNameProfile")).isNull();
        }
    }

    @Test
    void usingKModuleProperty() {
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
        assertThat(results.getMessages(Level.WARNING)).hasSize(0);

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        assertThat(dmnModel.getDefinitions().getDrgElement()).hasSize(3);
        
        final InputData inputData1 = (InputData) dmnModel.getDefinitions().getDrgElement().get(1);
        assertThat(inputData1.getName()).isEqualTo("First Name");
        final DMNElement.ExtensionElements id1elements = inputData1.getExtensionElements();
        assertThat(id1elements).isNotNull();
        assertThat(id1elements.getAny()).hasSize(1);
        final FirstNameDescription firstNameDescription = (FirstNameDescription) id1elements.getAny().get(0);
        assertThat(firstNameDescription.getContent()).isEqualTo("First name in latin characters");
        
        final InputData inputData2 = (InputData) dmnModel.getDefinitions().getDrgElement().get(2);
        assertThat(inputData2.getName()).isEqualTo("Last Name");
        final DMNElement.ExtensionElements id2elements = inputData2.getExtensionElements();
        assertThat(id2elements).isNotNull();
        assertThat(id2elements.getAny()).hasSize(1);
        final LastNameDescription lastNameDescription = (LastNameDescription) id2elements.getAny().get(0);
        assertThat(lastNameDescription.getContent()).isEqualTo("Last name in latin characters");
    }

    @Test
    void usingKModulePropertyWrongClasses() {
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
        assertThat(results.getMessages(Level.WARNING)).hasSizeGreaterThan(0);

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/kie-dmn", "0001-input-data-string" );
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        assertThat(dmnModel.getDefinitions().getDrgElement()).hasSize(3);
        final InputData inputData1 = (InputData) dmnModel.getDefinitions().getDrgElement().get(1);
        assertThat(inputData1.getName()).isEqualTo("First Name");
        final DMNElement.ExtensionElements id1elements = inputData1.getExtensionElements();
        assertThat(id1elements).isNotNull();
        assertThat(id1elements.getAny()).hasSize(0);
        
        final InputData inputData2 = (InputData) dmnModel.getDefinitions().getDrgElement().get(2);
        assertThat(inputData2.getName()).isEqualTo("Last Name");
        final DMNElement.ExtensionElements id2elements = inputData2.getExtensionElements();
        assertThat(id2elements).isNotNull();
        assertThat(id2elements.getAny()).hasSize(0);
    }

    private String formatMessages(final List<DMNMessage> messages) {
        return messages.stream().map(Object::toString).collect(Collectors.joining("\n" ) );
    }
}
