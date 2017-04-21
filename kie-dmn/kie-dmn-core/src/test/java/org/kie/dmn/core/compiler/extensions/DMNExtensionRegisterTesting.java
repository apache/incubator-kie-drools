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

package org.kie.dmn.core.compiler.extensions;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.Definitions;
import org.kie.dmn.model.v1_1.InputData;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.utils.ChainedProperties;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DMNExtensionRegisterTesting {

    private void setProperties() {
        System.setProperty("foo", "bar");
        System.setProperty("org.kie.dmn.marshaller.extension.firstname", "org.kie.dmn.core.compiler.extensions.FirstNameDescriptionRegister");
        System.setProperty("org.kie.dmn.marshaller.extension.lastname", "org.kie.dmn.core.compiler.extensions.LastNameDescriptionRegister");
    }

    private Map<String, String> loadExtensionProperties() {
        ChainedProperties props = new ChainedProperties("extension.properties", this.getClass().getClassLoader(), true);
        Map<String, String> extensionClassNames = new HashMap<String, String>();
        props.mapStartsWith(extensionClassNames, "org.kie.dmn.marshaller.extension.", false);
        return extensionClassNames;
    }

    private void clearProperties() {
        System.clearProperty("foo");
        System.clearProperty("org.kie.dmn.marshaller.extension.firstname");
        System.clearProperty("org.kie.dmn.marshaller.extension.lastname");
    }

    private DMNCompilerConfiguration loadDMNCompilerConfig() {
        DMNCompilerConfiguration compilerConfig = null;
        setProperties();
        Map<String, String> extensionClassNames = loadExtensionProperties();
        List<DMNExtensionRegister> extensionRegisters = new ArrayList<DMNExtensionRegister>();
        KnowledgeBuilder kbuilder = new KnowledgeBuilderImpl();
        try {
            for (Map.Entry<String, String> extensionProperty : extensionClassNames.entrySet()) {
                String extRegClassName = extensionProperty.getValue();
                DMNExtensionRegister extRegister = (DMNExtensionRegister) ((KnowledgeBuilderImpl) kbuilder).getRootClassLoader()
                        .loadClass(extRegClassName).newInstance();
                extensionRegisters.add(extRegister);
            }
            compilerConfig = DMNFactory.newCompilerConfiguration();
            compilerConfig.addExtensions(extensionRegisters);
        } catch(ClassNotFoundException e) {
            System.out.println( "Trying to load a non-existing extension element register " + e.getLocalizedMessage());
        } catch(Exception e) {
            System.out.println("Other exception");
        }
        return compilerConfig;
    }

    @Test
    public void checkClassesLoaded() {
        DMNCompilerConfiguration compilerConfig = loadDMNCompilerConfig();
        assertEquals(2, compilerConfig.getRegisteredExtensions().size());
        assertTrue(compilerConfig.getRegisteredExtensions().get(0) instanceof DMNExtensionRegister);
        assertEquals(FirstNameDescriptionRegister.class, compilerConfig.getRegisteredExtensions().get(0).getClass());
        clearProperties();
    }

    @Test
    public void testFirstNameConverter() {
        setProperties();
        DMNCompilerConfiguration compilerConfig = loadDMNCompilerConfig();
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newMarshallerWithExtensions(compilerConfig.getRegisteredExtensions());
        final InputStream is = this.getClass().getResourceAsStream( "0001-input-data-string-with-extensions.dmn" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Definitions def = DMNMarshaller.unmarshal( isr );
        InputData inputData = (InputData)def.getDrgElement().get(1);
        DMNElement.ExtensionElements elements = inputData.getExtensionElements();
        assertTrue(elements != null);
        assertEquals(1, elements.getAny().size());
        FirstNameDescription desription = (FirstNameDescription) elements.getAny().get(0);
        assertTrue(desription.getContent().contains("First"));
        clearProperties();
    }


    @Test
    public void testLastNameConverter() {
        setProperties();
        DMNCompilerConfiguration compilerConfig = loadDMNCompilerConfig();
        final DMNMarshaller DMNMarshaller = DMNMarshallerFactory.newMarshallerWithExtensions(compilerConfig.getRegisteredExtensions());
        final InputStream is = this.getClass().getResourceAsStream( "0001-input-data-string-with-extensions.dmn" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Definitions def = DMNMarshaller.unmarshal( isr );
        InputData inputData = (InputData)def.getDrgElement().get(2);
        DMNElement.ExtensionElements elements = inputData.getExtensionElements();
        assertTrue(elements != null);
        assertEquals(1, elements.getAny().size());
        LastNameDescription desription = (LastNameDescription) elements.getAny().get(0);
        assertTrue(desription.getContent().contains("Last"));
        clearProperties();
    }

}
