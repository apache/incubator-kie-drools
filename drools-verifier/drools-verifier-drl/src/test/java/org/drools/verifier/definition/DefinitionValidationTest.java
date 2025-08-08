/*
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
package org.drools.verifier.definition;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.io.ClassPathResource;
import org.drools.verifier.TestBase;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierConfiguration;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.components.Definition;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DefinitionValidationTest extends TestBase {

    @Test
    void testDefinitionsAreCreated() throws Exception {
        PackageDescr packageDescr = getPackageDescr(
            getClass().getResourceAsStream("DefinitionValidationTest.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<VerifierComponent> definitions = verifierData.getAll(VerifierComponentType.DEFINITION);

        assertThat(definitions).hasSize(3);

        Set<String> definitionNames = new HashSet<>();
        for (VerifierComponent component : definitions) {
            Definition definition = (Definition) component;
            definitionNames.add(definition.getTypeName());
        }

        assertThat(definitionNames).containsExactlyInAnyOrder("Person", "Address", "Vehicle");
    }

    @Test
    void testDefinitionPropertiesAreCorrect() throws Exception {
        PackageDescr packageDescr = getPackageDescr(
            getClass().getResourceAsStream("DefinitionValidationTest.drl"));

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<VerifierComponent> definitions = verifierData.getAll(VerifierComponentType.DEFINITION);
        Definition personDefinition = null;
        Definition addressDefinition = null;
        Definition vehicleDefinition = null;

        for (VerifierComponent component : definitions) {
            Definition definition = (Definition) component;
            if ("Person".equals(definition.getTypeName())) {
                personDefinition = definition;
            } else if ("Address".equals(definition.getTypeName())) {
                addressDefinition = definition;
            } else if ("Vehicle".equals(definition.getTypeName())) {
                vehicleDefinition = definition;
            }
        }

        assertThat(personDefinition).isNotNull();
        assertThat(personDefinition.getPackageName()).isEqualTo("org.drools.verifier.definition");
        assertThat(personDefinition.getPath()).isEqualTo("package[@name='org.drools.verifier.definition']/definition[@name='Person']");

        assertThat(addressDefinition).isNotNull();
        assertThat(vehicleDefinition).isNotNull();
    }

    @Test
    void testAllFactTypesHaveDefinitions() throws Exception {
        PackageDescr packageDescr = getPackageDescr(
            getClass().getResourceAsStream("DefinitionValidationTest.drl"));

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<VerifierComponent> definitions = verifierData.getAll(VerifierComponentType.DEFINITION);
        Collection<VerifierComponent> objectTypes = verifierData.getAll(VerifierComponentType.OBJECT_TYPE);

        Set<String> definedTypes = new HashSet<>();
        for (VerifierComponent component : definitions) {
            Definition definition = (Definition) component;
            definedTypes.add(definition.getTypeName());
        }

        Set<String> usedTypes = new HashSet<>();
        for (VerifierComponent component : objectTypes) {
            ObjectType objectType = (ObjectType) component;
            usedTypes.add(objectType.getName());
        }

        for (String usedType : usedTypes) {
            assertThat(definedTypes)
                .as("All fact types used in rules should have corresponding definitions. Missing definition for: " + usedType)
                .contains(usedType);
        }
    }

    @Test
    void testMissingDefinitionsDetection() throws Exception {
        PackageDescr packageDescr = getPackageDescr(
            getClass().getResourceAsStream("MissingDefinitionTest.drl"));

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<VerifierComponent> definitions = verifierData.getAll(VerifierComponentType.DEFINITION);
        Collection<VerifierComponent> objectTypes = verifierData.getAll(VerifierComponentType.OBJECT_TYPE);

        Set<String> definedTypes = new HashSet<>();
        for (VerifierComponent component : definitions) {
            Definition definition = (Definition) component;
            definedTypes.add(definition.getTypeName());
        }

        Set<String> usedTypes = new HashSet<>();
        for (VerifierComponent component : objectTypes) {
            ObjectType objectType = (ObjectType) component;
            usedTypes.add(objectType.getName());
        }

        assertThat(definedTypes).containsExactly("Person");
        assertThat(usedTypes).containsExactlyInAnyOrder("Person", "Employee", "Vehicle");

        Set<String> missingDefinitions = new HashSet<>(usedTypes);
        missingDefinitions.removeAll(definedTypes);

        assertThat(missingDefinitions)
            .as("Should detect missing definitions for Employee and Vehicle")
            .containsExactlyInAnyOrder("Employee", "Vehicle");
    }

    @Test
    void testDefinitionPathsAreUnique() throws Exception {
        PackageDescr packageDescr = getPackageDescr(
            getClass().getResourceAsStream("DefinitionValidationTest.drl"));

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<VerifierComponent> allComponents = verifierData.getAll();
        Set<String> paths = new HashSet<>();

        for (VerifierComponent component : allComponents) {
            String path = component.getPath();
            assertThat(paths)
                .as("Duplicate path found: " + path)
                .doesNotContain(path);
            paths.add(path);
        }
    }

    @Test
    void testDefinitionComponentsAreCreatedWithVerifier() throws Exception {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Check that the builder works
        assertThat(vBuilder.hasErrors()).isFalse();
        assertThat(vBuilder.getErrors().size()).isEqualTo(0);

        // Use the existing VerifyingScope.drl which has the global result already set up
        vConfiguration.getVerifyingResources().put(
            new ClassPathResource("VerifyingScope.drl", 
                                org.drools.verifier.Verifier.class),
            ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vConfiguration);

        // Add the DRL file with complete definitions to verify
        verifier.addResourcesToVerify(
            new ClassPathResource("DefinitionValidationTest.drl",
                                getClass()),
            ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();
        assertThat(verifier.getErrors().size()).isEqualTo(0);

        // Fire analysis 
        boolean works = verifier.fireAnalysis(new ScopesAgendaFilter(true,
                ScopesAgendaFilter.VERIFYING_SCOPE_SINGLE_RULE));

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();

        // Verify that Definition components were created in the VerifierData
        Collection<VerifierComponent> definitions = result.getVerifierData().getAll(VerifierComponentType.DEFINITION);
        assertThat(definitions).hasSize(3);

        Set<String> definitionNames = new HashSet<>();
        for (VerifierComponent component : definitions) {
            Definition definition = (Definition) component;
            definitionNames.add(definition.getTypeName());
        }

        assertThat(definitionNames).containsExactlyInAnyOrder("Person", "Address", "Vehicle");
        
        // Verify that definitions track their declared fields
        for (VerifierComponent component : definitions) {
            Definition definition = (Definition) component;
            assertThat(definition.getDeclaredFields()).isNotEmpty();
            
            if ("Person".equals(definition.getTypeName())) {
                assertThat(definition.hasField("name")).isTrue();
                assertThat(definition.hasField("age")).isTrue();
                assertThat(definition.hasField("address")).isTrue();
                assertThat(definition.getFieldNames()).containsExactlyInAnyOrder("name", "age", "address");
            }
        }
    }

    @Test
    void testMissingDefinitionDetectionWithVerifier() throws Exception {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();

        // Use the existing VerifyingScope.drl
        vConfiguration.getVerifyingResources().put(
            new ClassPathResource("VerifyingScope.drl", 
                                org.drools.verifier.Verifier.class),
            ResourceType.DRL);

        Verifier verifier = vBuilder.newVerifier(vConfiguration);

        // Add the DRL file with missing definitions
        verifier.addResourcesToVerify(
            new ClassPathResource("MissingDefinitionTest.drl",
                                getClass()),
            ResourceType.DRL);

        assertThat(verifier.hasErrors()).isFalse();

        boolean works = verifier.fireAnalysis(new ScopesAgendaFilter(true,
                ScopesAgendaFilter.VERIFYING_SCOPE_SINGLE_RULE));

        assertThat(works).isTrue();

        VerifierReport result = verifier.getResult();
        assertThat(result).isNotNull();

        // Verify that we can detect the mismatch - only Person has a definition
        Collection<VerifierComponent> definitions = result.getVerifierData().getAll(VerifierComponentType.DEFINITION);
        Collection<VerifierComponent> objectTypes = result.getVerifierData().getAll(VerifierComponentType.OBJECT_TYPE);

        Set<String> definedTypes = new HashSet<>();
        for (VerifierComponent component : definitions) {
            Definition definition = (Definition) component;
            definedTypes.add(definition.getTypeName());
        }

        Set<String> usedTypes = new HashSet<>();
        for (VerifierComponent component : objectTypes) {
            ObjectType objectType = (ObjectType) component;
            usedTypes.add(objectType.getName());
        }

        // Only Person has a definition
        assertThat(definedTypes).containsExactly("Person");
        
        // But we use Person, Employee, and Vehicle in rules
        assertThat(usedTypes).containsExactlyInAnyOrder("Person", "Employee", "Vehicle");

        // Calculate missing definitions
        Set<String> missingDefinitions = new HashSet<>(usedTypes);
        missingDefinitions.removeAll(definedTypes);

        assertThat(missingDefinitions)
            .as("Should detect missing definitions for Employee and Vehicle")
            .containsExactlyInAnyOrder("Employee", "Vehicle");
    }
}