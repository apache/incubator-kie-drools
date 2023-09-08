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
import org.drools.model.functions.Block4;
import org.drools.model.functions.Block5;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function3;
import org.drools.model.functions.Predicate4;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;

public interface Pattern4Def<A, B, C, D> extends PatternDef {

    Pattern4Def<A, B, C, D> filter(Predicate4<A, B, C, D> predicate);

    <V> Pattern4Def<A, B, C, D> filter(Function1<D, V> leftExtractor, Index.ConstraintType constraintType, Function3<A, B, C, V> rightExtractor);
    <V> Pattern4Def<A, B, C, D> filter(String fieldName, Function1<D, V> leftExtractor, Index.ConstraintType constraintType, Function3<A, B, C, V> rightExtractor);

    void execute(Block4<A, B, C, D> block);

    <G> void execute(G globalObject, Block5<G, A, B, C, D> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block5<ConsequenceDataStore<T>, A, B, C, D> block);
}
