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
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadAccumulateFunction;

final class QuadGroupBy2Map2CollectFastMutator<A, B, C, D, NewA, NewB, NewC, NewD> extends AbstractQuadGroupByMutator {

    private final QuadFunction<A, B, C, D, NewA> groupKeyMappingA;
    private final QuadFunction<A, B, C, D, NewB> groupKeyMappingB;
    private final QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC;
    private final QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD;

    public QuadGroupBy2Map2CollectFastMutator(QuadFunction<A, B, C, D, NewA> groupKeyMappingA,
            QuadFunction<A, B, C, D, NewB> groupKeyMappingB, QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC,
            QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD) {
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
        Variable<D> inputD = ruleAssembler.getVariable(3);
        Variable<QuadTuple<A, B, C, D>> accumulateSource = ruleAssembler.createVariable(QuadTuple.class, "source");
        ruleAssembler.getLastPrimaryPattern()
                .bind(accumulateSource, inputA, inputB, inputC, (d, a, b, c) -> new QuadTuple<>(a, b, c, d));
        Variable<BiTuple<NewA, NewB>> groupKey = ruleAssembler.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> outputC = ruleAssembler.createVariable("outputC");
        Variable<NewD> outputD = ruleAssembler.createVariable("outputD");
        ViewItem groupByPattern = groupBy(getInnerAccumulatePattern(ruleAssembler), inputA, inputB, inputC, inputD,
                groupKey, (a, b, c, d) -> new BiTuple<>(groupKeyMappingA.apply(a, b, c, d),
                        groupKeyMappingB.apply(a, b, c, d)),
                accFunction(() -> new DroolsQuadAccumulateFunction<>(collectorC), accumulateSource).as(outputC),
                accFunction(() -> new DroolsQuadAccumulateFunction<>(collectorD), accumulateSource).as(outputD));
        Variable<NewA> newA = ruleAssembler.createVariable("newA", from(groupKey, k -> k.a));
        Variable<NewB> newB = ruleAssembler.createVariable("newB", from(groupKey, k -> k.b));
        Variable<NewC> newC = ruleAssembler.createVariable("newC", from(outputC));
        Variable<NewD> newD = ruleAssembler.createVariable("newD", from(outputD));
        return toQuad(ruleAssembler, groupByPattern, newA, newB, newC, newD);
    }
}
