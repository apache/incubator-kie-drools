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
package org.kie.dmn.model.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.model.api.Artifact;
import org.kie.dmn.model.api.BusinessContextElement;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.ElementCollection;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.v1_6.TDecision;
import org.kie.dmn.model.v1_6.TDecisionService;
import org.kie.dmn.model.v1_6.TDefinitions;
import org.kie.dmn.model.v1_6.TImport;
import org.kie.dmn.model.v1_6.TItemDefinition;
import org.kie.dmn.model.v1_6.TTextAnnotation;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractTDefinitionsTest {

    @Test
    void getImportInitializesListWhenNull() {
        Definitions definitions = new TDefinitions();
        
        List<Import> imports = definitions.getImport();
        
        assertThat(imports).isNotNull();
        assertThat(imports).isEmpty();
    }

    @Test
    void getImportAllowsAddingElements() {
        Definitions definitions = new TDefinitions();
        
        Import import1 = new TImport();
        import1.setNamespace("http://example.com/ns1");
        definitions.getImport().add(import1);
        
        assertThat(definitions.getImport()).hasSize(1);
        assertThat(definitions.getImport()).contains(import1);
    }

    @Test
    void getItemDefinitionInitializesListWhenNull() {
        Definitions definitions = new TDefinitions();
        
        List<ItemDefinition> itemDefinitions = definitions.getItemDefinition();
        
        assertThat(itemDefinitions).isNotNull();
        assertThat(itemDefinitions).isEmpty();
    }

    @Test
    void getItemDefinitionAllowsAddingElements() {
        Definitions definitions = new TDefinitions();
        
        ItemDefinition item = new TItemDefinition();
        item.setName("CustomType");
        definitions.getItemDefinition().add(item);
        
        assertThat(definitions.getItemDefinition()).hasSize(1);
        assertThat(definitions.getItemDefinition()).contains(item);
    }

    @Test
    void getDrgElementInitializesListWhenNull() {
        Definitions definitions = new TDefinitions();
        
        List<DRGElement> drgElements = definitions.getDrgElement();
        
        assertThat(drgElements).isNotNull();
        assertThat(drgElements).isEmpty();
    }

    @Test
    void getDrgElementAllowsAddingElements() {
        Definitions definitions = new TDefinitions();
        
        DRGElement decision = new TDecision();
        decision.setName("Decision1");
        definitions.getDrgElement().add(decision);
        
        assertThat(definitions.getDrgElement()).hasSize(1);
        assertThat(definitions.getDrgElement()).contains(decision);
    }

    @Test
    void getArtifactInitializesListWhenNull() {
        Definitions definitions = new TDefinitions();
        
        List<Artifact> artifacts = definitions.getArtifact();
        
        assertThat(artifacts).isNotNull();
        assertThat(artifacts).isEmpty();
    }

    @Test
    void getArtifactAllowsAddingElements() {
        Definitions definitions = new TDefinitions();
        
        TTextAnnotation annotation = new TTextAnnotation();
        annotation.setText("Test annotation");
        definitions.getArtifact().add(annotation);
        
        assertThat(definitions.getArtifact()).hasSize(1);
        assertThat(definitions.getArtifact()).contains(annotation);
    }

    @Test
    void getElementCollectionInitializesListWhenNull() {
        Definitions definitions = new TDefinitions();
        
        List<ElementCollection> collections = definitions.getElementCollection();
        
        assertThat(collections).isNotNull();
        assertThat(collections).isEmpty();
    }

    @Test
    void getBusinessContextElementInitializesListWhenNull() {
        Definitions definitions = new TDefinitions();
        
        List<BusinessContextElement> elements = definitions.getBusinessContextElement();
        
        assertThat(elements).isNotNull();
        assertThat(elements).isEmpty();
    }

    @Test
    void getDecisionServiceWithNullDrgElement() {
        Definitions definitions = new TDefinitions();
        
        List<DecisionService> decisionServices = definitions.getDecisionService();
        
        assertThat(decisionServices).isNotNull();
        assertThat(decisionServices).isEmpty();
    }

    @Test
    void getDecisionServiceWithDecisionServices() {
        Definitions definitions = new TDefinitions();
        
        DecisionService decisionService1 = new TDecisionService();
        decisionService1.setName("Service1");
        definitions.getDrgElement().add(decisionService1);
        
        DecisionService decisionService2 = new TDecisionService();
        decisionService2.setName("Service2");
        definitions.getDrgElement().add(decisionService2);
        
        List<DecisionService> decisionServices = definitions.getDecisionService();
        
        assertThat(decisionServices).isNotNull();
        assertThat(decisionServices).hasSize(2);
        assertThat(decisionServices).containsExactly(decisionService1, decisionService2);
    }

    @Test
    void getDecisionServiceWithMixedDrgElements() {
        Definitions definitions = new TDefinitions();
        
        DecisionService decisionService = new TDecisionService();
        decisionService.setName("Service1");
        definitions.getDrgElement().add(decisionService);
        
        // Add other DRGElement types (TDecision is not a DecisionService)
        DRGElement decision = new TDecision();
        decision.setName("Decision1");
        definitions.getDrgElement().add(decision);
        
        DecisionService decisionService2 = new TDecisionService();
        decisionService2.setName("Service2");
        definitions.getDrgElement().add(decisionService2);
        
        List<DecisionService> decisionServices = definitions.getDecisionService();
        
        assertThat(decisionServices).isNotNull();
        assertThat(decisionServices).hasSize(2);
        assertThat(decisionServices).containsExactly(decisionService, decisionService2);
    }
}