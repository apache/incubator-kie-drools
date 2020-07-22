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

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Variable;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;

final class UniGroupBy2Map0CollectMutator<A, NewA, NewB> extends AbstractUniGroupByMutator {

    private final Function<A, NewA> groupKeyMappingA;
    private final Function<A, NewB> groupKeyMappingB;

    public UniGroupBy2Map0CollectMutator(Function<A, NewA> groupKeyMappingA, Function<A, NewB> groupKeyMappingB) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        BiConsumer<PatternDef, Variable<NewA>> binder = (pattern, tuple) -> pattern.bind(tuple, a -> {
            final NewA newA = groupKeyMappingA.apply((A) a);
            final NewB newB = groupKeyMappingB.apply((A) a);
            return new BiTuple<>(newA, newB);
        });
        return universalGroup(ruleAssembler, binder,
                (var, pattern, accumulate) -> regroup(ruleAssembler, var, pattern, accumulate));
    }
}
