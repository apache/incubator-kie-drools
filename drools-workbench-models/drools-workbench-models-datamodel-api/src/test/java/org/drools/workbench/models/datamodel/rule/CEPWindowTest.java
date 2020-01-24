/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.datamodel.rule;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CEPWindowTest {

    private CEPWindow cepWindowAllFieldsFilled;
    private CEPWindow cepWindowDefaultValues;
    private CEPWindow cepWindowNullValues;
    private CEPWindow cepWindowNullParameters;
    private CEPWindow cepWindowNullOperator;

    @Before
    public void setUp() throws Exception {

        cepWindowAllFieldsFilled = new CEPWindow();
        cepWindowAllFieldsFilled.setOperator("over window:time");
        cepWindowAllFieldsFilled.setParameters(makeTestParameters());

        cepWindowDefaultValues = new CEPWindow();

        cepWindowNullValues = new CEPWindow();
        cepWindowNullValues.setOperator(null);
        cepWindowNullValues.setParameters(null);

        cepWindowNullParameters = new CEPWindow();
        cepWindowNullParameters.setOperator("over window:time");
        cepWindowNullParameters.setParameters(null);

        cepWindowNullOperator = new CEPWindow();
        cepWindowNullOperator.setOperator(null);
        cepWindowNullOperator.setParameters(makeTestParameters());
    }

    private Map<String, String> makeTestParameters() {
        final HashMap<String, String> result = new HashMap<>();
        result.put("a", "b");
        return result;
    }

    @Test
    public void genericValues() {

        final CEPWindow other = new CEPWindow();
        other.setOperator("over window:time");
        other.setParameters(makeTestParameters());

        assertNotNull(cepWindowAllFieldsFilled.hashCode());
        assertNotNull(other.hashCode());

        assertEquals(cepWindowAllFieldsFilled.hashCode(), other.hashCode());
    }

    @Test
    public void genericValuesDifferentParameters() {

        final CEPWindow other = new CEPWindow();
        other.setOperator("over window:time");
        final HashMap<String, String> parameters = new HashMap<>();
        parameters.put("c", "d");
        other.setParameters(parameters);

        assertNotNull(cepWindowAllFieldsFilled.hashCode());
        assertNotNull(other.hashCode());

        assertNotEquals(cepWindowAllFieldsFilled.hashCode(), other.hashCode());
    }
    
    @Test
    public void genericValuesDifferentOperator() {

        final CEPWindow other = new CEPWindow();
        other.setOperator("over window:length( 10 )");
        other.setParameters(makeTestParameters());

        assertNotNull(cepWindowAllFieldsFilled.hashCode());
        assertNotNull(other.hashCode());

        assertNotEquals(cepWindowAllFieldsFilled.hashCode(), other.hashCode());
    }

    @Test
    public void defaultValues() {
        final CEPWindow other = new CEPWindow();

        assertNotNull(cepWindowDefaultValues.hashCode());
        assertNotNull(other.hashCode());

        assertEquals(cepWindowDefaultValues.hashCode(), other.hashCode());
    }

    @Test
    public void checkNulls() {

        final CEPWindow other = new CEPWindow();
        other.setOperator(null);
        other.setParameters(null);

        assertNotNull(cepWindowNullValues.hashCode());
        assertNotNull(other.hashCode());

        assertEquals(cepWindowNullValues.hashCode(), other.hashCode());
    }

    @Test
    public void nullParameters() {

        final CEPWindow other = new CEPWindow();
        other.setOperator("over window:time");
        other.setParameters(null);

        assertNotNull(cepWindowNullParameters.hashCode());
        assertNotNull(other.hashCode());

        assertEquals(cepWindowNullParameters.hashCode(), other.hashCode());
    }

    @Test
    public void nullOperator() {
        final CEPWindow other = new CEPWindow();
        other.setOperator(null);
        other.setParameters(makeTestParameters());

        assertNotNull(cepWindowNullOperator.hashCode());
        assertNotNull(other.hashCode());

        assertEquals(cepWindowNullOperator.hashCode(), other.hashCode());
    }

    @Test
    public void hashCodesShouldNotBeEqual() {

        assertNotEquals(cepWindowAllFieldsFilled.hashCode(), cepWindowDefaultValues.hashCode());
        assertNotEquals(cepWindowAllFieldsFilled.hashCode(), cepWindowNullValues.hashCode());
        assertNotEquals(cepWindowAllFieldsFilled.hashCode(), cepWindowNullParameters.hashCode());
        assertNotEquals(cepWindowAllFieldsFilled.hashCode(), cepWindowNullOperator.hashCode());

        assertEquals(cepWindowDefaultValues.hashCode(), cepWindowNullValues.hashCode()); // equals because at least at the moment nulls are defaults
        assertNotEquals(cepWindowDefaultValues.hashCode(), cepWindowNullParameters.hashCode());
        assertNotEquals(cepWindowDefaultValues.hashCode(), cepWindowNullOperator.hashCode());

        assertNotEquals(cepWindowNullValues.hashCode(), cepWindowNullParameters.hashCode());
        assertNotEquals(cepWindowNullValues.hashCode(), cepWindowNullOperator.hashCode());

        assertNotEquals(cepWindowNullParameters.hashCode(), cepWindowNullOperator.hashCode());
    }
}