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
import org.drools.model.functions.Block2;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;

public interface Pattern1Def<A> extends PatternDef {

    Pattern1Def<A> filter(Predicate1<A> predicate);

    Pattern1Def<A> filter(String fieldName, Predicate1<A> predicate);

    Pattern1Def<A> filter(Index.ConstraintType constraintType, A rightValue);

    <V> Pattern1Def<A> filter(Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue);

    <V> Pattern1Def<A> filter(String fieldName, Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue);

    <V> Pattern1Def<A> filter(Function1<A, V> extractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor);

    <V> Pattern1Def<A> filter(String fieldName, Function1<A, V> extractor, Index.ConstraintType constraintType, String rightFieldName, Function1<A, V> rightExtractor);

    <B> Pattern2Def<A, B> on(DataSource<B> dataSource);

    <B> Pattern2Def<A, B> join(Function1<RuleFactory, Pattern1Def<B>> patternBuilder);

    <B, C> Pattern2Def<A, C> accumulate(Function1<Pattern1Def<A>, PatternDef> patternBuilder, Accumulator1<B, C> acc);

    <B, K, V> Pattern3Def<A, K, V> groupBy(Function1<Pattern1Def<A>, PatternDef> patternBuilder, Function1<B, K> groupingFunction, Accumulator1<B, V> acc);

    Pattern1Def<A> exists(Function1<Pattern1Def<A>, PatternDef> patternBuilder);

    Pattern1Def<A> not(Function1<Pattern1Def<A>, PatternDef> patternBuilder);

    void execute(Block1<A> block);

    <G> void execute(G globalObject, Block1<G> block);

    <G> void execute(G globalObject, Block2<G, A> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block2<ConsequenceDataStore<T>, A> block);
}
