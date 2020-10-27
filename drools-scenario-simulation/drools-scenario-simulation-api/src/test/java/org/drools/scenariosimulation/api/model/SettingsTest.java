/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.scenariosimulation.api.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SettingsTest {

    private static final String DMO_SESSION = "dmoSession";
    private static final String DMN_PATH = "dmnfile.dmn";
    private static final String DMN_NAME = "dmnName";
    private static final String DMN_NAMESPACE = "dmnNameSpace";
    private static final String RULE_FLOW_GROUP = "ruleFlowGroup";
    private static final String FILENAME = "fileName";
    private static final String KIE_BASE = "kieBase";
    private static final String KIE_SESSION = "kieSession";

    private Settings settings;

    @Before
    public void setup() {
        settings = new Settings();
        settings.setDmoSession(DMO_SESSION);
        settings.setDmnFilePath(DMN_PATH);
        settings.setDmnName(DMN_NAME);
        settings.setDmnNamespace(DMN_NAMESPACE);
        settings.setRuleFlowGroup(RULE_FLOW_GROUP);
        settings.setType(ScenarioSimulationModel.Type.RULE);
        settings.setStateless(true);
        settings.setSkipFromBuild(true);
        settings.setFileName(FILENAME);
        settings.setKieBase(KIE_BASE);
        settings.setKieSession(KIE_SESSION);
    }

    @Test
    public void cloneEmptySettings() {
        final Settings cloneSettings = new Settings().cloneSettings();
        assertNull(cloneSettings.getDmoSession());
        assertNull(cloneSettings.getDmnFilePath());
        assertNull(cloneSettings.getDmnName());
        assertNull(cloneSettings.getDmnNamespace());
        assertNull(cloneSettings.getRuleFlowGroup());
        assertNull(cloneSettings.getType());
        assertFalse(cloneSettings.isStateless());
        assertFalse(cloneSettings.isSkipFromBuild());
        assertNull(cloneSettings.getFileName());
        assertNull(cloneSettings.getKieBase());
        assertNull(cloneSettings.getKieSession());
    }

    @Test
    public void cloneSettings() {
        final Settings cloneSettings = settings.cloneSettings();
        assertEquals(DMO_SESSION, cloneSettings.getDmoSession());
        assertEquals(DMN_PATH, cloneSettings.getDmnFilePath());
        assertEquals(DMN_NAME, cloneSettings.getDmnName());
        assertEquals(DMN_NAMESPACE, cloneSettings.getDmnNamespace());
        assertEquals(RULE_FLOW_GROUP, cloneSettings.getRuleFlowGroup());
        assertEquals(ScenarioSimulationModel.Type.RULE, cloneSettings.getType());
        assertTrue(cloneSettings.isStateless());
        assertTrue(cloneSettings.isSkipFromBuild());
        assertEquals(FILENAME, cloneSettings.getFileName());
        assertEquals(KIE_BASE, cloneSettings.getKieBase());
        assertEquals(KIE_SESSION, cloneSettings.getKieSession());
    }

    @Test
    public void cloneSettingsAndModifyIt() {
        final Settings cloneSettings = settings.cloneSettings();
        cloneSettings.setDmoSession(DMO_SESSION + "cl");
        settings.setDmnFilePath("src/" + DMN_PATH);
        settings.setDmnName("cl" + DMN_NAME);
        settings.setDmnNamespace("cl" + DMN_NAMESPACE);
        settings.setRuleFlowGroup("cl" + RULE_FLOW_GROUP);
        settings.setType(ScenarioSimulationModel.Type.DMN);
        settings.setStateless(false);
        settings.setSkipFromBuild(false);
        settings.setFileName("cl" + FILENAME);
        settings.setKieBase("cl" + KIE_BASE);
        settings.setKieSession("cl" + KIE_SESSION);
        assertNotEquals(settings.getDmoSession(), cloneSettings.getDmoSession());
        assertNotEquals(settings.getDmnFilePath(), cloneSettings.getDmnFilePath());
        assertNotEquals(settings.getDmnName(), cloneSettings.getDmnName());
        assertNotEquals(settings.getDmnNamespace(), cloneSettings.getDmnNamespace());
        assertNotEquals(settings.getRuleFlowGroup(), cloneSettings.getRuleFlowGroup());
        assertNotEquals(settings.getType(), cloneSettings.getType());
        assertNotEquals(settings.isStateless(), cloneSettings.isStateless());
        assertNotEquals(settings.isSkipFromBuild(), cloneSettings.isSkipFromBuild());
        assertNotEquals(settings.getFileName(), cloneSettings.getFileName());
        assertNotEquals(settings.getKieBase(), cloneSettings.getKieBase());
        assertNotEquals(settings.getKieSession(), cloneSettings.getKieSession());
    }
}
