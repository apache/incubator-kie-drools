package org.kie.dmn.core.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.NamedElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.addIfNotPresent;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.isInUnnamedImport;

public class UnnamedImportUtilsTest {

    @Test
    public void isInUnnamedImportTrueWithHrefNamespace() {
        commonIsInUnnamedImportTrue("valid_models/DMNv1_5/Importing_EmptyNamed_Model_With_Href_Namespace.dmn",
                                    "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    @Test
    public void isInUnnamedImportTrueWithoutHrefNamespace() {
        commonIsInUnnamedImportTrue("valid_models/DMNv1_5/Importing_EmptyNamed_Model_Without_Href_Namespace.dmn",
                                    "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    @Test
    public void isInUnnamedImportFalse() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("valid_models/DMNv1_5/Importing_Named_Model.dmn",
                                                                                       this.getClass(),
                                                                                       "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df44",
                                                        "Imported Model");
        assertThat(importedModel).isNotNull();
        final DMNModelImpl importingModel = (DMNModelImpl)runtime.getModel("http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc",
                                                                           "Importing named Model");
        assertThat(importingModel).isNotNull();
        importedModel.getDecisions().forEach(node -> assertFalse(isInUnnamedImport(node, importingModel)));
        importedModel.getBusinessKnowledgeModels().forEach(node -> assertFalse(isInUnnamedImport(node, importingModel)));
        importedModel.getDecisionServices().forEach(node -> assertFalse(isInUnnamedImport(node, importingModel)));
        importedModel.getInputs().forEach(node -> assertFalse(isInUnnamedImport(node, importingModel)));
        importedModel.getItemDefinitions().forEach(node -> assertFalse(isInUnnamedImport(node, importingModel)));
    }

    @Test
    public void addIfNotPresentTrue() throws IOException {
        URL importedModelFileResource = Thread.currentThread().getContextClassLoader().getResource(
                "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
        assertNotNull(importedModelFileResource);
        try (InputStream is = importedModelFileResource.openStream()) {
            String xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Definitions definitions = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(xml);
            definitions.getDecisionService().forEach(definition ->  assertTrue(added(definition)));
            definitions.getBusinessContextElement().forEach(definition ->  assertTrue(added(definition)));
            definitions.getDrgElement().forEach(definition ->  assertTrue(added(definition)));
            definitions.getImport().forEach(definition ->  assertTrue(added(definition)));
            definitions.getItemDefinition().forEach(definition ->  assertTrue(added(definition)));
        }
    }

    @Test
    public void addIfNotPresentFalseWithHrefNamespace() throws IOException {
        commonAddIfNotPresentFalse("valid_models/DMNv1_5/Importing_EmptyNamed_Model_With_Href_Namespace.dmn",
                                    "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    @Test
    public void addIfNotPresentFalseWithoutHrefNamespace() throws IOException {
        commonAddIfNotPresentFalse("valid_models/DMNv1_5/Importing_EmptyNamed_Model_Without_Href_Namespace.dmn",
                                    "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    private void commonIsInUnnamedImportTrue(String importingModelRef, String importedModelRef) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources(importingModelRef,
                                                                                       this.getClass(),
                                                                                       importedModelRef);

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f" +
                                                                "-9848-11ba35e0df44",
                                                        "Imported Model");
        assertThat(importedModel).isNotNull();
        final DMNModelImpl importingModel = (DMNModelImpl) runtime.getModel("http://www.trisotech" +
                                                                                    ".com/dmn/definitions/_f79aa7a4" +
                                                                                    "-f9a3-410a-ac95-bea496edabgc",
                                                                            "Importing empty-named Model");
        assertThat(importingModel).isNotNull();
        importedModel.getDecisions().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
        importedModel.getBusinessKnowledgeModels().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
        importedModel.getDecisionServices().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
        importedModel.getInputs().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
        importedModel.getItemDefinitions().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
    }

    private void commonAddIfNotPresentFalse(String importingModelRef, String importedModelRef) throws IOException {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources(importingModelRef,
                                                                                       this.getClass(),
                                                                                       importedModelRef);
        final DMNModelImpl importingModel = (DMNModelImpl)runtime.getModel("http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc",
                                                                           "Importing empty-named Model");
        assertThat(importingModel).isNotNull();

        Definitions importingDefinitions = importingModel.getDefinitions();
        URL importedModelFileResource = Thread.currentThread().getContextClassLoader().getResource(
                "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
        assertNotNull(importedModelFileResource);
        try (InputStream is = importedModelFileResource.openStream()) {
            String importedXml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Definitions importedDefinitions = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(importedXml);
            importedDefinitions.getDecisionService().forEach(definition -> assertFalse(added(importingDefinitions.getDecisionService(), definition)));
            importedDefinitions.getBusinessContextElement().forEach(definition -> assertFalse(added(importingDefinitions.getBusinessContextElement(), definition)));
            importedDefinitions.getDrgElement().forEach(definition -> assertFalse(added(importingDefinitions.getDrgElement(), definition)));
            importedDefinitions.getImport().forEach(definition -> assertFalse(added(importingDefinitions.getImport(),
                                                                                    definition)));
            importedDefinitions.getItemDefinition().forEach(definition -> assertFalse(added(importingDefinitions.getItemDefinition(), definition)));
        }
    }

    private  <T extends NamedElement> boolean added(T source) {
        return added(new ArrayList<>(), source);
    }

    private  <T extends NamedElement> boolean added(Collection<T> target, T source) {
        addIfNotPresent(target, source);
        return target.contains(source);
    }

}