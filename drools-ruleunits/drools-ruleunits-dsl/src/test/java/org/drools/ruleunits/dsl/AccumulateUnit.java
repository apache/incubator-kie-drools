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
import static org.drools.model.Index.ConstraintType.NOT_EQUAL;
import static org.drools.model.functions.Function1.identity;
import static org.drools.ruleunits.dsl.Accumulators.avg;
import static org.drools.ruleunits.dsl.Accumulators.count;
import static org.drools.ruleunits.dsl.Accumulators.max;
import static org.drools.ruleunits.dsl.Accumulators.sum;

public class AccumulateUnit implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final SingletonStore<Integer> threshold;
    private final List<String> results = new ArrayList<>();


    public AccumulateUnit() {
        this(DataSource.createStore(), DataSource.createSingleton());
    }

    public AccumulateUnit(DataStore<String> strings, SingletonStore<Integer> threshold) {
        this.strings = strings;
        this.threshold = threshold;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public List<String> getResults() {
        return results;
    }

    public SingletonStore<Integer> getThreshold() {
        return threshold;
    }

    @Override
    public void defineRules(RulesFactory rulesFactory) {
        // accumulate( $s: /strings[ this.substring(0, 1) == "A" ]; sum($s.length) )
        rulesFactory.rule("Sum length of String starting with A")
                .accumulate( rule -> rule.on(strings).filter(s -> s.substring(0, 1), EQUAL, "A"),
                             sum(String::length) )
                .execute(results, (r, sum) -> r.add("Sum of length of Strings starting with A is " + sum));

        // accumulate( $s: /strings[ this.substring(0, 1) != "A" ]; max($s.length) )
        rulesFactory.rule("Find mac length of String not starting with A")
                .accumulate( rule -> rule.on(strings).filter(s -> s.substring(0, 1), NOT_EQUAL, "A"),
                             max(String::length) )
                .execute(results, (r, max) -> r.add("Max length of Strings not starting with A is " + max));

        // $t : /threshold
        // accumulate( $s: /strings[ length >= $t ]; avg($s.length) )
        rulesFactory.rule("Find average length of String above a threshold")
                .on(threshold)
                // the context inside the accumulate brings in the patterns bind so far to allow using them
                .accumulate( rule -> rule.on(strings)
                                         .filter(String::length, GREATER_THAN, identity()), // beta constraint with the threshold as right field
                             avg(String::length) )
                .execute(results, (r, t, avg) -> r.add("Average length of Strings longer than threshold " + t + " is " + avg));

        // the join is in the accumulate, so this rule fires (having 0 as result) even without any matching tuple
        // accumulate( $t : /threshold and $s: /strings[ length >= $t ]; sum($s.length) )
        rulesFactory.rule("Count Strings having length above a threshold")
                .accumulate( rule -> rule.on(threshold).on(strings).filter(String::length, GREATER_THAN, identity()),
                             count() )
                .execute(results, (r, count) -> r.add("Count of Strings above threshold is " + count));
    }
}
