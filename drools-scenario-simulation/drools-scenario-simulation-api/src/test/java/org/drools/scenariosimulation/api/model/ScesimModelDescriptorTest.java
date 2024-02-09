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

import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.drools.scenariosimulation.api.model.FactMappingType.EXPECT;
import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;

public class ScesimModelDescriptorTest {


    private ScesimModelDescriptor modelDescriptor;
    private FactIdentifier factIdentifier;
    private ExpressionIdentifier expressionIdentifier;
    private FactIdentifier factIdentifier2;
    private ExpressionIdentifier expressionIdentifier2;

    @Before
    public void init() {
        modelDescriptor = new ScesimModelDescriptor();
        factIdentifier = FactIdentifier.create("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.create("test expression", EXPECT);
        factIdentifier2 = FactIdentifier.create("test fact 2", Integer.class.getCanonicalName());
        expressionIdentifier2 = ExpressionIdentifier.create("test expression 2", GIVEN);
    }

    @Test
    public void getFactIdentifiers() {
        modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        
        assertThat(modelDescriptor.getFactIdentifiers()).isNotNull().hasSize(1).containsExactly(factIdentifier);
    }

    @Test
    public void addFactMapping_byIndexAndFactMapping() {
        FactMapping toClone = new FactMapping();
        toClone.setFactAlias("ALIAS");
        toClone.setExpressionAlias("EXPRESSION_ALIAS");
        
        final FactMapping cloned = modelDescriptor.addFactMapping(0, toClone);
        
        assertThat(cloned.getFactAlias()).isEqualTo(toClone.getFactAlias());
        assertThat(cloned.getExpressionAlias()).isEqualTo(toClone.getExpressionAlias());
    }

    @Test
    public void addFactMapping_byFactIdentifierAndExpressionIdentifier() {
        modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        modelDescriptor.addFactMapping(factIdentifier2, expressionIdentifier2);
        
        assertThat(modelDescriptor.getFactMappingByIndex(0).getFactAlias()).isEqualTo(factIdentifier.getName());
        assertThat(modelDescriptor.getFactMappingByIndex(0).getFactIdentifier()).isEqualTo(factIdentifier);
        assertThat(modelDescriptor.getFactMappingByIndex(0).getExpressionIdentifier()).isEqualTo(expressionIdentifier);
        assertThat(modelDescriptor.getFactMappingByIndex(1).getFactAlias()).isEqualTo(factIdentifier2.getName());
        assertThat(modelDescriptor.getFactMappingByIndex(1).getFactIdentifier()).isEqualTo(factIdentifier2);
        assertThat(modelDescriptor.getFactMappingByIndex(1).getExpressionIdentifier()).isEqualTo(expressionIdentifier2);
    }

    @Test
    public void addFactMapping_byFactIdentifierAndExpressionIdentifier_fail() {
        modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);

        assertThatIllegalArgumentException().isThrownBy(() -> modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier));
    }

    @Test
    public void addFactMapping_byIndexAndFactIdentifierAndExpressionIdentifier() {
        modelDescriptor.addFactMapping(0, factIdentifier, expressionIdentifier);
        modelDescriptor.addFactMapping(0, factIdentifier2, expressionIdentifier2);
        
        assertThat(modelDescriptor.getFactMappingByIndex(1).getFactAlias()).isEqualTo(factIdentifier.getName());
        assertThat(modelDescriptor.getFactMappingByIndex(1).getFactIdentifier()).isEqualTo(factIdentifier);
        assertThat(modelDescriptor.getFactMappingByIndex(1).getExpressionIdentifier()).isEqualTo(expressionIdentifier);
        assertThat(modelDescriptor.getFactMappingByIndex(0).getFactAlias()).isEqualTo(factIdentifier2.getName());
        assertThat(modelDescriptor.getFactMappingByIndex(0).getFactIdentifier()).isEqualTo(factIdentifier2);
        assertThat(modelDescriptor.getFactMappingByIndex(0).getExpressionIdentifier()).isEqualTo(expressionIdentifier2);
    }

    @Test
    public void addFactMappingByIndexAndFactIdentifierAndExpressionIdentifierFail() {
        assertThatIllegalArgumentException().isThrownBy(() -> modelDescriptor.addFactMapping(1, factIdentifier, expressionIdentifier));
    }

    @Test
    public void removeFactMappingByIndex() {
        modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        
        modelDescriptor.removeFactMappingByIndex(0);
        
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> modelDescriptor.getFactMappingByIndex(0));
    }

    @Test
    public void removeFactMapping() {
        FactMapping retrieved = modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        
        modelDescriptor.removeFactMapping(retrieved);
        
        assertThat(modelDescriptor.getUnmodifiableFactMappings()).doesNotContain(retrieved);
    }

    @Test
    public void getIndexByIdentifierTest() {
        FactMapping factMapping0 = modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        FactMapping factMapping1 = modelDescriptor.addFactMapping(factIdentifier2, expressionIdentifier);
        int indexToCheck = 0;
        int indexRetrieved = modelDescriptor.getIndexByIdentifier(factMapping0.getFactIdentifier(), expressionIdentifier);
        
        assertThat(indexRetrieved).isEqualTo(indexToCheck);
        
        indexToCheck = 1;
        indexRetrieved = modelDescriptor.getIndexByIdentifier(factMapping1.getFactIdentifier(), expressionIdentifier);
        
        assertThat(indexRetrieved).isEqualTo(indexToCheck);
    }

    @Test
    public void getIndexByIdentifierTestFail() {
        FactIdentifier notExisting = new FactIdentifier();
        
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> modelDescriptor.getIndexByIdentifier(notExisting, expressionIdentifier));
    }

    @Test
    public void getFactMappingsByFactName() {
        modelDescriptor.addFactMapping(FactIdentifier.create("test", String.class.getCanonicalName()), ExpressionIdentifier.create("test expression 0", EXPECT));
        modelDescriptor.addFactMapping(FactIdentifier.create("test", String.class.getCanonicalName()), ExpressionIdentifier.create("test expression 1", EXPECT));
        modelDescriptor.addFactMapping(FactIdentifier.create("TEST", String.class.getCanonicalName()), ExpressionIdentifier.create("test expression 2", EXPECT));
        modelDescriptor.addFactMapping(FactIdentifier.create("Test", String.class.getCanonicalName()), ExpressionIdentifier.create("test expression 3", EXPECT));
        modelDescriptor.addFactMapping(FactIdentifier.create("tEsT", String.class.getCanonicalName()), ExpressionIdentifier.create("test expression 4", EXPECT));
        
        final Stream<FactMapping> retrieved = modelDescriptor.getFactMappingsByFactName("test");
        
        assertThat(retrieved).isNotNull().hasSize(5);
    }

    @Test
    public void moveFactMappingTest() {
        ExpressionIdentifier expressionIdentifier2 = ExpressionIdentifier.create("Test expression 2", GIVEN);
        ExpressionIdentifier expressionIdentifier3 = ExpressionIdentifier.create("Test expression 3", GIVEN);
        FactMapping factMapping1 = modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        FactMapping factMapping2 = modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier2);
        FactMapping factMapping3 = modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier3);

        assertThat(modelDescriptor.getUnmodifiableFactMappings()).containsExactly(factMapping1, factMapping2, factMapping3);

        modelDescriptor.moveFactMapping(0, 1);

        assertThat(modelDescriptor.getUnmodifiableFactMappings()).containsExactly(factMapping2, factMapping1, factMapping3);
    }

    @Test
    public void moveFactMapping_failsOutsideBoundaries() {
        ExpressionIdentifier expressionIdentifier2 = ExpressionIdentifier.create("Test expression 2", GIVEN);
        modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier);
        modelDescriptor.addFactMapping(factIdentifier, expressionIdentifier2);
        
        
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> modelDescriptor.moveFactMapping(2, 0));
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> modelDescriptor.moveFactMapping(-1, 0));
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> modelDescriptor.moveFactMapping(0, 2));
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> modelDescriptor.moveFactMapping(2, -1));

    }
}
