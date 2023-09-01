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

import java.util.UUID;

import org.drools.model.Condition;
import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.RuleFactory;
import org.drools.ruleunits.dsl.accumulate.AccumulatePattern2;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;
import org.drools.ruleunits.dsl.accumulate.GroupByPattern2;
import org.drools.ruleunits.dsl.constraints.AlphaConstraintWithRightExtractor;
import org.drools.ruleunits.dsl.constraints.AlphaConstraintWithRightValue;
import org.drools.ruleunits.dsl.util.RuleDefinition;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStoreImpl;
import org.kie.api.runtime.rule.RuleContext;

import static org.drools.model.functions.Function1.identity;

public class Pattern1DefImpl<A> extends SinglePatternDef<A> implements Pattern1Def<A> {

    public Pattern1DefImpl(RuleDefinition rule, Variable<A> variable) {
        super(rule, variable);
    }

    @Override
    public void execute(Block1<A> block) {
        rule.setConsequence( DSL.on(variable).execute(block) );
    }

    @Override
    public <G> void execute(G globalObject, Block2<G, A> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(globalObject), variable).execute(block) );
    }

    @Override
    public <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(dataStore)).execute( (drools, ds) -> block.execute(new ConsequenceDataStoreImpl<>((RuleContext) drools, (DataStore<T>) ds))) );
    }

    @Override
    public <T> void executeOnDataStore(DataStore<T> dataStore, Block2<ConsequenceDataStore<T>, A> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(dataStore), variable).execute( (drools, ds, a) -> block.execute(new ConsequenceDataStoreImpl<>((RuleContext) drools, (DataStore<T>) ds), (A) a)) );
    }

    @Override
    public <B> Pattern2DefImpl<A, B> on(DataSource<B> dataSource) {
        return join(rule.on(dataSource));
    }


    @Override
    public <B> Pattern2DefImpl<A, B> join(Function1<RuleFactory, Pattern1Def<B>> patternBuilder) {
        return join((Pattern1DefImpl) patternBuilder.apply(rule));
    }

    private <B> Pattern2DefImpl<A, B> join(Pattern1DefImpl<B> other) {
        return new Pattern2DefImpl<>(rule, this, other);
    }

    @Override
    public Pattern1DefImpl<A> filter(Predicate1<A> predicate) {
        constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate));
        return this;
    }

    @Override
    public Pattern1DefImpl<A> filter(String fieldName, Predicate1<A> predicate) {
        constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), predicate, PatternDSL.reactOn(fieldName)));
        return this;
    }

    @Override
    public Pattern1DefImpl<A> filter(Index.ConstraintType constraintType, A rightValue) {
        return filter("this", identity(), constraintType, rightValue);
    }

    @Override
    public <V> Pattern1DefImpl<A> filter(Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
        return filter(null, extractor, constraintType, rightValue);
    }

    @Override
    public <V> Pattern1DefImpl<A> filter(String fieldName, Function1<A, V> extractor, Index.ConstraintType constraintType, V rightValue) {
        constraints.add(new AlphaConstraintWithRightValue<>(variable, fieldName, extractor, constraintType, rightValue));
        return this;
    }

    @Override
    public <V> Pattern1DefImpl<A> filter(Function1<A, V> extractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor) {
        return filter(null, extractor, constraintType, null, rightExtractor);
    }

    @Override
    public <V> Pattern1DefImpl<A> filter(String fieldName, Function1<A, V> extractor, Index.ConstraintType constraintType, String rightFieldName, Function1<A, V> rightExtractor) {
        constraints.add(new AlphaConstraintWithRightExtractor(variable, fieldName, extractor, constraintType, rightFieldName, rightExtractor));
        return this;
    }

    @Override
    public <B, C> Pattern2Def<A, C> accumulate(Function1<Pattern1Def<A>, PatternDef> patternBuilder, Accumulator1<B, C> acc) {
        Pattern1DefImpl<C> patternC = (Pattern1DefImpl) rule.internalCreatePattern(this, patternBuilder);
        Pattern2DefImpl<A, C> accPattern = new AccumulatePattern2<>(rule, this, patternC, acc);
        rule.addPattern(accPattern);
        return accPattern;
    }

    @Override
    public <B, K, V> Pattern3Def<A, K, V> groupBy(Function1<Pattern1Def<A>, PatternDef> patternBuilder, Function1<B, K> groupingFunction, Accumulator1<B, V> acc) {
        GroupByPattern2 groupByPattern = new GroupByPattern2(rule, this, rule.internalCreatePattern(this, patternBuilder), groupingFunction, acc);
        rule.addPattern(groupByPattern);
        return groupByPattern;
    }

    @Override
    public Pattern1DefImpl<A> exists(Function1<Pattern1Def<A>, PatternDef> patternBuilder) {
        rule.addPattern( new ExistentialPatternDef( Condition.Type.EXISTS, rule.internalCreatePattern(this, patternBuilder) ) );
        return this;
    }

    @Override
    public Pattern1DefImpl<A> not(Function1<Pattern1Def<A>, PatternDef> patternBuilder) {
        rule.addPattern( new ExistentialPatternDef( Condition.Type.NOT, rule.internalCreatePattern(this, patternBuilder) ) );
        return this;
    }
}