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

import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadAccumulateFunction;

final class QuadGroupBy0Map1CollectMutator<A, B, C, D, NewA> extends AbstractQuadGroupByMutator {

    private final QuadConstraintCollector<A, B, C, D, ?, NewA> collector;

    public QuadGroupBy0Map1CollectMutator(QuadConstraintCollector<A, B, C, D, ?, NewA> collector) {
        this.collector = collector;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        DroolsQuadAccumulateFunction<A, B, C, D, ?, NewA> bridge = new DroolsQuadAccumulateFunction<>(collector);
        return collect(ruleAssembler, bridge);
    }
}
