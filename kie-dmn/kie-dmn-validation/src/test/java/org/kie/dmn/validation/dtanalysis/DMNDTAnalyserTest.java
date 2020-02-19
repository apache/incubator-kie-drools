/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation.dtanalysis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.core.util.AnalyticsMsg;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DMNDTAnalyserTest {

    @Test
    public void finnishLocaleWorks() {

        final DMNDTAnalyser dmndtAnalyser = new DMNDTAnalyser(new ArrayList<>());
        dmndtAnalyser.setMSG(AnalyticsMsg.create("fi"));
        final DMNModel model = mock(DMNModel.class);
        final Definitions definitions = mock(Definitions.class);
        doReturn(definitions).when(model).getDefinitions();
        final ArrayList<Object> decisionTables = new ArrayList<>();

        final DecisionTable decisionTable = mock(DecisionTable.class);
        decisionTables.add(decisionTable);
        doReturn(HitPolicy.FIRST).when(decisionTable).getHitPolicy();

        doReturn(decisionTables).when(definitions).findAllChildren(DecisionTable.class);
        final List<DTAnalysis> analyse = dmndtAnalyser.analyse(model);
        final List<DMNMessage> dmnMessages = analyse.get(0).asDMNMessages();
        assertFalse(dmnMessages.isEmpty());
        for (DMNMessage dmnMessage : dmnMessages) {
            assertTrue(dmnMessage.getText().contains("No niin"));
        }
    }

    @Test
    public void swedishLocaleUsesDefault() { // If we ever add se support this fails.

        final DMNDTAnalyser dmndtAnalyser = new DMNDTAnalyser(new ArrayList<>());
        dmndtAnalyser.setMSG(AnalyticsMsg.create("se"));
        final DMNModel model = mock(DMNModel.class);
        final Definitions definitions = mock(Definitions.class);
        doReturn(definitions).when(model).getDefinitions();
        final ArrayList<Object> decisionTables = new ArrayList<>();

        final DecisionTable decisionTable = mock(DecisionTable.class);
        decisionTables.add(decisionTable);
        doReturn(HitPolicy.FIRST).when(decisionTable).getHitPolicy();

        doReturn(decisionTables).when(definitions).findAllChildren(DecisionTable.class);
        final List<DTAnalysis> analyse = dmndtAnalyser.analyse(model);
        final List<DMNMessage> dmnMessages = analyse.get(0).asDMNMessages();
        assertFalse(dmnMessages.isEmpty());
        assertTrue(containsMessage(dmnMessages, "DMN: The HitPolicy for decision table '[ID: null]' should be UNIQUE (DMN Validation, Decision Table Analysis, Hit Policy Recommender) "));
    }

    @Test
    public void defaultLocaleUsesDefault() {

        final DMNDTAnalyser dmndtAnalyser = new DMNDTAnalyser(new ArrayList<>());
        dmndtAnalyser.setMSG(AnalyticsMsg.create(null));
        final DMNModel model = mock(DMNModel.class);
        final Definitions definitions = mock(Definitions.class);
        doReturn(definitions).when(model).getDefinitions();
        final ArrayList<Object> decisionTables = new ArrayList<>();

        final DecisionTable decisionTable = mock(DecisionTable.class);
        decisionTables.add(decisionTable);
        doReturn(HitPolicy.FIRST).when(decisionTable).getHitPolicy();

        doReturn(decisionTables).when(definitions).findAllChildren(DecisionTable.class);
        final List<DTAnalysis> analyse = dmndtAnalyser.analyse(model);
        final List<DMNMessage> dmnMessages = analyse.get(0).asDMNMessages();
        assertFalse(dmnMessages.isEmpty());
        assertTrue(containsMessage(dmnMessages, "DMN: The HitPolicy for decision table '[ID: null]' should be UNIQUE (DMN Validation, Decision Table Analysis, Hit Policy Recommender) "));
    }

    public boolean containsMessage(final List<DMNMessage> dmnMessages,
                                   final String text) {
        for (DMNMessage dmnMessage : dmnMessages) {
            if (dmnMessage.getText().equals(text)) {
                return true;
            }
        }
        return false;
    }
}