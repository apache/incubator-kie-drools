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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SimulationDescriptorTest {

    SimulationDescriptor simulationDescriptor;
    FactIdentifier factIdentifier;
    ExpressionIdentifier expressionIdentifier;

    @Before
    public void init() {
        simulationDescriptor = new SimulationDescriptor();
        factIdentifier = FactIdentifier.create("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.create("test expression", FactMappingType.EXPECT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFactMappingTest() {
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);

        // Should fail
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFactMappingIndexTest() {
        // Should fail
        simulationDescriptor.addFactMapping(1, factIdentifier, expressionIdentifier);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeFactMappingByIndex() {
        int testingIndex = 0;
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        assertNotNull(simulationDescriptor.getFactMappingByIndex(testingIndex));
        simulationDescriptor.removeFactMappingByIndex(testingIndex);
        simulationDescriptor.getFactMappingByIndex(testingIndex);
    }

    @Test
    public void removeFactMapping() {
        FactMapping retrieved = simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        assertTrue(simulationDescriptor.getUnmodifiableFactMappings().contains(retrieved));
        simulationDescriptor.removeFactMapping(retrieved);
        assertFalse(simulationDescriptor.getUnmodifiableFactMappings().contains(retrieved));
    }

    @Test
    public void getIndexByIdentifierTest() {
        List<FactMapping> originalFactMappings = IntStream.range(0, 2).boxed()
                .map(i -> simulationDescriptor
                        .addFactMapping(FactIdentifier.create("test " + i, String.class.getCanonicalName()), this.expressionIdentifier)
                ).collect(Collectors.toList());
        int indexToCheck = 0;
        int indexRetrieved = simulationDescriptor.getIndexByIdentifier(originalFactMappings.get(indexToCheck).getFactIdentifier(), this.expressionIdentifier);
        assertEquals(indexToCheck, indexRetrieved);
        indexToCheck = 1;
        indexRetrieved = simulationDescriptor.getIndexByIdentifier(originalFactMappings.get(indexToCheck).getFactIdentifier(), this.expressionIdentifier);
        assertEquals(indexToCheck, indexRetrieved);
    }

    @Test
    public void moveFactMappingTest() {
        ExpressionIdentifier expressionIdentifier2 = ExpressionIdentifier.create("Test expression 2", FactMappingType.GIVEN);
        ExpressionIdentifier expressionIdentifier3 = ExpressionIdentifier.create("Test expression 3", FactMappingType.GIVEN);
        FactMapping factMapping1 = simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        FactMapping factMapping2 = simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier2);
        FactMapping factMapping3 = simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier3);
        List<FactMapping> factMappings = simulationDescriptor.getUnmodifiableFactMappings();

        assertEquals(factMappings.get(0), factMapping1);
        assertEquals(factMappings.get(1), factMapping2);
        assertEquals(factMappings.get(2), factMapping3);

        simulationDescriptor.moveFactMapping(0, 1);

        factMappings = simulationDescriptor.getUnmodifiableFactMappings();
        assertEquals(factMappings.get(0), factMapping2);
        assertEquals(factMappings.get(1), factMapping1);
        assertEquals(factMappings.get(2), factMapping3);

        simulationDescriptor.moveFactMapping(2, 1);

        factMappings = simulationDescriptor.getUnmodifiableFactMappings();
        assertEquals(factMappings.get(0), factMapping2);
        assertEquals(factMappings.get(1), factMapping3);
        assertEquals(factMappings.get(2), factMapping1);

        simulationDescriptor.moveFactMapping(2, 2);

        factMappings = simulationDescriptor.getUnmodifiableFactMappings();
        assertEquals(factMappings.get(0), factMapping2);
        assertEquals(factMappings.get(1), factMapping3);
        assertEquals(factMappings.get(2), factMapping1);
    }

    @Test
    public void moveFactMappingOldFailTest() {
        ExpressionIdentifier expressionIdentifier2 = ExpressionIdentifier.create("Test expression 2", FactMappingType.GIVEN);
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        simulationDescriptor.addFactMapping(factIdentifier, expressionIdentifier2);

        muteException(() -> {
                          simulationDescriptor.moveFactMapping(2, 0);
                          fail();
                      },
                      IllegalArgumentException.class);

        muteException(() -> {
                          simulationDescriptor.moveFactMapping(-1, 0);
                          fail();
                      },
                      IllegalArgumentException.class);

        muteException(() -> {
                          simulationDescriptor.moveFactMapping(0, 2);
                          fail();
                      },
                      IllegalArgumentException.class);

        muteException(() -> {
                          simulationDescriptor.moveFactMapping(0, -1);
                          fail();
                      },
                      IllegalArgumentException.class);
    }

    private <T extends Throwable> void muteException(Runnable toBeExecuted, Class<T> expected) {
        try {
            toBeExecuted.run();
        } catch (Throwable t) {
            //noinspection NonJREEmulationClassesInClientCode
            if (!t.getClass().isAssignableFrom(expected)) {
                throw t;
            }
        }
    }
}
