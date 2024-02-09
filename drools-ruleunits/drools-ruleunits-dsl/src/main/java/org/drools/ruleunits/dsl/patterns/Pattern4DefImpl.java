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
import org.drools.model.functions.Block4;
import org.drools.model.functions.Block5;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function3;
import org.drools.model.functions.Predicate4;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.dsl.constraints.Beta3Constraint;
import org.drools.ruleunits.dsl.util.RuleDefinition;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStoreImpl;
import org.kie.api.runtime.rule.RuleContext;

public class Pattern4DefImpl<A, B, C, D> extends SinglePatternDef<D> implements Pattern4Def<A, B, C, D> {

    protected final Pattern1DefImpl<A> patternA;
    protected final Pattern1DefImpl<B> patternB;
    protected final Pattern1DefImpl<C> patternC;
    protected final Pattern1DefImpl<D> patternD;

    public Pattern4DefImpl(RuleDefinition rule, Pattern1DefImpl<A> patternA, Pattern1DefImpl<B> patternB, Pattern1DefImpl<C> patternC, Pattern1DefImpl<D> patternD) {
        super(rule, patternD.variable);
        this.patternA = patternA;
        this.patternB = patternB;
        this.patternC = patternC;
        this.patternD = patternD;
    }

    @Override
    public Pattern4DefImpl<A, B, C, D> filter(Predicate4<A, B, C, D> predicate) {
        patternD.constraints.add(patternDef -> patternDef.expr(UUID.randomUUID().toString(), patternA.variable, patternB.variable, patternC.variable, (d,a,b,c) -> predicate.test((A) a, (B) b, (C) c, (D) c)));
        return this;
    }

    @Override
    public <V> Pattern4DefImpl<A, B, C, D> filter(Function1<D, V> leftExtractor, Index.ConstraintType constraintType, Function3<A, B, C, V> rightExtractor) {
        return filter(null, leftExtractor, constraintType, rightExtractor);
    }

    @Override
    public <V> Pattern4DefImpl<A, B, C, D> filter(String fieldName, Function1<D, V> leftExtractor, Index.ConstraintType constraintType, Function3<A, B, C, V> rightExtractor) {
        patternD.constraints.add(new Beta3Constraint<>(variable, fieldName, leftExtractor, constraintType, patternA.variable, patternB.variable, patternC.variable, rightExtractor));
        return this;
    }

    @Override
    public void execute(Block4<A, B, C, D> block) {
        rule.setConsequence( DSL.on(patternA.variable, patternB.variable, patternC.variable, variable).execute(block) );
    }

    @Override
    public <G> void execute(G globalObject, Block5<G, A, B, C, D> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(globalObject), patternA.variable, patternB.variable, patternC.variable, variable).execute(block) );
    }


    @Override
    public <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(dataStore)).execute( (drools, ds) -> block.execute(new ConsequenceDataStoreImpl<>((RuleContext) drools, (DataStore<T>) ds))) );
    }

    @Override
    public <T> void executeOnDataStore(DataStore<T> dataStore, Block5<ConsequenceDataStore<T>, A, B, C, D> block) {
        rule.setConsequence( DSL.on(rule.asGlobal(dataStore), patternA.variable, patternB.variable, patternC.variable, variable).execute( (drools, ds, a, b, c, d) -> block.execute(new ConsequenceDataStoreImpl<>((RuleContext) drools, (DataStore<T>) ds), (A) a, (B) b, (C) c, (D) d)) );
    }

    @Override
    public InternalPatternDef subPatternFrom(InternalPatternDef from) {
        throw new UnsupportedOperationException();
    }
}