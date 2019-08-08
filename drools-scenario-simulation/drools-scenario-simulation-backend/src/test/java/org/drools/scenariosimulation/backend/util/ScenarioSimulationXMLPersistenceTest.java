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
package org.drools.scenariosimulation.backend.util;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.drools.scenariosimulation.backend.TestUtils.getFileContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ScenarioSimulationXMLPersistenceTest {

    protected ScenarioSimulationXMLPersistence instance = ScenarioSimulationXMLPersistence.getInstance();
    protected String currentVersion = new ScenarioSimulationModel().getVersion();
    protected MigrationStrategy migrationInstance = new InMemoryMigrationStrategy();

    @Test
    public void noFQCNUsed() {
        final ScenarioSimulationModel simulationModel = new ScenarioSimulationModel();
        simulationModel.getImports().addImport(new Import("org.test.Test"));

        final String xml = instance.marshal(simulationModel);

        assertFalse(xml.contains("org.drools.scenariosimulation.api.model"));
        assertFalse(xml.contains("org.kie.soup.project.datamodel.imports"));
    }

    @Test
    public void versionAttributeExists() {
        final String xml = instance.marshal(new ScenarioSimulationModel());
        assertTrue(xml.startsWith("<ScenarioSimulationModel version=\"" + ScenarioSimulationXMLPersistence.getCurrentVersion() + "\">"));
    }

    @Test
    public void migrateIfNecessary_1_0_to_1_1() {
        try {
            String toMigrate = getFileContent("scesim-1-0.scesim");
            Document document = DOMParserUtil.getDocument(toMigrate);
            migrationInstance.from1_0to1_1().accept(document);
            Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodes(document, "expressionIdentifier", "type");
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            retrieved.forEach((node, typeNodes) -> {
                assertEquals(1, typeNodes.size());
                assertEquals("EXPECT", typeNodes.get(0).getTextContent());
            });
            commonCheckDocument(document, "1.1");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void migrateIfNecessary_1_1_to_1_2() {
        try {
            String toMigrate = getFileContent("scesim-1-1.scesim");
            Document document = DOMParserUtil.getDocument(toMigrate);
            migrationInstance.from1_1to1_2().accept(document);
            Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodes(document, "ScenarioSimulationModel", "dmoSession");
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            retrieved = DOMParserUtil.getChildrenNodes(document, "ScenarioSimulationModel", "type");
            commonVerifySingleNodeSingleChild(retrieved, "RULE");
            commonCheckDocument(document, "1.2");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void migrateIfNecessary_1_2_to_1_3() {
        try {
            String toMigrate = getFileContent("scesim-1-2.scesim");
            Document document = DOMParserUtil.getDocument(toMigrate);
            migrationInstance.from1_2to1_3().accept(document);
            Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodes(document, "factMappings", "FactMapping");
            assertNotNull(retrieved);
            assertEquals(1, retrieved.size());
            List<Node> factMappingNodes = retrieved.values().iterator().next();
            for (Node factMappingNode : factMappingNodes) {
                List<Node> expressionElementsNodes = DOMParserUtil.getChildrenNodes(factMappingNode, "expressionElements");
                assertTrue(expressionElementsNodes.size() >= 1);
            }
            commonCheckDocument(document, "1.3");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void migrateIfNecessary_1_3_to_1_4() {
        try {
            String toMigrate = getFileContent("scesim-1-3-rule.scesim");
            Document document = DOMParserUtil.getDocument(toMigrate);
            migrationInstance.from1_3to1_4().accept(document);
            Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "fileName");
            commonVerifySingleNodeSingleChild(retrieved, null);
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "kieSession");
            commonVerifySingleNodeSingleChild(retrieved, "default");
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "kieBase");
            commonVerifySingleNodeSingleChild(retrieved, "default");
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "ruleFlowGroup");
            commonVerifySingleNodeSingleChild(retrieved, "default");
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "dmoSession");
            commonVerifySingleNodeSingleChild(retrieved, "default");
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "skipFromBuild");
            commonVerifySingleNodeSingleChild(retrieved, "false");
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "type");
            commonVerifySingleNodeSingleChild(retrieved, "RULE");
            commonCheckDocument(document, "1.4");

            toMigrate = getFileContent("scesim-1-3-dmn_1.scesim");
            document = DOMParserUtil.getDocument(toMigrate);
            migrationInstance.from1_3to1_4().accept(document);
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "dmnNamespace");
            commonVerifySingleNodeSingleChild(retrieved, null);
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "dmnName");
            commonVerifySingleNodeSingleChild(retrieved, null);
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "skipFromBuild");
            commonVerifySingleNodeSingleChild(retrieved, "false");
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "type");
            commonVerifySingleNodeSingleChild(retrieved, "DMN");
            commonCheckDocument(document, "1.4");

            toMigrate = getFileContent("scesim-1-3-dmn_2.scesim");
            document = DOMParserUtil.getDocument(toMigrate);
            migrationInstance.from1_3to1_4().accept(document);
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "dmnNamespace");
            commonVerifySingleNodeSingleChild(retrieved, null);
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "dmnName");
            commonVerifySingleNodeSingleChild(retrieved, null);
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "skipFromBuild");
            commonVerifySingleNodeSingleChild(retrieved, "false");
            retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "type");
            commonVerifySingleNodeSingleChild(retrieved, "DMN");
            commonCheckDocument(document, "1.4");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void migrateIfNecessary_1_4_to_1_5() {
        try {
            String toMigrate = getFileContent("scesim-1-4-rule.scesim");
            Document document = DOMParserUtil.getDocument(toMigrate);
            migrationInstance.from1_4to1_5().accept(document);
            Map<Node, List<Node>> retrieved = DOMParserUtil.getChildrenNodes(document, "simulationDescriptor", "dmoSession");
            assertNotNull(retrieved);
            assertTrue(retrieved.isEmpty());
            commonCheckDocument(document, "1.5");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void migrateIfNecessary_1_5_to_1_6() {
        try {
            String toMigrate = getFileContent("scesim-1-5-dmn.scesim");
            Document document = DOMParserUtil.getDocument(toMigrate);
            migrationInstance.from1_5to1_6().accept(document);
            Map<Node, String> retrieved = DOMParserUtil.getAttributeValues(document, "reference");
            assertNotNull(retrieved);
            assertTrue(retrieved.isEmpty());
            commonCheckDocument(document, "1.6");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void migrateIfNecessary() {
        Assertions.assertThatThrownBy(() -> instance.migrateIfNecessary("<ScenarioSimulationModel version=\"9999999999.99999999999\" />"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Version 9999999999.99999999999 of the file is not supported. Current version is " + ScenarioSimulationXMLPersistence.getCurrentVersion());

        String noMigrationNeeded = "<ScenarioSimulationModel version=\"" + currentVersion + "\" />";
        try {
            String afterMigration = instance.migrateIfNecessary(noMigrationNeeded);
            Document document = DOMParserUtil.getDocument(afterMigration);
            commonCheckVersion(document, "1.6");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void extractVersion() {
        String version = instance.extractVersion("<ScenarioSimulationModel version=\"1.0\" version=\"1.1\">");
        assertEquals("1.0", version);
    }

    @Test
    public void unmarshalRULE() throws Exception {
        String toUnmarshal = getFileContent("scesim-rule.scesim");
        final ScenarioSimulationModel retrieved = ScenarioSimulationXMLPersistence.getInstance().unmarshal(toUnmarshal);
        assertEquals(retrieved.getSimulation().getSimulationDescriptor().getType(), ScenarioSimulationModel.Type.RULE);
        commonCheckScenarioSimulationModel(retrieved);
    }

    @Test
    public void unmarshalDMN() throws Exception {
        String toUnmarshal = getFileContent("scesim-dmn.scesim");
        final ScenarioSimulationModel retrieved = ScenarioSimulationXMLPersistence.getInstance().unmarshal(toUnmarshal);
        assertEquals(retrieved.getSimulation().getSimulationDescriptor().getType(), ScenarioSimulationModel.Type.DMN);
        commonCheckScenarioSimulationModel(retrieved);
    }

    /**
     * Verify the given <code>Map</code> has one single entry, whose <code>List</code> value also has a single children.
     * If <b>expectedTextContent</b> is given, it also check the children text content match
     * @param toCheck
     * @param expectedTextContent
     */
    private void commonVerifySingleNodeSingleChild(Map<Node, List<Node>> toCheck, String expectedTextContent) {
        assertNotNull(toCheck);
        assertEquals(1, toCheck.size());
        assertNotNull(toCheck.values());
        assertEquals(1, toCheck.values().iterator().next().size());
        if (expectedTextContent != null) {
            assertEquals(expectedTextContent, toCheck.values().iterator().next().get(0).getTextContent());
        }
    }

    private void commonCheckDocument(Document document, String expectedVersion) throws Exception {
        commonCheckVersion(document, expectedVersion);
        commonCheckScenarioSimulationModel(document);
    }

    private void commonCheckVersion(Document document, String expectedVersion) {
        final Map<Node, String> attributeValues = DOMParserUtil.getAttributeValues(document, "ScenarioSimulationModel", "version");
        assertNotNull(attributeValues);
        assertEquals(1, attributeValues.size());
        assertEquals(expectedVersion, (String) attributeValues.values().toArray()[0]);
    }

    private void commonCheckScenarioSimulationModel(Document toCheck) throws Exception {
        String migrated = DOMParserUtil.getString(toCheck);
        ScenarioSimulationModel scenarioSimulationModel = instance.internalUnmarshal(migrated);
        commonCheckScenarioSimulationModel(scenarioSimulationModel);
    }

    private void commonCheckScenarioSimulationModel(ScenarioSimulationModel toCheck) throws Exception {
        assertNotNull(toCheck);
        assertNotNull(toCheck.getSimulation());
        assertNotNull(toCheck.getSimulation().getSimulationDescriptor());
        toCheck.getSimulation().getUnmodifiableScenarios().forEach(scenario -> {
            scenario.getUnmodifiableFactMappingValues().forEach(factMappingValue -> {
//                assertNotNull(factMappingValue.getFactIdentifier());
//                assertNotNull(factMappingValue.getExpressionIdentifier());
            });
        });
    }
}
