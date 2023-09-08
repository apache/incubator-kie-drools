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
package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.SingletonStore;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.GREATER_THAN;
import static org.drools.model.functions.Function1.identity;

public class ExistentialUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final SingletonStore<Integer> threshold;
    private final List<String> results = new ArrayList<>();

    public ExistentialUnit() {
        this(DataSource.createStore(), DataSource.createSingleton());
    }

    public ExistentialUnit(DataStore<String> strings, SingletonStore<Integer> threshold) {
        this.strings = strings;
        this.threshold = threshold;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public SingletonStore<Integer> getThreshold() {
        return threshold;
    }

    public List<String> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        // not( /strings[ this == "Hello World" ] )
        rulesFactory.rule("Not exists string 'Hello World'")
                .not( rule -> rule.on(strings).filter(EQUAL, "Hello World") )
                .execute(results, r -> r.add("There's no Hello World"));

        // exists( /strings[ this == "Hello World" ] )
        rulesFactory.rule("Exists string 'Hello World'")
                .exists( rule -> rule.on(strings).filter(EQUAL, "Hello World") )
                .execute(results, r -> r.add("There is at least one Hello World"));

        // $i : /threshold
        // exists( $s: /strings[ length >= $i ] )
        rulesFactory.rule("Exists string longer than threshold")
                .on(threshold)
                .exists( rule -> rule.on(strings).filter(String::length, GREATER_THAN, identity()) )
                .execute(results, (r, t) -> r.add("There is at least a String longer than threshold " + t));
    }
}
