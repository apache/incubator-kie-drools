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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.util.function.Function;

import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupBy;
import org.optaplanner.core.impl.score.stream.drools.common.GroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;

final class DroolsUniToQuadGroupBy<A, NewA, NewB, NewC, NewD>
        extends DroolsAbstractGroupBy<A, QuadTuple<NewA, NewB, NewC, NewD>> {

    private final Function<A, NewA> groupKeyAMapping;
    private final Function<A, NewB> groupKeyBMapping;
    private final UniConstraintCollector<A, ?, NewC> collectorC;
    private final UniConstraintCollector<A, ?, NewD> collectorD;

    public DroolsUniToQuadGroupBy(Function<A, NewA> groupKeyAMapping, Function<A, NewB> groupKeyBMapping,
            UniConstraintCollector<A, ?, NewC> collectorC, UniConstraintCollector<A, ?, NewD> collectorD) {
        this.groupKeyAMapping = groupKeyAMapping;
        this.groupKeyBMapping = groupKeyBMapping;
        this.collectorC = collectorC;
        this.collectorD = collectorD;
    }

    @Override
    protected GroupByCollectorProcessor<A, QuadTuple<NewA, NewB, NewC, NewD>> newAccumulator() {
        return new DroolsUniToQuadGroupByCollectorProcessor<>(groupKeyAMapping, groupKeyBMapping, collectorC, collectorD);
    }

}
