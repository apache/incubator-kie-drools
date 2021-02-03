/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScenarioSimulationBackendMessagesTest {

    @Test
    public void getGenericCollectionErrorMessage() {
        String message = ScenarioSimulationBackendMessages.getGenericCollectionErrorMessage();
        assertNotNull(message);
        assertFalse(message.trim().isEmpty());
    }

    @Test
    public void getCollectionHTMLErrorMessage_ValueAndPath() {
        String message = ScenarioSimulationBackendMessages.getCollectionHTMLErrorMessage("wrong", Arrays.asList("Step 1", "Step 2"));
        assertNotNull(message);
        assertFalse(message.trim().isEmpty());
        assertTrue(message.contains("<strong>\"wrong\"</strong>"));
        assertTrue(message.contains("<em>Step 1\nStep 2</em>"));
    }

    @Test
    public void getCollectionHTMLErrorMessage_Path() {
        String message = ScenarioSimulationBackendMessages.getCollectionHTMLErrorMessage(null, Arrays.asList("Step 1", "Step 2"));
        assertNotNull(message);
        assertFalse(message.trim().isEmpty());
        assertFalse(message.contains("<strong>"));
        assertTrue(message.contains("<em>Step 1\nStep 2</em>"));
    }

    @Test
    public void getCollectionHTMLErrorMessage_NoWrongValueAndPath() {
        String message = ScenarioSimulationBackendMessages.getCollectionHTMLErrorMessage(null, Arrays.asList());
        assertNotNull(message);
        assertFalse(message.trim().isEmpty());
        assertEquals(ScenarioSimulationBackendMessages.getGenericCollectionErrorMessage(), message);
        message = ScenarioSimulationBackendMessages.getCollectionHTMLErrorMessage(null, null);
        assertNotNull(message);
        assertFalse(message.trim().isEmpty());
        assertEquals(ScenarioSimulationBackendMessages.getGenericCollectionErrorMessage(), message);
    }

}