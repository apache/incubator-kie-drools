/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.xml.stream.Location;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.v1_1.Artifact;
import org.kie.dmn.model.v1_1.BusinessContextElement;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.DMNElementReference;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.DRGElement;
import org.kie.dmn.model.v1_1.DecisionService;
import org.kie.dmn.model.v1_1.Definitions;
import org.kie.dmn.model.v1_1.ElementCollection;
import org.kie.dmn.model.v1_1.Import;
import org.kie.dmn.model.v1_1.ItemDefinition;
import org.kie.dmn.model.v1_1.NamedElement;
import org.kie.dmn.model.v1_1.UnaryTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DMNMarshallerTest {

    private static final Logger logger = LoggerFactory.getLogger(DMNMarshallerTest.class);

    @Test
    public void testMarshaller() throws IOException {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Loan_Prequalification_Condensed_Invalid.dmn", DMNInputRuntimeTest.class );
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ba68fb9d-7421-4f3a-a7ab-f785ea0bae6b", "Loan Prequalification Condensed" );
        assertThat( dmnModel, notNullValue() );

        final Definitions definitions = dmnModel.getDefinitions();
        assertThat( definitions, notNullValue() );

        final DMNMarshaller defaultMarshaller = DMNMarshallerFactory.newDefaultMarshaller();
        try (final Reader reader = new InputStreamReader(getClass().getResourceAsStream("Loan_Prequalification_Condensed_Invalid.dmn") )) {
            final Definitions definitionsFromDefaultMarshaller = defaultMarshaller.unmarshal(reader);
            compareDefinitions(definitions, definitionsFromDefaultMarshaller);
        }
    }

    private void compareDefinitions(final Definitions definitions1, final Definitions definitions2) {
        compareNamedElement(definitions1, definitions2);

        Assertions.assertThat(definitions1.getExpressionLanguage()).isEqualTo(definitions2.getExpressionLanguage());
        Assertions.assertThat(definitions1.getTypeLanguage()).isEqualTo(definitions2.getTypeLanguage());
        Assertions.assertThat(definitions1.getNamespace()).isEqualTo(definitions2.getNamespace());
        Assertions.assertThat(definitions1.getExporter()).isEqualTo(definitions2.getExporter());
        Assertions.assertThat(definitions1.getExporterVersion()).isEqualTo(definitions2.getExporterVersion());

        compareImportList(definitions1.getImport(), definitions2.getImport());
        compareArtifactList(definitions1.getArtifact(), definitions2.getArtifact());
        compareElementCollectionList(definitions1.getElementCollection(), definitions2.getElementCollection());
        compareDRGElementList(definitions1.getDrgElement(), definitions2.getDrgElement());
        compareBusinessContextElementList(definitions1.getBusinessContextElement(), definitions2.getBusinessContextElement());
        compareDecisionServiceList(definitions1.getDecisionService(), definitions2.getDecisionService());
        compareItemDefinitionList(definitions1.getItemDefinition(), definitions2.getItemDefinition());
    }

    private void compareItemDefinitionList(final List<ItemDefinition> itemDefinitionList1, final List<ItemDefinition> itemDefinitionList2) {
        if (itemDefinitionList1 == null || itemDefinitionList2 == null) {
            Assertions.assertThat(itemDefinitionList1).isNull();
            Assertions.assertThat(itemDefinitionList2).isNull();
        } else {
            Assertions.assertThat(itemDefinitionList1)
                    .usingElementComparator((itemDefinition1, itemDefinition2) -> {
                        try {
                            compareItemDefinition(itemDefinition1, itemDefinition2);
                            return 0;
                        } catch (AssertionError error) {
                            logger.error(error.getMessage(), error);
                            return -1;
                        }
                    }).isEqualTo(itemDefinitionList2);
        }
    }

    private void compareItemDefinition(final ItemDefinition itemDefinition1, final ItemDefinition itemDefinition2) {
        if (itemDefinition1 == null || itemDefinition2 == null) {
            Assertions.assertThat(itemDefinition1).isNull();
            Assertions.assertThat(itemDefinition2).isNull();
        } else {
            compareNamedElement(itemDefinition1, itemDefinition2);

            compareUnaryTests(itemDefinition1.getAllowedValues(), itemDefinition2.getAllowedValues());
            Assertions.assertThat(itemDefinition1.isIsCollection()).isEqualTo(itemDefinition2.isIsCollection());
            Assertions.assertThat(itemDefinition1.getTypeLanguage()).isEqualTo(itemDefinition2.getTypeLanguage());
            Assertions.assertThat(itemDefinition1.getTypeRef()).isEqualTo(itemDefinition2.getTypeRef());

            // Transitively recursive
            compareItemDefinitionList(itemDefinition1.getItemComponent(), itemDefinition2.getItemComponent());
        }
    }

    private void compareUnaryTests(final UnaryTests unaryTests1, final UnaryTests unaryTests2) {
        if (unaryTests1 == null || unaryTests2 == null) {
            Assertions.assertThat(unaryTests1).isNull();
            Assertions.assertThat(unaryTests2).isNull();
        } else {
            compareDMNElement(unaryTests1, unaryTests2);

            Assertions.assertThat(unaryTests1.getText()).isEqualTo(unaryTests2.getText());
            Assertions.assertThat(unaryTests1.getExpressionLanguage()).isEqualTo(unaryTests2.getExpressionLanguage());
        }
    }

    private void compareDecisionServiceList(final List<DecisionService> serviceList1, final List<DecisionService> serviceList2) {
        if (serviceList1 == null || serviceList2 == null) {
            Assertions.assertThat(serviceList1).isNull();
            Assertions.assertThat(serviceList2).isNull();
        } else {
            Assertions.assertThat(serviceList1)
                    .usingElementComparator((service1, service2) -> {
                        try {
                            compareDecisionService(service1, service2);
                            return 0;
                        } catch (AssertionError error) {
                            logger.error(error.getMessage(), error);
                            return -1;
                        }
                    }).isEqualTo(serviceList2);
        }
    }

    private void compareDecisionService(final DecisionService decisionService1, final DecisionService decisionService2) {
        if (decisionService1 == null || decisionService2 == null) {
            Assertions.assertThat(decisionService1).isNull();
            Assertions.assertThat(decisionService2).isNull();
        } else {
            compareNamedElement(decisionService1, decisionService2);
            compareDMNElementReferenceList(decisionService1.getInputData(), decisionService2.getInputData());
            compareDMNElementReferenceList(decisionService1.getOutputDecision(), decisionService2.getOutputDecision());
            compareDMNElementReferenceList(decisionService1.getEncapsulatedDecision(), decisionService2.getEncapsulatedDecision());
            compareDMNElementReferenceList(decisionService1.getInputDecision(), decisionService2.getInputDecision());
        }
    }

    private void compareElementCollectionList(final List<ElementCollection> elementCollectionList1, final List<ElementCollection> elementCollectionList2) {
        if (elementCollectionList1 == null || elementCollectionList2 == null) {
            Assertions.assertThat(elementCollectionList1).isNull();
            Assertions.assertThat(elementCollectionList2).isNull();
        } else {
            Assertions.assertThat(elementCollectionList1)
                    .usingElementComparator((collection1, collection2) -> {
                        try {
                            compareElementCollection(collection1, collection2);
                            return 0;
                        } catch (AssertionError error) {
                            logger.error(error.getMessage(), error);
                            return -1;
                        }
                    }).isEqualTo(elementCollectionList2);
        }
    }

    private void compareElementCollection(final ElementCollection collection1, final ElementCollection collection2) {
        if (collection1 == null || collection2 == null) {
            Assertions.assertThat(collection1).isNull();
            Assertions.assertThat(collection2).isNull();
        } else {
            compareNamedElement(collection1, collection2);
            compareDMNElementReferenceList(collection1.getDrgElement(), collection2.getDrgElement());
        }
    }

    private void compareDMNElementReferenceList(final List<DMNElementReference> referenceList1, final List<DMNElementReference> referenceList2) {
        if (referenceList1 == null || referenceList2 == null) {
            Assertions.assertThat(referenceList1).isNull();
            Assertions.assertThat(referenceList2).isNull();
        } else {
            Assertions.assertThat(referenceList1)
                    .usingElementComparator((reference1, reference2) -> {
                        try {
                            compareDMNElementReference(reference1, reference2);
                            return 0;
                        } catch (AssertionError error) {
                            logger.error(error.getMessage(), error);
                            return -1;
                        }
                    }).isEqualTo(referenceList2);
        }
    }

    private void compareDMNElementReference(final DMNElementReference reference1, final DMNElementReference reference2) {
        if (reference1 == null || reference2 == null) {
            Assertions.assertThat(reference1).isNull();
            Assertions.assertThat(reference2).isNull();
        } else {
            compareDMNModelInstrumentedBase(reference1, reference2);
            Assertions.assertThat(reference1.getHref()).isEqualTo(reference2.getHref());
        }
    }

    private void compareBusinessContextElementList(final List<BusinessContextElement> elementList1, final List<BusinessContextElement> elementList2) {
        if (elementList1 == null || elementList2 == null) {
            Assertions.assertThat(elementList1).isNull();
            Assertions.assertThat(elementList2).isNull();
        } else {
            Assertions.assertThat(elementList1)
                    .usingElementComparator((element1, element2) -> {
                        try {
                            compareBusinessContextElement(element1, element2);
                            return 0;
                        } catch (AssertionError error) {
                            logger.error(error.getMessage(), error);
                            return -1;
                        }
                    }).isEqualTo(elementList2);
        }
    }

    private void compareBusinessContextElement(final BusinessContextElement element1, final BusinessContextElement element2) {
        if (element1 == null || element2 == null) {
            Assertions.assertThat(element1).isNull();
            Assertions.assertThat(element2).isNull();
        } else {
            compareNamedElement(element1, element2);
            Assertions.assertThat(element1.getURI()).isEqualTo(element2.getURI());
        }
    }

    private void compareImportList(final List<Import> importList1, final List<Import> importList2) {
        if (importList1 == null || importList2 == null) {
            Assertions.assertThat(importList1).isNull();
            Assertions.assertThat(importList2).isNull();
        } else {
            Assertions.assertThat(importList1)
                    .usingElementComparator((import1, import2) -> {
                        try {
                            compareImport(import1, import2);
                            return 0;
                        } catch (AssertionError error) {
                            logger.error(error.getMessage(), error);
                            return -1;
                        }
                    }).isEqualTo(importList2);
        }
    }

    private void compareImport(final Import import1, final Import import2) {
        if (import1 == null || import2 == null) {
            Assertions.assertThat(import1).isNull();
            Assertions.assertThat(import2).isNull();
        } else {
            compareDMNModelInstrumentedBase(import1, import2);

            Assertions.assertThat(import1.getNamespace()).isEqualTo(import2.getNamespace());
            Assertions.assertThat(import1.getLocationURI()).isEqualTo(import2.getLocationURI());
            Assertions.assertThat(import1.getImportType()).isEqualTo(import2.getImportType());
        }
    }

    private void compareDRGElementList(final List<DRGElement> elementList1, final List<DRGElement> elementList2) {
        if (elementList1 == null || elementList2 == null) {
            Assertions.assertThat(elementList1).isNull();
            Assertions.assertThat(elementList2).isNull();
        } else {
            Assertions.assertThat(elementList1)
                    .usingElementComparator((element1, element2) -> {
                        try {
                            compareNamedElement(element1, element2);
                            return 0;
                        } catch (AssertionError error) {
                            logger.error(error.getMessage(), error);
                            return -1;
                        }
                    }).isEqualTo(elementList2);
        }
    }

    private void compareNamedElement(final NamedElement element1, final NamedElement element2) {
        if (element1 == null || element2 == null) {
            Assertions.assertThat(element1).isNull();
            Assertions.assertThat(element2).isNull();
        } else {
            compareDMNElement(element1, element2);
            Assertions.assertThat(element1.getName()).isEqualTo(element2.getName());
        }
    }

    private void compareArtifactList(final List<Artifact> artifactList1, final List<Artifact> artifactList2) {
        if (artifactList1 == null || artifactList2 == null) {
            Assertions.assertThat(artifactList1).isNull();
            Assertions.assertThat(artifactList2).isNull();
        } else {
            Assertions.assertThat(artifactList1)
                    .usingElementComparator((artifact1, artifact2) -> {
                        try {
                            compareDMNElement(artifact1, artifact2);
                            return 0;
                        } catch (AssertionError error) {
                            logger.error(error.getMessage(), error);
                            return -1;
                        }
                    }).isEqualTo(artifactList2);
        }
    }

    private void compareDMNElement(final DMNElement element1, final DMNElement element2) {
        if (element1 == null || element2 == null) {
            Assertions.assertThat(element1).isNull();
            Assertions.assertThat(element2).isNull();
        } else {
            compareDMNModelInstrumentedBase(element1, element2);

            Assertions.assertThat(element1.getId()).isEqualTo(element2.getId());
            Assertions.assertThat(element1.getLabel()).isEqualTo(element2.getLabel());
            Assertions.assertThat(element1.getDescription()).isEqualTo(element2.getDescription());

            compareExtensionElements(element1.getExtensionElements(), element2.getExtensionElements());
        }
    }

    private void compareExtensionElements(final DMNElement.ExtensionElements elements1, final DMNElement.ExtensionElements elements2) {
        if (elements1 == null || elements2 == null) {
            Assertions.assertThat(elements1).isNull();
            Assertions.assertThat(elements2).isNull();
        } else {
            compareDMNModelInstrumentedBase(elements1, elements2);
            Assertions.assertThat(elements1.getAny()).containsExactlyElementsOf(elements2.getAny());
        }
    }

    private void compareDMNModelInstrumentedBase(final DMNModelInstrumentedBase object1, final DMNModelInstrumentedBase object2) {
        if (object1 == null || object2 == null) {
            Assertions.assertThat(object1).isNull();
            Assertions.assertThat(object2).isNull();
        } else {
            if (object1.getParent() != null || object2.getParent() != null) {
                Assertions.assertThat(object1.getParent()).isNotNull();
                Assertions.assertThat(object2.getParent()).isNotNull();
                Assertions.assertThat(object1.getParent().getIdentifierString()).isEqualTo(object2.getParent().getIdentifierString());
            }

            Assertions.assertThat(object1.getNsContext()).containsAllEntriesOf(object2.getNsContext());
            Assertions.assertThat(object2.getNsContext()).containsAllEntriesOf(object1.getNsContext());
            Assertions.assertThat(object1.getAdditionalAttributes()).containsAllEntriesOf(object2.getAdditionalAttributes());
            Assertions.assertThat(object2.getAdditionalAttributes()).containsAllEntriesOf(object1.getAdditionalAttributes());

            compareLocation(object1.getLocation(), object2.getLocation());
        }
    }

    private void compareLocation(final Location location1, final Location location2) {
        if (location1 == null || location2 == null) {
            Assertions.assertThat(location1).isNull();
            Assertions.assertThat(location2).isNull();
        } else {
            Assertions.assertThat(location1.getLineNumber()).isEqualTo(location2.getLineNumber());
            Assertions.assertThat(location1.getColumnNumber()).isEqualTo(location2.getColumnNumber());
            Assertions.assertThat(location1.getCharacterOffset()).isEqualTo(location2.getCharacterOffset());
            Assertions.assertThat(location1.getPublicId()).isEqualTo(location2.getPublicId());
            Assertions.assertThat(location1.getSystemId()).isEqualTo(location2.getSystemId());
        }
    }
}
