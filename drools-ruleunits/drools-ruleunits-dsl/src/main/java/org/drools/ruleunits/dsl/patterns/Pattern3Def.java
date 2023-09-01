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
package org.drools.ruleunits.dsl.patterns;

import org.drools.model.Index;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Block4;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Predicate3;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;

public interface Pattern3Def<A, B, C> extends PatternDef {

    Pattern3Def<A, B, C> filter(Predicate3<A, B, C> predicate);

    <V> Pattern3Def<A, B, C> filter(Function1<C, V> leftExtractor, Index.ConstraintType constraintType, Function2<A, B, V> rightExtractor);
    <V> Pattern3Def<A, B, C> filter(String fieldName, Function1<C, V> leftExtractor, Index.ConstraintType constraintType, Function2<A, B, V> rightExtractor);

    <D> Pattern4Def<A, B, C, D> on(DataSource<D> dataSource);

    <D> Pattern4Def<A, B, C, D> join(Function1<RuleFactory, Pattern1Def<D>> patternBuilder);

    void execute(Block3<A, B, C> block);

    <G> void execute(G globalObject, Block4<G, A, B, C> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block4<ConsequenceDataStore<T>, A, B, C> block);
}
