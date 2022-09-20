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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.dsl.domain.Cheese;
import org.drools.ruleunits.dsl.domain.Person;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleUnitsTest {

    @Test
    public void testHelloWorld() {
        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Hello World");

        RuleUnitInstance<HelloWorldUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        assertThat(unitInstance.fire()).isEqualTo(2);
        assertThat(unit.getResults()).containsExactlyInAnyOrder("it worked!", "it also worked with HELLO WORLD");

        unit.getResults().clear();
        unit.getInts().add(11);
        assertThat(unitInstance.fire()).isEqualTo(1);
        assertThat(unit.getResults()).containsExactly("String 'Hello World' is 11 characters long");
    }

    @Test
    public void testInference() {
        InferenceUnit unit = new InferenceUnit();
        unit.getStrings().add("test");

        RuleUnitInstance<InferenceUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        assertThat(unitInstance.fire()).isEqualTo(0);

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

        unit.getStrings().add("Mario");
        assertThat(unitInstance.fire()).isEqualTo(3);
        assertThat(success.get()).isTrue();
    }

    @Test
    public void testSelfJoin() {
        SelfJoinUnit unit = new SelfJoinUnit();
        unit.getStrings().add("abc");
        unit.getStrings().add("bcd");
        unit.getStrings().add("xyz");

        RuleUnitInstance<SelfJoinUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);
        assertThat(unitInstance.fire()).isEqualTo(1);
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

        RuleUnitInstance<AccumulateUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        int fireNr = unitInstance.fire();
        assertThat(fireNr).isEqualTo(3);
        assertThat(unit.getResults()).containsExactlyInAnyOrder(
                "Sum of length of Strings starting with A is 12",
                "Max length of Strings not starting with A is 5",
                "Count of Strings above threshold is 0"
        );

        unit.getResults().clear();

        unit.getThreshold().set(4);
        fireNr = unitInstance.fire();
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
        unit.getPersons().add(new Person("Marilena", 17));
        unit.getPersons().add(new Person("Edson", 38));
        unit.getPersons().add(new Person("Edoardo", 33));
        unit.getPersons().add(new Person("Mark", 45));
        unit.getPersons().add(new Person("Daniele", 13));

        RuleUnitInstance<GroupByUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        assertThat(unitInstance.fire()).isEqualTo(2);
        assertThat(unit.getResults().keySet()).containsExactlyInAnyOrder("M", "E");
        assertThat(unit.getResults().get("M")).isEqualTo(93);
        assertThat(unit.getResults().get("E")).isEqualTo(71);

        unit.getResults().clear();
        unit.getThreshold().set(12);

        assertThat(unitInstance.fire()).isEqualTo(3);
        assertThat(unit.getResults().keySet()).containsExactlyInAnyOrder("M", "E", "D");
        assertThat((List)unit.getResults().get("M")).containsExactlyInAnyOrder("Mario", "Marilena", "Mark");
        assertThat((List)unit.getResults().get("E")).containsExactlyInAnyOrder("Edson", "Edoardo");
        assertThat((List)unit.getResults().get("D")).containsExactlyInAnyOrder("Daniele");
    }

    @Test
    public void testExistential() {
        ExistentialUnit unit = new ExistentialUnit();

        RuleUnitInstance<ExistentialUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        assertThat(unitInstance.fire()).isEqualTo(1);
        assertThat(unit.getResults()).contains("There's no Hello World");
        unit.getResults().clear();

        unit.getStrings().add("test");
        assertThat(unitInstance.fire()).isEqualTo(0);

        unit.getStrings().add("Hello World");
        assertThat(unitInstance.fire()).isEqualTo(1);
        assertThat(unit.getResults()).contains("There is at least one Hello World");
        unit.getResults().clear();

        unit.getStrings().add("Hello World");
        assertThat(unitInstance.fire()).isEqualTo(0);

        unit.getThreshold().set(20);
        assertThat(unitInstance.fire()).isEqualTo(0);

        unit.getStrings().add("This is a very long String");
        assertThat(unitInstance.fire()).isEqualTo(1);
        assertThat(unit.getResults()).contains("There is at least a String longer than threshold 20");
    }

    @Test
    public void testMultiJoin() {
        MultiJoinUnit unit = new MultiJoinUnit();

        RuleUnitInstance<MultiJoinUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit);

        unit.getStrings().add("Hello World");
        unit.getInts().add(11);
        unit.getPersons().add(new Person("Sofia", 4));
        unit.getCheeses().add(new Cheese("Gorgonzola", 20));
        unit.getCheeses().add(new Cheese("Mozzarella", 12));

        assertThat(unitInstance.fire()).isEqualTo(0);

        unit.getPersons().add(new Person("Mark", 47));
        assertThat(unitInstance.fire()).isEqualTo(0);

        unit.getPersons().add(new Person("Mario", 48));
        assertThat(unitInstance.fire()).isEqualTo(1);
        assertThat(unit.getResults()).containsExactly("Found Mario who eats Mozzarella");
    }

    @Test
    public void testUnitsCoordination() {
        UnitOne unitOne = new UnitOne();
        UnitTwo unitTwo = new UnitTwo(unitOne.getInts());

        RuleUnitInstance<UnitOne> unitInstanceOne = RuleUnitProvider.get().createRuleUnitInstance(unitOne);
        RuleUnitInstance<UnitTwo> unitInstanceTwo = RuleUnitProvider.get().createRuleUnitInstance(unitTwo);

        unitOne.getStrings().add("Hello World");
        assertThat(unitInstanceOne.fire()).isEqualTo(1);
        assertThat(unitInstanceTwo.fire()).isEqualTo(1);
        assertThat(unitTwo.getResults()).containsExactly("Found 11");
    }

    @Test
    public void testSelfJoinWithInferenceAndNot() {
        SelfJoinWithInferenceAndNotUnit unit = new SelfJoinWithInferenceAndNotUnit();
        unit.getStrings().add("abc");
        unit.getStrings().add("axy");

        RuleUnitInstance<SelfJoinWithInferenceAndNotUnit> unitInstanceOne = RuleUnitProvider.get().createRuleUnitInstance(unit);
        assertThat(unitInstanceOne.fire()).isEqualTo(1);
    }

    @Test
    public void testLogicalAdd() {
        LogicalAddUnit unit = new LogicalAddUnit();
        DataHandle dh = unit.getStrings().add("abc");

        RuleUnitInstance<LogicalAddUnit> unitInstanceOne = RuleUnitProvider.get().createRuleUnitInstance(unit);

        assertThat(unitInstanceOne.fire()).isEqualTo(2);
        assertThat(unit.getResults()).containsExactly("exists");

        unit.getResults().clear();

        unit.getStrings().remove(dh);
        assertThat(unitInstanceOne.fire()).isEqualTo(1);
        assertThat(unit.getResults()).containsExactly("not exists");
    }
}
