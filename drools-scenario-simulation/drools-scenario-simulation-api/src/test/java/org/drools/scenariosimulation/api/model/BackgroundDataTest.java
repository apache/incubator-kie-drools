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
package org.drools.scenariosimulation.api.model;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BackgroundDataTest {

    private ScesimModelDescriptor scesimModelDescriptor;
    private BackgroundData backgroundData;
    private FactIdentifier factIdentifier;
    private ExpressionIdentifier expressionIdentifier;
    private Background background;

    @Before
    public void init() {
        background = new Background();
        scesimModelDescriptor = background.getScesimModelDescriptor();
        backgroundData = background.addData();
        factIdentifier = FactIdentifier.create("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.create("test expression", FactMappingType.EXPECT);
    }

    @Test
    public void removeFactMappingValueByIdentifiersTest() {
        backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        Optional<FactMappingValue> retrieved = backgroundData.getFactMappingValue(factIdentifier, expressionIdentifier);
        assertTrue(retrieved.isPresent());
        backgroundData.removeFactMappingValueByIdentifiers(factIdentifier, expressionIdentifier);
        retrieved = backgroundData.getFactMappingValue(factIdentifier, expressionIdentifier);
        assertFalse(retrieved.isPresent());
    }

    @Test
    public void removeFactMappingValue() {
        backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        Optional<FactMappingValue> retrieved = backgroundData.getFactMappingValue(factIdentifier, expressionIdentifier);
        assertTrue(retrieved.isPresent());
        backgroundData.removeFactMappingValue(retrieved.get());
        retrieved = backgroundData.getFactMappingValue(factIdentifier, expressionIdentifier);
        assertFalse(retrieved.isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMappingValueTest() {
        backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        // Should fail
        backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value");
    }

    @Test
    public void getDescriptionTest() {
        assertEquals("", backgroundData.getDescription());

        String description = "Test Description";
        backgroundData.addMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, description);
        assertEquals(description, backgroundData.getDescription());

        BackgroundData scenarioWithDescriptionNull = background.addData();
        scenarioWithDescriptionNull.setDescription(null);
        assertEquals("", scenarioWithDescriptionNull.getDescription());
    }

    @Test
    public void addOrUpdateMappingValue() {
        Object value1 = "Test 1";
        Object value2 = "Test 2";
        FactMappingValue factMappingValue = backgroundData.addMappingValue(factIdentifier, expressionIdentifier, value1);
        assertEquals(factMappingValue.getRawValue(), value1);
        FactMappingValue factMappingValue1 = backgroundData.addOrUpdateMappingValue(factIdentifier, expressionIdentifier, value2);
        assertEquals(factMappingValue, factMappingValue1);
        assertEquals(factMappingValue1.getRawValue(), value2);
    }
}