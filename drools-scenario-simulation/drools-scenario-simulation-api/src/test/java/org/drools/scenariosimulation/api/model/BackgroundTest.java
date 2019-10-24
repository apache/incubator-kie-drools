/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class BackgroundTest {

    Background background;
    BackgroundData originalBackgroundData;

    @Before
    public void setup() {
        background = new Background();
        FactIdentifier factIdentifier = FactIdentifier.create("Test", String.class.getCanonicalName());
        ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create("Test", FactMappingType.GIVEN);
        background.getSimulationDescriptor().addFactMapping(factIdentifier, expressionIdentifier);

        originalBackgroundData = background.addScesimData();
        originalBackgroundData.setDescription("Test Description");
        originalBackgroundData.addMappingValue(factIdentifier, expressionIdentifier, "TEST");
    }

    @Test
    public void addScesimData() {
        background.addScesimData(1);

        muteException(() -> {
                          background.addScesimData(-1);
                          fail();
                      },
                      IllegalArgumentException.class);
        muteException(() -> {
                          background.addScesimData(3);
                          fail();
                      },
                      IllegalArgumentException.class);
    }

    @Test
    public void cloneScesimData() {
        BackgroundData clonedBackgroundData = background.cloneScesimData(0, 1);

        assertEquals(originalBackgroundData.getDescription(), clonedBackgroundData.getDescription());
        assertEquals(originalBackgroundData.getUnmodifiableFactMappingValues().size(), clonedBackgroundData.getUnmodifiableFactMappingValues().size());
        assertEquals(originalBackgroundData, background.getScesimDataByIndex(0));
        assertEquals(clonedBackgroundData, background.getScesimDataByIndex(1));

        assertNotEquals(originalBackgroundData, clonedBackgroundData);
        assertNotEquals(originalBackgroundData.getUnmodifiableFactMappingValues().get(0), clonedBackgroundData.getUnmodifiableFactMappingValues().get(0));
    }

    @Test
    public void cloneScesimDataFail() {

        muteException(() -> {
                          background.cloneScesimData(-1, 1);
                          fail();
                      },
                      IllegalArgumentException.class);

        muteException(() -> {
                          background.cloneScesimData(2, 1);
                          fail();
                      },
                      IllegalArgumentException.class);

        muteException(() -> {
                          background.cloneScesimData(0, -1);
                          fail();
                      },
                      IllegalArgumentException.class);

        muteException(() -> {
                          background.cloneScesimData(0, 2);
                          fail();
                      },
                      IllegalArgumentException.class);
    }

    @Test
    public void removeFactMappingByIndex() {
        assertEquals(2, background.getUnmodifiableScesimData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(1, background.getSimulationDescriptor().getUnmodifiableFactMappings().size());
        background.removeFactMappingByIndex(0);
        assertEquals(1, background.getUnmodifiableScesimData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(0, background.getSimulationDescriptor().getUnmodifiableFactMappings().size());
    }

    @Test
    public void removeFactMapping() {
        assertEquals(2, background.getUnmodifiableScesimData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(1, background.getSimulationDescriptor().getUnmodifiableFactMappings().size());
        background.removeFactMapping(background.getSimulationDescriptor().getFactMappingByIndex(0));
        assertEquals(1, background.getUnmodifiableScesimData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(0, background.getSimulationDescriptor().getUnmodifiableFactMappings().size());
    }

    private <T extends Throwable> void muteException(Runnable toBeExecuted, Class<T> expected) {
        try {
            toBeExecuted.run();
        } catch (Throwable t) {
            if (!t.getClass().isAssignableFrom(expected)) {
                throw t;
            }
        }
    }
}