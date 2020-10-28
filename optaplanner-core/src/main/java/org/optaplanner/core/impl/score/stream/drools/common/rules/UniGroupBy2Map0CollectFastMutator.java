/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.stream.drools.common.rules;

import static org.drools.model.PatternDSL.groupBy;
import static org.drools.modelcompiler.dsl.flow.D.from;

import java.util.function.Function;

import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;

final class UniGroupBy2Map0CollectFastMutator<A, NewA, NewB> extends AbstractUniGroupByMutator {

    private final Function<A, NewA> groupKeyMappingA;
    private final Function<A, NewB> groupKeyMappingB;

    public UniGroupBy2Map0CollectFastMutator(Function<A, NewA> groupKeyMappingA, Function<A, NewB> groupKeyMappingB) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        Variable<A> input = ruleAssembler.getVariable(0);
        Variable<BiTuple<NewA, NewB>> groupKey = ruleAssembler.createVariable(BiTuple.class, "groupKey");
        ViewItem groupByPattern = groupBy(getInnerAccumulatePattern(ruleAssembler), input, groupKey,
                a -> new BiTuple<>(groupKeyMappingA.apply(a), groupKeyMappingB.apply(a)));
        Variable<NewA> newA = ruleAssembler.createVariable("newA", from(groupKey, k -> k.a));
        Variable<NewA> newB = ruleAssembler.createVariable("newB", from(groupKey, k -> k.b));
        return toBi(ruleAssembler, groupByPattern, newA, newB);
    }
}
