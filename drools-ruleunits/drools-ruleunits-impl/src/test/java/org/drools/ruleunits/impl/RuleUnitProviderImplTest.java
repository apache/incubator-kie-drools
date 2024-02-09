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
package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.drools.ruleunits.impl.domain.Measurement;
import org.drools.ruleunits.impl.domain.Person;
import org.drools.ruleunits.impl.domain.Sensor;
import org.drools.ruleunits.impl.listener.TestAgendaEventListener;
import org.drools.ruleunits.impl.listener.TestRuleEventListener;
import org.drools.ruleunits.impl.listener.TestRuleRuntimeEventListener;
import org.junit.jupiter.api.Test;
import org.kie.api.builder.CompilationErrorsException;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class RuleUnitProviderImplTest {

    @Test
    public void helloWorldGenerated() {
        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Hello World");

        try ( RuleUnitInstance<HelloWorldUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
        }
    }

    @Test
    public void notWithAndWithoutSingleQuote() {
        NotTestUnit unit = new NotTestUnit();

        try ( RuleUnitInstance<NotTestUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {
            assertThat(unitInstance.fire()).isEqualTo(2);
        }
    }

    @Test
    public void logicalAdd() {
        // KOGITO-6466
        LogicalAddTestUnit unit = new LogicalAddTestUnit();

        try ( RuleUnitInstance<LogicalAddTestUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {

            DataHandle dh = unit.getStrings().add("abc");

            assertThat(unitInstance.fire()).isEqualTo(2);
            assertThat(unit.getResults()).containsExactly("exists");

            unit.getResults().clear();

            unit.getStrings().remove(dh);
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("not exists");
        }
    }

    @Test
    public void logicalAddByElement() {
        // DROOLS-7583
        LogicalAddByElementTestUnit unit = new LogicalAddByElementTestUnit();

        ArrayList<String> eventsRecorded = new ArrayList<>();

        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();
        ruleConfig.getRuleRuntimeListeners().add(new RuleRuntimeEventListener() {
            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                String byRuleName = Optional.ofNullable(event.getRule())
                  .map(rule -> " by " + rule.getName())
                  .orElse("");
                eventsRecorded.add(event.getObject() + " inserted" + byRuleName);
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
            }

            @Override
            public void objectDeleted(ObjectDeletedEvent event) {
                String byRuleName = Optional.ofNullable(event.getRule())
                  .map(rule -> " by " + rule.getName())
                  .orElse("");
                eventsRecorded.add(event.getOldObject() + " deleted" + byRuleName);
            }
        });

        try ( RuleUnitInstance<LogicalAddByElementTestUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit, ruleConfig) ) {

            DataHandle handleToStringWithLength3 = unit.getStrings().add("abc");
            unit.getStrings().add("len4");

            assertThat(unitInstance.fire()).isEqualTo(4);

            assertThat(eventsRecorded).containsExactly(
              "abc inserted",
              "len4 inserted",
              "3 inserted by R1",
              "4 inserted by R1");
            assertThat(unit.getResults()).containsExactly("3 exists", "4 exists");

            eventsRecorded.clear();
            unit.getResults().clear();

            unit.getStrings().remove(handleToStringWithLength3);
            assertThat(unitInstance.fire()).isEqualTo(0);

            assertThat(eventsRecorded).doesNotContain("4 deleted");
            assertThat(eventsRecorded).containsExactly("abc deleted", "3 deleted");
        }
    }

    @Test
    public void update() {
        UpdateTestUnit unit = new UpdateTestUnit();

        try ( RuleUnitInstance<UpdateTestUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {

            unit.getPersons().add(new Person("Mario", 17));

            assertThat(unitInstance.fire()).isEqualTo(2);
            assertThat(unit.getResults()).containsExactly("ok");
        }
    }

    @Test
    public void updateNoDS() {
        UpdateNoDSTestUnit unit = new UpdateNoDSTestUnit();

        try ( RuleUnitInstance<UpdateNoDSTestUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {

            unit.getPersons().add(new Person("Mario", 17));

            assertThat(unitInstance.fire()).isEqualTo(2);
            assertThat(unit.getResults()).containsExactly("ok");
        }
    }

    @Test
    public void wrongType() {
        try {
            RuleUnitProvider.get().createRuleUnitInstance(new WronglyTypedUnit());
            fail("Compilation should fail");
        } catch (CompilationErrorsException e) {
            assertThat(
                    e.getErrorMessages().stream().map(Objects::toString)
                            .anyMatch( s -> s.contains("The method add(Integer) in the type DataStore<Integer> is not applicable for the arguments (String)"))
            ).isTrue();
        }
    }

    @Test
    public void agendaGroup() {
        try {
            RuleUnitProvider.get().createRuleUnitInstance(new AgendaGroupUnit());
            fail("Compilation should fail");
        } catch (CompilationErrorsException e) {
            assertThat(
                    e.getErrorMessages().stream().map(Objects::toString)
                            .anyMatch( s -> s.contains("Rule RuleInGroup belongs to unit AgendaGroupUnit and cannot have an agenda-group or a ruleflow-group"))
            ).isTrue();
        }
    }

    @Test
    public void addEventListeners() {
        TestAgendaEventListener testAgendaEventListener = new TestAgendaEventListener();
        TestRuleRuntimeEventListener testRuleRuntimeEventListener = new TestRuleRuntimeEventListener();
        TestRuleEventListener testRuleEventListener = new TestRuleEventListener();

        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();
        ruleConfig.getAgendaEventListeners().add(testAgendaEventListener);
        ruleConfig.getRuleRuntimeListeners().add(testRuleRuntimeEventListener);
        ruleConfig.getRuleEventListeners().add(testRuleEventListener);

        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Hello World");

        try (RuleUnitInstance<HelloWorldUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit, ruleConfig)) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
            assertThat(testAgendaEventListener.getResults()).containsExactly("matchCreated : HelloWorld", "beforeMatchFired : HelloWorld", "afterMatchFired : HelloWorld");
            assertThat(testRuleRuntimeEventListener.getResults()).containsExactly("objectInserted : Hello World");
            assertThat(testRuleEventListener.getResults()).containsExactly("onBeforeMatchFire : HelloWorld", "onAfterMatchFire : HelloWorld");
        }
    }

    @Test
    public void fireWithAgendaFilter() {
        RuleNameUnit unit = new RuleNameUnit();
        unit.getStrings().add("Hello World");

        try (RuleUnitInstance<RuleNameUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit)) {
            assertThat(unitInstance.fire(new RuleNameStartsWithAgendaFilter("GoodBye"))).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("GoodByeWorld");
        }
    }

    @Test
    public void multipleOOPathLines() {
        MultipleDataSourceUnit unit = new MultipleDataSourceUnit();
        unit.getMeasurements().add(new Measurement("color", "red"));
        unit.getSensors().add(new Sensor("color"));

        try ( RuleUnitInstance<MultipleDataSourceUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getControlSet()).containsExactly("red");
        }
    }
}
