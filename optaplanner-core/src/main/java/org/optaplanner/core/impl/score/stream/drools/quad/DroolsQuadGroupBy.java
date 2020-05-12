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

package org.optaplanner.core.impl.score.stream.drools.quad;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupBy;
import org.optaplanner.core.impl.score.stream.drools.common.GroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;

final class DroolsQuadGroupBy<A, B, C, D, NewA, NewB, NewC, NewD>
        extends DroolsAbstractGroupBy<QuadTuple<A, B, C, D>, QuadTuple<NewA, NewB, NewC, NewD>> {

    private final QuadFunction<A, B, C, D, NewA> groupKeyAMapping;
    private final QuadFunction<A, B, C, D, NewB> groupKeyBMapping;
    private final QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC;
    private final QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD;

    public DroolsQuadGroupBy(QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
            QuadFunction<A, B, C, D, NewB> groupKeyBMapping, QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC,
            QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD) {
        this.groupKeyAMapping = groupKeyAMapping;
        this.groupKeyBMapping = groupKeyBMapping;
        this.collectorC = collectorC;
        this.collectorD = collectorD;
    }

    @Override
    protected GroupByCollectorProcessor<QuadTuple<A, B, C, D>, QuadTuple<NewA, NewB, NewC, NewD>> newAccumulator() {
        return new DroolsQuadGroupByCollectorProcessor<>(groupKeyAMapping, groupKeyBMapping, collectorC, collectorD);
    }

}
