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

import org.drools.model.functions.Function1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

import static org.drools.model.Index.ConstraintType.EQUAL;
import static org.drools.model.Index.ConstraintType.NOT_EQUAL;

public class SelfJoinWithInferenceAndNotUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final DataStore<String> alerts;

    public SelfJoinWithInferenceAndNotUnit() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public SelfJoinWithInferenceAndNotUnit(DataStore<String> strings, DataStore<String> alerts) {
        this.strings = strings;
        this.alerts = alerts;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesFactory.rule("Self join")
                .on(strings)
                .on(strings)
                .filter(NOT_EQUAL, Function1.identity())
                .filter(s -> s.substring(0,1), EQUAL, s -> s.substring(0,1))
                .not( rule -> rule.on(alerts) )
                .execute(alerts, (a, s1, s2) -> a.add("Found String with same initial '" + s1 + "' and '" + s2 + "'"));
    }
}
