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

import java.util.function.BiFunction;

import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiAccumulateFunction;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;

class BiGroupBy1Map1CollectFastMutator<A, B, NewA, NewB> extends AbstractBiGroupByMutator {

    private final BiFunction<A, B, NewA> groupKeyMappingA;
    private final BiConstraintCollector<A, B, ?, NewB> collectorB;

    public BiGroupBy1Map1CollectFastMutator(BiFunction<A, B, NewA> groupKeyMappingA,
            BiConstraintCollector<A, B, ?, NewB> collectorB) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.collectorB = collectorB;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        Variable<A> inputA = ruleAssembler.getVariable(0);
        Variable<B> inputB = ruleAssembler.getVariable(1);
        Variable<BiTuple<A, B>> accumulateSource = ruleAssembler.createVariable(BiTuple.class, "source");
        ruleAssembler.getLastPrimaryPattern()
                .bind(accumulateSource, inputA, (b, a) -> new BiTuple<>(a, b));
        Variable<NewA> groupKey = ruleAssembler.createVariable("groupKey");
        Variable<NewB> output = ruleAssembler.createVariable("output");
        ViewItem groupByPattern = groupBy(getInnerAccumulatePattern(ruleAssembler), inputA, inputB, groupKey,
                groupKeyMappingA::apply,
                accFunction(() -> new DroolsBiAccumulateFunction<>(collectorB), accumulateSource).as(output));
        Variable<NewA> newA = ruleAssembler.createVariable("newA", from(groupKey));
        Variable<NewB> newB = ruleAssembler.createVariable("newB", from(output));
        return toBi(ruleAssembler, groupByPattern, newA, newB);
    }
}
