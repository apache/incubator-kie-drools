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

package org.optaplanner.core.impl.score.stream.drools.tri;

import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupBy;
import org.optaplanner.core.impl.score.stream.drools.common.GroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;

final class DroolsTriToBiGroupBy<A, B, C, NewA, NewB>
        extends DroolsAbstractGroupBy<TriTuple<A, B, C>, BiTuple<NewA, NewB>> {

    private final TriFunction<A, B, C, NewA> groupKeyMapping;
    private final TriConstraintCollector<A, B, C, ?, NewB> collector;

    public DroolsTriToBiGroupBy(TriFunction<A, B, C, NewA> groupKeyMapping,
            TriConstraintCollector<A, B, C, ?, NewB> collector) {
        this.groupKeyMapping = groupKeyMapping;
        this.collector = collector;
    }

    @Override
    protected GroupByCollectorProcessor<TriTuple<A, B, C>, BiTuple<NewA, NewB>> newAccumulator() {
        return new DroolsTriToBiGroupByCollectorProcessor<>(groupKeyMapping, collector);
    }

}
