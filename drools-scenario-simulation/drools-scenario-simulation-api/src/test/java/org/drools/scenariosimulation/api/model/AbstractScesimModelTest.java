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

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AbstractScesimModelTest {

    private final static int SCENARIO_DATA = 5;
    private final static int FACT_MAPPINGS = 3;
    private AbstractScesimModel<Scenario> model;

    @Before
    public void init() {
        model = spy(new AbstractScesimModel<Scenario>() {

            @Override
            public AbstractScesimModel<Scenario> cloneModel() {
                return null;
            }

            @Override
            public Scenario addData(int index) {
                return null;
            }
        });
        IntStream.range(0, SCENARIO_DATA).forEach(index -> model.scesimData.add(getSpyScenario(index)));
        IntStream.range(0, FACT_MAPPINGS).forEach(index -> model.scesimModelDescriptor.getFactMappings().add(getSpyFactMapping()));
    }

    @Test
    public void getUnmodifiableData() {
        assertThat(model.getUnmodifiableData()).isNotNull().hasSize(SCENARIO_DATA);
    }

    @Test
    public void getUnmodifiableData_isUnmodifiable() {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> model.getUnmodifiableData().add(new Scenario()));
    }

    @Test
    public void getDataByIndex() {
        final Scenario dataByIndex = model.getDataByIndex(3);
         
        assertThat(dataByIndex).isNotNull();
        assertThat(model.scesimData).contains(dataByIndex);
    }
    
    @Test
    public void removeDataByIndex() {
        final Scenario dataByIndex = model.getDataByIndex(3);
        
        model.removeDataByIndex(3);
        
        assertThat(model.scesimData).hasSize(SCENARIO_DATA - 1).doesNotContain(dataByIndex);
    }

    @Test
    public void removeData() {
        final Scenario dataByIndex = model.getDataByIndex(3);
        
        model.removeData(dataByIndex);
        
        assertThat(model.scesimData).hasSize(SCENARIO_DATA - 1).doesNotContain(dataByIndex);
    }

    @Test
    public void replaceData() {
        final Scenario replaced = model.getDataByIndex(3);
        final Scenario replacement = new Scenario();
        
        model.replaceData(3, replacement);
        
        assertThat(model.scesimData).hasSize(SCENARIO_DATA).doesNotContain(replaced);
        assertThat(model.scesimData.get(3)).isEqualTo(replacement);
    }
    
    @Test
    public void cloneData() {
        final Scenario cloned = model.getDataByIndex(3);
        final Scenario clone = model.cloneData(3, 4);
        
        assertThat(clone).isNotNull();
        assertThat(model.scesimData.get(4)).isEqualTo(clone);
        assertThat(clone.getDescription()).isEqualTo(cloned.getDescription());
    }

    @Test
    public void clear() {
        model.clear();
        
        verify(model, times(1)).clearDatas();
    }

    @Test
    public void clearDatas() {
        model.clearDatas();
        
        assertThat(model.scesimData).isEmpty();
    }

    @Test
    public void resetErrors() {
        model.resetErrors();
        
        model.scesimData.forEach(scesimData -> verify(scesimData, times(1)).resetErrors());
    }

    @Test
    public void removeFactMappingByIndex() {
        final FactMapping factMappingByIndex = model.scesimModelDescriptor.getFactMappingByIndex(2);
        
        model.removeFactMappingByIndex(2);
        
        verify(model, times(1)).clearDatas(eq(factMappingByIndex));
        assertThat(model.scesimModelDescriptor.getFactMappings()).hasSize(FACT_MAPPINGS - 1).doesNotContain(factMappingByIndex);
    }

    @Test
    public void removeFactMapping() {
        final FactMapping factMappingByIndex = model.scesimModelDescriptor.getFactMappingByIndex(2);
        
        model.removeFactMapping(factMappingByIndex);
        
        verify(model, times(1)).clearDatas(eq(factMappingByIndex));
        assertThat(model.scesimModelDescriptor.getFactMappings()).hasSize(FACT_MAPPINGS - 1).doesNotContain(factMappingByIndex);
    }

    @Test
    public void clearDatasByFactMapping() {
        final FactMapping factMappingByIndex = model.scesimModelDescriptor.getFactMappingByIndex(2);
        model.clearDatas(factMappingByIndex);
        final FactIdentifier factIdentifier = factMappingByIndex.getFactIdentifier();
        final ExpressionIdentifier expressionIdentifier = factMappingByIndex.getExpressionIdentifier();

        model.scesimData.forEach(scesimData -> verify(scesimData, times(1)).removeFactMappingValueByIdentifiers(eq(factIdentifier), eq(expressionIdentifier)));
    }

    private Scenario getSpyScenario(int index) {
        Scenario toReturn = spy(new Scenario());
        toReturn.setDescription("INDEX-" + index);
        return toReturn;
    }

    private FactMapping getSpyFactMapping() {
        FactMapping toReturn = spy(new FactMapping());
        when(toReturn.getFactIdentifier()).thenReturn(mock(FactIdentifier.class));
        when(toReturn.getExpressionIdentifier()).thenReturn(mock(ExpressionIdentifier.class));
        return spy(new FactMapping());
    }
}
