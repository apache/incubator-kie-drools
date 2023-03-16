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
import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(cloned).isNotNull();
        final ScesimModelDescriptor originalDescriptor = background.getScesimModelDescriptor();
        final ScesimModelDescriptor clonedDescriptor = cloned.getScesimModelDescriptor();
        assertThat(clonedDescriptor.getUnmodifiableFactMappings().size()).isEqualTo(originalDescriptor.getUnmodifiableFactMappings().size());
        IntStream.range(0, originalDescriptor.getUnmodifiableFactMappings().size()).forEach(index -> {
            assertThat(clonedDescriptor.getUnmodifiableFactMappings().get(index)).isEqualTo(originalDescriptor.getUnmodifiableFactMappings().get(index));
        });
        assertThat(cloned.getUnmodifiableData().size()).isEqualTo(background.getUnmodifiableData().size());
        IntStream.range(0, background.getUnmodifiableData().size()).forEach(index -> {
        	assertThat(cloned.getUnmodifiableData().get(index).getDescription()).isEqualTo(background.getUnmodifiableData().get(index).getDescription());
        });
    }

    @Test
    public void cloneData() {
        BackgroundData clonedBackgroundData = background.cloneData(0, 1);

        assertThat(clonedBackgroundData.getDescription()).isEqualTo(originalBackgroundData.getDescription());
        assertThat(clonedBackgroundData.getUnmodifiableFactMappingValues().size()).isEqualTo(originalBackgroundData.getUnmodifiableFactMappingValues().size());
        assertThat(background.getDataByIndex(0)).isEqualTo(originalBackgroundData);
        assertThat(background.getDataByIndex(1)).isEqualTo(clonedBackgroundData);

        assertThat(clonedBackgroundData).isNotEqualTo(originalBackgroundData);
        assertThat(clonedBackgroundData.getUnmodifiableFactMappingValues().get(0)).isNotEqualTo(originalBackgroundData.getUnmodifiableFactMappingValues().get(0));
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
        assertThat(background.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size()).isEqualTo(2);
        assertThat(background.getScesimModelDescriptor().getUnmodifiableFactMappings().size()).isEqualTo(1);
        background.removeFactMappingByIndex(0);
        assertThat(background.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size()).isEqualTo(1);
        assertThat(background.getScesimModelDescriptor().getUnmodifiableFactMappings().size()).isEqualTo(0);
    }

    @Test
    public void removeFactMapping() {
        assertThat(background.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size()).isEqualTo(2);
        assertThat(background.getScesimModelDescriptor().getUnmodifiableFactMappings().size()).isEqualTo(1);
        background.removeFactMapping(background.getScesimModelDescriptor().getFactMappingByIndex(0));
        assertThat(background.getUnmodifiableData().get(0).getUnmodifiableFactMappingValues().size()).isEqualTo(1);
        assertThat(background.getScesimModelDescriptor().getUnmodifiableFactMappings().size()).isEqualTo(0);
    }

    @Test
    public void getBackgroundDataWithIndex() {
        List<BackgroundDataWithIndex> backgroundDataWithIndex = background.getBackgroundDataWithIndex();
        assertThat(backgroundDataWithIndex.size()).isEqualTo(background.getUnmodifiableData().size());
        BackgroundDataWithIndex backgroundData = backgroundDataWithIndex.get(0);
        int index = backgroundData.getIndex();
        assertThat(backgroundData.getScesimData()).isEqualTo(background.getDataByIndex(index - 1));
    }
}