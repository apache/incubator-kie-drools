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
package org.kie.yard.core;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MVELLiteralExpressionInterpreterTest {


    private MVELLiteralExpressionInterpreter sum;
    private YaRDDefinitions yardDefinitions;

    void setUp(final String expression) {
        sum = new MVELLiteralExpressionInterpreter("sum", QuotedExprParsed.from(expression));

        final Map<String, StoreHandle<Object>> outputs = new HashMap<>();
        outputs.put("C", StoreHandle.of(5));
        outputs.put("sum", StoreHandle.empty(Object.class));
        yardDefinitions = new YaRDDefinitions(Collections.emptyMap(), Collections.EMPTY_LIST, outputs);

    }

    @Test
    public void testSum() {

        setUp("1+1");

        final int fire = sum.fire(Collections.emptyMap(), yardDefinitions);

        assertEquals(1, fire);
        assertEquals(2, yardDefinitions.outputs().get("sum").get());
    }

    @Test
    public void testOutputVar() {

        setUp("Math.max(C, 1)");

        final int fire = sum.fire(Collections.emptyMap(), yardDefinitions);

        assertEquals(1, fire);
        assertEquals(5, yardDefinitions.outputs().get("sum").get());
    }

    @Test
    public void testContext() {

        setUp("A+B");

        final Map<String, Object> context = new HashMap<>();
        context.put("A", 1);
        context.put("B", 2);
        final int fire = sum.fire(context, yardDefinitions);

        assertEquals(1, fire);
        assertEquals(3, yardDefinitions.outputs().get("sum").get());
    }

    @Test
    public void testMaps() {

        setUp("person.address.street + ' ' + person.address.number");

        final Map<String, Object> context = new HashMap<>();
        final Map<String, Object> person = new HashMap<>();
        final Map<String, Object> address = new HashMap<>();
        address.put("street", "My Street");
        address.put("number", 12);
        person.put("address", address);
        context.put("person", person);
        final int fire = sum.fire(context, yardDefinitions);

        assertEquals(1, fire);
        assertEquals("My Street 12", yardDefinitions.outputs().get("sum").get());
    }
}