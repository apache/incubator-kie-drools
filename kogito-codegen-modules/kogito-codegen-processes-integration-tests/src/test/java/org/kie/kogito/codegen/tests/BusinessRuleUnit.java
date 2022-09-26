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
package org.kie.kogito.codegen.tests;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitData;
import org.kie.kogito.codegen.data.Person;

public class BusinessRuleUnit implements RuleUnitData {

    private DataStore<Person> persons = DataSource.createStore();
    private DataStream<String> strings = DataSource.createStream();
    private String myGlobal;

    public DataStore<Person> getPersons() {
        return persons;
    }

    public DataStream<String> getStrings() {
        return strings;
    }

    public void setMyGlobal(String myGlobal) {
        this.myGlobal = myGlobal;
    }

    public String getMyGlobal() {
        return myGlobal;
    }
}
