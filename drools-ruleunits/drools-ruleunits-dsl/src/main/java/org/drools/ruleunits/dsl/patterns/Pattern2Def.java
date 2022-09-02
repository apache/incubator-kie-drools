/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.dsl.patterns;

import org.drools.model.Index;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate2;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.dsl.RuleFactory;

public interface Pattern2Def<A, B> extends PatternDef {

    Pattern2Def<A, B> filter(Predicate2<A, B> predicate);

    Pattern2Def<A, B> filter(Index.ConstraintType constraintType, Function1<A, B> rightExtractor);

    <V> Pattern2Def<A, B> filter(Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor);

    <V> Pattern2Def<A, B> filter(String fieldName, Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor);

    <C> Pattern3Def<A, B, C> on(DataSource<C> dataSource);

    <C> Pattern3Def<A, B, C> join(Function1<RuleFactory, Pattern1Def<C>> patternBuilder);

    void execute(Block2<A, B> block);

    <G> void execute(G globalObject, Block3<G, A, B> block);
}
