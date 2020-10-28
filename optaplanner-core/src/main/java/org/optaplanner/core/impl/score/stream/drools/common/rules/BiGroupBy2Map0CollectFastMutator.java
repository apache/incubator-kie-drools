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

import static org.drools.model.PatternDSL.from;
import static org.drools.model.PatternDSL.groupBy;

import java.util.function.BiFunction;

import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;

final class BiGroupBy2Map0CollectFastMutator<A, B, NewA, NewB> extends AbstractBiGroupByMutator {

    private final BiFunction<A, B, NewA> groupKeyMappingA;
    private final BiFunction<A, B, NewB> groupKeyMappingB;

    public BiGroupBy2Map0CollectFastMutator(BiFunction<A, B, NewA> groupKeyMappingA,
            BiFunction<A, B, NewB> groupKeyMappingB) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        Variable<A> inputA = ruleAssembler.getVariable(0);
        Variable<B> inputB = ruleAssembler.getVariable(1);
        Variable<BiTuple<NewA, NewB>> groupKey = ruleAssembler.createVariable(BiTuple.class, "groupKey");
        ViewItem groupByPattern = groupBy(getInnerAccumulatePattern(ruleAssembler), inputA, inputB, groupKey,
                (a, b) -> new BiTuple<>(groupKeyMappingA.apply(a, b), groupKeyMappingB.apply(a, b)));
        Variable<NewA> newA = ruleAssembler.createVariable("newA", from(groupKey, k -> k.a));
        Variable<NewB> newB = ruleAssembler.createVariable("newB", from(groupKey, k -> k.b));
        return toBi(ruleAssembler, groupByPattern, newA, newB);
    }
}
