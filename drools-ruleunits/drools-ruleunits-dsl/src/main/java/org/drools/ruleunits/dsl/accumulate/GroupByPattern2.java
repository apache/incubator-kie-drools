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
package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.functions.Function1;
import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.patterns.InternalPatternDef;
import org.drools.ruleunits.dsl.patterns.Pattern1DefImpl;
import org.drools.ruleunits.dsl.patterns.Pattern3DefImpl;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.DSL.declarationOf;
import static org.drools.ruleunits.dsl.accumulate.GroupByPattern1.createGroupByItem;

public class GroupByPattern2<A, B, K, V> extends Pattern3DefImpl<A, K, V> {

    private final InternalPatternDef pattern;
    private final Function1<B, K> groupingFunction;
    private final Accumulator1<B, V> acc;

    public GroupByPattern2(RuleDefinition rule, Pattern1DefImpl<A> patternA, InternalPatternDef pattern, Function1<B, K> groupingFunction, Accumulator1<B, V> acc) {
        super(rule, patternA, new Pattern1DefImpl(rule, declarationOf( Object.class )), new Pattern1DefImpl(rule, declarationOf( Object.class )));
        this.pattern = pattern;
        this.groupingFunction = groupingFunction;
        this.acc = acc;
    }

    @Override
    public ViewItem toExecModelItem() {
        return createGroupByItem(pattern, groupingFunction, acc, patternB.getVariable(), patternC.getVariable());
    }
}
