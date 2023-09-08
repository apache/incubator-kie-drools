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

import org.drools.model.functions.Block1;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.patterns.Pattern1Def;
import org.drools.ruleunits.dsl.patterns.Pattern2Def;
import org.drools.ruleunits.dsl.patterns.PatternDef;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;

/**
 * The root of the fluent Java DSL to define a rule.
 */
public interface RuleFactory {

    <A> Pattern1Def<A> on(DataSource<A> dataSource);

    RuleFactory not(Function1<RuleFactory, PatternDef> patternBuilder);

    RuleFactory exists(Function1<RuleFactory, PatternDef> patternBuilder);

    <A, B> Pattern1Def<B> accumulate(Function1<RuleFactory, PatternDef> patternBuilder, Accumulator1<A, B> acc);

    <A, K, V> Pattern2Def<K, V> groupBy(Function1<RuleFactory, PatternDef> patternBuilder, Function1<A, K> groupingFunction, Accumulator1<A, V> acc);

    <T> void execute(T globalObject, Block1<T> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block);
}
