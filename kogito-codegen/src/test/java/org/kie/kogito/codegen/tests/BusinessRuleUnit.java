/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.tests;

import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.RuleUnitMemory;

public class BusinessRuleUnit implements RuleUnitMemory {

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
