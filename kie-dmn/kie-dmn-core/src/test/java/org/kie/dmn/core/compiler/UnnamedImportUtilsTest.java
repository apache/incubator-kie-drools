package org.kie.dmn.core.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.util.FileUtils;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.NamedElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.addIfNotPresent;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.isInUnnamedImport;

public class UnnamedImportUtilsTest {

    @Test
    public void isInUnnamedImportTrue() {
        File importingModelFile = FileUtils.getFile("Importing_EmptyNamed_Model.dmn");
        assertThat(importingModelFile).isNotNull().exists();
        File importedModelFile = FileUtils.getFile("Imported_Model_Unamed.dmn");
        assertThat(importedModelFile).isNotNull().exists();
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources(importingModelFile,
                                                                                       importedModelFile);

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df36",
                                                        "Imported Model");
        assertThat(importedModel).isNotNull();
        final DMNModelImpl importingModel = (DMNModelImpl)runtime.getModel("http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc",
                                                             "Importing empty-named Model");
        assertThat(importingModel).isNotNull();
        importedModel.getDecisions().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
        importedModel.getBusinessKnowledgeModels().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
        importedModel.getDecisionServices().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
        importedModel.getInputs().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
        importedModel.getItemDefinitions().forEach(node -> assertTrue(isInUnnamedImport(node, importingModel)));
    }

    @Test
    public void isInUnnamedImportFalse() {
        File importingModelFile = FileUtils.getFile("Importing_Named_Model.dmn");
        assertThat(importingModelFile).isNotNull().exists();
        File importedModelFile = FileUtils.getFile("Imported_Model_Unamed.dmn");
        assertThat(importedModelFile).isNotNull().exists();
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources(importingModelFile,
                                                                                       importedModelFile);

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df36",
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
        File importedModelFile = FileUtils.getFile("Imported_Model_Unamed.dmn");
        assertThat(importedModelFile).isNotNull().exists();

        String xml = new String(Files.readAllBytes(Paths.get(importedModelFile.toURI())));
        Definitions definitions = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(xml);
        definitions.getDecisionService().forEach(definition ->  assertTrue(added(definition)));
        definitions.getBusinessContextElement().forEach(definition ->  assertTrue(added(definition)));
        definitions.getDrgElement().forEach(definition ->  assertTrue(added(definition)));
        definitions.getImport().forEach(definition ->  assertTrue(added(definition)));
        definitions.getItemDefinition().forEach(definition ->  assertTrue(added(definition)));
    }

    @Test
    public void addIfNotPresentFalse() throws IOException {
        File importingModelFile = FileUtils.getFile("Importing_OverridingEmptyNamed_Model.dmn");
        assertThat(importingModelFile).isNotNull().exists();
        File importedModelFile = FileUtils.getFile("Imported_Model_Unamed.dmn");
        assertThat(importedModelFile).isNotNull().exists();
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources(importingModelFile,
                                                                                       importedModelFile);

        final DMNModelImpl importingModel = (DMNModelImpl)runtime.getModel("http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc",
                                                                           "Importing empty-named Model");
        assertThat(importingModel).isNotNull();

        Definitions importingDefinitions = importingModel.getDefinitions();

        String importedXml = new String(Files.readAllBytes(Paths.get(importedModelFile.toURI())));
        Definitions importedDefinitions = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(importedXml);
        importedDefinitions.getDecisionService().forEach(definition ->  assertFalse(added(importingDefinitions.getDecisionService(), definition)));
        importedDefinitions.getBusinessContextElement().forEach(definition ->  assertFalse(added(importingDefinitions.getBusinessContextElement(), definition)));
        importedDefinitions.getDrgElement().forEach(definition -> assertFalse(added(importingDefinitions.getDrgElement(), definition)));
        importedDefinitions.getImport().forEach(definition ->  assertFalse(added(importingDefinitions.getImport(), definition)));
        importedDefinitions.getItemDefinition().forEach(definition ->  assertFalse(added(importingDefinitions.getItemDefinition(), definition)));
    }

    private  <T extends NamedElement> boolean added(T source) {
        return added(new ArrayList<>(), source);
    }

    private  <T extends NamedElement> boolean added(Collection<T> target, T source) {
        addIfNotPresent(target, source);
        return target.contains(source);
    }

}