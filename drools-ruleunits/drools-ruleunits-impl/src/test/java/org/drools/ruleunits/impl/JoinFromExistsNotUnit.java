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
package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.impl.domain.Person;

public class JoinFromExistsNotUnit implements RuleUnitData {
    private final List<String> results = new ArrayList<>();
    private final DataStore<Person> persons;
    private final DataStore<String> strings;
    private final DataStore<Integer> integers;

    public JoinFromExistsNotUnit() {
        this(DataSource.createStore(), DataSource.createStore(), DataSource.createStore());
    }

    public JoinFromExistsNotUnit(DataStore<Person> persons, DataStore<String> strings, DataStore<Integer> integers) {
        this.persons = persons;
        this.strings = strings;
        this.integers = integers;
    }

    public DataStore<Person> getPersons() {
        return persons;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public DataStore<Integer> getIntegers() {
        return integers;
    }

    public List<String> getResults() {
        return results;
    }
}
