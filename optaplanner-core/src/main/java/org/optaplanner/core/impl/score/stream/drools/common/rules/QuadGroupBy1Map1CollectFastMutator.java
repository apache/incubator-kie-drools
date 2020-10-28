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
import static org.drools.model.PatternDSL.from;
import static org.drools.model.PatternDSL.groupBy;

import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadAccumulateFunction;

final class QuadGroupBy1Map1CollectFastMutator<A, B, C, D, NewA, NewB> extends AbstractQuadGroupByMutator {

    private final QuadFunction<A, B, C, D, NewA> groupKeyMappingA;
    private final QuadConstraintCollector<A, B, C, D, ?, NewB> collectorB;

    public QuadGroupBy1Map1CollectFastMutator(QuadFunction<A, B, C, D, NewA> groupKeyMappingA,
            QuadConstraintCollector<A, B, C, D, ?, NewB> collectorB) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.collectorB = collectorB;
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
        Variable<NewA> groupKey = ruleAssembler.createVariable("groupKey");
        Variable<NewB> output = ruleAssembler.createVariable("output");
        ViewItem groupByPattern = groupBy(getInnerAccumulatePattern(ruleAssembler), inputA, inputB, inputC, inputD,
                groupKey, groupKeyMappingA::apply,
                accFunction(() -> new DroolsQuadAccumulateFunction<>(collectorB), accumulateSource).as(output));
        Variable<NewA> newA = ruleAssembler.createVariable("newA", from(groupKey));
        Variable<NewB> newB = ruleAssembler.createVariable("newB", from(output));
        return toBi(ruleAssembler, groupByPattern, newA, newB);
    }
}
