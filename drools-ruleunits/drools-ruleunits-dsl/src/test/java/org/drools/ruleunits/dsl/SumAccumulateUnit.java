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

import static org.drools.model.functions.Function1.identity;
import static org.drools.ruleunits.dsl.Accumulators.sum;

public class SumAccumulateUnit implements RuleUnitDefinition {
    private final DataStore<Integer> integers;
    private List<Integer> results = new ArrayList<>();

    public SumAccumulateUnit() {
        this(DataSource.createStore());
    }

    public SumAccumulateUnit(DataStore<Integer> integers) {
        this.integers = integers;
    }

    public DataStore<Integer> getIntegers() {
        return integers;
    }

    public List<Integer> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        rulesFactory.rule("Sum of Integers")
                .accumulate( rule -> rule.on(integers), sum(identity()) )
                .execute(results, List::add);
    }
}
