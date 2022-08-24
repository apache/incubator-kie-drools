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
import org.drools.ruleunits.dsl.domain.Person;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleUnitsTest {

    @Test
    public void testHelloWorld() {
        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Hello World");

        assertThat(unit.fire()).isEqualTo(2);
        assertThat(unit.getResults()).containsExactlyInAnyOrder("it worked!", "it also worked with HELLO WORLD");

        unit.getResults().clear();
        unit.getInts().add(11);
        assertThat(unit.fire()).isEqualTo(1);
        assertThat(unit.getResults()).containsExactly("String 'Hello World' is 11 characters long");
    }

    @Test
    public void testInference() {
        InferenceUnit unit = new InferenceUnit();
        unit.getStrings().add("test");

        assertThat(unit.fire()).isEqualTo(0);

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
        assertThat(unit.fire()).isEqualTo(2);
        assertThat(success.get()).isTrue();
    }

    @Test
    public void testSelfJoin() {
        SelfJoinUnit unit = new SelfJoinUnit();
        unit.getStrings().add("abc");
        unit.getStrings().add("bcd");
        unit.getStrings().add("xyz");

        assertThat(unit.fire()).isEqualTo(1);
        assertThat(unit.getResults()).containsExactly("Found 'abc' and 'bcd'");
    }

    @Test
    public void testAccumulate() {
        AccumulateUnit unit = new AccumulateUnit();
        unit.getStrings().add("A1");
        unit.getStrings().add("A123");
        unit.getStrings().add("B12");
        unit.getStrings().add("ABCDEF");
        unit.getStrings().add("BCDEF");
        unit.getStrings().add("Cx");

        int fireNr = unit.fire();
        assertThat(fireNr).isEqualTo(3);
        assertThat(unit.getResults()).containsExactlyInAnyOrder(
                "Sum of length of Strings starting with A is 12",
                "Max length of Strings not starting with A is 5",
                "Count of Strings above threshold is 0"
        );

        unit.getResults().clear();

        unit.getThreshold().set(4);
        fireNr = unit.fire();
        assertThat(fireNr).isEqualTo(2);
        assertThat(unit.getResults()).containsExactlyInAnyOrder(
                "Average length of Strings longer than threshold 4 is 5.5",
                "Count of Strings above threshold is 2"
        );
    }

    @Test
    public void testGroupBy() {
        GroupByUnit unit = new GroupByUnit();

        unit.getPersons().add(new Person("Mario", 48));
        unit.getPersons().add(new Person("Matteo", 10));
        unit.getPersons().add(new Person("Edson", 38));
        unit.getPersons().add(new Person("Edoardo", 33));
        unit.getPersons().add(new Person("Mark", 45));
        unit.getPersons().add(new Person("Daniele", 13));

        assertThat(unit.fire()).isEqualTo(2);
        assertThat(unit.getResults().keySet()).containsExactlyInAnyOrder("M", "E");
        assertThat(unit.getResults().get("M")).isEqualTo(93);
        assertThat(unit.getResults().get("E")).isEqualTo(71);
    }

    @Test
    public void testExistential() {
        ExistentialUnit unit = new ExistentialUnit();

        assertThat(unit.fire()).isEqualTo(1);
        assertThat(unit.getResults()).contains("There's no Hello World");
        unit.getResults().clear();

        unit.getStrings().add("test");
        assertThat(unit.fire()).isEqualTo(0);

        unit.getStrings().add("Hello World");
        assertThat(unit.fire()).isEqualTo(1);
        assertThat(unit.getResults()).contains("There is at least one Hello World");
        unit.getResults().clear();

        unit.getStrings().add("Hello World");
        assertThat(unit.fire()).isEqualTo(0);

        unit.getThreshold().set(20);
        assertThat(unit.fire()).isEqualTo(0);

        unit.getStrings().add("This is a very long String");
        assertThat(unit.fire()).isEqualTo(1);
        assertThat(unit.getResults()).contains("There is at least a String longer than threshold 20");
    }

    @Test
    public void testMultiJoin() {
        MultiJoinUnit unit = new MultiJoinUnit();
        unit.getStrings().add("Hello World");
        unit.getInts().add(11);
        unit.getPersons().add(new Person("Sofia", 4));

        assertThat(unit.fire()).isEqualTo(0);

        unit.getPersons().add(new Person("Mark", 47));
        assertThat(unit.fire()).isEqualTo(0);

        unit.getPersons().add(new Person("Mario", 48));
        assertThat(unit.fire()).isEqualTo(1);
        assertThat(unit.getResults()).containsExactly("Found 'Mario'");
    }

    @Test
    public void testUnitsCoordination() {
        UnitOne unitOne = new UnitOne();
        UnitTwo unitTwo = new UnitTwo(unitOne.getInts());

        unitOne.getStrings().add("Hello World");
        assertThat(unitOne.fire()).isEqualTo(1);
        assertThat(unitTwo.fire()).isEqualTo(1);
        assertThat(unitTwo.getResults()).containsExactly("Found 11");
    }
}
