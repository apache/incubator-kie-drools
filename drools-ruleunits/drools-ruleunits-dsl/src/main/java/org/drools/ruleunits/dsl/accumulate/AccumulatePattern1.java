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

import org.drools.model.DSL;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.patterns.Pattern1DefImpl;
import org.drools.ruleunits.dsl.patterns.InternalPatternDef;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.declarationOf;

public class AccumulatePattern1<A, B> extends Pattern1DefImpl<B> {

    private final InternalPatternDef pattern;
    private final Accumulator1<A, B> acc;

    public AccumulatePattern1(RuleDefinition rule, InternalPatternDef pattern, Accumulator1<A, B> acc) {
        super(rule, declarationOf( acc.getAccClass() ));
        this.pattern = pattern;
        this.acc = acc;
    }

    @Override
    public ViewItem toExecModelItem() {
        return createAccumulate1Item(pattern, getVariable(), acc);
    }

    static ExprViewItem<Object> createAccumulate1Item(InternalPatternDef pattern, Variable variable, Accumulator1 acc) {
        ViewItem patternDef = pattern.toExecModelItem();
        Variable boundVar = declarationOf( acc.getAccClass());
        bindAccVar(acc, patternDef, boundVar);
        return DSL.accumulate(patternDef, accFunction(acc.getAccFuncSupplier(), boundVar).as(variable));
    }

    static void bindAccVar(Accumulator1 acc, ViewItem patternDef, Variable boundVar) {
        if (patternDef instanceof PatternDSL.PatternDef) {
            ((PatternDSL.PatternDef) patternDef).bind(boundVar, acc.getBindingFunc());
        } else {
            ViewItem[] items = ((CombinedExprViewItem) patternDef).getExpressions();
            bindAccVar(acc, items[items.length-1], boundVar);
        }
    }
}