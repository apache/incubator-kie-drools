/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.unit;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.annotation.When;
import org.kie.kogito.codegen.data.Person;

public class AnnotatedRules implements RuleUnitData {

    private final DataStore<Person> persons = DataSource.createStore();

    public void adult(@When("/persons[ age >= 18 ]") Person person) {
        System.out.println(person);
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    public AnnotatedRules getUnit() {
        return this;
    }
}
