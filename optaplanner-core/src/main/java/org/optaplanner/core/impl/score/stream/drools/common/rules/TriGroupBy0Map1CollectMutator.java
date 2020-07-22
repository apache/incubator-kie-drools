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

import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriAccumulateFunction;

class TriGroupBy0Map1CollectMutator<A, B, C, NewA> extends AbstractTriGroupByMutator {

    private final TriConstraintCollector<A, B, C, ?, NewA> collector;

    public TriGroupBy0Map1CollectMutator(TriConstraintCollector<A, B, C, ?, NewA> collector) {
        this.collector = collector;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        DroolsTriAccumulateFunction<A, B, C, ?, NewA> bridge = new DroolsTriAccumulateFunction<>(collector);
        return collect(ruleAssembler, bridge);
    }
}
