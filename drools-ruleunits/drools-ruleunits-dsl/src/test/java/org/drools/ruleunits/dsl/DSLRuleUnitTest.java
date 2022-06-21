/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.dsl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DSLRuleUnitTest {

    @Test
    public void testHelloWorld() {
        HelloWorld unit = new HelloWorld();
        unit.getStrings().add("Hello World");

        RuleUnitInstance<HelloWorld> unitInstance = DSLRuleUnit.instance(unit);
        assertEquals(2, unitInstance.fire());
        assertTrue(unit.getResults().contains("it worked!"));
        assertTrue(unit.getResults().contains("it also worked with HELLO WORLD"));

        unit.getResults().clear();
        unit.getInts().add(11);
        assertEquals(1, unitInstance.fire());
        assertEquals("String 'Hello World' is 11 characters long", unit.getResults().get(0));
    }

    @Test
    public void testInference() {
        InferenceUnit unit = new InferenceUnit();
        unit.getStrings().add("test");

        RuleUnitInstance<InferenceUnit> unitInstance = DSLRuleUnit.instance(unit);
        assertEquals(0, unitInstance.fire());

        AtomicBoolean success = new AtomicBoolean(false);
        unit.getStrings().subscribe(new DataProcessor<String>() {
            @Override
            public FactHandle insert(DataHandle handle, String object) {
                if (object.equals("ok")) {
                    success.set(true);
                }
                return null;
            }

            @Override
            public void update(DataHandle handle, String object) {

            }

            @Override
            public void delete(DataHandle handle) {

            }
        });

        unit.getStrings().add("this is just a test");
        assertEquals(2, unitInstance.fire());
        assertTrue(success.get());
    }

    @Test
    public void testSelfJoin() {
        SelfJoin unit = new SelfJoin();
        unit.getStrings().add("abc");
        unit.getStrings().add("bcd");
        unit.getStrings().add("xyz");

        RuleUnitInstance<SelfJoin> unitInstance = DSLRuleUnit.instance(unit);
        assertEquals(1, unitInstance.fire());
        assertEquals("Found 'abc' and 'bcd'", unit.getResults().get(0));
    }

    @Test
    public void testAccumulate() {
        AccumulateUnit unit = new AccumulateUnit();
        unit.getStrings().add("A1");
        unit.getStrings().add("A123");
        unit.getStrings().add("B12");
        unit.getStrings().add("ABCDEF");

        RuleUnitInstance<AccumulateUnit> unitInstance = DSLRuleUnit.instance(unit);
        assertEquals(1, unitInstance.fire());
        assertEquals("Sum of length of Strings starting with A is 12", unit.getResults().get(0));
    }
}
