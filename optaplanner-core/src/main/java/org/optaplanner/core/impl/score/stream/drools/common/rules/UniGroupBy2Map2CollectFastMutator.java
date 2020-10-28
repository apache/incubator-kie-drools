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

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.from;
import static org.drools.model.DSL.groupBy;

import java.util.function.Function;

import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniAccumulateFunction;

final class UniGroupBy2Map2CollectFastMutator<A, NewA, NewB, NewC, NewD> extends AbstractUniGroupByMutator {

    private final Function<A, NewA> groupKeyMappingA;
    private final Function<A, NewB> groupKeyMappingB;
    private final UniConstraintCollector<A, ?, NewC> collectorC;
    private final UniConstraintCollector<A, ?, NewD> collectorD;

    public UniGroupBy2Map2CollectFastMutator(Function<A, NewA> groupKeyMappingA, Function<A, NewB> groupKeyMappingB,
            UniConstraintCollector<A, ?, NewC> collectorC, UniConstraintCollector<A, ?, NewD> collectorD) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.collectorC = collectorC;
        this.collectorD = collectorD;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        Variable<A> input = ruleAssembler.getVariable(0);
        Variable<BiTuple<NewA, NewB>> groupKey = ruleAssembler.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> outputC = ruleAssembler.createVariable("outputC");
        Variable<NewD> outputD = ruleAssembler.createVariable("outputD");
        ViewItem groupByPattern = groupBy(getInnerAccumulatePattern(ruleAssembler), input, groupKey,
                a -> new BiTuple<>(groupKeyMappingA.apply(a), groupKeyMappingB.apply(a)),
                accFunction(() -> new DroolsUniAccumulateFunction<>(collectorC), input).as(outputC),
                accFunction(() -> new DroolsUniAccumulateFunction<>(collectorD), input).as(outputD));
        Variable<NewA> newA = ruleAssembler.createVariable("newA", from(groupKey, k -> k.a));
        Variable<NewB> newB = ruleAssembler.createVariable("newB", from(groupKey, k -> k.b));
        Variable<NewC> newC = ruleAssembler.createVariable("newC", from(outputC));
        Variable<NewD> newD = ruleAssembler.createVariable("newD", from(outputD));
        return toQuad(ruleAssembler, groupByPattern, newA, newB, newC, newD);
    }
}
