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

import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriAccumulateFunction;

final class TriGroupBy2Map2CollectFastMutator<A, B, C, NewA, NewB, NewC, NewD> extends AbstractTriGroupByMutator {

    private final TriFunction<A, B, C, NewA> groupKeyMappingA;
    private final TriFunction<A, B, C, NewB> groupKeyMappingB;
    private final TriConstraintCollector<A, B, C, ?, NewC> collectorC;
    private final TriConstraintCollector<A, B, C, ?, NewD> collectorD;

    public TriGroupBy2Map2CollectFastMutator(TriFunction<A, B, C, NewA> groupKeyMappingA,
            TriFunction<A, B, C, NewB> groupKeyMappingB, TriConstraintCollector<A, B, C, ?, NewC> collectorC,
            TriConstraintCollector<A, B, C, ?, NewD> collectorD) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.collectorC = collectorC;
        this.collectorD = collectorD;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        Variable<A> inputA = ruleAssembler.getVariable(0);
        Variable<B> inputB = ruleAssembler.getVariable(1);
        Variable<C> inputC = ruleAssembler.getVariable(2);
        Variable<TriTuple<A, B, C>> accumulateSource = ruleAssembler.createVariable(TriTuple.class, "source");
        ruleAssembler.getLastPrimaryPattern()
                .bind(accumulateSource, inputA, inputB, (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<BiTuple<NewA, NewB>> groupKey = ruleAssembler.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> outputC = ruleAssembler.createVariable("outputC");
        Variable<NewD> outputD = ruleAssembler.createVariable("outputD");
        ViewItem groupByPattern = groupBy(getInnerAccumulatePattern(ruleAssembler), inputA, inputB, inputC, groupKey,
                (a, b, c) -> new BiTuple<>(groupKeyMappingA.apply(a, b, c), groupKeyMappingB.apply(a, b, c)),
                accFunction(() -> new DroolsTriAccumulateFunction<>(collectorC), accumulateSource).as(outputC),
                accFunction(() -> new DroolsTriAccumulateFunction<>(collectorD), accumulateSource).as(outputD));
        Variable<NewA> newA = ruleAssembler.createVariable("newA", from(groupKey, k -> k.a));
        Variable<NewB> newB = ruleAssembler.createVariable("newB", from(groupKey, k -> k.b));
        Variable<NewC> newC = ruleAssembler.createVariable("newC", from(outputC));
        Variable<NewD> newD = ruleAssembler.createVariable("newD", from(outputD));
        return toQuad(ruleAssembler, groupByPattern, newA, newB, newC, newD);
    }
}
