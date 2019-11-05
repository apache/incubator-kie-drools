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

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class BackgroundTest {

    private Background background;
    private BackgroundData originalBackgroundData;

    @Before
    public void setup() {
        background = new Background();
        FactIdentifier factIdentifier = FactIdentifier.create("Test", String.class.getCanonicalName());
        ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create("Test", FactMappingType.GIVEN);
        background.getScesimModelDescriptor().addFactMapping(factIdentifier, expressionIdentifier);

        originalBackgroundData = background.addData();
        originalBackgroundData.setDescription("Test Description");
        originalBackgroundData.addMappingValue(factIdentifier, expressionIdentifier, "TEST");
    }

    @Test
    public void addData() {
        background.addData(1);

        assertThatThrownBy(() -> background.addData(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> background.addData(3))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void cloneModel() {
        final Background cloned = this.background.cloneModel();
        assertNotNull(cloned);
        final ScesimModelDescriptor originalDescriptor = background.getScesimModelDescriptor();
        final ScesimModelDescriptor clonedDescriptor = cloned.getScesimModelDescriptor();
        assertEquals(originalDescriptor.getUnmodifiableFactMappings().size(), clonedDescriptor.getUnmodifiableFactMappings().size());
        IntStream.range(0, originalDescriptor.getUnmodifiableFactMappings().size()).forEach(index -> {
            assertEquals(originalDescriptor.getUnmodifiableFactMappings().get(index), clonedDescriptor.getUnmodifiableFactMappings().get(index));
        });
        assertEquals(background.getUnmodifiableData().size(), cloned.getUnmodifiableData().size());
        IntStream.range(0, background.getUnmodifiableData().size()).forEach(index -> {
            assertEquals(background.getUnmodifiableData().get(index).getDescription(), cloned.getUnmodifiableData().get(index).getDescription());
        });
    }

    @Test
    public void cloneData() {
        BackgroundData clonedBackgroundData = background.cloneData(0, 1);

        assertEquals(originalBackgroundData.getDescription(), clonedBackgroundData.getDescription());
        assertEquals(originalBackgroundData.getUnmodifiableFactMappingValues().size(), clonedBackgroundData.getUnmodifiableFactMappingValues().size());
        assertEquals(originalBackgroundData, background.getDataByIndex(0));
        assertEquals(clonedBackgroundData, background.getDataByIndex(1));

        assertNotEquals(originalBackgroundData, clonedBackgroundData);
        assertNotEquals(originalBackgroundData.getUnmodifiableFactMappingValues().get(0), clonedBackgroundData.getUnmodifiableFactMappingValues().get(0));
    }

    @Test
    public void cloneScesimDataFail() {

        assertThatThrownBy(() -> background.cloneData(-1, 1))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> background.cloneData(2, 1))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> background.cloneData(0, -1))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> background.cloneData(0, 2))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void removeFactMappingByIndex() {
        assertEquals(2, background.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(1, background.getScesimModelDescriptor().getUnmodifiableFactMappings().size());
        background.removeFactMappingByIndex(0);
        assertEquals(1, background.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(0, background.getScesimModelDescriptor().getUnmodifiableFactMappings().size());
    }

    @Test
    public void removeFactMapping() {
        assertEquals(2, background.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(1, background.getScesimModelDescriptor().getUnmodifiableFactMappings().size());
        background.removeFactMapping(background.getScesimModelDescriptor().getFactMappingByIndex(0));
        assertEquals(1, background.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size());
        assertEquals(0, background.getScesimModelDescriptor().getUnmodifiableFactMappings().size());
    }

    @Test
    public void getBackgroundDataWithIndex() {
        List<BackgroundDataWithIndex> backgroundDataWithIndex = background.getBackgroundDataWithIndex();
        assertEquals(background.getUnmodifiableData().size(), backgroundDataWithIndex.size());
        BackgroundDataWithIndex backgroundData = backgroundDataWithIndex.get(0);
        int index = backgroundData.getIndex();
        assertEquals(background.getDataByIndex(index - 1), backgroundData.getScesimData());
    }
}