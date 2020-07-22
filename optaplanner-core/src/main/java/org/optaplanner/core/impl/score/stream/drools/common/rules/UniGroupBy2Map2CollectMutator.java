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

import java.util.function.Function;

import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniToQuadGroupByAccumulator;

final class UniGroupBy2Map2CollectMutator<A, NewA, NewB, NewC, NewD> extends AbstractUniGroupByMutator {

    private final Function<A, NewA> groupKeyMappingA;
    private final Function<A, NewB> groupKeyMappingB;
    private final UniConstraintCollector<A, ?, NewC> collectorC;
    private final UniConstraintCollector<A, ?, NewD> collectorD;

    public UniGroupBy2Map2CollectMutator(Function<A, NewA> groupKeyMappingA, Function<A, NewB> groupKeyMappingB,
            UniConstraintCollector<A, ?, NewC> collectorC, UniConstraintCollector<A, ?, NewD> collectorD) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.collectorC = collectorC;
        this.collectorD = collectorD;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        return groupBiWithCollectBi(ruleAssembler, () -> new DroolsUniToQuadGroupByAccumulator<>(groupKeyMappingA,
                groupKeyMappingB, collectorC, collectorD, ruleAssembler.getVariable(0)));
    }
}
