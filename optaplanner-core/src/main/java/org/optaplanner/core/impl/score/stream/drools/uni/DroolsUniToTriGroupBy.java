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
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;

final class DroolsUniToTriGroupBy<A, NewA, NewB, NewC>
        extends DroolsAbstractGroupBy<A, TriTuple<NewA, NewB, NewC>> {

    private final Function<A, NewA> groupKeyAMapping;
    private final Function<A, NewB> groupKeyBMapping;
    private final UniConstraintCollector<A, ?, NewC> collector;

    public DroolsUniToTriGroupBy(Function<A, NewA> groupKeyAMapping, Function<A, NewB> groupKeyBMapping,
            UniConstraintCollector<A, ?, NewC> collector) {
        this.groupKeyAMapping = groupKeyAMapping;
        this.groupKeyBMapping = groupKeyBMapping;
        this.collector = collector;
    }

    @Override
    protected GroupByCollectorProcessor<A, TriTuple<NewA, NewB, NewC>> newAccumulator() {
        return new DroolsUniToTriGroupByCollectorProcessor<>(groupKeyAMapping, groupKeyBMapping, collector);
    }

}
