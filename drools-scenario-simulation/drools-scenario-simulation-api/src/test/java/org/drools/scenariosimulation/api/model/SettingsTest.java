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
package org.drools.scenariosimulation.api.model;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        
        assertThat(cloneSettings.getDmoSession()).isNull();
        assertThat(cloneSettings.getDmnFilePath()).isNull();
        assertThat(cloneSettings.getDmnName()).isNull();
        assertThat(cloneSettings.getDmnNamespace()).isNull();
        assertThat(cloneSettings.getRuleFlowGroup()).isNull();
        assertThat(cloneSettings.getType()).isNull();
        assertThat(cloneSettings.isStateless()).isFalse();
        assertThat(cloneSettings.isSkipFromBuild()).isFalse();
        assertThat(cloneSettings.getFileName()).isNull();
        assertThat(cloneSettings.getKieBase()).isNull();
        assertThat(cloneSettings.getKieSession()).isNull();
    }

    @Test
    public void cloneSettings() {
        final Settings cloneSettings = settings.cloneSettings();
        assertThat(cloneSettings.getDmoSession()).isEqualTo(DMO_SESSION);
        assertThat(cloneSettings.getDmnFilePath()).isEqualTo(DMN_PATH);
        assertThat(cloneSettings.getDmnName()).isEqualTo(DMN_NAME);
        assertThat(cloneSettings.getDmnNamespace()).isEqualTo(DMN_NAMESPACE);
        assertThat(cloneSettings.getRuleFlowGroup()).isEqualTo(RULE_FLOW_GROUP);
        assertThat(cloneSettings.getType()).isEqualTo(ScenarioSimulationModel.Type.RULE);
        assertThat(cloneSettings.isStateless()).isTrue();
        assertThat(cloneSettings.isSkipFromBuild()).isTrue();
        assertThat(cloneSettings.getFileName()).isEqualTo(FILENAME);
        assertThat(cloneSettings.getKieBase()).isEqualTo(KIE_BASE);
        assertThat(cloneSettings.getKieSession()).isEqualTo(KIE_SESSION);
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
        
        assertThat(cloneSettings.getDmoSession()).isNotEqualTo(settings.getDmoSession());
        assertThat(cloneSettings.getDmnFilePath()).isNotEqualTo(settings.getDmnFilePath());
        assertThat(cloneSettings.getDmnName()).isNotEqualTo(settings.getDmnName());
        assertThat(cloneSettings.getDmnNamespace()).isNotEqualTo(settings.getDmnNamespace());
        assertThat(cloneSettings.getRuleFlowGroup()).isNotEqualTo(settings.getRuleFlowGroup());
        assertThat(cloneSettings.getType()).isNotEqualTo(settings.getType());
        assertThat(cloneSettings.isStateless()).isNotEqualTo(settings.isStateless());
        assertThat(cloneSettings.isSkipFromBuild()).isNotEqualTo(settings.isSkipFromBuild());
        assertThat(cloneSettings.getFileName()).isNotEqualTo(settings.getFileName());
        assertThat(cloneSettings.getKieBase()).isNotEqualTo(settings.getKieBase());
        assertThat(cloneSettings.getKieSession()).isNotEqualTo(settings.getKieSession());
    }
}
