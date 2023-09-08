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

import org.drools.model.DSL;
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
import org.drools.ruleunits.dsl.constraints.Beta2Constraint;
import org.drools.ruleunits.dsl.util.RuleDefinition;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStoreImpl;
import org.kie.api.runtime.rule.RuleContext;

public class Pattern3DefImpl<A, B, C> extends SinglePatternDef<C> implements Pattern3Def<A, B, C> {

    protected final Pattern1DefImpl<A> patternA;
    protected final Pattern1DefImpl<B> patternB;
    protected final Pattern1DefImpl<C> patternC;

    public Pattern3DefImpl(RuleDefinition rule, Pattern1DefImpl<A> patternA, Pattern1DefImpl<B> patternB, Pattern1DefImpl<C> patternC) {
        super(rule, patternC.variable);
        this.patternA = patternA;
        this.patternB = patternB;
        this.patternC = patternC;
    }

    @Override
    public Pattern3DefImpl<A, B, C> filter(Predicate3<A, B, C> predicate) {
        patternC.constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), patternA.variable, patternB.variable, (c,a,b) -> predicate.test((A) a, (B) b, (C) c)));
        return this;
    }

    @Override
    public <V> Pattern3DefImpl<A, B, C> filter(Function1<C, V> leftExtractor, Index.ConstraintType constraintType, Function2<A, B, V> rightExtractor) {
        return filter(null, leftExtractor, constraintType, rightExtractor);
    }

    @Override
    public <V> Pattern3DefImpl<A, B, C> filter(String fieldName, Function1<C, V> leftExtractor, Index.ConstraintType constraintType, Function2<A, B, V> rightExtractor) {
        patternC.constraints.add(new Beta2Constraint<>(variable, fieldName, leftExtractor, constraintType, patternA.variable, patternB.variable, rightExtractor));
        return this;
    }

    @Override
    public <D> Pattern4Def<A, B, C, D> on(DataSource<D> dataSource) {
        return join(rule.on(dataSource));
    }

    @Override
    public <D> Pattern4Def<A, B, C, D> join(Function1<RuleFactory, Pattern1Def<D>> patternBuilder) {
        return join((Pattern1DefImpl) patternBuilder.apply(rule));
    }

    private <D> Pattern4Def<A, B, C, D> join(Pattern1DefImpl<D> other) {
        return new Pattern4DefImpl<>(rule, patternA, patternB, patternC, other);
    }

    @Override
    public void execute(Block3<A, B, C> block) {
        rule.setConsequence( DSL.on(patternA.variable, patternB.variable, variable).execute(block) );
    }

    @Override
    public <G> void execute(G globalObject, Block4<G, A, B, C> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(globalObject), patternA.variable, patternB.variable, variable).execute(block) );
    }

    @Override
    public <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(dataStore)).execute( (drools, ds) -> block.execute(new ConsequenceDataStoreImpl<>((RuleContext) drools, (DataStore<T>) ds))) );
    }

    @Override
    public <T> void executeOnDataStore(DataStore<T> dataStore, Block4<ConsequenceDataStore<T>, A, B, C> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(dataStore), patternA.variable, patternB.variable, variable).execute( (drools, ds, a, b, c) -> block.execute(new ConsequenceDataStoreImpl<>((RuleContext) drools, (DataStore<T>) ds), (A) a, (B) b, (C) c)) );
    }

    public Pattern1DefImpl<A> getPatternA() {
        return patternA;
    }

    public Pattern1DefImpl<B> getPatternB() {
        return patternB;
    }

    @Override
    public InternalPatternDef subPatternFrom(InternalPatternDef from) {
        if (from instanceof Pattern2DefImpl && patternA == ((Pattern2DefImpl<?, ?>) from).getPatternA() && patternB == ((Pattern2DefImpl<?, ?>) from).getPatternB()) {
            return patternC;
        }
        throw new IllegalArgumentException();
    }
}