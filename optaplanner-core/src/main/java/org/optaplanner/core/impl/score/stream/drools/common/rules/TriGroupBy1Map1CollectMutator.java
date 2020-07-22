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

import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriToBiGroupByAccumulator;

class TriGroupBy1Map1CollectMutator<A, B, C, NewA, NewB> extends AbstractTriGroupByMutator {

    private final TriFunction<A, B, C, NewA> groupKeyMappingA;
    private final TriConstraintCollector<A, B, C, ?, NewB> collectorB;

    public TriGroupBy1Map1CollectMutator(TriFunction<A, B, C, NewA> groupKeyMappingA,
            TriConstraintCollector<A, B, C, ?, NewB> collectorB) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.collectorB = collectorB;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        return groupWithCollect(ruleAssembler, () -> new DroolsTriToBiGroupByAccumulator<>(groupKeyMappingA, collectorB,
                ruleAssembler.getVariable(0), ruleAssembler.getVariable(1), ruleAssembler.getVariable(2)));
    }
}
