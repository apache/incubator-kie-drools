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
package org.kie.dmn.validation;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.io.FileSystemResource;
import org.drools.io.ReaderResource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.kie.api.io.Resource;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.feel.util.ClassLoaderUtil;
import org.kie.dmn.model.api.Definitions;
import org.kie.internal.utils.ChainedProperties;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractValidatorTest {

    protected static DMNValidator validator;
    protected static DMNMarshaller marshaller;

    @BeforeAll
    public static void init() {
        List<DMNProfile> defaultDMNProfiles = DMNAssemblerService.getDefaultDMNProfiles(ChainedProperties.getChainedProperties(ClassLoaderUtil.findDefaultClassLoader()));
        validator = DMNValidatorFactory.newValidator(defaultDMNProfiles);
        List<DMNExtensionRegister> extensionRegisters = defaultDMNProfiles.stream().flatMap(dmnp -> dmnp.getExtensionRegisters().stream()).collect(Collectors.toList());
        if (!extensionRegisters.isEmpty()) {
            marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(extensionRegisters);
        } else {
            marshaller = DMNMarshallerFactory.newDefaultMarshaller();
        }
    }

    @AfterAll
    public static void dispose() {
    	if (validator != null)
        validator.dispose();
    }

    protected Reader getReader(final String resourceFileName ) {
        return getReader(resourceFileName, this.getClass());
    }

    /**
     * Return the Reader for the specified Resource with resourceFileName, using the supplied Class to locate it.
     */
    protected Reader getReader(final String resourceFileName, Class<?> clazz) {
        return new InputStreamReader(clazz.getResourceAsStream(resourceFileName)); 
    }

    protected File getFile(final String resourceFileName ) {
        return new File(this.getClass().getResource(resourceFileName).getFile());
    }
    
    protected Resource getResource(final String resourceFileName ) {
        return new FileSystemResource(new File(this.getClass().getResource(resourceFileName).getFile()));
    }
    
    protected Resource getResource(final String resourceFileName, Class<?> clazz) {
        return new ReaderResource(new InputStreamReader(clazz.getResourceAsStream(resourceFileName)));
    }

    protected Definitions getDefinitions(final String resourceName, final String namespace, final String modelName ) {
        final Definitions definitions = marshaller.unmarshal(getReader(resourceName));
        assertThat(definitions).isNotNull();
        assertThat(definitions.getNamespace()).isEqualTo(namespace);
        assertThat(definitions.getName()).isEqualTo(modelName);
        return definitions;
    }

    protected Definitions getDefinitions(final Reader resourceReader, final String namespace, final String modelName) {
        final Definitions definitions = marshaller.unmarshal(resourceReader);
        assertThat(definitions).isNotNull();
        assertThat(definitions.getNamespace()).isEqualTo(namespace);
        assertThat(definitions.getName()).isEqualTo(modelName);
        return definitions;
    }

    protected Definitions getDefinitions(final List<String> resourcesName, final String namespace, final String modelName) {
        if (resourcesName.size() < 2) {
            throw new RuntimeException("use proper method");
        }
        List<Definitions> definitionss = resourcesName.stream()
                                                      .map(this::getReader)
                                                      .map(marshaller::unmarshal)
                                                      .collect(Collectors.toList());
        assertThat(definitionss).isNotEmpty();

        final Optional<Definitions> definitions = definitionss.stream()
                                                              .filter(d -> {
                                                                  return d.getNamespace().equals(namespace) && d.getName().equals(modelName);
                                                              }).findFirst();
        assertThat(definitions.isPresent()).isTrue();
        return definitions.get();
    }
}
