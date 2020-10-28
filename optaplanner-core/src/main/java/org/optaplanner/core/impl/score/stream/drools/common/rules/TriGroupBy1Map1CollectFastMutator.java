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
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriAccumulateFunction;

final class TriGroupBy1Map1CollectFastMutator<A, B, C, NewA, NewB> extends AbstractTriGroupByMutator {

    private final TriFunction<A, B, C, NewA> groupKeyMapping;
    private final TriConstraintCollector<A, B, C, ?, NewB> collectorB;

    public TriGroupBy1Map1CollectFastMutator(TriFunction<A, B, C, NewA> groupKeyMapping,
            TriConstraintCollector<A, B, C, ?, NewB> collectorB) {
        this.groupKeyMapping = groupKeyMapping;
        this.collectorB = collectorB;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        Variable<A> inputA = ruleAssembler.getVariable(0);
        Variable<B> inputB = ruleAssembler.getVariable(1);
        Variable<C> inputC = ruleAssembler.getVariable(2);
        Variable<TriTuple<A, B, C>> accumulateSource = ruleAssembler.createVariable(TriTuple.class, "source");
        ruleAssembler.getLastPrimaryPattern()
                .bind(accumulateSource, inputA, inputB, (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<NewA> groupKey = ruleAssembler.createVariable("groupKey");
        Variable<NewB> output = ruleAssembler.createVariable("output");
        ViewItem groupByPattern = groupBy(getInnerAccumulatePattern(ruleAssembler), inputA, inputB, inputC, groupKey,
                groupKeyMapping::apply,
                accFunction(() -> new DroolsTriAccumulateFunction<>(collectorB), accumulateSource).as(output));
        Variable<NewA> newA = ruleAssembler.createVariable("newA", from(groupKey));
        Variable<NewB> newB = ruleAssembler.createVariable("newB", from(output));
        return toBi(ruleAssembler, groupByPattern, newA, newB);
    }
}
