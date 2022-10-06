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
package org.drools.ruleunits.impl;

import java.util.Objects;

import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.junit.jupiter.api.Test;
import org.kie.api.builder.CompilationErrorsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class InterpretedRuleUnitTest {

    @Test
    public void testHelloWorldInterpreted() {
        HelloWorld unit = new HelloWorld();
        unit.getStrings().add("Hello World");

        try ( RuleUnitInstance<HelloWorld> unitInstance = InterpretedRuleUnit.instance(unit) ) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
        }
    }

    @Test
    public void testHelloWorldCompiled() {
        HelloWorld unit = new HelloWorld();
        unit.getStrings().add("Hello World");

        try ( RuleUnitInstance<HelloWorld> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
        }
    }

    @Test
    public void testHelloWorldGenerated() {
        HelloWorld unit = new HelloWorld();
        unit.getStrings().add("Hello World");

        try ( RuleUnitInstance<HelloWorld> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
        }
    }

    @Test
    public void testNotWithAndWithoutSingleQuote() {
        NotTestUnit unit = new NotTestUnit();

        try ( RuleUnitInstance<NotTestUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {
            assertThat(unitInstance.fire()).isEqualTo(2);
        }
    }

    @Test
    public void testLogicalAdd() {
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
    public void testUpdate() {
        UpdateTestUnit unit = new UpdateTestUnit();

        try ( RuleUnitInstance<UpdateTestUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit) ) {

            unit.getPersons().add(new Person("Mario", 17));

            assertThat(unitInstance.fire()).isEqualTo(2);
            assertThat(unit.getResults()).containsExactly("ok");
        }
    }

    @Test
    public void testWrongType() {
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
}
