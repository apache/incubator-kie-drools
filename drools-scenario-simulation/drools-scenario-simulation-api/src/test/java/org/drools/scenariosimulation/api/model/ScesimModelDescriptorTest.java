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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ScesimModelDescriptorTest {

    private ScesimModelDescriptor scesimModelDescriptor;
    private FactIdentifier factIdentifier;
    private ExpressionIdentifier expressionIdentifier;

    @Before
    public void init() {
        scesimModelDescriptor = new ScesimModelDescriptor();
        factIdentifier = FactIdentifier.create("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.create("test expression", FactMappingType.EXPECT);
    }

    @Test
    public void getFactIdentifiers() {
        scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        final Set<FactIdentifier> retrieved = scesimModelDescriptor.getFactIdentifiers();
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        assertEquals(factIdentifier, retrieved.iterator().next());
    }

    @Test
    public void addFactMappingByIndexAndFactMapping() {
        FactMapping toClone = new FactMapping();
        toClone.setFactAlias("ALIAS");
        toClone.setExpressionAlias("EXPRESSION_ALIAS");
        final FactMapping cloned = scesimModelDescriptor.addFactMapping(0, toClone);
        assertEquals(toClone.getFactAlias(), cloned.getFactAlias());
        assertEquals(toClone.getExpressionAlias(), cloned.getExpressionAlias());
    }

    @Test
    public void addFactMappingByFactIdentifierAndExpressionIdentifier() {
        scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFactMappingByFactIdentifierAndExpressionIdentifierFail() {
        scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        // Should fail
        scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
    }

    @Test
    public void addFactMappingByIndexAndFactIdentifierAndExpressionIdentifier() {
        scesimModelDescriptor.addFactMapping(0, factIdentifier, expressionIdentifier);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFactMappingByIndexAndFactIdentifierAndExpressionIdentifierFail() {
        // Should fail
        scesimModelDescriptor.addFactMapping(1, factIdentifier, expressionIdentifier);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeFactMappingByIndex() {
        int testingIndex = 0;
        scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        assertNotNull(scesimModelDescriptor.getFactMappingByIndex(testingIndex));
        scesimModelDescriptor.removeFactMappingByIndex(testingIndex);
        scesimModelDescriptor.getFactMappingByIndex(testingIndex);
    }

    @Test
    public void removeFactMapping() {
        FactMapping retrieved = scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        assertTrue(scesimModelDescriptor.getUnmodifiableFactMappings().contains(retrieved));
        scesimModelDescriptor.removeFactMapping(retrieved);
        assertFalse(scesimModelDescriptor.getUnmodifiableFactMappings().contains(retrieved));
    }

    @Test
    public void getIndexByIdentifierTest() {
        List<FactMapping> originalFactMappings = IntStream.range(0, 2).boxed()
                .map(i -> scesimModelDescriptor
                        .addFactMapping(FactIdentifier.create("test " + i, String.class.getCanonicalName()), this.expressionIdentifier)
                ).collect(Collectors.toList());
        int indexToCheck = 0;
        int indexRetrieved = scesimModelDescriptor.getIndexByIdentifier(originalFactMappings.get(indexToCheck).getFactIdentifier(), this.expressionIdentifier);
        assertEquals(indexToCheck, indexRetrieved);
        indexToCheck = 1;
        indexRetrieved = scesimModelDescriptor.getIndexByIdentifier(originalFactMappings.get(indexToCheck).getFactIdentifier(), this.expressionIdentifier);
        assertEquals(indexToCheck, indexRetrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIndexByIdentifierTestFail() {
        IntStream.range(0, 2).forEach(i -> scesimModelDescriptor
                .addFactMapping(FactIdentifier.create("test " + i, String.class.getCanonicalName()), this.expressionIdentifier));
        FactIdentifier notExisting = new FactIdentifier();
        scesimModelDescriptor.getIndexByIdentifier(notExisting, this.expressionIdentifier);
    }

    @Test
    public void getFactMappingsByFactName() {
        IntStream.range(0, 2).forEach(i -> scesimModelDescriptor
                .addFactMapping(FactIdentifier.create("test", String.class.getCanonicalName()), ExpressionIdentifier.create("test expression " + i, FactMappingType.EXPECT)));
        scesimModelDescriptor
                .addFactMapping(FactIdentifier.create("TEST", String.class.getCanonicalName()), ExpressionIdentifier.create("test expression 2", FactMappingType.EXPECT));
        scesimModelDescriptor
                .addFactMapping(FactIdentifier.create("Test", String.class.getCanonicalName()), ExpressionIdentifier.create("test expression 3", FactMappingType.EXPECT));
        scesimModelDescriptor
                .addFactMapping(FactIdentifier.create("tEsT", String.class.getCanonicalName()), ExpressionIdentifier.create("test expression 4", FactMappingType.EXPECT));
        final List<FactMapping> retrieved = scesimModelDescriptor.getFactMappingsByFactName("test");
        assertNotNull(retrieved);
        assertEquals(5, retrieved.size());
    }

    @Test
    public void moveFactMappingTest() {
        ExpressionIdentifier expressionIdentifier2 = ExpressionIdentifier.create("Test expression 2", FactMappingType.GIVEN);
        ExpressionIdentifier expressionIdentifier3 = ExpressionIdentifier.create("Test expression 3", FactMappingType.GIVEN);
        FactMapping factMapping1 = scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        FactMapping factMapping2 = scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier2);
        FactMapping factMapping3 = scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier3);
        List<FactMapping> factMappings = scesimModelDescriptor.getUnmodifiableFactMappings();

        assertEquals(factMappings.get(0), factMapping1);
        assertEquals(factMappings.get(1), factMapping2);
        assertEquals(factMappings.get(2), factMapping3);

        scesimModelDescriptor.moveFactMapping(0, 1);

        factMappings = scesimModelDescriptor.getUnmodifiableFactMappings();
        assertEquals(factMappings.get(0), factMapping2);
        assertEquals(factMappings.get(1), factMapping1);
        assertEquals(factMappings.get(2), factMapping3);

        scesimModelDescriptor.moveFactMapping(2, 1);

        factMappings = scesimModelDescriptor.getUnmodifiableFactMappings();
        assertEquals(factMappings.get(0), factMapping2);
        assertEquals(factMappings.get(1), factMapping3);
        assertEquals(factMappings.get(2), factMapping1);

        scesimModelDescriptor.moveFactMapping(2, 2);

        factMappings = scesimModelDescriptor.getUnmodifiableFactMappings();
        assertEquals(factMappings.get(0), factMapping2);
        assertEquals(factMappings.get(1), factMapping3);
        assertEquals(factMappings.get(2), factMapping1);
    }

    @Test
    public void moveFactMappingOldFailTest() {
        ExpressionIdentifier expressionIdentifier2 = ExpressionIdentifier.create("Test expression 2", FactMappingType.GIVEN);
        scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        scesimModelDescriptor.addFactMapping(factIdentifier, expressionIdentifier2);

        muteException(() -> {
                          scesimModelDescriptor.moveFactMapping(2, 0);
                          fail();
                      },
                      IndexOutOfBoundsException.class);

        muteException(() -> {
                          scesimModelDescriptor.moveFactMapping(-1, 0);
                          fail();
                      },
                      IndexOutOfBoundsException.class);

        muteException(() -> {
                          scesimModelDescriptor.moveFactMapping(0, 2);
                          fail();
                      },
                      IndexOutOfBoundsException.class);

        muteException(() -> {
                          scesimModelDescriptor.moveFactMapping(0, -1);
                          fail();
                      },
                      IndexOutOfBoundsException.class);
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
