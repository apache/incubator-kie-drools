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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.assertj.core.api.Assertions;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;

import static org.drools.scenariosimulation.backend.TestUtils.getFileContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ScenarioSimulationXMLPersistenceTest {

    ScenarioSimulationXMLPersistence instance = ScenarioSimulationXMLPersistence.getInstance();

    @Test
    public void noFQCNUsed() throws Exception {
        final ScenarioSimulationModel simulationModel = new ScenarioSimulationModel();
        simulationModel.getImports().addImport(new Import("org.test.Test"));

        final String xml = instance.marshal(simulationModel);

        assertFalse(xml.contains("org.drools.scenariosimulation.api.model"));
        assertFalse(xml.contains("org.kie.soup.project.datamodel.imports"));
    }

    @Test
    public void versionAttributeExists() throws Exception {
        final String xml = instance.marshal(new ScenarioSimulationModel());
        assertTrue(xml.startsWith("<ScenarioSimulationModel version=\"" + ScenarioSimulationXMLPersistence.getCurrentVersion() + "\">"));
    }

    @Test
    public void migrateIfNecessary_1_0_to_1_1() throws Exception {
        String toMigrate = getFileContent("scesim-1-0.scesim");
        String migrated = instance.migrateIfNecessary(toMigrate);
        assertTrue(toMigrate.contains("<ScenarioSimulationModel version=\"1.0\">"));
        assertFalse(migrated.contains("<ScenarioSimulationModel version=\"1.0\">"));
        assertTrue(migrated.contains("EXPECT"));
        assertFalse(migrated.contains("EXPECTED"));
    }

    @Test
    public void migrateIfNecessary_1_1_to_1_2() throws Exception {
        String toMigrate = getFileContent("scesim-1-1.scesim");
        String migrated = instance.migrateIfNecessary(toMigrate);
        assertTrue(toMigrate.contains("<ScenarioSimulationModel version=\"1.1\">"));
        assertFalse(migrated.contains("<ScenarioSimulationModel version=\"1.1\">"));
        assertTrue(migrated.contains("dmoSession></dmoSession>"));
        assertTrue(migrated.contains("<type>RULE</type>"));
    }

    @Test
    public void migrateIfNecessary_1_2_to_1_3() throws Exception {
        String toMigrate = getFileContent("scesim-1-2.scesim");
        String migrated = instance.migrateIfNecessary(toMigrate);
        assertTrue(toMigrate.contains("<ScenarioSimulationModel version=\"1.2\">"));
        assertFalse(migrated.contains("<ScenarioSimulationModel version=\"1.2\">"));
        try {
            ScenarioSimulationModel unmarshalled = instance.unmarshal(migrated, false);
            for (FactMapping factMapping : unmarshalled.getSimulation().getSimulationDescriptor().getUnmodifiableFactMappings()) {
                assertTrue(factMapping.getExpressionElements().size() >= 1);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void migrateIfNecessary_1_3_to_1_4() throws Exception {
        String toMigrate = getFileContent("scesim-1-3-rule.scesim");
        String migrated = instance.migrateIfNecessary(toMigrate);
        assertTrue(toMigrate.contains("<ScenarioSimulationModel version=\"1.3\">"));
        assertFalse(migrated.contains("<ScenarioSimulationModel version=\"1.3\">"));
        assertTrue(migrated.contains("<ScenarioSimulationModel version=\"1.4\">"));
        assertTrue(migrated.contains("<fileName></fileName>"));
        assertTrue(migrated.contains("<kieSession>default</kieSession>"));
        assertTrue(migrated.contains("<kieBase>default</kieBase>"));
        assertTrue(migrated.contains("<ruleFlowGroup>default</ruleFlowGroup>"));
        assertTrue(migrated.contains("<dmoSession></dmoSession>"));
        assertTrue(migrated.contains("<skipFromBuild>false</skipFromBuild>"));
        assertTrue(migrated.contains("<type>RULE</type>"));
        try {
            instance.internalUnmarshal(migrated);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        toMigrate = getFileContent("scesim-1-3-dmn.scesim");
        migrated = instance.migrateIfNecessary(toMigrate);
        assertTrue(toMigrate.contains("<ScenarioSimulationModel version=\"1.3\">"));
        assertFalse(migrated.contains("<ScenarioSimulationModel version=\"1.3\">"));
        assertTrue(migrated.contains("<ScenarioSimulationModel version=\"1.4\">"));
        assertTrue(migrated.contains("<dmnNamespace></dmnNamespace>"));
        assertTrue(migrated.contains("<dmnName></dmnName>"));
        assertTrue(migrated.contains("<skipFromBuild>false</skipFromBuild>"));
        assertTrue(migrated.contains("<type>DMN</type>"));
        try {
            instance.internalUnmarshal(migrated);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void migrateIfNecessary() {
        Assertions.assertThatThrownBy(() -> instance.migrateIfNecessary("<ScenarioSimulationModel version=\"9999999999.99999999999\">"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Version 9999999999.99999999999 of the file is not supported. Current version is " + ScenarioSimulationXMLPersistence.getCurrentVersion());

        String noMigrationNeeded = "<ScenarioSimulationModel version=\"" + ScenarioSimulationXMLPersistence.getCurrentVersion() + "\">";
        String afterMigration = instance.migrateIfNecessary(noMigrationNeeded);
        assertEquals(noMigrationNeeded, afterMigration);
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
        assertNotNull(retrieved.getSimulation().getSimulationDescriptor().getDmoSession());
        assertNull(retrieved.getSimulation().getSimulationDescriptor().getDmnFilePath());
    }

    @Test
    public void unmarshalDMN() throws Exception {
        String toUnmarshal = getFileContent("scesim-dmn.scesim");
        final ScenarioSimulationModel retrieved = ScenarioSimulationXMLPersistence.getInstance().unmarshal(toUnmarshal);
        assertEquals(retrieved.getSimulation().getSimulationDescriptor().getType(), ScenarioSimulationModel.Type.DMN);
        assertNotNull(retrieved.getSimulation().getSimulationDescriptor().getDmnFilePath());
        assertNull(retrieved.getSimulation().getSimulationDescriptor().getDmoSession());
    }

}
