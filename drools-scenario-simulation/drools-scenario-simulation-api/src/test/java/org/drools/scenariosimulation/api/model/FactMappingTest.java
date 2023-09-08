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

import java.util.ArrayList;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;

public class FactMappingTest {

    private FactMapping original;

    @Test
    public void cloneFactMapping() {
        original = new FactMapping("FACT_ALIAS", FactIdentifier.create("FI_TEST", "com.test.Foo"), new ExpressionIdentifier("EI_TEST", GIVEN));
        original.addExpressionElement("FIRST_STEP", String.class.getName());
        original.setExpressionAlias("EA_TEST");
        original.setGenericTypes(new ArrayList<>());
        
        assertThat(original.cloneFactMapping()).isEqualTo(original);
    }

    @Test
    public void getExpressionElementsWithoutClass_missingExpression() {
        original = new FactMapping("FACT_ALIAS", FactIdentifier.create("FI_TEST", "com.test.Foo"), new ExpressionIdentifier("EI_TEST", GIVEN));
        
        assertThatThrownBy(original::getExpressionElementsWithoutClass)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("ExpressionElements malformed");
        assertThat(original.getExpressionElements()).hasSize(0);

    }
    
    @Test
    public void getExpressionElementsWithoutClass_properlyFormed() {
        original = new FactMapping("FACT_ALIAS", FactIdentifier.create("FI_TEST", "com.test.Foo"), new ExpressionIdentifier("EI_TEST", GIVEN));
        original.addExpressionElement("STEP", String.class.getCanonicalName());

        assertThat(original.getExpressionElementsWithoutClass()).hasSize(0);
        assertThat(original.getExpressionElements()).hasSize(1);
    }

}