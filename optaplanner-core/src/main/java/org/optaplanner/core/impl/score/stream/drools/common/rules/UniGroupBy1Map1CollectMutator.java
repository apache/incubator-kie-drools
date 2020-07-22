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
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniToBiGroupByAccumulator;

final class UniGroupBy1Map1CollectMutator<A, NewA, NewB> extends AbstractUniGroupByMutator {

    private final Function<A, NewA> groupKeyMappingA;
    private final UniConstraintCollector<A, ?, NewB> collectorB;

    public UniGroupBy1Map1CollectMutator(Function<A, NewA> groupKeyMappingA,
            UniConstraintCollector<A, ?, NewB> collectorB) {
        this.groupKeyMappingA = groupKeyMappingA;
        this.collectorB = collectorB;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        return groupWithCollect(ruleAssembler, () -> new DroolsUniToBiGroupByAccumulator<>(groupKeyMappingA, collectorB,
                ruleAssembler.getVariable(0)));
    }
}
