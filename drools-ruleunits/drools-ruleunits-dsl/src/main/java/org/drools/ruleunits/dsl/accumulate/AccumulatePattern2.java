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
package org.drools.ruleunits.dsl.accumulate;

import org.drools.model.view.ViewItem;
import org.drools.ruleunits.dsl.patterns.Pattern1DefImpl;
import org.drools.ruleunits.dsl.patterns.Pattern2DefImpl;
import org.drools.ruleunits.dsl.util.RuleDefinition;

import static org.drools.ruleunits.dsl.accumulate.AccumulatePattern1.toAccumulate1Item;

public class AccumulatePattern2<A, B, C> extends Pattern2DefImpl<A, C> {

    private final Accumulator1<B, C> acc;

    private AccumulatePattern2(RuleDefinition rule, Pattern1DefImpl<A> patternA, Pattern1DefImpl<C> patternC, Accumulator1<B, C> acc) {
        super(rule, patternA, patternC);
        this.acc = acc;
    }

    @Override
    public ViewItem toExecModelItem() {
        return toAccumulate1Item(patternB, getVariable(), acc);
    }

    public static <A, B, C> AccumulatePattern2<A, B, C> createAccumulatePattern2(RuleDefinition rule, Pattern1DefImpl<A> patternA, Pattern1DefImpl<B> patternB, Accumulator1<B, C> acc) {
        return new AccumulatePattern2<>(rule, patternA, (Pattern1DefImpl<C>) patternB, acc);
    }
}