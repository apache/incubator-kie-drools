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

import java.util.ArrayList;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FactMappingTest {

    @Test
    public void cloneFactMapping() {
        FactMapping original = new FactMapping("FACT_ALIAS", FactIdentifier.create("FI_TEST", "com.test.Foo"), new ExpressionIdentifier("EI_TEST", FactMappingType.GIVEN));
        original.addExpressionElement("FIRST_STEP", String.class.getName());
        original.setExpressionAlias("EA_TEST");
        original.setGenericTypes(new ArrayList<>());
        FactMapping retrieved = original.cloneFactMapping();
        assertTrue(retrieved.equals(original));
    }

    @Test
    public void getExpressionElementsWithoutClass() {
        FactMapping original = new FactMapping("FACT_ALIAS", FactIdentifier.create("FI_TEST", "com.test.Foo"), new ExpressionIdentifier("EI_TEST", FactMappingType.GIVEN));
        assertThatThrownBy(original::getExpressionElementsWithoutClass)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("ExpressionElements malformed");
        assertEquals(0, original.getExpressionElements().size());
        original.addExpressionElement("STEP", String.class.getCanonicalName());

        assertEquals(0, original.getExpressionElementsWithoutClass().size());
        assertEquals(1, original.getExpressionElements().size());
    }
}