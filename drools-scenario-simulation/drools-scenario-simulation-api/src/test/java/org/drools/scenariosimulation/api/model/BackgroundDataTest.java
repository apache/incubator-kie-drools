/*
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

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class BackgroundDataTest {

    private ScesimModelDescriptor scesimModelDescriptor;
    private BackgroundData backgroundData;
    private FactIdentifier factIdentifier;
    private ExpressionIdentifier expressionIdentifier;
    private Background background;

    @BeforeEach
    void init() {
        background = new Background();
        scesimModelDescriptor = background.getScesimModelDescriptor();
        backgroundData = background.addData();
        factIdentifier = FactIdentifier.create("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.create("test expression", FactMappingType.EXPECT);
    }

    @Test
    void removeFactMappingValueByIdentifiersTest() {
        backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        
        backgroundData.removeFactMappingValueByIdentifiers(factIdentifier, expressionIdentifier);
        
        assertThat(backgroundData.getFactMappingValue(factIdentifier, expressionIdentifier)).isNotPresent();
    }

    @Test
    void removeFactMappingValue() {
        backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value");

        Optional<FactMappingValue> retrieved = backgroundData.getFactMappingValue(factIdentifier, expressionIdentifier);
        backgroundData.removeFactMappingValue(retrieved.get());
        
        assertThat(backgroundData.getFactMappingValue(factIdentifier, expressionIdentifier)).isNotPresent();
    }

    @Test
    void addOMappingValue() {
        FactMappingValue factMappingValue = backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        
        assertThat((Object) "test value").isEqualTo(factMappingValue.getRawValue());
    }
    

    
    @Test
    void addMappingValue_multipleInvocationsFail() {
        backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value");

        assertThatIllegalArgumentException().isThrownBy(() -> backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value"));
    }
    
    @Test
    void addMappingValue_singleInvocationSucceed() {
        backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        
        assertThat(backgroundData.getFactMappingValue(factIdentifier, expressionIdentifier)).isPresent();
    }

    @Test
    void getDescription_initialEmptyDescription() {
        assertThat(backgroundData.getDescription()).isEqualTo("");
    }

    @Test
    void getDescription_descriptionSetToNullValue() {
        backgroundData.addMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, null);
        
        assertThat(backgroundData.getDescription()).isEqualTo("");
    }

    @Test
    void getDescription_descriptionSetToNonNullValue() {
        backgroundData.addMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, "Test Description");
        
        assertThat(backgroundData.getDescription()).isEqualTo("Test Description");
    }
    
    @Test
    void setDescription_nullValue() {
        backgroundData.setDescription(null);
        
        assertThat(backgroundData.getDescription()).isEqualTo("");
    }
    
    @Test
    void setDescription_nonNullValue() {
        backgroundData.setDescription("Test Description");
        
        assertThat(backgroundData.getDescription()).isEqualTo("Test Description");
    }
    
    @Test
    void addOrUpdateMappingValue() {
        FactMappingValue oldMappingValue = backgroundData.addMappingValue(factIdentifier, expressionIdentifier, "Test 1");
        
        FactMappingValue newMappingValue = backgroundData.addOrUpdateMappingValue(factIdentifier, expressionIdentifier, "Test 2");

        assertThat(newMappingValue).isEqualTo(oldMappingValue);
        assertThat(newMappingValue.getRawValue()).isEqualTo("Test 2");
    }
}