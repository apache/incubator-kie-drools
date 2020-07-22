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

import java.util.function.BiFunction;

import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiToQuadGroupByAccumulator;

final class BiGroupBy2Map2CollectMutator<A, B, NewA, NewB, NewC, NewD> extends AbstractBiGroupByMutator {

    private final BiFunction<A, B, NewA> groupKeyMappingA;
    private final BiFunction<A, B, NewB> groupKeyMappingB;
    private final BiConstraintCollector<A, B, ?, NewC> collectorC;
    private final BiConstraintCollector<A, B, ?, NewD> collectorD;

    public BiGroupBy2Map2CollectMutator(BiFunction<A, B, NewA> groupKeyMappingA,
            BiFunction<A, B, NewB> groupKeyMappingB, BiConstraintCollector<A, B, ?, NewC> collectorC,
            BiConstraintCollector<A, B, ?, NewD> collectorD) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.collectorC = collectorC;
        this.collectorD = collectorD;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        return groupBiWithCollectBi(ruleAssembler, () -> new DroolsBiToQuadGroupByAccumulator<>(groupKeyMappingA,
                groupKeyMappingB, collectorC, collectorD, ruleAssembler.getVariable(0), ruleAssembler.getVariable(1)));
    }
}
